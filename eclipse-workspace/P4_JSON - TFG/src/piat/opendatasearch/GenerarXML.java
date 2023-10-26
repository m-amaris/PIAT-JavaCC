package piat.opendatasearch;

import java.util.List;
import java.util.Map;

/**
 * @author
 * 
 */

public class GenerarXML {

	private List<Concept> concepts;
	private String codigoCategoria;
	private List<Dataset> datasets;
	private static int conceptsSize;
	private Map<String, List<Resource>> resourceList;

	private static final String conceptPattern = "\n\t\t\t<concept id=#ID#/>";
	private static final String cabeceraXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<searchResults xmlns=\"http://www.piat.dte.upm.es/practica3\"\n"
			+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
			+ "xsi:schemaLocation=\"http://www.piat.dte.upm.es/practica3 ResultadosBusquedaP3.xsd\">";
	private static final String datasetPattern = "\n\t\t\t<dataset id=#ID#>";
	private static final String titlePattern = "\n\t\t\t\t<title>#TITLE#</title>";
	private static final String descriptionPattern = "\n\t\t\t\t<description>#DESC#</description>";
	private static final String themePattern = "\n\t\t\t\t<theme>#THEME#</theme>";
	private static final String resourcePattern = "\n\t\t\t<resource id=\"#ID#\">\n\t\t\t\t<concept id=\"#CONCEPTID#\"/>\n\t\t\t\t<link>#LINK#</link>\n\t\t\t\t<title>#TITLE#</title>"
			+ "\n\t\t\t\t<location>\n\t\t\t\t\t\t<eventLocation>#EVENTLOCATION#</eventLocation>\n\t\t\t\t\t\t<area>#AREA#</area>\n\t\t\t\t\t\t<timetable>\n\t\t\t\t\t\t\t<start>#START#</start>\n\t\t\t\t\t\t\t<end>#END#</end></timetable>\n\t\t\t\t\t\t<georeference>#LATITUDE# #LONGITUDE#</georeference>\n\t\t\t\t</location>\n\t\t\t\t<organization>\n\t\t\t\t\t\t<accesibility>#ACCESIBILITY#</accesibility>\n\t\t\t\t\t\t<organizationName>#ORGANIZATIONNAME#</organizationName>\n\t\t\t\t</organization>\n\t\t\t\t<description>#DESCRIPTION#</description>\n\t\t\t</resource>";

		
	public GenerarXML(List<Concept> concepts, String nombreCategoria, String codigoCategoria, List<Dataset> datasets,Map<String, List<Resource>> resourceList) {
		this.concepts = concepts;
		this.codigoCategoria = codigoCategoria;
		this.datasets = datasets;
		conceptsSize = 0;
		this.resourceList = resourceList;
	}

	public static int recursiveConcept(List<Concept> concepts, StringBuilder sbSalida) {
		for (Concept c : concepts) {
			sbSalida.append(conceptPattern.replace("#ID#", c.getId()));
			conceptsSize++;
			recursiveConcept(c.getConcepts(), sbSalida);
		}
		return conceptsSize;
	}

	private static String conceptsToXML(List<Concept> lConcepts) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append("\n\t\t<concepts>");
		recursiveConcept(lConcepts, sbSalida);
		sbSalida.append("\n\t\t</concepts>");
		return sbSalida.toString();
	}

	private String summaryToXML(String codigoCategoria, List<Concept> concepts, List<Dataset> datasets) {
		String summary = "\n\t<summary>\n\t\t<query>" + codigoCategoria + "</query>\n\t\t<numConcepts>"
				+ recursiveConcept(concepts, new StringBuilder()) + "</numConcepts>\n\t\t<numDatasets>"
				+ datasets.size() + "</numDatasets>\n\t</summary>";

		return summary;
	}

	private static String genResources(Map<String, List<Resource>> mDatasetConcepts) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append("\n\t\t<resources>");
		for (Map.Entry<String, List<Resource>> entry : mDatasetConcepts.entrySet()) {
			List<Resource> array = entry.getValue(); // Array graphs

			for(Resource r : array) {
				String s = resourcePattern.replace("#ID#", entry.getKey());
				s = s.replace("#CONCEPTID#", r.getConceptId());
				s = s.replace("#LINK#", "<![CDATA[" + r.getLink() + "]]>");
				s = s.replace("#TITLE#", r.getTitle());
				s = s.replace("#EVENTLOCATION#", r.getEventLocation());
				s = s.replace("#AREA#", r.getArea());
				s = s.replace("#START#", r.getStartDate());
				s = s.replace("#END#", r.getEndDate());
				s = s.replace("#LATITUDE#", Double.toString(r.getLatitude()));
				s = s.replace("#LONGITUDE#", Double.toString(r.getLongitude())); 
				s = s.replace("#ACCESIBILITY#", r.getAccesibility());
				s = s.replace("#ORGANIZATIONNAME#", r.getOrganizationName());
				s = s.replace("#DESCRIPTION#", r.getDescription());
				sbSalida.append(s);
			}
		}
		sbSalida.append("\n\t\t</resources>");
		return sbSalida.toString();
	}
	
	private String datasetsToXML(List<Dataset> datasets) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append("\n\t\t<datasets>");
		for (Dataset unDataset : datasets) {
			sbSalida.append(datasetPattern.replace("#ID#", unDataset.getId()));
			sbSalida.append(titlePattern.replace("#TITLE#", unDataset.getTitle()));
			sbSalida.append(descriptionPattern.replace("#DESC#", unDataset.getDescription()));
			sbSalida.append(themePattern.replace("#THEME#", unDataset.getTheme()));
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
		sbResults.append(genResources(resourceList));
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
