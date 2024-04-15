package piat.opendatasearch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author 
 *
 */

public class XPathProcess {

	/**
	 * Método que se encarga de evaluar las expresiones xpath sobre el fichero XML
	 * generado en la práctica 4
	 * 
	 * @return - Una lista con la propiedad resultante de evaluar cada expresion
	 *         xpath
	 * @throws IOException
	 * 
	 * @throws ParserConfigurationException
	 */

	public static List<Propiedad> evaluar(String ficheroXML) throws IOException, XPathExpressionException {
		// TODO: Realizar las 4 consultas xpath al documento XML de entrada qeu se
		// indican en el enunciado en el apartado "3.2 Búsqueda de información y
		// generación del documento de resultados."
		// Cada consulta devolverá una información que se añadirá a la una colección
		// List <Propiedad>
		// Una consulta puede devolver una propiedad o varias

		List<Propiedad> lista = new ArrayList<Propiedad>();
		try {
			InputSource inputSource = new InputSource(ficheroXML);

			DocumentBuilderFactory factory = DocumentBuilderFactory .newInstance();	// Creación de la factoria
			factory.setNamespaceAware(true);										// Configuración de la factoria para que trabaje con espacios de nombres
			DocumentBuilder builder = factory.newDocumentBuilder();						 	
			Document inputDoc = builder.parse (inputSource);						// Creación de un árbol DOM con la información del XML
			
			XPath xpath = XPathFactory.newInstance().newXPath();

			
			String query = (String) xpath.evaluate("//summary/query/text()", inputDoc, XPathConstants.STRING);

			System.out.println(query);
			lista.add(new Propiedad("query", query));
			lista.add(new Propiedad("numero",
					(String) xpath.evaluate("count(//results/datasets/dataset)", inputDoc, XPathConstants.STRING)));
			lista.add(new Propiedad("titulo", (String) xpath.evaluate("//descendant::resource/title/text()",
					inputSource, XPathConstants.STRING)));
			String at = (String) xpath.evaluate("//descendant::dataset/@id", inputDoc, XPathConstants.STRING);
			lista.add(new Propiedad(at,
					(String) xpath.evaluate("count(//descendant::resource[@id=//descendant::dataset/@id])", inputDoc,
							XPathConstants.STRING)));

		} catch (Throwable t) {
			t.printStackTrace();
		}

		System.out.println(lista.toString());

		return lista;
	}

	/**
	 * Esta clase interna define una propiedad equivalente a "nombre":"valor" en
	 * JSON
	 */
	public static class Propiedad {
		public final String nombre;
		public final String valor;

		public Propiedad(String nombre, String valor) {
			this.nombre = nombre;
			this.valor = valor;
		}

		@Override
		public String toString() {
			return "\"" + this.nombre + "\": \"" + this.valor + "\"";

		}

	} // Fin de la clase interna Propiedad

} // Fin de la clase XPathProcess
