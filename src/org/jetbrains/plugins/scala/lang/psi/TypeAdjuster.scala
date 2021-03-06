package org.jetbrains.plugins.scala.lang.psi

import com.intellij.diagnostic.LogMessageEx
import com.intellij.openapi.application.{ApplicationAdapter, ApplicationManager}
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Key
import com.intellij.psi._
import com.intellij.psi.impl.DebugUtil
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.plugins.scala.annotator.intention.ScalaImportTypeFix
import org.jetbrains.plugins.scala.extensions._
import org.jetbrains.plugins.scala.lang.formatting.settings.ScalaCodeStyleSettings
import org.jetbrains.plugins.scala.lang.psi.api.base.ScReferenceElement
import org.jetbrains.plugins.scala.lang.psi.api.base.patterns.ScBindingPattern
import org.jetbrains.plugins.scala.lang.psi.api.base.types.{ScParameterizedTypeElement, ScSimpleTypeElement, ScTypeElement, ScTypeProjection}
import org.jetbrains.plugins.scala.lang.psi.api.statements.{ScTypeAlias, ScTypeAliasDefinition}
import org.jetbrains.plugins.scala.lang.psi.impl.ScalaPsiElementFactory
import org.jetbrains.plugins.scala.lang.psi.types.api.ScTypePresentation
import org.jetbrains.plugins.scala.lang.psi.types.api.designator.ScDesignatorType
import org.jetbrains.plugins.scala.lang.refactoring.ScalaNamesValidator.isIdentifier
import org.jetbrains.plugins.scala.lang.refactoring.util.ScalaNamesUtil
import org.jetbrains.plugins.scala.lang.resolve.ResolveTargets._
import org.jetbrains.plugins.scala.lang.resolve.processor.BaseProcessor
import org.jetbrains.plugins.scala.util.ScEquivalenceUtil

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


object TypeAdjuster extends ApplicationAdapter {
  private val LOG = Logger.getInstance(getClass)

  ApplicationManager.getApplication.addApplicationListener(this)

  private val markedElements = ArrayBuffer[SmartPsiElementPointer[PsiElement]]()

  override def writeActionFinished(action: scala.Any): Unit = {
    if (markedElements.nonEmpty) {
      val project = markedElements.head.getProject
      PsiDocumentManager.getInstance(project).commitAllDocuments()
      adjustMarkedElements()
    }
  }

  def adjustFor(elements: Seq[PsiElement], addImports: Boolean = true, useTypeAliases: Boolean = true): Unit = {

    val typeElements = elements.toVector.flatMap(collectAdjustableTypeElements).distinct

    val rInfos = toReplacementInfos(typeElements, useTypeAliases)

    replaceAndAddImports(rInfos, addImports)
  }

  def markToAdjust(element: PsiElement): Any = {
    if (element != null && element.isValid) {
      val manager = SmartPointerManager.getInstance(element.getProject)
      markedElements += manager.createSmartPsiElementPointer(element)
    }
  }

  private def adjustMarkedElements() = {
    val elements = markedElements.flatMap(_.getElement.toOption).filter(_.isValid)
    markedElements.clear()

    adjustFor(elements)
  }

  private def newRef(text: String, position: PsiElement): Option[ScReferenceElement] = {
    findRef(newTypeElem(text, position))
  }

  private def newTypeElem(name: String, position: PsiElement) = {
    val newTypeElem = ScalaPsiElementFactory.createTypeElementFromText(name, position.getContext, position)
    if (newTypeElem == null) {
      val messageEvent = LogMessageEx.createEvent(s"Cannot create type from text:\n$name", DebugUtil.currentStackTrace())
      LOG.error(messageEvent)
    }
    newTypeElem
  }

  private def toReplacementInfos(typeElements: Seq[ScTypeElement], useTypeAliases: Boolean): Seq[ReplacementInfo] = {
    val infos = typeElements.map(ReplacementInfo.initial)
    infos.flatMap(simplify)
      .map(shortenReference(_, useTypeAliases))
  }

  private val toReplaceKey: Key[String] = Key.create[String]("type.element.to.replace")

