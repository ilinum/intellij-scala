package org.jetbrains.plugins.scala
package lang.refactoring.introduceField

import com.intellij.internal.statistic.UsageTrigger
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.{Document, Editor}
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.{PsiDocumentManager, PsiElement, PsiFile}
import com.intellij.refactoring.HelpID
import com.intellij.refactoring.util.CommonRefactoringUtil
import org.jetbrains.plugins.scala.extensions.childOf
import org.jetbrains.plugins.scala.lang.psi.ScalaPsiUtil
import org.jetbrains.plugins.scala.lang.psi.api.expr._
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.ScEarlyDefinitions
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.templates.{ScExtendsBlock, ScTemplateParents}
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.{ScMember, ScTemplateDefinition}
import org.jetbrains.plugins.scala.lang.psi.impl.ScalaPsiElementFactory._
import org.jetbrains.plugins.scala.lang.psi.types.ScType
import org.jetbrains.plugins.scala.lang.refactoring.introduceField.ScalaIntroduceFieldHandlerBase._
import org.jetbrains.plugins.scala.lang.refactoring.util.ScalaRefactoringUtil._
import org.jetbrains.plugins.scala.util.ScalaUtils


/**
 * Nikolay.Tropin
 * 6/27/13
 */
class ScalaIntroduceFieldFromExpressionHandler extends ScalaIntroduceFieldHandlerBase {

  private var occurrenceHighlighters = Seq.empty[RangeHighlighter]

  def invoke(project: Project, editor: Editor, file: PsiFile, startOffset: Int, endOffset: Int) {
    try {
      UsageTrigger.trigger(ScalaBundle.message("introduce.field.id"))
      PsiDocumentManager.getInstance(project).commitAllDocuments()
      checkFile(file, project, editor, REFACTORING_NAME)

      val (expr: ScExpression, types: Array[ScType]) = getExpression(project, editor, file, startOffset, endOffset) match {
        case Some((e, tps)) => (e, tps)
        case None =>
          showErrorMessage(ScalaBundle.message("cannot.refactor.not.expression"), project, editor)
          return
      }

      afterClassChoosing[ScExpression](expr, types, project, editor, file, "Choose class for Introduce Field") {
        convertExpressionToField
      }
    }
    catch {
      case _: IntroduceException =>
    }
  }

  override def invoke(project: Project, editor: Editor, file: PsiFile, dataContext: DataContext) {
    val canBeIntroduced: (ScExpression) => Boolean = checkCanBeIntroduced(_)
    afterExpressionChoosing(project, editor, file, dataContext, REFACTORING_NAME, canBeIntroduced) {
      trimSpacesAndComments(editor, file)
      invoke(project, editor, file, editor.getSelectionModel.getSelectionStart, editor.getSelectionModel.getSelectionEnd)
    }
  }

  override def invoke(project: Project, elements: Array[PsiElement], dataContext: DataContext) {
    //nothing
  }

  def convertExpressionToField(ifc: IntroduceFieldContext[ScExpression]) {

    val possiblePlace = checkCanBeIntroduced(ifc.element, showErrorMessage(_, ifc.project, ifc.editor))
    if (!possiblePlace) return

    def runWithDialog() {
      val settings = new IntroduceFieldSettings(ifc)
      if (!settings.canBeInitInDeclaration && !settings.canBeInitLocally) {
        showErrorMessage("Cannot create field from this expression", ifc.project, ifc.editor)
      } else {
        val dialog = getDialog(ifc, settings)
        if (dialog.isOK) {
          runRefactoring(ifc, settings)
        }
      }
    }

    runWithDialog()
  }

