package org.jetbrains.plugins.scala.lang.psi.impl.toplevel.imports

import api.toplevel.typedef.ScTypeDefinition
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode

import org.jetbrains.plugins.scala.lang.parser.ScalaElementTypes
import org.jetbrains.plugins.scala.lang.lexer._
import org.jetbrains.plugins.scala.lang.psi.ScalaPsiElementImpl
import org.jetbrains.annotations._

import org.jetbrains.plugins.scala.icons.Icons

import org.jetbrains.plugins.scala.lang.psi.api.toplevel.imports._
import org.jetbrains.plugins.scala.lang.resolve.ResolverEnv
import com.intellij.psi._
import _root_.scala.collection.mutable.HashSet

/** 
* @author Alexander Podkhalyuzin
* Date: 20.02.2008
*/

class ScImportStmtImpl(node: ASTNode) extends ScalaPsiElementImpl(node) with ScImportStmt {
  override def toString: String = "ScImportStatement"

  import scope._

  override def processDeclarations(processor: PsiScopeProcessor,
                                  state: ResolveState,
                                  lastParent: PsiElement,
                                  place: PsiElement): Boolean = {
    for (e <- importExprs) {
      if (e == lastParent) return true
      val elem = e.reference match {
        case Some(ref) =>
          ref.bind match {
            case None => null
            case Some(result) => result.element
          }
        case _ => null
      }
      if (elem != null) {
        val elems : Seq[PsiNamedElement] = elem match {
          //if we have a class or object then the opposite should be processed as well
          case td : ScTypeDefinition => JavaPsiFacade.getInstance(getProject).findClasses(td.getQualifiedName, getResolveScope)
          case _ => Seq.single(elem)
        }

        for (elem <- elems) {
          e.selectorSet match {
            case None =>
              if (e.singleWildcard) {
                if (!elem.processDeclarations(processor, state, null, place)) return false
              } else {
                if (!processor.execute(elem, state)) return false
              }
            case Some(set) => {
              val shadowed: HashSet[PsiElement] = HashSet.empty
              for (selector <- set.selectors) {
                selector.reference.bind match {
                  case Some(result) => {
                    shadowed += result.element
                    if (!processor.execute(result.element, state.put(ResolverEnv.nameKey, selector.importedName))) return false
                  }
                  case _ =>
                }
              }
              if (set.hasWildcard) {
                val p1 = new PsiScopeProcessor {
                  def getHint[T](hintClass: Class[T]): T = processor.getHint(hintClass)

                  def handleEvent(event: PsiScopeProcessor.Event, associated: Object) =
                    processor.handleEvent(event, associated)

                  def execute(element: PsiElement, state: ResolveState): Boolean = {
                    if (shadowed.contains(element)) true else processor.execute(element, state)
                  }
                }
                if (!elem.processDeclarations(p1, state, null, place)) return false
              }
            }
          }
        }
      }
    }

    true
  }

  def deleteStmt: Unit = {
    val node = getParent.getNode
    val remove = node.removeChild _
    val next = getNode.getTreeNext
    if (next == null) {
      remove(getNode)
    }
    else if (next.getText.indexOf("\n") != -1) {
      remove(next)
      remove(getNode)
    } else if (next.getText.charAt(0) == ';') {
      val nextnext = next.getTreeNext
      if (nextnext == null) {
        remove(next)
        remove(getNode)
      }
      else if (next.getText.indexOf("\n") != -1) {
        remove(nextnext)
        remove(next)
        remove(getNode)
      }
    }
    else {
      remove(getNode)
    }
  }
}