package org.jetbrains.plugins.scala.codeInspection.unusedInspections

import com.intellij.codeInsight.daemon.ProblemHighlightFilter
import com.intellij.openapi.util.Key
import com.intellij.psi.util.{PsiModificationTracker, PsiTreeUtil}
import com.intellij.psi.{PsiElement, PsiFile}
import org.jetbrains.plugins.scala.annotator.importsTracker.{ImportTracker, ScalaRefCountHolder}
import org.jetbrains.plugins.scala.lang.psi.ScalaPsiUtil
import org.jetbrains.plugins.scala.lang.psi.api.base.ScReferenceElement
import org.jetbrains.plugins.scala.lang.psi.api.base.types.{ScSimpleTypeElement, ScTypeElement}
import org.jetbrains.plugins.scala.lang.psi.api.expr._
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.imports.usages.{ReadValueUsed, ValueUsed, WriteValueUsed}
import org.jetbrains.plugins.scala.lang.psi.api.{ImplicitParametersOwner, ScalaFile, ScalaRecursiveElementVisitor}
import org.jetbrains.plugins.scala.lang.psi.light.scala.isLightScNamedElement
import org.jetbrains.plugins.scala.lang.resolve.ScalaResolveResult

/**
  * Created by Svyatoslav Ilinskiy on 18.07.16.
  */
object LocalReferenceCounter {
  def countReferences(file: ScalaFile): Unit = {
    if (!isUpToDate(file)) {
      val holder: ScalaRefCountHolder = ScalaRefCountHolder.getInstance(file)
      val importTracker = ImportTracker.getInstance(file.getProject)
      val visitor = new ScalaRecursiveElementVisitor {
        override def visitReference(ref: ScReferenceElement): Unit = {
          if (!ref.isSoft) {
            ref.multiResolve(false).foreach {
              case resolveResult: ScalaResolveResult =>
                registerUsedElement(ref, resolveResult, checkWrite = true, holder)
                importTracker.registerUsedImports(file, resolveResult.importsUsed)
              case _ =>
            }
          }
          super.visitReference(ref)
        }

        override def visitExpression(expr: ScExpression): Unit = {
          checkImplicitParameters(expr)
          checkTypeImplicitConversions(expr)
          super.visitExpression(expr)
        }

        override def visitTypeElement(te: ScTypeElement): Unit = {
          te match {
            case simple: ScSimpleTypeElement => checkImplicitParameters(simple)
            case _ =>
          }
          super.visitTypeElement(te)
        }

        override def visitMethodCallExpression(call: ScMethodCall): Unit = {
          importTracker.registerUsedImports(file, call.getImportsUsed)
          super.visitMethodCallExpression(call)
        }


        override def visitForExpression(expr: ScForStatement): Unit = {
          importTracker.registerUsedImports(file, ScalaPsiUtil.getExprImports(expr))
          super.visitForExpression(expr)
        }

        private def checkImplicitParameters(owner: ImplicitParametersOwner): Unit = {
          if (!file.isCompiled) {
            owner.findImplicitParameters match {
              case Some(seq) =>
                seq.foreach { resolveResult =>
                  registerUsedElement(owner, resolveResult, checkWrite = false, holder)
                  importTracker.registerUsedImports(file, resolveResult.importsUsed)
                }
              case _ =>
            }
          }
        }

        private def checkTypeImplicitConversions(expr: ScExpression) = {
          def check(fromUnderscore: Boolean): Unit = {
            val res = expr.getTypeAfterImplicitConversion(expectedOption = expr.smartExpectedType(fromUnderscore),
              fromUnderscore = fromUnderscore)
            importTracker.registerUsedImports(file, res.importsUsed)
          }

          if (ScUnderScoreSectionUtil.isUnderscoreFunction(expr)) {
            check(fromUnderscore = true)
          }
          check(fromUnderscore = false)
        }
      }

      val dirtyScope = file.getTextRange
      holder.analyze(() => file.accept(visitor), Option(dirtyScope), file)
      markFileUpToDate(file)
    }
  }

  private def registerUsedElement(element: PsiElement, resolveResult: ScalaResolveResult, checkWrite: Boolean, holder: ScalaRefCountHolder) {
    val named = resolveResult.getActualElement match {
      case isLightScNamedElement(e) => e
      case e => e
    }
    val file = element.getContainingFile
    if (named.isValid && named.getContainingFile == file && !PsiTreeUtil.isAncestor(named, element, true)) {
      //to filter recursive usages
      val value: ValueUsed = element match {
        case ref: ScReferenceExpression if checkWrite &&
          ScalaPsiUtil.isPossiblyAssignment(ref.asInstanceOf[PsiElement]) => WriteValueUsed(named)
        case _ => ReadValueUsed(named)
      }
      holder.registerValueUsed(value)
      // For use of unapply method, see SCL-3463
      resolveResult.parentElement.foreach(parent => holder.registerValueUsed(ReadValueUsed(parent)))
    }
  }

  private val ScalaLocalReferenceCounterPass: Key[java.lang.Long] = Key.create("ScalaLocalReferenceCounterPass")


  private def isUpToDate(file: PsiFile): Boolean = {
    val lastStamp = file.getUserData(ScalaLocalReferenceCounterPass)
    val currentStamp: Long = PsiModificationTracker.SERVICE.getInstance(file.getProject).getModificationCount
    lastStamp != null && lastStamp == currentStamp || !ProblemHighlightFilter.shouldHighlightFile(file)
  }

  private def markFileUpToDate(file: PsiFile) {
    val lastStamp: java.lang.Long = PsiModificationTracker.SERVICE.getInstance(file.getProject).getModificationCount
    file.putUserData(ScalaLocalReferenceCounterPass, lastStamp)
  }
}
