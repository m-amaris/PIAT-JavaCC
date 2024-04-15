package piat.opendatasearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author
 *
 */

public class ManejadorXML extends DefaultHandler implements ParserCatalogo {
	private String sNombreCategoria; // Nombre de la categoría
	private List<String> lConcepts; // Lista con los uris de los elementos <concept> que pertenecen a la categoría
	private Map<String, HashMap<String, String>> hDatasets; // Mapa con información de los dataset que pertenecen a la
															// categoría
	private String sCodigoConcepto;

	private String idTemp;
	private String idData;
	private String tmpValue;
	private String tmpTitle;
	private String tmpDesc;
	private String tmpTheme;

	private int nivel;
	private boolean encontrada;
	private boolean esConcept;
	private boolean esDataset;

	private HashMap<String, String> tmpMap;
//	private String tmpTitle;
//	private String tmpDesc;
//	private String tmpTheme;

	/**
	 * @param sCodigoConcepto código de la categoría a procesar
	 * @throws ParserConfigurationException
	 */
	public ManejadorXML(String sCodigoConcepto) throws SAXException, ParserConfigurationException {
		this.sCodigoConcepto = sCodigoConcepto;
		lConcepts = new ArrayList<String>();
		hDatasets = new HashMap<String, HashMap<String, String>>();
		nivel = 0;
		encontrada = false;
		esConcept = false;
		esDataset = false;
	}

	// ===========================================================
	// Métodos a implementar de la interfaz ParserCatalogo
	// ===========================================================

	/**
	 * <code><b>getLabel</b></code>
	 * 
	 * @return Valor de la cadena del elemento <code>label</code> del
	 *         <code>concept</code> cuyo elemento <code><b>code</b></code> sea
	 *         <b>igual</b> al criterio a búsqueda. <br>
	 *         null si no se ha encontrado el concept pertinente o no se dispone de
	 *         esta información
	 */
	@Override
	public String getLabel() {
		return sNombreCategoria;
	}

	/**
	 * <code><b>getConcepts</b></code> Devuelve una lista con información de los
	 * <code><b>concepts</b></code> resultantes de la búsqueda. <br>
	 * Cada uno de los elementos de la lista contiene la <code><em>URI</em></code>
	 * del <code>concept</code>
	 * 
	 * <br>
	 * Se considerarán pertinentes el <code><b>concept</b></code> cuyo código sea
	 * igual al criterio de búsqueda y todos sus <code>concept</code> descendientes.
	 * 
	 * @return - List con la <em>URI</em> de los concepts pertinentes. <br>
	 *         - null si no hay concepts pertinentes.
	 * 
	 */
	@Override
	public List<String> getConcepts() {
		return lConcepts;
	}

	/**
	 * <code><b>getDatasets</b></code>
	 * 
	 * @return Mapa con información de los <code>dataset</code> resultantes de la
	 *         búsqueda. <br>
	 *         Si no se ha realizado ninguna búsqueda o no hay dataset pertinentes
	 *         devolverá el valor <code>null</code> <br>
	 *         Estructura de cada elemento del map: <br>
	 *         . <b>key</b>: valor del atributo ID del elemento
	 *         <code>dataset</code>con la cadena de la <code><em>URI</em></code>
	 *         <br>
	 *         . <b>value</b>: Mapa con la información a extraer del
	 *         <code>dataset</code>. Cada <code>key</code> tomará los valores
	 *         <em>title</em>, <em>description</em> o <em>theme</em>, y
	 *         <code>value</code> sus correspondientes valores.
	 * 
	 * @return - Map con información de los <code>dataset</code> resultantes de la
	 *         búsqueda. <br>
	 *         - null si no hay datasets pertinentes.
	 */
	@Override
	public Map<String, HashMap<String, String>> getDatasets() {
		return hDatasets;
	}

	// ===========================================================
	// Métodos a implementar de SAX DocumentHandler
	// ===========================================================

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		// TODO

	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		// TODO

	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		if (localName.equalsIgnoreCase("concept")) {
			esConcept = true;
			idTemp = attributes.getValue("id");
		}
		if (localName.equalsIgnoreCase("concept") && encontrada && esConcept) {
			lConcepts.add(attributes.getValue("id"));
			nivel++;
		}

		if (localName.equalsIgnoreCase("dataset")) {
			esDataset = true;
			idData = attributes.getValue("id");
			tmpMap = new HashMap<String, String>();
		}

		if (localName.equalsIgnoreCase("concept") && esDataset) {
			if (lConcepts.contains(attributes.getValue("id"))) {
				tmpMap.put("title", tmpTitle);
				tmpMap.put("description", tmpDesc);
				tmpMap.put("theme", tmpTheme);
				hDatasets.putIfAbsent(idData, tmpMap);
			}

		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);

		if (localName.equalsIgnoreCase("code")) {
			if (tmpValue.equals(sCodigoConcepto) && esConcept) {
				lConcepts.add(idTemp);
				encontrada = true;
				nivel = 1;
			}
		}

		if (localName.equalsIgnoreCase("label") && esConcept && encontrada && nivel == 1) {
			sNombreCategoria = tmpValue;
		}

		if (localName.equalsIgnoreCase("concept")) {
			nivel--;
			if (nivel == 0) {
				esConcept = false;
				encontrada = false;
			}
		}

		if (localName.equalsIgnoreCase("title")) {
			tmpTitle = tmpValue;
		}

		if (localName.equalsIgnoreCase("description")) {
			tmpDesc = tmpValue;
		}

		if (localName.equalsIgnoreCase("theme")) {
			tmpTheme = tmpValue;
		}

		if (localName.equalsIgnoreCase("dataset")) {
			esDataset = false;
		}

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		tmpValue = new String(ch, start, length);
	}

}