  private def simplify(info: ReplacementInfo): Option[ReplacementInfo] = {
    def markToReplace(typeElemOrRef: PsiElement): Unit = typeElemOrRef.putUserData(toReplaceKey, "")
    def isMarkedToReplace(typeElem: ScTypeElement): Boolean = typeElem.getUserData(toReplaceKey) != null

    object `with .type#` {
      def unapply(info: SimpleInfo): Option[ReplacementInfo] = {
        val text = info.replacement
        if (text.contains(".type#"))
          Some(info.withNewText(text.replace(".type#", ".")))
        else None
      }
    }

    object withThisRef {
      def unapply(info: SimpleInfo): Option[ReplacementInfo] = {
        val text = info.replacement
        val thisWithDot = "this."
        val index = text.indexOf(thisWithDot)
        if (index >= 0) {
          val endIndex = index + thisWithDot.length
          val withoutThisType = text.substring(endIndex)
          if (cannotCreateTypeElement(withoutThisType)) None
          else {
            val newResolve = newRef(withoutThisType, info.origElement).flatMap(_.resolve().toOption)
            for {
              oldRes <- info.resolve
              newRes <- newResolve
              if ScEquivalenceUtil.smartEquivalence(oldRes, newRes)
            } yield {
              info.withNewText(withoutThisType)
            }
          }
        } else None
      }

      private def cannotCreateTypeElement(withoutThisText: String) = {
        withoutThisText.startsWith("type") && !isIdentifier(withoutThisText.take(5))
      }
    }

    object withExpandableTypeAlias {
      def unapply(info: SimpleInfo): Option[ReplacementInfo] = {
        val dummyTypeElem = newTypeElem(info.replacement, info.origElement)
        val replacementType = dummyTypeElem.calcType
        val withoutAliases = replacementType.removeAliasDefinitions(expandableOnly = true)
        if (replacementType.presentableText != withoutAliases.presentableText) {
          markToReplace(info.origElement)
          val text = withoutAliases.canonicalText
          val newTypeEl = newTypeElem(text, info.origElement)
          val subTypeElems = collectAdjustableTypeElements(newTypeEl).filter(_ != newTypeEl)
          val newInfo =
            if (subTypeElems.isEmpty)
              ReplacementInfo.initial(newTypeEl).copy(origElement = info.origElement)
            else
              CompoundInfo(info.origElement, newTypeEl, subTypeElems.map(ReplacementInfo.initial))
          Some(newInfo)
        }
        else None
      }
    }

    if (info.origElement.parentsInFile.filterByType[ScTypeElement].exists(isMarkedToReplace)) None
    else info match {
      case cmp: CompoundInfo =>
        Some(cmp.copy(childInfos = cmp.childInfos.flatMap(simplify)))
      case withExpandableTypeAlias(newInfo) => simplify(newInfo)
      case withThisRef(newInfo) => simplify(newInfo)
      case `with .type#`(newInfo) => simplify(newInfo)
      case _ => Some(info)
    }
  }

  private def replaceElem(rInfo: ReplacementInfo): Unit = {
    rInfo match {
      case simple: SimpleInfo =>
        val origElem = simple.origElement
        val replacement = simple.replacement
        if (origElem.getText != replacement) {
          origElem match {
            case _: ScTypeElement =>
              origElem.replace(newTypeElem(replacement, origElem))
            case _: ScReferenceElement =>
              newRef(replacement, origElem).foreach(origElem.replace)
            case _ =>
          }
        }
      case cmp: CompoundInfo =>
        val tempElem = cmp.tempTypeElem
        cmp.childInfos.foreach(replaceElem)
        cmp.origElement.replace(tempElem).asInstanceOf[ScTypeElement]
    }
  }

  private def findRef(elem: PsiElement): Option[ScReferenceElement] = elem match {
    case r: ScReferenceElement => Some(r)
    case ScSimpleTypeElement(Some(r)) => Some(r)
    case _ => None
  }

