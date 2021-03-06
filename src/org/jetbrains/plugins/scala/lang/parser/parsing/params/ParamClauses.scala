package org.jetbrains.plugins.scala
package lang
package parser
package parsing
package params

import org.jetbrains.plugins.scala.lang.parser.parsing.builder.ScalaPsiBuilder

/**
* @author Alexander Podkhalyuzin
* Date: 06.03.2008
*/

/*
 * ParamClauses ::= {ParamClause} [ImplicitParamClause]
 */
object ParamClauses extends ParamClauses {
  override protected def paramClause = ParamClause
  override protected def implicitParamClause = ImplicitParamClause
}

trait ParamClauses {
  protected def paramClause: ParamClause
  protected def implicitParamClause: ImplicitParamClause

  def parse(builder: ScalaPsiBuilder): Boolean = parse(builder, flag = false)
  def parse(builder: ScalaPsiBuilder, flag: Boolean): Boolean = {
    val paramMarker = builder.mark
    if (flag) {
      if (!paramClause.parse(builder)) {
        builder error ErrMsg("param.clause.expected")
      }
    }
    while (paramClause.parse(builder)) {}
    implicitParamClause parse builder
    paramMarker.done(ScalaElementTypes.PARAM_CLAUSES)
    return true
  }
}