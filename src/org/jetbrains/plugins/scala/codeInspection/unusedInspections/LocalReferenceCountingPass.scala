package org.jetbrains.plugins.scala.codeInspection.unusedInspections

import com.intellij.codeHighlighting.{Pass, TextEditorHighlightingPass, TextEditorHighlightingPassFactory, TextEditorHighlightingPassRegistrar}
import com.intellij.openapi.editor.{Document, Editor}
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile

/**
  * Created by Svyatoslav Ilinskiy on 18.07.16.
  */
class LocalReferenceCountingPass(file: ScalaFile, document: Option[Document])
  extends TextEditorHighlightingPass(file.getProject, document.orNull) {

  //this pass collects information about unused references
  override def doApplyInformationToEditor(): Unit = {}

  override def doCollectInformation(progress: ProgressIndicator): Unit = {
    LocalReferenceCounter.countReferences(file)
  }
}

class LocalReferenceCountingPassFactory(project: Project) extends TextEditorHighlightingPassFactory {
  val id = TextEditorHighlightingPassRegistrar.getInstance(project).
    registerTextEditorHighlightingPass(this, Array[Int](Pass.LOCAL_INSPECTIONS), null, false, -1)

  def projectClosed() {}

  def projectOpened() {}

  def createHighlightingPass(file: PsiFile, editor: Editor): TextEditorHighlightingPass = file match {
    case scalaFile: ScalaFile => new LocalReferenceCountingPass(scalaFile, Option(editor.getDocument))
    case _ => null
  }

  def initComponent() {}

  def disposeComponent() {}

  def getComponentName: String = "Scala Local Reference Counting Pass"
}

object LocalReferenceCountingPassFactory {
  def instance(project: Project): LocalReferenceCountingPassFactory = {
    project.getComponent(classOf[LocalReferenceCountingPassFactory])
  }
}
