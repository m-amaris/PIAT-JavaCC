package piat.opendatasearch;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import piat.opendatasearch.XPathProcess.Propiedad;

/**
 * @author 
 *
 */

public class P5_XPATH {

	private static File ficheroCatalogo;
	private static FileWriter ficheroSalida;
	private static List<String> concepts;
	private static String nombreCategoria;
	private static Map<String, HashMap<String, String>> datasets;
	private static Map<String, List<Map<String, String>>> mDatasetConcepts;

	/**
	 * Clase principal de la aplicación de extracción de información del Portal de
	 * Datos Abiertos del Ayuntamiento de Madrid
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws XPathExpressionException 
	 *
	 */

	public static void main(String[] args)
			throws SAXException, ParserConfigurationException, IOException, InterruptedException, XPathExpressionException {

		// Verificar nº de argumentos correcto
		if (args.length != 4) {
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
		// NUEVO PRACTICA 4:Invocar al método getDatasetConcepts del objeto
		// JSONDatasetParser para obtener un mapa con los concepts de los datasets
		/**
		 * getDatasetConcepts es un metodo de JSONDataParser que hace un return del mapa
		 * pero al ser un ConcurrentHashMap no hace falta ya que se actualiza
		 * automaticamente
		 */
		mDatasetConcepts = getDatasetConcepts(concepts,datasets);

		System.out.println(mDatasetConcepts);
		
		// Crear el fichero de salida con el nombre recibido en el tercer argumento de
		// main()
		// Volcar al fichero de salida los datos en el formato XML especificado por
		// ResultadosBusquedaP3.xsd
		GenerarXML gen = new GenerarXML(concepts, nombreCategoria, args[1], datasets, mDatasetConcepts);
		ficheroSalida = new FileWriter(args[2]);
		ficheroSalida.write(gen.generarP4());
		ficheroSalida.close();
		System.out.println("Fichero generado.");

		List<Propiedad> lista=XPathProcess.evaluar(args[2]);
		
//		StringBuilder sbSalida  = new StringBuilder();
//		sbSalida.append("{");
//		sbSalida.append("\n\t"+lista.get(0).toString());
//		sbSalida.append("\n\t"+lista.get(1).toString());
//		sbSalida.append("\n\t\t\"infDatasets\": [");
//		sbSalida.append("\n\t\t\t{");
//		sbSalida.append("\n\t\t\t"+lista.get(0));
//		sbSalida.append("{");
//		sbSalida.append("{");
//		sbSalida.append("{");

		
		
		
		
		
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
				+ "\t ficheroSalida:\t\t nombre del fichero XML de salida\n"
				+ "\t ficheroJSON:\t\t nombre del fichero JSON de salida\n");
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
		
		if (!args[3].endsWith(".json")) {
			mostrarUso("ERROR: El archivo '" + args[2] + "' no termina en \".json\"");
			System.exit(-1);
		}

	}

	private static Map<String, List<Map<String, String>>> getDatasetConcepts(List<String> lConcepts,
			Map<String, HashMap<String, String>> mDatasets) throws InterruptedException {
		ConcurrentHashMap<String, List<Map<String, String>>> mDatasetConcepts = new ConcurrentHashMap<String, List<Map<String, String>>>();

		final int numDeNucleos = Runtime.getRuntime().availableProcessors();
//		final int numDeNucleos = 1;

		System.out.print("\nLanzando hilos en los " + numDeNucleos + " nucleos disponibles de este ordenador ");

		// Crear un pool donde ejecutar los hilos. El pool tendrÃ¡ un tamaÃ±o del nÂº de
		// nÃºcleos del ordenador
		// por lo que nunca podrÃ¡ haber mÃ¡s hilos que ese nÃºmero en ejecuciÃ³n
		// simultÃ¡nea.
		// Si se quiere hacer pruebas con un solo trabajador en ejecuciÃ³n, poner como
		// argumento un 1. IrÃ¡ mucho mÃ¡s lenta
		final ExecutorService ejecutor = Executors.newFixedThreadPool(numDeNucleos);

		final AtomicInteger numTrabajadoresTerminados = new AtomicInteger(0);
		int numTrabajadores = 0;
		for (Map.Entry<String, HashMap<String, String>> unDataset : datasets.entrySet()) {
			System.out.print(".");
			ejecutor.execute(new JSONDatasetParser(unDataset.getKey(), concepts, mDatasetConcepts));
			numTrabajadores++;
			// break; // Descomentando este break, solo se ejecuta el primer trabajador
		}
		System.out
				.print("\nSe van a ejecutar " + numTrabajadores + " trabajadores en el pool. Esperar a que terminen ");

		// Esperar a que terminen todos los trabajadores
		ejecutor.shutdown(); // Cerrar el ejecutor cuando termine el Ãºltimo trabajador
		// Cada 10 segundos mostrar cuantos trabajadores se han ejecutado y los que
		// quedan
		while (!ejecutor.awaitTermination(10, TimeUnit.SECONDS)) {
			final int terminados = numTrabajadoresTerminados.get();
			System.out.print("\nYa han terminado " + terminados + ". Esperando a los " + (numTrabajadores - terminados)
					+ " que quedan ");
		}
		// Mostrar todos los trabajadores que se han ejecutado. Debe coincidir con los
		// creados
		System.out.println("\nYa han terminado los " + numTrabajadoresTerminados.get() + " trabajadores");

		
	return mDatasetConcepts;
	}

}
