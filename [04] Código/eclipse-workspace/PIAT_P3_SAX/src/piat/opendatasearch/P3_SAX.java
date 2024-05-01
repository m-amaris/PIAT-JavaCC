package piat.opendatasearch;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

/**
 * @author 
 *
 */

public class P3_SAX {

	private static File ficheroCatalogo;
	private static FileWriter ficheroSalida;
	private static List<String> concepts;
	private static String nombreCategoria;
	private static Map<String, HashMap<String, String>> datasets;

	/**
	 * Clase principal de la aplicación de extracción de información del Portal de
	 * Datos Abiertos del Ayuntamiento de Madrid
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 *
	 */

	public static void main(String[] args) throws SAXException, ParserConfigurationException, IOException {

		// Verificar nº de argumentos correcto
		if (args.length != 3) {
			String mensaje = "ERROR: Argumentos incorrectos.";
			if (args.length > 0)
				mensaje += " He recibido estos argumentos: " + Arrays.asList(args).toString() + "\n";
			mostrarUso(mensaje);
			System.exit(1);
		}

		// TODO
		// Validar los argumentos recibidos en main()
		validarArgumentos(args);
		// Instanciar un objeto ManejadorXML pasando como parámetro el código de la
		// categoría recibido en el segundo argumento de main()
		ManejadorXML man = new ManejadorXML(args[1]);
		// Instanciar un objeto SAXParser e invocar a su método parse() pasando como
		// parámetro un descriptor de fichero, cuyo nombre se recibió en el primer
		// argumento de main(), y la instancia del objeto ManejadorXML
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser parsero = factory.newSAXParser();
		parsero.parse(ficheroCatalogo, man);
		// Invocar al método getConcepts() del objeto ManejadorXML para obtener un
		// List<String> con las uris de los elementos <concept> cuyo elemento <code>
		// contiene el código de la categoría buscado
		concepts = man.getConcepts();
		// Invocar al método getLabel() del objeto ManejadorXML para obtener el nombre
		// de la categoría buscada
		nombreCategoria = man.getLabel();
		// Invocar al método getDatasets() del objeto ManejadorXML para obtener un mapa
		// con los datasets de la categoría buscada
		datasets = man.getDatasets();
		// Crear el fichero de salida con el nombre recibido en el tercer argumento de
		// main()
		// Volcar al fichero de salida los datos en el formato XML especificado por
		// ResultadosBusquedaP3.xsd
		GenerarXML gen = new GenerarXML(concepts, nombreCategoria, args[1], datasets);
		ficheroSalida = new FileWriter(args[2]);
		ficheroSalida.write(gen.generarXML());
		ficheroSalida.close();
		System.out.println("Fichero generado.");
		
		System.exit(0);
	}

	/**
	 * Muestra mensaje de los argumentos esperados por la aplicación. Deberá
	 * invocase en la fase de validación ante la detección de algún fallo
	 *
	 * @param mensaje Mensaje adicional informativo (null si no se desea)
	 */
	private static void mostrarUso(String mensaje) {
		Class<? extends Object> thisClass = new Object() {
		}.getClass();

		if (mensaje != null)
			System.err.println(mensaje + "\n");
		System.err.println("Uso: " + thisClass.getEnclosingClass().getCanonicalName()
				+ " <ficheroCatalogo> <códigoCategoría> <ficheroSalida>\n" + "donde:\n"
				+ "\t ficheroCatalogo:\t path al fichero XML con el catálogo de datos\n"
				+ "\t códigoCategoría:\t código de la categoría de la que se desea obtener datos\n"
				+ "\t ficheroSalida:\t\t nombre del fichero XML de salida\n");
	}

	/**
	 * Verifica que se ha pasado un argumento con el nombre del directorio y que
	 * este existe y se puede leer. En caso contrario aborta la aplicaciÃ³n.
	 *
	 * @param args Argumentos a analizar
	 */
	private static void validarArgumentos(String[] args) {

		if (args[0].endsWith(".xml")) {
			ficheroCatalogo = new File(args[0]);
			if (!ficheroCatalogo.canRead()) {
				mostrarUso("ERROR: El archivo '" + args[0] + "' no tiene permiso de lectura.");
				System.exit(-1);
			}
		} else {
			mostrarUso("ERROR: El archivo '" + args[0] + "' no termina en \".xml\"");
			System.exit(-1);
		}

		if (!args[2].endsWith(".xml")) {
			mostrarUso("ERROR: El archivo '" + args[2] + "' no termina en \".xml\"");
			System.exit(-1);
		}

		Pattern p = Pattern.compile("^[0-9]{3,4}(-[A-Z0-9]{3,8})?");
		Matcher m = p.matcher(args[1]);

		if (!m.matches()) {
			mostrarUso("ERROR: El codigo de la categoria no es valido.");
			System.exit(-1);
		}

	}

}