  private def shortenReference(info: ReplacementInfo, useTypeAliases: Boolean = true): ReplacementInfo = {
    object hasStableReplacement {
      def unapply(rInfo: SimpleInfo): Option[ReplacementInfo] = {
        rInfo.resolve.flatMap { resolved =>
          val position = findRef(rInfo.origElement).getOrElse(info.origElement  )
          val importAlias = ScalaPsiUtil.importAliasFor(resolved, position)
          resolved match {
            case _ if importAlias.isDefined => Some(rInfo.withNewText(importAlias.get.refName))
            case named: PsiNamedElement if ScalaPsiUtil.hasStablePath(named) =>
              named match {
                case clazz: PsiClass =>
                  availableTypeAliasFor(clazz, position, useTypeAliases) match {
                    case Some(ta) if !ta.isAncestorOf(position) =>
                      if (ScalaPsiUtil.hasStablePath(ta)) Some(rInfo.updateTarget(ta))
                      else Some(rInfo.withNewText(ta.name))
                    case _ =>
                      Some(rInfo.updateTarget(named))
                  }
                case _: ScTypeAlias | _: ScBindingPattern => Some(rInfo.updateTarget(named))
                case _ => None
              }
            case _ => checkQualifier(rInfo)
          }
        }
      }

      private def checkQualifier(rInfo: SimpleInfo): Option[ReplacementInfo] = {
        qualifier(rInfo.origElement).map(ReplacementInfo.initial).collect {
          case hasStableReplacement(s: SimpleInfo) if s.replacement != s.origElement.getText => s
        }
      }

      @scala.annotation.tailrec
      private def qualifier(elem: PsiElement): Option[ScReferenceElement] = {
        elem match {
          case ScSimpleTypeElement(Some(ref: ScReferenceElement)) => qualifier(ref)
          case ScReferenceElement.withQualifier(qual: ScReferenceElement) => Some(qual)
          case _ => None
        }
      }
    }

    info match {
      case cmp: CompoundInfo =>
        cmp.copy(childInfos = cmp.childInfos.map(shortenReference(_, useTypeAliases)))
      case hasStableReplacement(rInfo) => rInfo
      case _ => info
    }
  }

  private def collectImportHolders(rInfos: Set[ReplacementInfo]): Map[ReplacementInfo, ScImportsHolder] = {
    def findMaxHolders(infos: Set[ReplacementInfo]): Set[(ReplacementInfo, ScImportsHolder)] = {
      val infosToHolders = infos.map(info => info -> ScalaImportTypeFix.getImportHolder(info.origElement, info.origElement.getProject))
      val holders = infosToHolders.map(_._2)
      val maxHolders = holders.filter(h => !holders.exists(_.isAncestorOf(h)))
      infosToHolders.map {
        case (i, h) => (i, maxHolders.find(PsiTreeUtil.isAncestor(_, h, false)).get)
      }
    }

    val byPath = mutable.Map[String, Set[ReplacementInfo]]()
    for {
      info <- rInfos
      path <- info.pathsToImport
    } {
      byPath.update(path, byPath.getOrElseUpdate(path, Set.empty) + info)
    }

    val withMaxHolders = byPath.values.map(infos => findMaxHolders(infos))
    withMaxHolders.flatten.toMap
  }

  private def replaceAndAddImports(rInfos: Seq[ReplacementInfo], addImports: Boolean): Unit = {
    assert(rInfos.forall(_.origElement.isValid), "Psi shouldn't be modified before this stage!")

    val replacementsWithSameResolve = rInfos.filter(_.checkReplacementResolve).toSet
    val importHolders = collectImportHolders(rInfos.toSet -- replacementsWithSameResolve)
    val holderToPaths = mutable.Map[ScImportsHolder, Set[String]]()

    rInfos.foreach { info =>
      if (!addImports && !replacementsWithSameResolve.contains(info)) {}
      else {
        replaceElem(info)
        val holder = importHolders.get(info)
        if (info.pathsToImport.nonEmpty && holder.isDefined) {
          val pathsToAdd = holderToPaths.getOrElseUpdate(holder.get, Set.empty) ++ info.pathsToImport
          holderToPaths += holder.get -> pathsToAdd
        }
      }
    }

    for ((holder, paths) <- holderToPaths) {
      holder.addImportsForPaths(paths.toSeq, null)
    }
  }

  private def availableTypeAliasFor(clazz: PsiClass, position: PsiElement, useTypeAliases: Boolean): Option[ScTypeAliasDefinition] = {
    if (!useTypeAliases) None
    else {
      class FindTypeAliasProcessor extends BaseProcessor(ValueSet(CLASS))(clazz) {
        var collected: Option[ScTypeAliasDefinition] = None

        override def execute(element: PsiElement, state: ResolveState): Boolean = {
          element match {
            case ta: ScTypeAliasDefinition if ta.isAliasFor(clazz) && !ScTypePresentation.shouldExpand(ta) =>
              collected = Some(ta)
              false
            case _ => true
          }
        }
      }
      val processor = new FindTypeAliasProcessor
      PsiTreeUtil.treeWalkUp(processor, position, null, ResolveState.initial())
      processor.collected
    }
  }

  private def collectAdjustableTypeElements(element: PsiElement): Vector[ScTypeElement] = {
    element.depthFirst().collect {
      case s: ScSimpleTypeElement => s
      case p: ScTypeProjection => p
      case p: ScParameterizedTypeElement => p
    }.toVector
  }

