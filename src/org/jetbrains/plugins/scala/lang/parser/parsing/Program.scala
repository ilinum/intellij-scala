package org.jetbrains.plugins.scala.lang.parser.parsing

import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenTypes
import org.jetbrains.plugins.scala.lang.lexer.ScalaElementType
import com.intellij.lang.PsiBuilder

/**
 * User: Dmitry.Krasilschikov
 * Date: 02.10.2006
 * Time: 12:53:26
 */

class PROGRAM extends ScalaTokenTypes {
    def parse(builder: PsiBuilder): Unit = {
        var marker = builder.mark()

        while( !builder.eof() ){
            if(builder.getTokenType().toString == "class") {
                marker.drop()
            }

        }
        marker.done(new ScalaElementType("class"));
    }
}