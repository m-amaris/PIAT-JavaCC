/*
	Regression test for XPath and XSLT pattern parsers.
	Author: Ingo Macherius <macherius@gmd.de>
	(c) 1999 GMD
	No warranties, use at your own risk.
*/

import java.io.*;

public class ParserTest {

	final static boolean DEBUG = false;

	public static void main( String[] args ) {

		Reader stream = new StringReader("");
		XPathParser xpp = new XPathParser(stream);
		if (DEBUG)
			xpp.enable_tracing();
		else
			xpp.disable_tracing();

		System.out.println("\n*****************************");
		System.out.println("*       XPath tests         *");
		System.out.println("*****************************");

		for (int i=0; i<xpath_queries.length; i++) {

			stream = new StringReader(xpath_queries[i]);
			xpp.ReInit( stream );

			try {
				xpp.XPath();
			}
			catch (ParseException e) {
				System.out.println("Failed: " + xpath_queries[i] );
				e.printStackTrace();
				continue;
			}
			System.out.println( "OK    : " + xpath_queries[i] );
		}

		System.out.println("\n*****************************");
		System.out.println("*     XSLT pattern tests    *");
		System.out.println("*****************************");

		for (int i=0; i<xslt_queries.length; i++) {

			stream = new StringReader(xslt_queries[i]);
			xpp.ReInit( stream );

			try {
				xpp.Pattern();
			}
			catch (ParseException e) {
				System.out.println("Failed: " + xslt_queries[i] );
				e.printStackTrace();
				continue;
			}
			System.out.println( "OK    : " + xslt_queries[i] );
		}
	}

	static final String[] xpath_queries = new String[] {

		// Unabreviated queries

		"child::para",		// selects the para element children of the context node
		"child::*",			// selects all element children of the context node
		"child::text()",	// selects all text node children of the context node
		"child::node()",	//  selects all the children of the context node, whatever their node type
		"attribute::name",	// selects the name attribute of the context node
		"attribute::*",		// selects all the attributes of the context node
		"descendant::para",	// selects the para element descendants of the context node
		"ancestor::div",	// selects all div ancestors of the context node
		"ancestor-or-self::div",	// selects the div ancestors of the context node and, if the context node is a div element, the context node as well
		"descendant-or-self::para",	// selects the para element descendants of the context node and, if the context node is a para element, the context node as well
		"self::para",		// selects the context node if it is a para element, and otherwise selects nothing
		"child::chapter/descendant::para",	//selects the para element descendants of the chapter element children of the context node
		"child::*/child::para", 			// selects all para grandchildren of the context node
		"/",			 	// selects the document root (which is always the parent of the document element)
		"/descendant::para",	// selects all the para elements in the same document as the context node
		"/descendant::olist/child::item",	// selects all the item elements in the same document as the context node that have an olist parent
		"child::para[position()=1]",	// selects the first para child of the context node
		"child::para[position()=last()]",	//selects the last para child of the context node
		"child::para[position()=last()-1]",	// selects the last but one para child of the context node
		"child::para[position()>1]",	// selects all the para children of the context node other than the first para child of the context node
		"following-sibling::chapter[position()=1]",	// selects the next chapter sibling of the context node
		"preceding-sibling::chapter[position()=1]",	// selects the previous chapter sibling of the context node
		"/descendant::figure[position()=42]",	// selects the forty-second figure element in the document
		"/child::doc/child::chapter[position()=5]/child::section[position()=2]",	// selects the second section of the fifth chapter of the doc document element
		"child::para[attribute::type='warning']",	// selects all para children of the context node that have a type attribute with value warning
		"child::para[attribute::type='warning'][position()=5]",	// selects the fifth para child of the context node that has a type attribute with value warning
		"child::para[position()=5][attribute::type='warning']",	// selects the fifth para child of the context node if that child has a type attribute with value warning
		"child::chapter[child::title='Introduction']",	// selects the chapter children of the context node that have one or more title children with string-value equal to Introduction
		"child::chapter[child::title]",	// selects the chapter children of the context node that have one or more title children
		"child::*[self::chapter or self::appendix]",	// selects the chapter and appendix children of the context node
		"child::*[self::chapter or self::appendix][position()=last()]",	// selects the last chapter or appendix child of the context node

		// Abbreviated queries

		"para",	// selects the para element children of the context node
		"*", //	selects all element children of the context node
		"text()",	// selects all text node children of the context node
		"@name",	// selects the name attribute of the context node
		"@*",		// selects all the attributes of the context node
		"para[1]",	// selects the first para child of the context node
		"para[last()]",	// selects the last para child of the context node
		"*/para",	// selects all para grandchildren of the context node
		"/doc/chapter[5]/section[2]",	// selects the second section of the fifth chapter of the doc
		"chapter//para",	// selects the para element descendants of the chapter element children of the context node
		"//para",	// selects all the para descendants of the document root and thus selects all para elements in the same document as the context node
		"//olist/item",	// selects all the item elements in the same document as the context node that have an olist parent
		".",	// selects the context node
		".//para",	// selects the para element descendants of the context node
		"..",	// selects the parent of the context node
		"../@lang",	// selects the lang attribute of the parent of the context node
		"para[@type='warning']",	// selects all para children of the context node that have a type attribute with value warning
		"para[@type='warning'][5]",	// selects the fifth para child of the context node that has a type attribute with value warning
		"para[5][@type='warning']",	// selects the fifth para child of the context node if that child has a type attribute with value warning
		"chapter[title='Introduction']",	// selects the chapter children of the context node that have one or more title children with string-value equal to Introduction
		"chapter[title]",	//selects the chapter children of the context node that have one or more title children
		"employee[@secretary and @assistant]"	// selects all the employee children of the context node that have both a secretary attribute and an assistant attribute
	};

	static final String[] xslt_queries = new String[] {
		"para",	// matches any para element
		"*",	// matches any element
		"chapter|appendix", // matches any chapter element and any appendix element
		"olist/item",	// matches any item element with an olist parent
		"appendix//para",	// matches any para element with an appendix ancestor element
		"/",	// matches the root node
		"text()",	// matches any text node
		"processing-instruction()",	// matches any processing instruction
		"node()",	// matches any node other than an attribute node and the root node
		"id('W11')",	// matches the element with unique ID W11
		"para[1]",	// matches any para element that is the first para child element of its parent
		"*[position()=1 and self::para]",	// matches any para element that is the first child element of its parent
		"para[last()=1]",	// matches any para element that is the only child element of its parent
		"items/item[position()>1]",	// matches any item element that has a items parent and that is not the first item child of its parent
		"item[position() mod 2 = 1]",	// would be true for any item element that is an odd-numbered item child of its parent.
		"div[@class='appendix']//p",	// matches any p element with a div ancestor element that has a class attribute with value appendix
		"@class",	// matches any class attribute (not any element that has a class attribute)
		"@*"	// matches any attribute
	};
}