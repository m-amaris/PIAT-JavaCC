package piat.opendatasearch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author Miguel Amarís Martos 54022315F
 *
 */

public class GenerarXML {
	private static final String conceptPattern = "\n\t\t\t<concept id=\"#ID#\"/>";
	private static final String datasetPattern = "\n\t\t\t<dataset id=\"#ID#\">\n\t\t\t\t<title>#TITLE#</title>"
			+ "\n\t\t\t\t<description>#DESCRIPTION#</description>\n\t\t\t\t<theme>#THEME#</theme>\n\t\t\t</dataset>";

	private static String genConcepts(List<String> lConcepts) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append("\n\t\t<concepts>");

		for (String unConcepto : lConcepts) {
			sbSalida.append(conceptPattern.replace("#ID", unConcepto));
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

	public static void genXML(String query, int numConcepts, int numDatasets, List<String> lConcepts,
			Map<String, HashMap<String, String>> hDatasets, Path path) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append(
				"<?xml version = \"1.0\" encoding=\"UTF-8\"?>\n<searchResults xmlns=\"http://www.piat.dte.upm.es/ResultadosBusquedaP3\""
						+ "\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\nxsi:schemaLocation=\"http://www.piat.dte.upm.es/ResultadosBusquedaP3 ResultadosBusquedaP3.xsd\">");
		sbSalida.append("\n\t<summary>" + "\n\t\t<query>" + query + "</query>" + "\n\t\t<numConcepts>" + numConcepts
				+ "</numConcepts>" + "\n\t\t<numDatasets>" + numDatasets + "<datasets>"
				+ "<\n\t</summary>\n\t<results>");
		sbSalida.append(genConcepts(lConcepts));
		sbSalida.append(genDatasets(hDatasets));
		sbSalida.append("\n\t</results>\n</searchResults>");

		try {
			Files.write(path, sbSalida.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return sbSalida.toString();
	}
}
