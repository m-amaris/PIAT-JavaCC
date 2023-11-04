/* Generated By:JavaCC: Do not edit this line. XMLParserConstants.java */

/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface XMLParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int OPEN_CONCEPTS = 5;
  /** RegularExpression Id. */
  int OPEN_CONCEPT = 6;
  /** RegularExpression Id. */
  int CLOSE_CONCEPTS = 7;
  /** RegularExpression Id. */
  int OPEN_CODE = 8;
  /** RegularExpression Id. */
  int CLOSE_CODE = 9;
  /** RegularExpression Id. */
  int OPEN_LABEL = 10;
  /** RegularExpression Id. */
  int CLOSE_LABEL = 11;
  /** RegularExpression Id. */
  int VALUE = 12;
  /** RegularExpression Id. */
  int CLOSE_CONCEPT = 13;
  /** RegularExpression Id. */
  int ID = 14;
  /** RegularExpression Id. */
  int STRING = 15;
  /** RegularExpression Id. */
  int END_ELEMENT = 16;
  /** RegularExpression Id. */
  int ANYTHING = 17;

  /** Lexical state. */
  int CONCEPTS = 0;
  /** Lexical state. */
  int CONCEPT = 1;
  /** Lexical state. */
  int DEFAULT = 2;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\r\"",
    "\"\\n\"",
    "\"<concepts>\"",
    "\"<concept\"",
    "\"</concepts>\"",
    "\"<code\"",
    "\"/code>\"",
    "\"<label\"",
    "\"/label>\"",
    "<VALUE>",
    "\"</concept>\"",
    "\"id=\"",
    "<STRING>",
    "\">\"",
    "<ANYTHING>",
  };

}
