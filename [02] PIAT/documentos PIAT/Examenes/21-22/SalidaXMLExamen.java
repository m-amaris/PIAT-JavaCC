package piat.opendatasearch;

/* Clase estática para crear un String con el xml a partir de la información que se pasa como parámetro en mGraph*/

import java.util.Map;

/**
 * @author Sergio Delgado Cantalapiedra 53748114N
 *
 */

public class SalidaXMLExamen {

	private static final String cabeceraXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<graphs xmlns=\"http://www.piat.dte.upm.es/examenJunio\"\n"
			+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
			+ "xsi:schemaLocation=\"http://www.piat.dte.upm.es/examenJunio graphs.xsd\">\n";

	private static final String graphPattern = "\n\t<graph id=\"#ID#\">";
	private static final String tipoPCPattern = "\n\t\t<tipoPostalCode>#TPC#</tipoPostalCode>";
	private static final String postalCodePattern = "\n\t\t<PostalCode>#PC#</PostalCode>";

	public static String generar(Map<String, Map<String, String>> mGraph) {
		StringBuilder salidaXML = new StringBuilder();
		// TODO: Almacenar en salidaXML el texto XML válido respecto al esquema del
		// documento graphs.xsd a partir de la información que hay en el mapa mGraph
		salidaXML.append(cabeceraXML);
		for (Map.Entry<String, Map<String, String>> unGraph : mGraph.entrySet()) {
			salidaXML.append(graphPattern.replace("#ID#", unGraph.getKey()));
			salidaXML.append(tipoPCPattern.replace("#TPC#", unGraph.getValue().get("tipoPostalCode")));
			salidaXML.append(postalCodePattern.replace("#PC#", unGraph.getValue().get("PostalCode")));
			salidaXML.append("\n\t</graph>");
		}
		salidaXML.append("\n</graphs>");

		return salidaXML.toString();

	}

}
