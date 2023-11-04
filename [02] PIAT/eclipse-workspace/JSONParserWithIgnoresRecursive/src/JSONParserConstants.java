/* Generated By:JavaCC: Do not edit this line. JSONParserConstants.java */

/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface JSONParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int GRAPHS = 5;
  /** RegularExpression Id. */
  int OBJECT_START = 6;
  /** RegularExpression Id. */
  int OBJECT_END = 7;
  /** RegularExpression Id. */
  int ARRAY_START = 8;
  /** RegularExpression Id. */
  int ARRAY_END = 9;
  /** RegularExpression Id. */
  int STRING = 10;
  /** RegularExpression Id. */
  int INTEGER = 11;
  /** RegularExpression Id. */
  int DOUBLE = 12;
  /** RegularExpression Id. */
  int TRUE = 13;
  /** RegularExpression Id. */
  int FALSE = 14;
  /** RegularExpression Id. */
  int NULL = 15;
  /** RegularExpression Id. */
  int COMMA = 16;
  /** RegularExpression Id. */
  int COLON = 17;
  /** RegularExpression Id. */
  int ANYTHING = 18;

  /** Lexical state. */
  int ARRAY = 0;
  /** Lexical state. */
  int DEFAULT = 1;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\r\"",
    "\"\\n\"",
    "\"\\\"@graph\\\"\"",
    "\"{\"",
    "\"}\"",
    "\"[\"",
    "\"]\"",
    "<STRING>",
    "<INTEGER>",
    "<DOUBLE>",
    "\"true\"",
    "\"false\"",
    "\"null\"",
    "\",\"",
    "\":\"",
    "<ANYTHING>",
  };

}
