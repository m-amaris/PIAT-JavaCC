package piat.opendatasearch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Miguel Amarís Martos 54022315F
 *
 */
public class GenerarXML {
	private static final String conceptPattern = "\n\t\t\t<concept id=\"#ID#\"/>";
	private static final String datasetPattern = "\n\t\t\t<dataset id=\"#ID#\">\n\t\t\t\t<title>#TITLE#</title>"
			+ "\n\t\t\t\t<description>#DESCRIPTION#</description>\n\t\t\t\t<theme>#THEME#</theme>\n\t\t\t</dataset>";
	private static final String resourcePattern = "\n\t\t\t<resource id=\"#ID#\">\n\t\t\t\t<concept id=\"#CONCEPTID#\"/>\n\t\t\t\t<link>#LINK#</link>\n\t\t\t\t<title>#TITLE#</title>"
			+ "\n\t\t\t\t<location>\n\t\t\t\t\t\t<eventLocation>#EVENTLOCATION#</eventLocation>\n\t\t\t\t\t\t<area>#AREA#</area>\n\t\t\t\t\t\t<timetable>\n\t\t\t\t\t\t\t<start>#START#</start>\n\t\t\t\t\t\t\t<end>#END#</end></timetable>\n\t\t\t\t\t\t<georeference>#LATITUDE# #LONGITUDE#</georeference>\n\t\t\t\t</location>\n\t\t\t\t<organization>\n\t\t\t\t\t\t<accesibility>#ACCESIBILITY#</accesibility>\n\t\t\t\t\t\t<organizationName>#ORGANIZATIONNAME#</organizationName>\n\t\t\t\t</organization>\n\t\t\t\t<description>#DESCRIPTION#</description>\n\t\t\t</resource>";


	private static String genConcepts(List<String> lConcepts) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append("\n\t\t<concepts>");

