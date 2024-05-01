/*
	Commandline driver for XPath parser.
	Author: Ingo Macherius <macherius@gmd.de>
	(c) 1999 GMD
	No warranties, use at your own risk.
*/

import java.io.*;

public class Driver {

	final static byte XSLT  = 1;
	final static byte XPATH = 2;

	final static byte USE = XPATH;	// Specify what to test for, XPath or XSLT
	final static boolean DEBUG = true;	// Specify for verbose output

	public static void main( String[] args ) {

		if (args[0] == null) {
			if (USE == XSLT)
				System.err.println( "Usage: java Driver XSLT_pattern" );
			else
				System.err.println( "Usage: java Driver XPATH_pattern" );
			return;
		}

		Reader stream = new StringReader(args[0]);
		XPathParser xpp = new XPathParser(stream);

		if (DEBUG)
			xpp.enable_tracing();
		else
			xpp.disable_tracing();
		try {
			if (USE == XSLT)
				xpp.Pattern();
			else
				xpp.XPath();
		}
		catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Failed parse.");
			return;
		}
		System.out.println("Valid parse.");
	}
}