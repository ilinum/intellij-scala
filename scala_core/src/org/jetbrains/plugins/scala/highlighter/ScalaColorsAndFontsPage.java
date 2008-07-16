package org.jetbrains.plugins.scala.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scala.icons.Icons;
import org.jetbrains.plugins.scala.ScalaFileType;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Alexander Podkhalyuzin
 * Date: 15.07.2008
 */
public class ScalaColorsAndFontsPage implements ColorSettingsPage {
  @NotNull
  public String getDisplayName() {
    return "Scala";
  }

  @Nullable
  public Icon getIcon() {
    return Icons.FILE_TYPE_LOGO;
  }

  @NotNull
  public AttributesDescriptor[] getAttributeDescriptors() {
    return ATTRS;
  }

  private static final AttributesDescriptor[] ATTRS;

  static {
    ATTRS = new AttributesDescriptor[]{
            new AttributesDescriptor(DefaultHighlighter.KEYWORD_ID, DefaultHighlighter.KEYWORD),
            new AttributesDescriptor(DefaultHighlighter.NUMBER_ID, DefaultHighlighter.NUMBER),
            new AttributesDescriptor(DefaultHighlighter.STRING_ID, DefaultHighlighter.STRING),
            new AttributesDescriptor(DefaultHighlighter.ASSIGN_ID, DefaultHighlighter.ASSIGN),
            new AttributesDescriptor(DefaultHighlighter.PARENTHESES_ID, DefaultHighlighter.PARENTHESES),
            new AttributesDescriptor(DefaultHighlighter.BRACES_ID, DefaultHighlighter.BRACES),
            new AttributesDescriptor(DefaultHighlighter.BRACKETS_ID, DefaultHighlighter.BRACKETS),
            new AttributesDescriptor(DefaultHighlighter.COLON_ID, DefaultHighlighter.COLON),
            new AttributesDescriptor(DefaultHighlighter.SEMICOLON_ID, DefaultHighlighter.SEMICOLON),
            new AttributesDescriptor(DefaultHighlighter.DOT_ID, DefaultHighlighter.DOT),
            new AttributesDescriptor(DefaultHighlighter.COMMA_ID, DefaultHighlighter.COMMA),
            new AttributesDescriptor(DefaultHighlighter.LINE_COMMENT_ID, DefaultHighlighter.LINE_COMMENT),
            new AttributesDescriptor(DefaultHighlighter.BLOCK_COMMENT_ID, DefaultHighlighter.BLOCK_COMMENT),
            new AttributesDescriptor(DefaultHighlighter.DOC_COMMENT_ID, DefaultHighlighter.DOC_COMMENT),
            new AttributesDescriptor(DefaultHighlighter.TYPE_ID, DefaultHighlighter.TYPE),
            new AttributesDescriptor(DefaultHighlighter.BAD_CHARACTER_ID, DefaultHighlighter.BAD_CHARACTER),
    };
  }

  @NotNull
  public ColorDescriptor[] getColorDescriptors() {
    return new ColorDescriptor[0];
  }

  @NotNull
  public SyntaxHighlighter getHighlighter() {
    return SyntaxHighlighterFactory.getSyntaxHighlighter(ScalaFileType.SCALA_LANGUAGE, null, null);
  }

  @NonNls
  @NotNull
  public String getDemoText() {
    return "<keyword>import</keyword> scala<dot>.</dot>collection<dot>.</dot>mutable<dot>.</dot>_\n\n" +
            "<scaladoc>/**\n" +
            " * ScalaDoc comment\n" +
            " */</scaladoc>\n" +
            "<keyword>class</keyword> ScalaClass<par>(</par>x<colon>:</colon> <type>Int</type><par>)</par>" +
            " <keyword>extends</keyword>" +
            " ScalaObject <brace>{</brace>\n" +
            "  <keyword>val</keyword> <classfield>field</classfield> <assign>=</assign> <string>\"String\"</string>\n" +
            "  <keyword>def</keyword> foo<par>(</par>x<colon>:</colon> <type>Float</type><comma>," +
            "</comma> y<colon>:</colon> <type>Float</type><par>)</par> <assign>=</assign> <brace>{</brace>\n" +
            "    Math.sqrt<par>(" +
            "</par>x + y + <number>1000</number><par>)</par><semicolon>;</semicolon>\n" +
            "  <brace>}</brace><linecomment>//this can crash</linecomment>\n" +
            "  <keyword>def</keyword> t<bracket>[</bracket><type>T</type><bracket>]</bracket><colon>:</colon> " +
            "<type>T</type> <assign>=</assign> <keyword>null</keyword>\n" +
            "<brace>}</brace>\n" +
            "\n" +
            "<blockcomment>/*" +
            "  And now ScalaObject" +
            " */</blockcomment>" +
            "<keyword>object</keyword> ScalaObject <brace>{</brace>\n" +
            "<brace>}</brace>";
  }

  @Nullable
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    Map<String, TextAttributesKey> map = new HashMap<String, TextAttributesKey>();
    map.put("keyword", DefaultHighlighter.KEYWORD);
    map.put("par", DefaultHighlighter.PARENTHESES);
    map.put("brace", DefaultHighlighter.BRACES);
    map.put("colon", DefaultHighlighter.COLON);
    map.put("scaladoc", DefaultHighlighter.DOC_COMMENT);
    map.put("string", DefaultHighlighter.STRING);
    //map.put("classfield",);
    map.put("type",DefaultHighlighter.TYPE);
    map.put("assign", DefaultHighlighter.ASSIGN);
    map.put("bracket",DefaultHighlighter.BRACKETS);
    map.put("dot",DefaultHighlighter.DOT);
    map.put("semicolon",DefaultHighlighter.SEMICOLON);
    map.put("comma", DefaultHighlighter.COMMA);
    map.put("number",DefaultHighlighter.NUMBER);
    map.put("linecomment",DefaultHighlighter.LINE_COMMENT);
    map.put("blockcomment",DefaultHighlighter.BLOCK_COMMENT);
    //map.put(,);
    return map;
  }
}