		for (String unConcepto : lConcepts) {
			sbSalida.append(conceptPattern.replace("#ID#", unConcepto));
		}
		sbSalida.append("\n\t\t</concepts>");
		return sbSalida.toString();
	}

	private static String genDatasets(Map<String, HashMap<String, String>> hDatasets) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append("\n\t\t<datasets>");

		for (Map.Entry<String, HashMap<String, String>> entry : hDatasets.entrySet()) {
			String s = datasetPattern.replace("#ID#", entry.getKey());
			s = s.replace("#TITLE#", entry.getValue().get("title"));
			s = s.replace("#DESCRIPTION#", entry.getValue().get("description"));
			s = s.replace("#THEME#", entry.getValue().get("theme"));
			sbSalida.append(s);
		}
		sbSalida.append("\n\t\t</datasets>");
		return sbSalida.toString();
	}

	private static String genResources(Map<String, JsonArray> mDatasetConcepts) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append("\n\t\t<resources>");
		for (Map.Entry<String, JsonArray> entry : mDatasetConcepts.entrySet()) {
			JsonArray array = entry.getValue(); // Array graphs

			for (int i = 0; i < array.size(); i++) {
				String s = resourcePattern;
				JsonObject obj = array.get(i).getAsJsonObject(); // Un objeto dentro del array @graphs
				s = processJsonObject(obj, "", s);
				sbSalida.append(s);
			}
		}
		sbSalida.append("\n\t\t</resources>");
		return sbSalida.toString();
	}

	// Función recursiva para procesar objetos JSON
	private static String processJsonObject(JsonObject jsonObject, String parentKey, String s) {
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {

			String key = entry.getKey();
			JsonElement value = entry.getValue();

			// Combina la clave con la clave del padre si es necesario
			String fullKey = parentKey.isEmpty() ? key : parentKey + "." + key;
			if (value.isJsonObject()) {
				// Si el valor es un objeto JSON, llama recursivamente a la función
				s = processJsonObject(value.getAsJsonObject(), fullKey, s);
			} else if (value.isJsonArray()) {
				// Si el valor es un array JSON, llama a la función para procesar cada elemento
				// del array
				s = processJsonArray(value.getAsJsonArray(), fullKey, s);
			} else {
				// Si el valor no es un objeto ni un array, imprime la clave y el valor
				switch (fullKey) {
				case "@id":
					s = s.replace("#ID#", value.toString().replaceAll("\"", ""));
					break;
				case "@type":
					s = s.replace("#CONCEPTID#", value.toString().replaceAll("\"", ""));
					break;
				case "title":
					s = s.replace("#TITLE#", value.toString().replaceAll("\"", ""));
					break;
				case "link":
					s = s.replace("#LINK#", "<![CDATA[" + value.toString().replaceAll("\"", "") + "]]>");
					break;
				case "address.area.@id":
					s = s.replace("#AREA#", value.toString().replaceAll("\"", ""));
					break;
				case "address":
					s = s.replace("#START#", value.toString().replaceAll("\"", ""));
					break;
				case "dtstart":
					s = s.replace("#START#", value.toString().replaceAll("\"", ""));
					break;
				case "dtend":
					s = s.replace("#END#", value.toString().replaceAll("\"", ""));
					break;
				case "location.latitude":
					s = s.replace("#LATITUDE#", value.toString().replaceAll("\"", ""));
					break;
				case "location.longitude":
					s = s.replace("#LONGITUDE#", value.toString().replaceAll("\"", ""));
					break;
				case "organization.organization-name":
					s = s.replace("#ORGANIZATIONNAME#", value.toString().replaceAll("\"", ""));
					break;
				case "organization.accesibility":
					s = s.replace("#ACCESIBILITY#", value.toString().replaceAll("\"", ""));
					break;
				case "event-location":
					s = s.replace("#EVENTLOCATION#", value.toString().replaceAll("\"", ""));
					break;
				case "description":
					s = s.replace("#DESCRIPTION#", value.toString().replaceAll("\"", ""));
					break;
				default:
					break;
				}
			}
		}
		return s;
	}

	// Función para procesar arrays JSON
	private static String processJsonArray(JsonArray jsonArray, String parentKey, String s) {
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonElement element = jsonArray.get(i);

			if (element.isJsonObject()) {
				// Si el elemento del array es un objeto JSON, llama recursivamente a la función
				s = processJsonObject(element.getAsJsonObject(), parentKey + "[" + i + "]", s);
			} else if (element.isJsonArray()) {
				// Si el elemento del array es un array JSON, llama a la función para procesar
				// el array
				s = processJsonArray(element.getAsJsonArray(), parentKey + "[" + i + "]", s);
			} else {
				// Si el elemento no es un objeto ni un array, imprime el valor
				System.out.println(parentKey + "[" + i + "]: " + element);
			}
		}
		return s;
	}

	public static void genXML(String query, int numConcepts, int numDatasets, List<String> lConcepts,
			Map<String, HashMap<String, String>> hDatasets, Map<String, JsonArray> mDatasetConcepts, Path path) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append(
				"<?xml version = \"1.0\" encoding=\"UTF-8\"?>\n<searchResults xmlns=\"http://www.piat.dte.upm.es/practica4\""
						+ "\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\nxsi:schemaLocation=\"http://www.piat.dte.upm.es/practica4 ./ResultadosBusquedaP4.xsd\">");
		sbSalida.append("\n\t<summary>" + "\n\t\t<query>" + query + "</query>" + "\n\t\t<numConcepts>" + numConcepts
				+ "</numConcepts>" + "\n\t\t<numDatasets>" + numDatasets + "</numDatasets>"
				+ "\n\t</summary>\n\t<results>");
		sbSalida.append(genConcepts(lConcepts));
		sbSalida.append(genDatasets(hDatasets));
		sbSalida.append(genResources(mDatasetConcepts));
		sbSalida.append("\n\t</results>\n</searchResults>");

		try {
			Files.write(path, sbSalida.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
