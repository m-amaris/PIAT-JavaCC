package piat.opendatasearch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.stream.JsonReader;

/* En esta clase se comportará como un hilo */

public class JSONDatasetParser2 implements Runnable {
	private String fichero;
	private List<String> lConcepts;
	private Map<String, List<Map<String, String>>> mDatasetConcepts;
	private String nombreHilo;

	public JSONDatasetParser2(String fichero, List<String> lConcepts,
			Map<String, List<Map<String, String>>> mDatasetConcepts) {
		this.fichero = fichero;
		this.lConcepts = lConcepts;
		this.mDatasetConcepts = mDatasetConcepts;
	}

	@Override
	public void run() {
		List<Map<String, String>> graphs = new ArrayList<Map<String, String>>(); // Aquí se almacenarán todos los graphs
																					// de un dataset cuyo objeto de
																					// nombre @type se corresponda con
																					// uno de los valores pasados en el
																					// la lista lConcepts
		boolean finProcesar = false; // Para detener el parser si se han agregado a la lista graphs 5 graph
		String clave;

		try {
			Thread.sleep(10); // Retraso para que al programa principal le de tiempo a lanzar todos los hilos
		} catch (InterruptedException e) {
		}

		Thread.currentThread().setName("JSON " + fichero);
		nombreHilo = "[" + Thread.currentThread().getName() + "] ";
		System.out.println(nombreHilo + "Empezar a descargar de internet el JSON");
		try {
			// FileReader inputStream=new FileReader (new File (fichero)); // Si se quisiera
			// leer de un fichero
			// Leer desde intenet
			InputStreamReader inputStream = new InputStreamReader(new URL(fichero).openStream(), "UTF-8"); // siendo
																											// fichero
																											// la URL
																											// donde se
																											// encuentra
																											// el .json
																											// a parsear

			// TODO:
			// Creacion del objeto de la clase JsonReader
			JsonReader reader = new JsonReader(inputStream);
			// Configurar el parser como "lenient" para poder saltarse valores con
			// skipValue()
			reader.setLenient(true);
			// Código del parser
			if (reader.nextName().equals("@graph")) {
				if (procesar_graph(reader, graphs, lConcepts))
					finProcesar = true;
			} else {
				reader.skipValue();
			}

			// Cierre del objeto de la clase JsonReader
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println(nombreHilo + "El fichero no existe. Ignorándolo");
		} catch (IOException e) {
			System.out.println(nombreHilo + "Hubo un problema al abrir el fichero. Ignorándolo" + e);
		}
		mDatasetConcepts.put(fichero, graphs); // Se añaden al Mapa de concepts de los Datasets

	}

	/*
	 * procesar_graph() Procesa el array @graph Devuelve true si ya se han añadido 5
	 * objetos a la lista graphs
	 */
	private boolean procesar_graph(JsonReader jsonReader, List<Map<String, String>> graphs, List<String> lConcepts)
			throws IOException {

		jsonReader.beginArray();
		while (graphs.size() < 5) {
			procesar_un_graph(jsonReader, graphs, lConcepts);
		}
		jsonReader.endArray();

		if (graphs.size() == 5)
			return true;
		else
			return false;
	}

	/*
	 * procesar_un_graph() Procesa un objeto del array @graph y lo añade a la lista
	 * graphs si en el objeto de nombre @type hay un valor que se corresponde con
	 * uno de la lista lConcepts
	 */

	private void procesar_un_graph(JsonReader jsonReader, List<Map<String, String>> graphs, List<String> lConcepts)
			throws IOException {

		Map<String, String> map = new HashMap<String, String>();

		jsonReader.beginObject();

		while (jsonReader.hasNext()) {
			String type = null;
			String link = null;
			String title = null;
			String eventLoc = null;
			String area = null;
			String start = null;
			String end = null;
			String latitude = null;
			String longitude = null;
			String accesibility = null;
			String orgName = null;
			String desc = null;
			String name = jsonReader.nextName();

			switch (name) {
			case "@type":
				type = jsonReader.nextString();
				break;
			case "link":
				link = jsonReader.nextString();
				break;
			case "title":
				title = jsonReader.nextString();
				break;
			case "event-location":
				eventLoc = jsonReader.nextString();
				break;
			case "area":
				area = jsonReader.nextString();
				break;
			case "dtstart":
				start = jsonReader.nextString();
				break;
			case "dtend":
				end = jsonReader.nextString();
				break;
			case "latitude":
				latitude = jsonReader.nextString();
				break;
			case "longitude":
				longitude = jsonReader.nextString();
				break;
			case "accesibility":
				accesibility = jsonReader.nextString();
				break;
			case "organization-name":
				orgName = jsonReader.nextString();
				break;
			case "description":
				desc = jsonReader.nextString();
				break;
			default:
				jsonReader.skipValue();
				break;
			}
			if (lConcepts.contains(type)) {
				map.put("@type", type);
				map.put("link", link);
				map.put("title", title);
				map.put("eventLocation", eventLoc);
				map.put("area", area);
				map.put("start", start);
				map.put("end", end);
				map.put("latitude", latitude);
				map.put("longitude", longitude);
				map.put("accesibility", accesibility);
				map.put("organizationName", orgName);
				map.put("descripton", desc);
				graphs.add(map);
			}

		}
		jsonReader.endObject();

	}

	public Map<String, List<Map<String, String>>> getDatasetsConcepts() {
		return mDatasetConcepts;
	}

}