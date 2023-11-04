package piat.opendatasearch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.stream.JsonReader;

/**
 * @author Sergio Delgado Cantalapiedra 53748114N
 *
 */

/* En esta clase se comportarÃ¡ como un hilo */

public class JSONGraphParser implements Runnable {
	private String fichero;
	private Map<String, Map<String, String>> mGraphValues;
	private String nombreHilo;

	public JSONGraphParser(String fichero, Map<String, Map<String, String>> mGraphValues) {
		this.fichero = fichero;
		this.mGraphValues = mGraphValues;
	}

	@Override
	public void run() {
		Thread.currentThread().setName("JSON " + fichero);
		nombreHilo = "[" + Thread.currentThread().getName() + "] ";

		// TODO: Procesar el fichero para extraer las propiedades pertinentes y
		// guardarlas en mGraphValues

		System.out.println(nombreHilo + "Empezar a descargar de internet el JSON");

		Map<String, String> map = new HashMap<String, String>();
		String tipoPostalCode = "";
		String postalCode = "";

		try {
			InputStreamReader inputStream = new InputStreamReader(new URL(fichero).openStream(), "UTF-8");
			// TODO:
			// - Crear objeto JsonReader a partir de inputStream
			// - Consumir el primer "{" del fichero
			// - Procesar los elementos del fichero JSON, hasta el final de fichero o hasta
			// que finProcesar=true
			// Si se encuentra el objeto @graph, invocar a procesar_graph()
			// Descartar el resto de objetos
			// - Si se ha llegado al fin del fichero, consumir el Ãºltimo "}" del fichero
			// - Cerrar el objeto JsonReader
			JsonReader reader = new JsonReader(inputStream);

			reader.setLenient(true);

			reader.beginObject();
			while (reader.hasNext()) {
				if (reader.nextName().equals("@context")) {
					tipoPostalCode = procesar_context(reader);
				}
				if (reader.nextName().equals("@graph")) {
					postalCode = procesar_graph(reader);
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
			inputStream.close();

		} catch (FileNotFoundException e) {
			System.out.println(nombreHilo + "El fichero no existe. IgnorÃ¡ndolo");
		} catch (IOException e) {
			System.out.println(nombreHilo + "Hubo un problema al abrir el fichero. IgnorÃ¡ndolo" + e);
		}

		map.put("tipoPostalCode", tipoPostalCode);
		map.put("PostalCode", postalCode);

		mGraphValues.put(fichero, map); // Se aÃ±aden al Mapa de concepts de los Datasets

	}

	private String procesar_context(JsonReader jsonReader) throws IOException {

		String tipoPostalCode = "";

		jsonReader.beginObject();
		while (jsonReader.hasNext()) {
			if (jsonReader.nextName().equals("postal-code")) {
				tipoPostalCode = jsonReader.nextString();
			} else {
				jsonReader.skipValue();
			}
		}
		jsonReader.endObject();

		return tipoPostalCode;
	}

	private String procesar_graph(JsonReader jsonReader) throws IOException {

		String postalCode = "";

		jsonReader.beginArray();
		while (jsonReader.hasNext()) {
			jsonReader.beginObject();
			while (jsonReader.hasNext()) {
				if (jsonReader.nextName().equals("address")) {
					jsonReader.beginObject();

					while (jsonReader.hasNext()) {
						if (jsonReader.nextName().equals("postal-code")) {
							postalCode = jsonReader.nextString();
						} else
							jsonReader.skipValue();
					}
					jsonReader.endObject();
				} else
					jsonReader.skipValue();
			}
			jsonReader.endObject();
		}
		while (jsonReader.hasNext()) {
			jsonReader.skipValue();
		}
		if (!jsonReader.hasNext())
			jsonReader.endArray();

		return postalCode;

	}

}