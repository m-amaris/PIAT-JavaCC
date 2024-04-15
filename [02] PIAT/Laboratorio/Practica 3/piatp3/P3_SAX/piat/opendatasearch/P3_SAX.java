package piat.opendatasearch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
/**
 * @author Miguel Amarís Martos 54022315F
 *
 */

public class P3_SAX {
	private final static String XML = ".xml";
	/**
	 * Clase principal de la aplicación de extracción de información del 
	 * Portal de Datos Abiertos del Ayuntamiento de Madrid
	 *
	 */

	/**
	 * 
	 * @param args (ARG0) Ruta al documento catalogo.xml. 
					(ARG1) código de la categoría de la que se desea información.
					(ARG2) Ruta al documento XML de salida
	 */
	public static void main(String[] args) {
		
		// Verificar nº de argumentos correcto
		if (args.length!=3){
			String mensaje="ERROR: Argumentos incorrectos.";
			if (args.length>0)
				mensaje+=" He recibido estos argumentos: "+ Arrays.asList(args).toString()+"\n";
			mostrarUso(mensaje);
			System.exit(1);
		}		
		
		// TODO
		/* 
		 * Validar los argumentos recibidos en main()
		 * Instanciar un objeto ManejadorXML pasando como parámetro el código de la categoría recibido en el segundo argumento de main()
		 * Instanciar un objeto SAXParser e invocar a su método parse() pasando como parámetro un descriptor de fichero, cuyo nombre se recibió en el primer argumento de main(), y la instancia del objeto ManejadorXML 
		 * Invocar al método getConcepts() del objeto ManejadorXML para obtener un List<String> con las uris de los elementos <concept> cuyo elemento <code> contiene el código de la categoría buscado
		 * Invocar al método getLabel() del objeto ManejadorXML para obtener el nombre de la categoría buscada
		 * Invocar al método getDatasets() del objeto ManejadorXML para obtener un mapa con los datasets de la categoría buscada 
		 * Crear el fichero de salida con el nombre recibido en el tercer argumento de main()
		 * Volcar al fichero de salida los datos en el formato XML especificado por ResultadosBusquedaP3.xsd
		 */
		
		if(!args[0].endsWith(XML) || !args[2].endsWith(XML)){
			System.err.println("Arguments 0 and 2 must end with extension "+XML);
			System.exit(1);
		}

		final String regex = "\\d{3,4}(-[A-Z0-9]{3,8})?";
		final Pattern pattern = Pattern.compile(regex);
        final Matcher m = pattern.matcher(args[1]);

		if(!m.matches()){
			System.err.println("The code does not match the format specified...");
			System.exit(1);
		}
		
		File inputFile = new File(args[0]);
		if(!inputFile.canRead()) {
			System.err.println("The file does not have read permissions...");
			System.exit(1);
		}
		File outputFile = new File(args[2]);
		if (!outputFile.exists()) {
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			if(!outputFile.canWrite()) {
				System.err.println("The file does not have write permissions...");
				System.exit(1);
			}
		}
		


		ManejadorXML handler;
		try {
			handler = new ManejadorXML(args[1]);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(new File(args[0]), handler);

			
			List<String> uris = handler.getConcepts();
			Map<String, HashMap<String, String>> datasetsCategoria = handler.getDatasets();
			
			GenerarXML.genXML(args[1], uris.size(), datasetsCategoria.size(), uris, datasetsCategoria, outputFile.toPath());
		} catch (SAXException | ParserConfigurationException | IOException e) {
			e.printStackTrace();
		}
		System.out.println("File generated!");
		System.exit(0);
	}
	

	
	/**
	 * Muestra mensaje de los argumentos esperados por la aplicación.
	 * Deberá invocase en la fase de validación ante la detección de algún fallo
	 *
	 * @param mensaje  Mensaje adicional informativo (null si no se desea)
	 */
	private static void mostrarUso(String mensaje){
		Class<? extends Object> thisClass = new Object(){}.getClass();
		
		if (mensaje != null)
			System.err.println(mensaje+"\n");
		System.err.println(
				"Uso: " + thisClass.getEnclosingClass().getCanonicalName() + " <ficheroCatalogo> <códigoCategoría> <ficheroSalida>\n" +
				"donde:\n"+
				"\t ficheroCatalogo:\t path al fichero XML con el catálogo de datos\n" +
				"\t códigoCategoría:\t código de la categoría de la que se desea obtener datos\n" +
				"\t ficheroSalida:\t\t nombre del fichero XML de salida\n"	
				);				
	}		

}
