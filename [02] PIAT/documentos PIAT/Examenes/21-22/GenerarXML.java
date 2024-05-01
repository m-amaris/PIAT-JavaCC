package piat.opendatasearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sergio Delgado Cantalapiedra 53748114N
 *
 */

public class GenerarXML {

	private List<String> concepts;
	private String nombreCategoria;
	private String codigoCategoria;
	private Map<String, HashMap<String, String>> datasets;
	private Map<String, List<Map<String, String>>> mDatasetConcepts;

	private static final String conceptPattern = "\n\t\t\t<concept id=\"#ID#\"/>";
	private static final String cabeceraXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<searchResults xmlns=\"http://www.piat.dte.upm.es/practica3\"\n"
			+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
			+ "xsi:schemaLocation=\"http://www.piat.dte.upm.es/practica3 ResultadosBusquedaP3.xsd\">\n";
	private static final String cabeceraXML4 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<searchResults xmlns=\"http://www.piat.dte.upm.es/practica4\"\n"
			+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
			+ "xsi:schemaLocation=\"http://www.piat.dte.upm.es/practica4 ResultadosBusquedaP4.xsd\">\n";
	private static final String datasetPattern = "\n\t\t\t<dataset id=\"#ID#\">";
	private static final String titlePattern = "\n\t\t\t\t<title>#TITLE#</title>";
	private static final String descriptionPattern = "\n\t\t\t\t<description>#DESC#</description>";
	private static final String themePattern = "\n\t\t\t\t<theme>#THEME#</theme>";

	private static final String resourcePattern = "\n\t\t<resource id=\"#ID#\">";
	private static final String linkPattern = "\n\t\t\t<link id=\"#ID#\"/>";
	private static final String title2Pattern = "\n\t\t\t<title>#TITLE#</title>";
	private static final String eventPattern = "\n\t\t\t\t<eventLocation>#EVENT#</eventLocation>";
	private static final String areaPattern = "\n\t\t\t\t<area>#AREA#</area>";
	private static final String startPattern = "\n\t\t\t\t\t<start> #START# </start>";
	private static final String endPattern = "\n\t\t\t\t\t<end> #END# </end>";
	private static final String accesPattern = "\n\t\t\t\t<accesibility>#ACCES#</accesibility>";
	private static final String orgPattern = "\n\t\t\t\t<organizationName>#ORG#</organizationName>";
	private static final String descPattern = "\n\t\t\t<description>#DESC#</description>";

	public GenerarXML(List<String> concepts, String nombreCategoria, String codigoCategoria,
			Map<String, HashMap<String, String>> datasets, Map<String, List<Map<String, String>>> mDatasetConcepts) {
		this.concepts = concepts;
		this.nombreCategoria = nombreCategoria;
		this.codigoCategoria = codigoCategoria;
		this.datasets = datasets;
		this.mDatasetConcepts = mDatasetConcepts;
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
		String summary = "\n\t<summary>\n\t\t<query>" + codigoCategoria + "</query>\n\t\t<numConcepts>"
				+ concepts.size() + "</numConcepts>\n\t\t<numDatasets>" + datasets.size()
				+ "</numDatasets>\n\t</summary>";

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

	private String resourcesToXML() {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append("\n\t<resources>");

		for (Map.Entry<String, List<Map<String, String>>> unMDatasetConcept : mDatasetConcepts.entrySet()) {
			for (int i = 0; i < unMDatasetConcept.getValue().size(); i++) {
			
				sbSalida.append(resourcePattern.replace("#ID#", unMDatasetConcept.getKey()));
				sbSalida.append(conceptPattern.replace("#ID#", unMDatasetConcept.getValue().get(i).get("@type")));
				sbSalida.append(linkPattern.replace("#ID#", unMDatasetConcept.getValue().get(i).get("link").replace("&", "&amp;")));
				sbSalida.append(title2Pattern.replace("#TITLE#", unMDatasetConcept.getValue().get(i).get("title")));
				sbSalida.append("\n\t\t\t<location>");
				sbSalida.append(eventPattern.replace("#EVENT#", unMDatasetConcept.getValue().get(i).get("eventLocation")));
				sbSalida.append(areaPattern.replace("#AREA#", unMDatasetConcept.getValue().get(i).get("area")));
				sbSalida.append("\n\t\t\t\t<timetable>");
				sbSalida.append(startPattern.replace("#START#", unMDatasetConcept.getValue().get(i).get("start")));
				sbSalida.append(endPattern.replace("#END#", unMDatasetConcept.getValue().get(i).get("end")));
				sbSalida.append("\n\t\t\t\t</timetable>");
				sbSalida.append("\n\t\t\t\t\t<geoReference> " + unMDatasetConcept.getValue().get(i).get("latitude")
						+ " " + unMDatasetConcept.getValue().get(i).get("longitude") + " </geoReference>");
				sbSalida.append("\n\t\t\t</location>");
				sbSalida.append("\n\t\t\t<organization>");
				sbSalida.append(accesPattern.replace("#ACCES#", unMDatasetConcept.getValue().get(i).get("accesibility")));
				sbSalida.append(orgPattern.replace("#ORG#", unMDatasetConcept.getValue().get(i).get("organizationName")));
				sbSalida.append("\n\t\t\t</organization>");
				sbSalida.append(descPattern.replace("#DESC#", unMDatasetConcept.getValue().get(i).get("descripton")));
				sbSalida.append("\n\t\t</resource>");
			}

		}
		sbSalida.append("\n\t</resources>");

		return sbSalida.toString();
	}

	private String resultsToXMLP4() {
		StringBuilder sbResults = new StringBuilder();
		sbResults.append("\n\t<results>");
		sbResults.append(conceptsToXML(concepts));
		sbResults.append(datasetsToXML(datasets));
		sbResults.append(resourcesToXML());
		sbResults.append("\n\t</results>");

		return sbResults.toString();
	}
	
	public String generarP4() {
		StringBuilder sbSalida = new StringBuilder();

		sbSalida.append(cabeceraXML4);
		sbSalida.append(summaryToXML(codigoCategoria, concepts, datasets));
		sbSalida.append(resultsToXMLP4());
		sbSalida.append("\n</searchResults>");

		return sbSalida.toString();
	}
}
