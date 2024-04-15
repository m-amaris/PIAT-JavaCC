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
	private Map<String, List<Resource>> resourceList;
	private int conceptsSize;

	private static final String CONCEPT_PATTERN = "\n\t\t\t<concept id=\"#ID#\"/>";
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<searchResults xmlns=\"http://www.piat.dte.upm.es/practica4\"\n"
			+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
			+ "xsi:schemaLocation=\"http://www.piat.dte.upm.es/practica4 ResultadosBusquedaP4.xsd\">";
	private static final String DATASET_PATTERN = "\n\t\t\t<dataset id=\"#ID#\">";
	private static final String TITLE_PATTERN = "\n\t\t\t\t<title>#TITLE#</title>";
	private static final String DESCRIPTION_PATTERN = "\n\t\t\t\t<description>#DESC#</description>";
	private static final String THEME_PATTERN = "\n\t\t\t\t<theme>#THEME#</theme>";
	private static final String RESOURCE_PATTERN = "\n\t\t\t<resource id=\"#ID#\">\n\t\t\t\t<concept id=\"#CONCEPTID#\"/>\n\t\t\t\t<link>#LINK#</link>\n\t\t\t\t<title>#TITLE#</title>"
			+ "\n\t\t\t\t<location>\n\t\t\t\t\t<eventLocation>#EVENTLOCATION#</eventLocation>\n\t\t\t\t\t<area>#AREA#</area>\n\t\t\t\t\t<timetable>\n\t\t\t\t\t\t<start>#START#</start>\n\t\t\t\t\t\t<end>#END#</end></timetable>\n\t\t\t\t\t<georeference>#LATITUDE# #LONGITUDE#</georeference>\n\t\t\t\t</location>\n\t\t\t\t<organization>\n\t\t\t\t\t<accesibility>#ACCESIBILITY#</accesibility>\n\t\t\t\t\t<organizationName>#ORGANIZATIONNAME#</organizationName>\n\t\t\t\t</organization>\n\t\t\t\t<description>#DESCRIPTION#</description>\n\t\t\t</resource>";

	public GenerarXML(List<Concept> concepts, String codigoCategoria, List<Dataset> datasets,
			Map<String, List<Resource>> resourceList) {
		this.concepts = concepts;
		this.codigoCategoria = codigoCategoria;
		this.datasets = datasets;
		conceptsSize = 0;
		this.resourceList = resourceList;
	}

	public int recursiveConcept(List<Concept> concepts, StringBuilder sbSalida) {
		for (Concept c : concepts) {
			sbSalida.append(CONCEPT_PATTERN.replace("#ID#", c.getId()));
			conceptsSize++;
			recursiveConcept(c.getConcepts(), sbSalida);
		}
		return conceptsSize;
	}

	private String conceptsToXML(List<Concept> lConcepts) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append("\n\t\t<concepts>");
		recursiveConcept(lConcepts, sbSalida);
		sbSalida.append("\n\t\t</concepts>");
		return sbSalida.toString();
	}

	private String summaryToXML(String codigoCategoria, List<Concept> concepts, List<Dataset> datasets) {
		return "\n\t<summary>\n\t\t<query>" + codigoCategoria + "</query>\n\t\t<numConcepts>"
				+ recursiveConcept(concepts, new StringBuilder()) + "</numConcepts>\n\t\t<numDatasets>"
				+ datasets.size() + "</numDatasets>\n\t</summary>";
	}

	private static String genResources(Map<String, List<Resource>> mDatasetConcepts) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append("\n\t\t<resources>");
		for (Map.Entry<String, List<Resource>> entry : mDatasetConcepts.entrySet()) {
			List<Resource> array = entry.getValue(); // Array graphs

			for (Resource r : array) {
				String s = RESOURCE_PATTERN.replace("#ID#", entry.getKey());
				s = s.replace("#CONCEPTID#", String.valueOf(r.getConceptId()));
				s = s.replace("#LINK#", "<![CDATA[" + String.valueOf(r.getLink()) + "]]>");
				s = s.replace("#TITLE#", String.valueOf(r.getTitle()));
				s = s.replace("#EVENTLOCATION#", String.valueOf(r.getEventLocation()));
				s = s.replace("#AREA#", String.valueOf(r.getArea()));
				s = s.replace("#START#", String.valueOf(r.getStartDate()));
				s = s.replace("#END#", String.valueOf(r.getEndDate()));
				s = s.replace("#LATITUDE#", Double.toString(r.getLatitude()));
				s = s.replace("#LONGITUDE#", Double.toString(r.getLongitude()));
				s = s.replace("#ACCESIBILITY#", String.valueOf(r.getAccesibility()));
				s = s.replace("#ORGANIZATIONNAME#", String.valueOf(r.getOrganizationName()));
				s = s.replace("#DESCRIPTION#", String.valueOf(r.getDescription()));
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
			sbSalida.append(DATASET_PATTERN.replace("#ID#", unDataset.getId()));
			sbSalida.append(TITLE_PATTERN.replace("#TITLE#", unDataset.getTitle()));
			sbSalida.append(DESCRIPTION_PATTERN.replace("#DESC#", unDataset.getDescription()));
			sbSalida.append(THEME_PATTERN.replace("#THEME#", unDataset.getTheme()));
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

		sbSalida.append(XML_HEADER);
		sbSalida.append(summaryToXML(codigoCategoria, concepts, datasets));
		sbSalida.append(resultsToXML());
		sbSalida.append("\n</searchResults>");
		return sbSalida.toString();

	}
}
