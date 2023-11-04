package piat.opendatasearch;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.net.URISyntaxException;

public class ProcessURL implements Runnable {
	private String fichero;
	private String nombreHilo;
	private List<String> conceptsList;
	private List<Resource> resourceList;

	
	public ProcessURL(String fichero, List < String > conceptsList, List <Resource> resourceList)
	  {
	    this.fichero = fichero;
	    this.conceptsList = conceptsList;
	    this.resourceList = resourceList;
	  }
	@Override
	public void run() {
		Thread.currentThread().setName("JSON " + fichero);
		nombreHilo = "[" + Thread.currentThread().getName() + "] ";
		System.out.println(nombreHilo + "Empezar a descargar de internet el JSON");
		JSONParser parser;
		try {
			InputStream inputStream = new URI(fichero.replace("\"", "")).toURL().openStream();
			
			parser = new JSONParser(
					new JSONParserTokenManager(new SimpleCharStream(new StreamProvider(inputStream, "UTF-8"))));

			List<Resource> lista = parser.document(conceptsList,resourceList);
			// datasetsList.put(fichero, resourceList);
			inputStream.close();
		} catch (IOException | ParseException | URISyntaxException e) {
			e.printStackTrace();
		}

	}
}
