package piat.opendatasearch;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author 
 */


public class XPathProcess{
	private static final String pathBase = "/searchResults/results/";
	/**
	 * Método que se encarga de evaluar las expresiones xpath sobre el fichero XML generado en la práctica 4
	 * @return
	 *  - Una lista con la propiedad resultante de evaluar cada expresion xpath
	 * @throws IOException

	 * @throws ParserConfigurationException 
	 */


	public static List<Propiedad> evaluar (String ficheroXML) throws IOException, XPathExpressionException {
		// TODO: Realizar las 4 consultas xpath al documento XML de entrada qeu se indican en el enunciado en el apartado "3.2 Búsqueda de información y generación del documento de resultados."
		// Cada consulta devolverá una información que se añadirá a la una colección List <Propiedad>
		// Una consulta puede devolver una propiedad o varias
		List<Propiedad>jsonElements = new ArrayList<Propiedad>();
		String sPath = null;
		String nDatasets = null;
		String query = null;
		
		
		
		
		try {
			NodeList elements;
			File f = new File(ficheroXML);
			DocumentBuilderFactory  builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			builder = builderFactory.newDocumentBuilder();
			Document xmlDocument = builder.parse(f);
			
			XPath xpath = XPathFactory.newInstance().newXPath();
			InputSource inputSource = new InputSource(ficheroXML);
			inputSource.setEncoding(StandardCharsets.UTF_8.displayName());
			
			/*Obtengo summary del XML. Consultas a y b*/
			sPath ="/searchResults/summary/";
			query = (String)xpath.evaluate(sPath + "query", xmlDocument, XPathConstants.STRING);
			nDatasets = (String)xpath.evaluate("count(//.//datasets/dataset)", xmlDocument, XPathConstants.STRING);
			System.out.println(query);
			System.out.println(nDatasets);
			jsonElements.add(new Propiedad("query", query));
			jsonElements.add(new Propiedad("numDataset", nDatasets));
		
			/* Busco el cotenido title, hijos de resource. Consulta c */
			sPath = pathBase + "resources/resource";
			List<Propiedad>titles = new ArrayList<Propiedad>();
			
			elements = (NodeList) xpath.evaluate(sPath, xmlDocument, XPathConstants.NODESET);
			for(int i = 0; i<elements.getLength(); i++)
			{
				Element element = (Element)elements.item(i); 
				String title = (String)xpath.evaluate("./title", element, XPathConstants.STRING);
				titles.add(new Propiedad("title", title));
				System.out.println(xpath.evaluate("./title", element, XPathConstants.STRING));
			}
			
			
			
			/*Obtengo infomrmacion de los dataset. Consulta d */
			sPath = pathBase + "datasets/dataset";
			List<Propiedad> infDatasets = new ArrayList<Propiedad>();
			elements = (NodeList) xpath.evaluate(sPath, xmlDocument, XPathConstants.NODESET);
			for(int i = 0; i<elements.getLength(); i++)
			{
				//Array de Propiedad para formar Propiedad compuesta.
				Propiedad infDataset [] = new Propiedad[2];
				Element element = (Element)elements.item(i); 
				
				String id = (String) xpath.evaluate("./@id", element, XPathConstants.STRING);
				String num = (String)xpath.evaluate("count(/searchResults/results/resources/resource[@id=(\"" + id + "\")])", xmlDocument, XPathConstants.STRING);
				
				infDataset[0] = new Propiedad("id", id);
				infDataset[1] = new Propiedad("num", num);
				
				infDatasets.add(new Propiedad("complex",infDataset));
				System.out.println(id);
				//Por cada elemento <dataset>, hijo de <datasets>, número de elementos 
				//<resource> cuyo atributo id es igual al atributo id del elemento <dataset>. 
				System.out.println(xpath.evaluate("count(/searchResults/results/resources/resource[@id=(\"" + id + "\")])", xmlDocument, XPathConstants.STRING));	
				//infDatasets.add(new Propiedad("num", num));
				
				
			}
			jsonElements.add(new Propiedad("infDatasets", infDatasets));
			jsonElements.add(new Propiedad("titles", titles));
			
			
			
		}
		catch(Throwable t){
			t.printStackTrace();
			
		}
		
		return jsonElements;

		
	}
	

	
	/**
	 * Esta clase interna define una propiedad equivalente a "nombre":"valor" en JSON
	 */
	public static class Propiedad {
		public final String nombre;
		public final String valor;
		public  List<Propiedad> valores;
		public Propiedad[] valorDimN;
		
		/* Propiedad simple, del tipo nombre:valor P.ej: query*/ 
		public Propiedad (String nombre, String valor){
			this.nombre=nombre;
			this.valor=valor;		
		}
		
		/* Propiedad compuesta de propiedades simples. P.ej: titles*/
		public Propiedad(String nombre, List<Propiedad>valores) {
			this.nombre = nombre;
			this.valor = "[";
			this.valores = valores;
		}
		/* Propiedad compuesta de propiedades compuestas. P.ej: infDatasets */ 
		public Propiedad(String nombre, Propiedad[]valorDimN) {
			this.nombre = nombre;
			this.valor = "[";
			this.valorDimN = valorDimN;
		}
		@Override
		public String toString() {
			if(!valor.equals("["))
				return '"' + this.nombre + '"' +" : "+ '"' + this.valor + '"';
			else 
				return '"' + this.nombre + '"' + ": " + this.valor;

		}
		
		
		

	} //Fin de la clase interna Propiedad

} //Fin de la clase XPathProcess
