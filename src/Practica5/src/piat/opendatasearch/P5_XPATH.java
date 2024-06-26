package piat.opendatasearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The main class for extracting information from  "Portal de Datos Abiertos
 * del Ayuntamiento de Madrid".
 *
 * This class contains the main method, which handles the runtime of 
 * the application.
 *
 * @author Miguel Amarís Martos
 */

public class P5_XPATH {

	/**
	 * Main entry point for the application.
	 * 
	 * @param args Command-line arguments containing input and output file paths.
	 * @throws ParseException if there is a parsing error.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the application is interrupted.
	 */
	public static void main(String[] args) throws ParseException, IOException, InterruptedException {

		validarArgumentos(args);

		final XMLParser parser = new XMLParser(new XMLParserTokenManager(
				new SimpleCharStream(new StreamProvider(new FileInputStream(args[0]), "UTF-8"))));
		final ManejadorXML manXML = parser.processFile(args[1]);

		// Added code of P4
		Map<String, List<Resource>> resourceList = getResources(getIdList(manXML.getConcepts()), manXML.getDatasets());
		final GenerarXML gen = new GenerarXML(manXML.getConcepts(), args[1], manXML.getDatasets(), resourceList);
		try (final FileWriter ficheroSalida = new FileWriter(args[2]);) {
			ficheroSalida.write(gen.generarXML());	
		}

		System.out.println("Fichero generado...");
		
		// Added code of P5
		final XMLParser2 parser2 = new XMLParser2(new XMLParser2TokenManager(new SimpleCharStream(new StreamProvider(new FileInputStream(args[2]), "UTF-8"))));
		
		final ManejadorJSON manJSON = parser2.processFile();

		JSONWriter writer = new JSONWriter(
				manJSON.getQuery(), manJSON.getNumDatasets(), manJSON.getNumDatasetsMap(), manJSON.getTitleList());
		writer.writeJSONToFile("output.json");
		
		
		System.exit(0);
	}

	
	
	/**
	 * Displays usage information and an optional additional message in case of
	 * invalid arguments. This method must be invoked in the validation 
	 * phase of the main method.
	 *
	 * @param mensaje An optional additional informative message 
	 * (null if not needed).
	 */
	private static void mostrarUso(StringBuilder mensaje) {
		Class<? extends Object> thisClass = P5_XPATH.class;

		if (mensaje != null) {
			System.err.println(mensaje + "\n");
		}
		System.err.println("Uso: " + thisClass.getEnclosingClass().getCanonicalName()
				+ " <ficheroCatalogo> <códigoCategoría> <ficheroSalida>\n" + "donde:\n"
				+ "\t ficheroCatalogo:\t path al fichero XML con el catálogo de datos\n"
				+ "\t códigoCategoría:\t código de la categoría de la que se desea obtener datos\n"
				+ "\t ficheroSalida:\t\t nombre del fichero XML de salida\n");

	}

	/**
	 * Validates the command-line arguments and checks the existence and readability
	 * of input files.
	 *
	 * @param args Command-line arguments to be validated.
	 * @throws IOException if there is an I/O error.
	 */
	private static void validarArgumentos(String[] args) throws IOException {

		if (args.length != 3) {
			StringBuilder mensaje = new StringBuilder("ERROR: Argumentos incorrectos.");
			if (args.length > 0)
				mensaje.append(" He recibido estos argumentos: " + Arrays.asList(args).toString() + "\n");
			mostrarUso(mensaje);
			System.exit(1);
		}

		if (args[0].endsWith(".xml")) {
			final File ficheroCatalogo = new File(args[0]);
			if (!ficheroCatalogo.canRead()) {
				mostrarUso(new StringBuilder(args[0] + "' no tiene permiso de lectura."));
				System.exit(-1);
			}
		} else {
			mostrarUso(new StringBuilder(args[0] + "' no termina en \".xml\""));
			System.exit(-1);
		}

		if (!args[2].endsWith(".xml")) {
			mostrarUso(new StringBuilder(args[2] + "' no termina en \".xml\""));
			System.exit(-1);
		}

		Pattern p = Pattern.compile("^\\d{3,4}(-[A-Z0-9]{3,8})?");
		Matcher m = p.matcher(args[1]);

		if (!m.matches()) {
			mostrarUso(new StringBuilder("ERROR: El codigo de la categoria no es valido."));
			System.exit(-1);
		}

		final File outputFile = new File(args[2]);
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		} else {
			if (!outputFile.canWrite()) {
				System.err.println("The file does not have write permissions...");
				System.exit(1);
			}
		}

	}

	/**
	 * Retrieves a list of resources for each concept in the dataset.
	 *
	 * @param lConcepts   List of concepts.
	 * @param mDatasets   List of datasets.
	 * @return A mapping of dataset IDs to lists of resources.
	 * @throws InterruptedException if the application is interrupted.
	 */
	private static Map<String, List<Resource>> getResources(List<String> lConcepts, List<Dataset> mDatasets)
			throws InterruptedException {
		Map<String, List<Resource>> mDatasetConcepts = new HashMap<>();
		int numDeNucleos = Runtime.getRuntime().availableProcessors();
		ExecutorService ejecutor = Executors.newFixedThreadPool(numDeNucleos);

		for (Dataset d : mDatasets) {
			final List<Resource> resourceList = new ArrayList<>();
			mDatasetConcepts.put(d.getId(), resourceList);
			ejecutor.execute(new ProcessURL(d.getId(), lConcepts, resourceList));
		}
		// wait for threads to end
		ejecutor.shutdown(); // close executor when last thread ends

		while (!ejecutor.awaitTermination(10, TimeUnit.SECONDS)) {
			System.out.print("\nEsperar a que termine ");

		}

		return mDatasetConcepts;
	}

	/**
	 * Retrieves a list of concept IDs recursively from a list of concepts.
	 *
	 * @param concepts List of concepts to extract IDs from.
	 * @return A list of concept IDs.
	 */
	private static List<String> getIdList(List<Concept> concepts) {
		List<String> idList = new ArrayList<>();
		for (Concept concept : concepts) {
			idList.add(concept.getId());
			idList.addAll(getIdList(concept.getConcepts()));
		}
		return idList;
	}

}
