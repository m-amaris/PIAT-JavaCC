
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class Manejador extends DefaultHandler {

    private boolean esTitulo = false;
    private boolean esAutor = false;

    public void setDocumentLocator(Locator loc) {
    }
    public void startDocument() {
    }
    public void endDocument() {
    }
    public void processingInstruction(String destino, String datos) {
    }
    public void startPrefixMapping(String prefijo, String uri) {
    }
    public void endPrefixMapping(String prefijo) {
    }
    public void startElement(String espacioNombres, String nomLocal, String nomCompleto, Attributes atrs) {
		System.out.println("Espacio de nombres:" + espacioNombres);
                System.out.println("Nombre local:" + nomLocal);
                System.out.println( " Nombre completo:" + nomCompleto);
		for ( int i = 0; i < atrs.getLength(); i++) {
			System.out.println("    ATRIB. Nombre local: " + atrs.getLocalName(i) );
			System.out.println("    ATRIB. Tipo:         " + atrs.getType(i) );
			System.out.println("    ATRIB. Valor:        " + atrs.getValue(i) );
		}
		if (nomLocal.equalsIgnoreCase("titulo"))
			esTitulo = true;
		if (nomLocal.equalsIgnoreCase("autor"))
			esAutor = true;
    }
    public void endElement(String espacio, String nomLocal, String nomCompleto) {
    }
    public void characters(char[] ch, int inicio, int longitud) {
    	if ( esTitulo || esAutor ) {
    		String cad = new String( ch, inicio, longitud);
    		System.out.println("    Caracteres:" + cad );
    		System.out.println("         Inicio:" + inicio + " Longitud:" + longitud );
    		esTitulo = false;
    		esAutor = false;
    	}
    }
    public void ignorableWhitespace(char[] ch, int comienzo, int fin) {
    }
    public void skippedEntity(String nombre) {
    }
    public void error(SAXParseException exc) throws SAXException {
		mostrarError( exc, "Se encontró un error");
    }
    public void fatalError(SAXParseException exc) throws SAXException {
		mostrarError( exc, "Se encontró un error fatal");
    }
    public void mostrarError( SAXParseException exc, String aviso )  throws SAXException {
		System.out.println( aviso + ".  Línea:    " + exc.getLineNumber() );
		System.out.println( "URI:     " + exc.getSystemId() );
		System.out.println( "Mensaje: " + exc.getMessage() );
		throw new SAXException(aviso);//
    }
}