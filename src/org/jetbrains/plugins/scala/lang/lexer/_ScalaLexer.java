/* The following code was generated by JFlex 1.4.1 on 26.09.06 20:24 */

package org.jetbrains.plugins.scala.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import java.util.*;
import java.lang.reflect.Field;
import org.jetbrains.annotations.NotNull;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.1
 * on 26.09.06 20:24 from the specification file
 * <tt>scala.flex</tt>
 */
public class _ScalaLexer implements FlexLexer, ScalaTokenTypes {
  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int IN_LINE_COMMENT_STATE = 2;
  public static final int IN_BLOCK_COMMENT_STATE = 1;

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\0\1\42\1\0\1\0\1\41\22\0\1\0\11\0\1\44"+
    "\4\0\1\43\1\33\7\35\2\34\7\0\6\36\5\40\1\32\16\40"+
    "\4\0\1\37\1\0\1\1\1\2\1\6\1\12\1\7\1\13\1\30"+
    "\1\10\1\17\1\25\1\27\1\11\1\21\1\16\1\14\1\22\1\31"+
    "\1\5\1\3\1\4\1\24\1\26\1\23\1\15\1\20\1\40\uff85\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\3\0\1\1\21\2\2\3\2\1\3\4\2\5\15\2"+
    "\1\6\7\2\1\7\7\2\1\3\1\0\1\3\1\10"+
    "\1\11\4\2\1\12\13\2\1\13\1\2\1\14\3\2"+
    "\1\15\10\2\1\16\1\17\1\3\4\2\1\20\1\2"+
    "\1\21\1\22\2\2\1\23\2\2\1\24\5\2\1\25"+
    "\13\2\1\26\1\27\1\30\2\2\1\31\1\32\1\2"+
    "\1\33\1\34\4\2\1\35\1\36\3\2\1\37\1\40"+
    "\1\2\1\41\1\42\3\2\1\43\2\2\1\44\5\2"+
    "\1\45\1\46\2\2\1\47\1\2\1\50\1\51\1\52"+
    "\1\53\1\54\1\2\1\55";

