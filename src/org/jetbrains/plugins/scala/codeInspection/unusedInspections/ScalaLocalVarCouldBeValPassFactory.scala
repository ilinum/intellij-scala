package org.jetbrains.plugins.scala.codeInspection.unusedInspections

import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar.Anchor
import com.intellij.codeHighlighting.{TextEditorHighlightingPass, TextEditorHighlightingPassFactory, TextEditorHighlightingPassRegistrar}
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile

/**
  * Created by Svyatoslav Ilinskiy on 11.07.16.
  */
class ScalaLocalVarCouldBeValPassFactory(project: Project) extends TextEditorHighlightingPassFactory {
  TextEditorHighlightingPassRegistrar.getInstance(project).
    registerTextEditorHighlightingPass(this, Anchor.AFTER, LocalReferenceCountingPassFactory.instance(project).id, false, false)

  def projectClosed() {}

  def projectOpened() {}

  def createHighlightingPass(file: PsiFile, editor: Editor): TextEditorHighlightingPass = file match {
    case scalaFile: ScalaFile => new ScalaLocalVarCouldBeValPass(scalaFile, Option(editor.getDocument))
    case _ => null
  }

  def initComponent() {}

  def disposeComponent() {}

  def getComponentName: String = "Scala Local Var Could Be Var Factory"
}