  private trait ReplacementInfo {
    def origElement: PsiElement

    def checkReplacementResolve: Boolean

    def pathsToImport: Seq[String]
  }

  private case class SimpleInfo(origElement: PsiElement, replacement: String,
                                resolve: Option[PsiElement], pathsToImport: Seq[String]) extends ReplacementInfo {

    def withNewText(s: String): SimpleInfo = copy(replacement = s)

    def updateTarget(target: PsiNamedElement): SimpleInfo = {
      def addSingletonType(typeText: String) = origElement match {
        case s: ScSimpleTypeElement if s.singleton => s"$typeText.type"
        case _ => typeText
      }

      def pathAndPrefixed(qual: String, prefixLength: Int): Option[(String, String)] = {
        val words = ScalaNamesUtil.splitName(qual)
        if (words.size > prefixLength) {
          val packageName = words.dropRight(prefixLength).mkString(".")
          val prefixed = words.takeRight(prefixLength + 1).mkString(".")
          Some((packageName, prefixed))
        }
        else None
      }

      def update(newTypeText: String, pathsToImport: Seq[String]) =
        copy(replacement = addSingletonType(newTypeText), resolve = Some(target), pathsToImport = pathsToImport)

      target match {
        case cl: PsiClass if needPrefix(cl) =>
          pathAndPrefixed(cl.qualifiedName, 1) match {
            case Some((packageName, prefixed)) =>
              update(prefixed, Seq(packageName))
            case _ => this
          }
        case _ =>
          ScalaNamesUtil.qualifiedName(target).flatMap { qualName =>
            val addingPrefixesIterator =
              Iterator.from(0)
                .map(pathAndPrefixed(qualName, _))
                .takeWhile(_.isDefined)
                .flatten
            addingPrefixesIterator.collectFirst {
              case (_, prefixed) if resolvesRight(prefixed) =>
                update(prefixed, Seq.empty)
              case (path, prefixed) if !resolvesWrong(prefixed) =>
                update(prefixed, Seq(path))
            }
          }.getOrElse(this)
      }
    }

    override def checkReplacementResolve: Boolean = resolvesRight(replacement)

    private def resolvesRight(refText: String): Boolean = alreadyResolves(refText).getOrElse(false)
    private def resolvesWrong(refText: String): Boolean = alreadyResolves(refText).contains(false)

    private def alreadyResolves(refText: String): Option[Boolean] = {
      def areEquivTypes(e1: PsiNamedElement, e2: PsiNamedElement): Boolean = {
        ScDesignatorType(e1).equiv(ScDesignatorType(e2))
      }

      val ref = newRef(refText, origElement)
      (ref.flatMap(_.resolve().toOption), resolve) match {
        case (Some(e1: PsiNamedElement), Some(e2: PsiNamedElement)) =>
          Some(ScEquivalenceUtil.smartEquivalence(e1, e2) || areEquivTypes(e1, e2))
        case (Some(_), _) => Some(false)
        case _ => None
      }
    }

    private def needPrefix(c: PsiClass) = {
      val fromSettings = ScalaCodeStyleSettings.getInstance(origElement.getProject).hasImportWithPrefix(c.qualifiedName)
      def forInnerClass = {
        val isExternalRefToInnerClass = Option(c.containingClass).exists(!_.isAncestorOf(origElement))
        isExternalRefToInnerClass && !resolvesRight(c.name)
      }

      fromSettings || forInnerClass
    }
  }

  private case class CompoundInfo(origElement: PsiElement, tempTypeElem: ScTypeElement,
                                  childInfos: Seq[ReplacementInfo]) extends ReplacementInfo {

    if (childInfos.isEmpty || childInfos.exists(i => !tempTypeElem.isAncestorOf(i.origElement))) {
      throw new IllegalArgumentException("Wrong usage of CompoundInfo")
    }

    override def checkReplacementResolve: Boolean = childInfos.forall(_.checkReplacementResolve)

    override def pathsToImport: Seq[String] = childInfos.flatMap(_.pathsToImport)
  }

  private object ReplacementInfo {
    private def inner(elem: PsiElement): SimpleInfo = {
      val text = elem.getText
      val resolve = TypeAdjuster.findRef(elem).flatMap(_.resolve().toOption)
      SimpleInfo(elem, text, resolve, Seq.empty)
    }

    def initial(te: ScTypeElement): SimpleInfo = inner(te)
    def initial(ref: ScReferenceElement): SimpleInfo = inner(ref)
  }
}

