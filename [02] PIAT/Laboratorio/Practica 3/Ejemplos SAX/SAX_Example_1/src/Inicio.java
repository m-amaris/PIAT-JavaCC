/*******
 * 
 * Ejemplo de uso de SAX.
 * Asignatura: PIAT.
 * Curso: 2020/2021
 * Autores: Javier Malagón, Gregorio RUbio
 * 
 *******/
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.*;
public class Inicio {

	public static void main(String[] args) {
		String nomArchivo = "./src/libreria.xml";
	    //String nomArchivo = "./src/PortadaPeriodico.xml";
			try {

				//// Directorio actual
				File dirActual = new File(".");
				System.out.println("El directorio actual es " + dirActual.getCanonicalPath());

				//// Abro archivo XML tomando una referencia relativa al directorio actual
				System.out.println("Trato de abrir el archivo " + dirActual.getCanonicalPath() + nomArchivo );
				File fich = new File(dirActual.getCanonicalPath() + nomArchivo );
				if ( fich.exists() )
					System.out.println("Se ha abierto el archivo. \n Inicio del análisis" );
				else {
					System.out.println("No se ha abierto el archivo " + fich.getAbsolutePath());
					return;
				}

				//// Creo y configuro una factoria
				SAXParserFactory factoria = SAXParserFactory.newInstance();
				factoria.setNamespaceAware(true);	// Soporta espacio de nombres XML
				//factoria.setValidating(true);		// Valida documento

				//// Análisis
				SAXParser parser= factoria.newSAXParser();
				Manejador man = new Manejador();

	            //// El lector valida de acuerdo al esquema
	            XMLReader xmlReader = parser.getXMLReader();
	            //xmlReader.setFeature("http://apache.org/xml/features/validation/schema",true);

				parser.parse (fich, man);		// Parser del fichero 'fich' con el manejador 'man'
				System.out.println("Fin del análisis.");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (SAXException e) {
				e.printStackTrace();
			}
			catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

	}

}