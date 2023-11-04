package piat.opendatasearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 *
 */

public class GenerarXML {
	
	private List<String> concepts;
	private String nombreCategoria;
	private String codigoCategoria;
	private Map<String, HashMap<String, String>> datasets;
	
	private static final String conceptPattern = "\n\t\t\t<concept id=\"#ID#\"/>";
	private static final String cabeceraXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<searchResults xmlns=\"http://www.piat.dte.upm.es/practica3\"\n"
			+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
			+ "xsi:schemaLocation=\"http://www.piat.dte.upm.es/practica3 ResultadosBusquedaP3.xsd\">\n";
	private static final String datasetPattern = "\n\t\t\t<dataset id=\"#ID#\">";
	private static final String titlePattern = "\n\t\t\t\t<title>#TITLE#</title>";
	private static final String descriptionPattern = "\n\t\t\t\t<description>#DESC#</description>";
	private static final String themePattern = "\n\t\t\t\t<theme>#THEME#</theme>";

	public GenerarXML(List<String> concepts, String nombreCategoria, String codigoCategoria,
			Map<String, HashMap<String, String>> datasets) {
		this.concepts = concepts;
		this.nombreCategoria = nombreCategoria;
		this.codigoCategoria= codigoCategoria;
		this.datasets = datasets;
	}

	private static String conceptsToXML(List<String> lConcepts) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append("\n\t\t<concepts>");
		for (String unConcepto : lConcepts) {
			sbSalida.append(conceptPattern.replace("#ID#", unConcepto));
		}
		sbSalida.append("\n\t\t</concepts>");
		return sbSalida.toString();
	}

	private String summaryToXML(String codigoCategoria, List<String> concepts,
			Map<String, HashMap<String, String>> datasets) {
		String summary = "\n\t<summary>\n\t\t<query>" + codigoCategoria + "</query>\n\t\t<numConcepts>" + concepts.size()
				+ "</numConcepts>\n\t\t<numDatasets>" + datasets.size() + "</numDatasets>\n\t</summary>";

		return summary;
	}
	
	private String datasetsToXML(Map<String, HashMap<String, String>> datasets) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append("\n\t\t<datasets>");
		for (Map.Entry<String, HashMap<String, String>> unDataset : datasets.entrySet()) {
			sbSalida.append(datasetPattern.replace("#ID#", unDataset.getKey()));
			sbSalida.append(titlePattern.replace("#TITLE#", unDataset.getValue().get("title")));
			sbSalida.append(descriptionPattern.replace("#DESC#", unDataset.getValue().get("description")));
			sbSalida.append(themePattern.replace("#THEME#", unDataset.getValue().get("theme")));
			sbSalida.append("\n\t\t\t</dataset>");
		}
		sbSalida.append("\n\t\t</datasets>");
		return sbSalida.toString();
	}
	
	private String resultsToXML() {
		StringBuilder sbResults = new StringBuilder();
		sbResults.append("\n\t<results>");
		sbResults.append(conceptsToXML(concepts));
		sbResults.append(datasetsToXML(datasets));
		sbResults.append("\n\t</results>");
		
		return sbResults.toString();
	}
	
	public String generarXML() {
		StringBuilder sbSalida = new StringBuilder();

		sbSalida.append(cabeceraXML);
		sbSalida.append(summaryToXML(codigoCategoria, concepts, datasets));
		sbSalida.append(resultsToXML());
		sbSalida.append("\n</searchResults>");
		return sbSalida.toString();

	}
}
