package piat.opendatasearch;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.net.URISyntaxException;

/**
 * Represents a process for downloading and analyzing JSON data from a URL.
 * 
 * This class implements the Runnable interface and is used to perform the
 * download of JSON data from a URL and its subsequent analysis.
 * 
 * @author Miguel Amarís Martos
 */
public class ProcessURL implements Runnable {
	private String fichero;
	private List<String> conceptsList;
	private List<Resource> resourceList;

	/**
     * Constructs an instance of the ProcessURL class.
     * 
     * @param fichero      The URL of the JSON file to download and analyze.
     * @param conceptsList The list of concepts used in JSON analysis.
     * @param resourceList The list of resources where analysis results are stored.
     */
	public ProcessURL(String fichero, List<String> conceptsList, List<Resource> resourceList) {
		this.fichero = fichero;
		this.conceptsList = conceptsList;
		this.resourceList = resourceList;
	}

	/**
     * Represents the main process of downloading and analyzing JSON data.
     * 
     * This method is executed when a new thread is started and performs the
     * following tasks: 1. Assigns a name to the current thread. 2. Initiates the
     * download of JSON data from the specified URL. 3. Analyzes the JSON data using
     * a custom JSON parser. 4. Closes the input data stream. 5. Handles
     * input/output and JSON parsing exceptions.
     */
	@Override
	public void run() {
		final String nombreHilo;
		Thread.currentThread().setName("JSON " + fichero);
		nombreHilo = "[" + Thread.currentThread().getName() + "] ";
		System.out.println(nombreHilo + "Empezar a descargar de internet el JSON");
		JSONParser parser;
		try {
			InputStream inputStream = new URI(fichero.replace("\"", "")).toURL().openStream();

			parser = new JSONParser(
					new JSONParserTokenManager(new SimpleCharStream(new StreamProvider(inputStream, "UTF-8"))));

			parser.processFile(conceptsList, resourceList);

			inputStream.close();
		} catch (IOException | ParseException | URISyntaxException e) {
			e.printStackTrace();
		}

	}
}