  private static int [] zzUnpackAction() {
    int [] result = new int[178];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\45\0\112\0\157\0\224\0\271\0\336\0\u0103"+
    "\0\u0128\0\u014d\0\u0172\0\u0197\0\u01bc\0\u01e1\0\u0206\0\u022b"+
    "\0\u0250\0\u0275\0\u029a\0\u02bf\0\u02e4\0\u0309\0\u032e\0\u0353"+
    "\0\u0378\0\157\0\u039d\0\u03c2\0\u03e7\0\157\0\u040c\0\u0431"+
    "\0\u0456\0\u047b\0\u04a0\0\u04c5\0\u04ea\0\u050f\0\u0534\0\u0559"+
    "\0\u057e\0\u05a3\0\u05c8\0\271\0\u05ed\0\u0612\0\u0637\0\u065c"+
    "\0\u0681\0\u06a6\0\u06cb\0\271\0\u06f0\0\u0715\0\u073a\0\u075f"+
    "\0\u0784\0\u07a9\0\u07ce\0\157\0\u07f3\0\u0818\0\157\0\157"+
    "\0\u083d\0\u0862\0\u0887\0\u08ac\0\271\0\u08d1\0\u08f6\0\u091b"+
    "\0\u0940\0\u0965\0\u098a\0\u09af\0\u09d4\0\u09f9\0\u0a1e\0\u0a43"+
    "\0\271\0\u0a68\0\271\0\u0a8d\0\u0ab2\0\u0ad7\0\271\0\u0afc"+
    "\0\u0b21\0\u0b46\0\u0b6b\0\u0b90\0\u0bb5\0\u0bda\0\u0bff\0\271"+
    "\0\271\0\u0c24\0\u0c49\0\u0c6e\0\u0c93\0\u0cb8\0\271\0\u0cdd"+
    "\0\271\0\271\0\u0d02\0\u0d27\0\271\0\u0d4c\0\u0d71\0\271"+
    "\0\u0d96\0\u0dbb\0\u0de0\0\u0e05\0\u0e2a\0\271\0\u0e4f\0\u0e74"+
    "\0\u0e99\0\u0ebe\0\u0ee3\0\u0f08\0\u0f2d\0\u0f52\0\u0f77\0\u0f9c"+
    "\0\u0fc1\0\271\0\271\0\271\0\u0fe6\0\u100b\0\271\0\271"+
    "\0\u1030\0\271\0\u1055\0\u107a\0\u109f\0\u10c4\0\u10e9\0\271"+
    "\0\271\0\u110e\0\u1133\0\u1158\0\271\0\271\0\u117d\0\271"+
    "\0\271\0\u11a2\0\u11c7\0\u11ec\0\271\0\u1211\0\u1236\0\271"+
    "\0\u125b\0\u1280\0\u12a5\0\u12ca\0\u12ef\0\271\0\271\0\u1314"+
    "\0\u1339\0\271\0\u135e\0\271\0\271\0\271\0\271\0\271"+
    "\0\u1383\0\271";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[178];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13"+
    "\2\6\1\14\1\15\1\16\1\6\1\17\1\20\1\21"+
    "\1\22\1\23\1\24\2\6\1\25\4\6\1\26\2\27"+
    "\3\6\1\30\1\4\1\31\1\4\41\32\1\33\2\32"+
    "\1\34\41\32\1\35\1\36\2\32\46\0\1\6\1\37"+
    "\30\6\3\40\3\6\5\0\32\6\3\40\3\6\5\0"+
    "\6\6\1\41\14\6\1\42\6\6\3\40\3\6\5\0"+
    "\4\6\1\43\2\6\1\44\7\6\1\45\12\6\3\40"+
    "\3\6\5\0\6\6\1\46\23\6\3\40\3\6\5\0"+
    "\1\47\7\6\1\50\21\6\3\40\3\6\5\0\10\6"+
    "\1\51\3\6\1\52\15\6\3\40\3\6\5\0\6\6"+
    "\1\53\4\6\1\54\16\6\3\40\3\6\5\0\1\55"+
    "\12\6\1\56\2\6\1\57\13\6\3\40\3\6\5\0"+
    "\1\6\1\60\23\6\1\61\4\6\3\40\3\6\5\0"+
    "\6\6\1\62\14\6\1\63\6\6\3\40\3\6\5\0"+
    "\12\6\1\64\5\6\1\65\11\6\3\40\3\6\5\0"+
    "\16\6\1\66\13\6\3\40\3\6\5\0\1\67\31\6"+
    "\3\40\3\6\5\0\1\70\3\6\1\71\25\6\3\40"+
    "\3\6\5\0\7\6\1\72\22\6\3\40\3\6\5\0"+
    "\1\73\31\6\3\40\3\6\15\0\1\74\3\0\1\75"+
    "\14\0\1\74\1\76\1\0\1\76\20\0\1\74\20\0"+
    "\1\74\3\27\51\0\1\4\45\0\1\77\1\100\42\0"+
    "\1\32\45\0\1\36\43\0\1\36\3\0\2\6\1\101"+
    "\27\6\3\40\3\6\5\0\36\40\1\0\1\40\5\0"+
    "\1\102\31\6\3\40\3\6\5\0\21\6\1\103\10\6"+
    "\3\40\3\6\5\0\1\104\16\6\1\105\3\6\1\106"+
    "\6\6\3\40\3\6\5\0\4\6\1\107\11\6\1\110"+
    "\13\6\3\40\3\6\5\0\21\6\1\111\10\6\3\40"+
    "\3\6\5\0\3\6\1\112\24\6\1\113\1\6\3\40"+
    "\3\6\5\0\2\6\1\114\1\115\26\6\3\40\3\6"+
    "\5\0\1\116\31\6\3\40\3\6\5\0\2\6\1\117"+
    "\27\6\3\40\3\6\5\0\3\6\1\120\26\6\3\40"+
    "\3\6\5\0\12\6\1\121\17\6\3\40\3\6\5\0"+
    "\10\6\1\122\21\6\3\40\3\6\5\0\4\6\1\123"+
    "\25\6\3\40\3\6\5\0\15\6\1\124\14\6\3\40"+
    "\3\6\5\0\24\6\1\125\5\6\3\40\3\6\5\0"+
    "\6\6\1\126\23\6\3\40\3\6\5\0\22\6\1\127"+
    "\7\6\3\40\3\6\5\0\10\6\1\130\21\6\3\40"+
    "\3\6\5\0\21\6\1\131\10\6\3\40\3\6\5\0"+
    "\6\6\1\132\23\6\3\40\3\6\5\0\3\6\1\133"+
    "\26\6\3\40\3\6\5\0\5\6\1\134\24\6\3\40"+
    "\3\6\5\0\13\6\1\135\2\6\1\136\13\6\3\40"+
    "\3\6\5\0\16\6\1\137\13\6\3\40\3\6\5\0"+
    "\4\6\1\140\3\6\1\141\21\6\3\40\3\6\5\0"+
    "\2\142\3\0\2\142\2\0\2\142\17\0\4\142\17\0"+
    "\1\74\20\0\1\74\1\76\1\0\1\76\10\0\3\6"+
    "\1\143\26\6\3\40\3\6\5\0\10\6\1\144\21\6"+
    "\3\40\3\6\5\0\6\6\1\145\23\6\3\40\3\6"+
    "\5\0\16\6\1\146\13\6\3\40\3\6\5\0\6\6"+
    "\1\147\23\6\3\40\3\6\5\0\13\6\1\150\16\6"+
    "\3\40\3\6\5\0\2\6\1\151\27\6\3\40\3\6"+
    "\5\0\6\6\1\152\23\6\3\40\3\6\5\0\23\6"+
    "\1\153\6\6\3\40\3\6\5\0\23\6\1\154\6\6"+
    "\3\40\3\6\5\0\6\6\1\155\23\6\3\40\3\6"+
    "\5\0\5\6\1\156\24\6\3\40\3\6\5\0\2\6"+
    "\1\157\27\6\3\40\3\6\5\0\6\6\1\160\23\6"+
    "\3\40\3\6\5\0\6\6\1\161\23\6\3\40\3\6"+
    "\5\0\2\6\1\162\27\6\3\40\3\6\5\0\1\163"+
    "\31\6\3\40\3\6\5\0\6\6\1\164\23\6\3\40"+
    "\3\6\5\0\4\6\1\165\25\6\3\40\3\6\5\0"+
    "\10\6\1\166\21\6\3\40\3\6\5\0\10\6\1\167"+
    "\2\6\1\170\16\6\3\40\3\6\5\0\10\6\1\171"+
    "\21\6\3\40\3\6\5\0\5\6\1\172\24\6\3\40"+
    "\3\6\5\0\26\6\1\173\3\6\3\40\3\6\5\0"+
    "\3\6\1\174\26\6\3\40\3\6\5\0\25\6\1\175"+
    "\4\6\3\40\3\6\5\0\3\6\1\176\4\6\1\177"+
    "\21\6\3\40\3\6\5\0\2\142\3\0\2\142\1\0"+
    "\1\74\2\142\16\0\1\74\4\142\7\0\4\6\1\200"+
    "\25\6\3\40\3\6\5\0\6\6\1\201\23\6\3\40"+
    "\3\6\5\0\4\6\1\202\25\6\3\40\3\6\5\0"+
    "\3\6\1\203\26\6\3\40\3\6\5\0\22\6\1\204"+
    "\7\6\3\40\3\6\5\0\4\6\1\205\25\6\3\40"+
    "\3\6\5\0\16\6\1\206\13\6\3\40\3\6\5\0"+
    "\7\6\1\207\22\6\3\40\3\6\5\0\2\6\1\210"+
    "\27\6\3\40\3\6\5\0\15\6\1\211\14\6\3\40"+
    "\3\6\5\0\6\6\1\212\23\6\3\40\3\6\5\0"+
    "\10\6\1\213\21\6\3\40\3\6\5\0\5\6\1\214"+
    "\24\6\3\40\3\6\5\0\4\6\1\215\25\6\3\40"+
    "\3\6\5\0\16\6\1\216\13\6\3\40\3\6\5\0"+
    "\4\6\1\217\25\6\3\40\3\6\5\0\11\6\1\220"+
    "\20\6\3\40\3\6\5\0\7\6\1\221\22\6\3\40"+
    "\3\6\5\0\1\222\31\6\3\40\3\6\5\0\6\6"+
    "\1\223\23\6\3\40\3\6\5\0\1\224\31\6\3\40"+
    "\3\6\5\0\7\6\1\225\22\6\3\40\3\6\5\0"+
    "\6\6\1\226\23\6\3\40\3\6\5\0\1\227\31\6"+
    "\3\40\3\6\5\0\11\6\1\230\20\6\3\40\3\6"+
    "\5\0\15\6\1\231\14\6\3\40\3\6\5\0\4\6"+
    "\1\232\25\6\3\40\3\6\5\0\11\6\1\233\20\6"+
    "\3\40\3\6\5\0\10\6\1\234\21\6\3\40\3\6"+
    "\5\0\3\6\1\235\26\6\3\40\3\6\5\0\16\6"+
    "\1\236\13\6\3\40\3\6\5\0\5\6\1\237\24\6"+
    "\3\40\3\6\5\0\3\6\1\240\26\6\3\40\3\6"+
    "\5\0\27\6\1\241\2\6\3\40\3\6\5\0\5\6"+
    "\1\242\24\6\3\40\3\6\5\0\3\6\1\243\26\6"+
    "\3\40\3\6\5\0\5\6\1\244\24\6\3\40\3\6"+
    "\5\0\6\6\1\245\23\6\3\40\3\6\5\0\2\6"+
    "\1\246\27\6\3\40\3\6\5\0\17\6\1\247\12\6"+
    "\3\40\3\6\5\0\11\6\1\250\20\6\3\40\3\6"+
    "\5\0\16\6\1\251\13\6\3\40\3\6\5\0\6\6"+
    "\1\252\23\6\3\40\3\6\5\0\3\6\1\253\26\6"+
    "\3\40\3\6\5\0\6\6\1\254\23\6\3\40\3\6"+
    "\5\0\3\6\1\255\26\6\3\40\3\6\5\0\2\6"+
    "\1\256\27\6\3\40\3\6\5\0\6\6\1\257\23\6"+
    "\3\40\3\6\5\0\3\6\1\260\26\6\3\40\3\6"+
    "\5\0\6\6\1\261\23\6\3\40\3\6\5\0\11\6"+
    "\1\262\20\6\3\40\3\6\4\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[5032];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;
  private static final char[] EMPTY_BUFFER = new char[0];
  private static final int YYEOF = -1;
  private static java.io.Reader zzReader = null; // Fake

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\3\0\1\11\25\1\1\11\3\1\1\11\35\1\1\11"+
    "\1\0\1\1\2\11\162\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[178];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private CharSequence zzBuffer = "";

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the textposition at the last state to be included in yytext */
  private int zzPushbackPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /**
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
    private IElementType process(IElementType type){
        return type;
    }



  public _ScalaLexer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public _ScalaLexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 100) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }

  public final int getTokenStart(){
    return zzStartRead;
  }

  public final int getTokenEnd(){
    return getTokenStart() + yylength();
  }

  public void reset(CharSequence buffer, int initialState){
    zzBuffer = buffer;
    zzCurrentPos = zzMarkedPos = zzStartRead = 0;
    zzPushbackPos = 0;
    zzAtEOF  = false;
    zzAtBOL = true;
    zzEndRead = buffer.length();
    yybegin(initialState);
  }

  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   *
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {
    return true;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final CharSequence yytext() {
    return zzBuffer.subSequence(zzStartRead, zzMarkedPos);
  }


  /**
   * Returns the character at position <tt>pos</tt> from the
   * matched text.
   *
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch.
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer.charAt(zzStartRead+pos);
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of
   * yypushback(int) and a match-all fallback rule) this method
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() {
    if (!zzEOFDone) {
      zzEOFDone = true;
    
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public IElementType advance() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    CharSequence zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = zzLexicalState;


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL.charAt(zzCurrentPosL++);
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL.charAt(zzCurrentPosL++);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 19: 
          { return process(kCASE);
          }
        case 46: break;
        case 18: 
          { return process(kTYPE);
          }
        case 47: break;
        case 37: 
          { return process(kEXTENDS);
          }
        case 48: break;
        case 20: 
          { return process(kELSE);
          }
        case 49: break;
        case 23: 
          { return process(kTRAIT);
          }
        case 50: break;
        case 27: 
          { return process(kFALSE);
          }
        case 51: break;
        case 22: 
          { return process(kSUPER);
          }
        case 52: break;
        case 15: 
          { return process(kVAL);
          }
        case 53: break;
        case 34: 
          { return process(kRETURN);
          }
        case 54: break;
        case 10: 
          { return process(kTRY);
          }
        case 55: break;
        case 26: 
          { return process(kCLASS);
          }
        case 56: break;
        case 1: 
          { return tSTUB;
          }
        case 57: break;
        case 17: 
          { return process(kTHIS);
          }
        case 58: break;
        case 41: 
          { return process(kABSTRACT);
          }
        case 59: break;
        case 6: 
          { return process(kDO);
          }
        case 60: break;
        case 35: 
          { return process(kOBJECT);
          }
        case 61: break;
        case 3: 
          { return process(tINTEGER);
          }
        case 62: break;
        case 5: 
          { yybegin(YYINITIAL);
                                            return process(tCOMMENT);
          }
        case 63: break;
        case 7: 
          { return process(kIF);
          }
        case 64: break;
        case 36: 
          { return process(kIMPORT);
          }
        case 65: break;
        case 38: 
          { return process(kFINALLY);
          }
        case 66: break;
        case 24: 
          { return process(kTHROW);
          }
        case 67: break;
        case 11: 
          { return process(kDEF);
          }
        case 68: break;
        case 39: 
          { return process(kPACKAGE);
          }
        case 69: break;
        case 29: 
          { return process(kYIELD);
          }
        case 70: break;
        case 14: 
          { return process(kVAR);
          }
        case 71: break;
        case 28: 
          { return process(kFINAL);
          }
        case 72: break;
        case 43: 
          { return process(kOVERRIDE);
          }
        case 73: break;
        case 42: 
          { return process(kREQUIRES);
          }
        case 74: break;
        case 21: 
          { return process(kNULL);
          }
        case 75: break;
        case 12: 
          { return process(kFOR);
          }
        case 76: break;
        case 44: 
          { return process(kIMPLICIT);
          }
        case 77: break;
        case 31: 
          { return process(kWHITH);
          }
        case 78: break;
        case 33: 
          { return process(kSEALED);
          }
        case 79: break;
        case 13: 
          { return process(kNEW);
          }
        case 80: break;
        case 32: 
          { return process(kWHILE);
          }
        case 81: break;
        case 8: 
          { yybegin(IN_LINE_COMMENT_STATE);
                                            return process(tCOMMENT);
          }
        case 82: break;
        case 40: 
          { return process(kPRIVATE);
          }
        case 83: break;
        case 45: 
          { return process(kPROTECTED);
          }
        case 84: break;
        case 2: 
          { return process(tIDENTIFIER);
          }
        case 85: break;
        case 4: 
          { return process(tCOMMENT);
          }
        case 86: break;
        case 9: 
          { yybegin(IN_BLOCK_COMMENT_STATE);
                                            return process(tCOMMENT);
          }
        case 87: break;
        case 25: 
          { return process(kCATCH);
          }
        case 88: break;
        case 16: 
          { return process(kTRUE);
          }
        case 89: break;
        case 30: 
          { return process(kMATCH);
          }
        case 90: break;
        default:
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
            return null;
          }
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