  private def runRefactoringInside(ifc: IntroduceFieldContext[ScExpression], settings: IntroduceFieldSettings[ScExpression]) {
    val expression = expressionToIntroduce(ifc.element)
    val mainOcc = ifc.occurrences.filter(_.getStartOffset == ifc.editor.getSelectionModel.getSelectionStart)
    val occurrencesToReplace = if (settings.replaceAll) ifc.occurrences else mainOcc
    val aClass = ifc.aClass
    val checkAnchor: PsiElement = anchorForNewDeclaration(expression, occurrencesToReplace, aClass)
    if (checkAnchor == null) {
      showErrorMessage("Cannot find place for the new field", ifc.project, ifc.editor)
      return
    }
    implicit val projectContext = aClass.projectContext
    val name = settings.name
    val typeName = Option(settings.scType).map(_.canonicalText).getOrElse("")
    val replacedOccurences = replaceOccurences(occurrencesToReplace, name, ifc.file)

    val anchor = anchorForNewDeclaration(expression, replacedOccurences, aClass)
    val initInDecl = settings.initInDeclaration
    var createdDeclaration: PsiElement = null
    if (initInDecl) {
      createdDeclaration = createDeclaration(name, typeName, settings.defineVar, expression)
    } else {
      val underscore = createExpressionFromText("_")
      createdDeclaration = createDeclaration(name, typeName, settings.defineVar, underscore)

      anchorForInitializer(replacedOccurences, ifc.file) match {
        case Some(anchorForInit) =>
          val parent = anchorForInit.getParent
          val assignStmt = createExpressionFromText(s"$name = ${expression.getText}")
          parent.addBefore(assignStmt, anchorForInit)
          parent.addBefore(createNewLine(), anchorForInit)
        case None => throw new IntroduceException

      }
    }

    settings.visibilityLevel match {
      case "" =>
      case other =>
        val modifier = createModifierFromText(other)
        createdDeclaration.asInstanceOf[ScMember].getModifierList.add(modifier)
    }

    lazy val document: Document = ifc.editor.getDocument

    anchor match {
      case (_: ScTemplateParents) childOf (extBl: ScExtendsBlock) =>
        val earlyDef = extBl.addEarlyDefinitions()
        createdDeclaration = earlyDef.addAfter(createdDeclaration, earlyDef.getFirstChild)
      case _ childOf (ed: ScEarlyDefinitions) if onOneLine(document, ed.getTextRange) =>
        def isBlockStmtOrMember(elem: PsiElement) = elem != null && (elem.isInstanceOf[ScBlockStatement] || elem.isInstanceOf[ScMember])
        var declaration = createdDeclaration.getText
        if (isBlockStmtOrMember(anchor)) declaration += "; "
        if (isBlockStmtOrMember(anchor.getPrevSibling)) declaration = "; " + declaration
        document.insertString(anchor.getTextRange.getStartOffset, declaration)
        PsiDocumentManager.getInstance(ifc.project).commitDocument(document)
      case _ childOf parent =>
        createdDeclaration = parent.addBefore(createdDeclaration, anchor)
        parent.addBefore(createNewLine(), anchor)
    }

    ScalaPsiUtil.adjustTypes(createdDeclaration)
  }

  def runRefactoring(ifc: IntroduceFieldContext[ScExpression], settings: IntroduceFieldSettings[ScExpression]) {
    val runnable = new Runnable {
      def run(): Unit = runRefactoringInside(ifc, settings)
    }
    ScalaUtils.runWriteAction(runnable, ifc.project, REFACTORING_NAME)
    ifc.editor.getSelectionModel.removeSelection()
  }

  protected def getDialog(ifc: IntroduceFieldContext[ScExpression], settings: IntroduceFieldSettings[ScExpression]): ScalaIntroduceFieldDialog = {
    val occCount = ifc.occurrences.length
    // Add occurrences highlighting
    if (occCount > 1)
      occurrenceHighlighters = highlightOccurrences(ifc.project, ifc.occurrences, ifc.editor)

    val dialog = new ScalaIntroduceFieldDialog(ifc, settings)
    dialog.show()
    if (!dialog.isOK) {
      if (occCount > 1) {
        occurrenceHighlighters.foreach(_.dispose())
        occurrenceHighlighters = Seq.empty
      }
    }
    dialog
  }

  protected override def isSuitableClass(elem: PsiElement, clazz: ScTemplateDefinition): Boolean = true

  private def onOneLine(document: Document, range: TextRange): Boolean = {
    document.getLineNumber(range.getStartOffset) == document.getLineNumber(range.getEndOffset)
  }

  private def showErrorMessage(text: String, project: Project, editor: Editor) = {
    CommonRefactoringUtil.showErrorHint(project, editor, text, REFACTORING_NAME, HelpID.INTRODUCE_FIELD)
  }

}
