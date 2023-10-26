package piat.opendatasearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * @author
 *
 */

public class P4_JSON {

	/**
	 * Clase principal de la aplicación de extracción de información del Portal de
	 * Datos Abiertos del Ayuntamiento de Madrid
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 *
	 */

	public static void main(String[] args) {
		validarArgumentos(args);
		try {
			
			File outputFile = new File(args[2]);
			if (!outputFile.exists()) {
				try {
					outputFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				if (!outputFile.canWrite()) {
					System.err.println("The file does not have write permissions...");
					System.exit(1);
				}
			}
			
			final XMLParser parser = new XMLParser(new XMLParserTokenManager(
					new SimpleCharStream(new StreamProvider(new FileInputStream(args[0]), "UTF-8"))));
			final ManejadorXML man = parser.processFile(args[1]); // Pending of changing name
			final List<Concept> lConcepts = man.getConcepts();
			final List<Dataset> mDatasets = man.getDatasets();

			List<String> conceptIds = getIdList(lConcepts);
			
			// Added code of P4
			Map<String, List<Resource>> resourceList = getResources(conceptIds,mDatasets);
			
			final GenerarXML gen = new GenerarXML(lConcepts, man.getLabel(), args[1], mDatasets,resourceList);
			
			try (final FileWriter ficheroSalida = new FileWriter(args[2]);) {
				ficheroSalida.write(gen.generarXML());
			}
			System.out.println("Fichero generado...");
			System.exit(0);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Muestra mensaje de los argumentos esperados por la aplicación. Deberá
	 * invocase en la fase de validación ante la detección de algún fallo
	 *
	 * @param mensaje Mensaje adicional informativo (null si no se desea)
	 */
	private static void mostrarUso(StringBuilder mensaje) {
		Class<? extends Object> thisClass = P4_JSON.class;

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
				mostrarUso(new StringBuilder("ERROR: El archivo '" + args[0] + "' no tiene permiso de lectura."));
				System.exit(-1);
			}
		} else {
			mostrarUso(new StringBuilder("ERROR: El archivo '" + args[0] + "' no termina en \".xml\""));
			System.exit(-1);
		}

		if (!args[2].endsWith(".xml")) {
			mostrarUso(new StringBuilder("ERROR: El archivo '" + args[2] + "' no termina en \".xml\""));
			System.exit(-1);
		}

		Pattern p = Pattern.compile("^[0-9]{3,4}(-[A-Z0-9]{3,8})?");
		Matcher m = p.matcher(args[1]);

		if (!m.matches()) {
			mostrarUso(new StringBuilder("ERROR: El codigo de la categoria no es valido."));
			System.exit(-1);
		}

		
		
		
	}

	private static Map<String, List <Resource>> getResources(List<String> lConcepts,
			List<Dataset> mDatasets) {
		Map<String, List <Resource>> mDatasetConcepts = new ConcurrentHashMap<>();
		int numDeNucleos = Runtime.getRuntime().availableProcessors();
		ExecutorService ejecutor = Executors.newFixedThreadPool(numDeNucleos);

		for(Dataset d : mDatasets) {
			ejecutor.execute(new JSONParser(d.getId(), lConcepts, mDatasetConcepts));
		}
		// wait for threads to end
		ejecutor.shutdown(); // close executor when last thread ends
		try {
			while (!ejecutor.awaitTermination(10, TimeUnit.SECONDS)) {
				System.out.print("\nEsperar a que termine ");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return mDatasetConcepts;
	}
	
	private static List<String> getIdList(List<Concept> concepts) {
	    List<String> idList = new ArrayList<>();
	    for (Concept concept : concepts) {
	        idList.add(concept.getId());
	        idList.addAll(getIdList(concept.getConcepts()));
	    }
	    return idList;
	}
	

}
