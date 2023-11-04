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
 * @author Anthony Chica Zambrano. DNI:53457387M
 *
 */

public class ManejadorXML extends DefaultHandler implements ParserCatalogo {
	private String 		 sNombreCategoria;	// Nombre de la categoría
	private String 		 sCodigoConcepto;		// Nombre del concepto.
	private String 		 idTemp;				//Atributo id temporalmente almacenado.
	private String 		 idDatasetTemp;
	private String		 titleTemp;
	private String 		 themeTemp;
	private String		 datasDescriptionTemp;
	private List<String> lConcepts; 	// Lista con los uris de los elementos <concept> que pertenecen a la categoría
	//private HashMap <String, String> datasetsContent;
	private Map <String, HashMap<String,String>> hDatasets;	// Mapa con información de los dataset que pertenecen a la categoría
	private boolean inDatasets = false;
	private boolean inDataset = false;
	private boolean conceptFound = false;
	private boolean inConcept = false;
	private boolean inConcepts = false;
	private boolean categoryFound = false;
	
	private int	level = 0;
	private StringBuilder contenidoElemento;
	

	/**  
	 * @param sCodigoConcepto código de la categoría a procesar
	 * @throws ParserConfigurationException 
	 */
	public ManejadorXML (String sCodigoConcepto) throws SAXException, ParserConfigurationException {
		// TODO
		this.sCodigoConcepto = sCodigoConcepto;
		lConcepts = new ArrayList<>();
		hDatasets = new HashMap<>();
		//datasetsContent = new HashMap<>();
		contenidoElemento = new StringBuilder();
	}

	 //===========================================================
	 // Métodos a implementar de la interfaz ParserCatalogo
	 //===========================================================

	/**
	 * <code><b>getLabel</b></code> 
	 * @return Valor de la cadena del elemento <code>label</code> del <code>concept</code> cuyo 
	 * elemento <code><b>code</b></code> sea <b>igual</b> al criterio a búsqueda.
	 * <br>
	 * null si no se ha encontrado el concept pertinente o no se dispone de esta información  
	 */
	@Override
	public String getLabel() {
		// TODO 	
		return sNombreCategoria;
	}

	/**
	 * <code><b>getConcepts</b></code>
	 *	Devuelve una lista con información de los <code><b>concepts</b></code> resultantes de la búsqueda. 
	 * <br> Cada uno de los elementos de la lista contiene la <code><em>URI</em></code> del <code>concept</code>
	 * 
	 * <br>Se considerarán pertinentes el <code><b>concept</b></code> cuyo código
	 *  sea igual al criterio de búsqueda y todos sus <code>concept</code> descendientes.
	 *  
	 * @return
	 * - List  con la <em>URI</em> de los concepts pertinentes.
	 * <br>
	 * - null  si no hay concepts pertinentes.
	 * 
	 */
	@Override	
	public List<String> getConcepts() {
		// TODO 
		return lConcepts;
	}

	/**
	 * <code><b>getDatasets</b></code>
	 * 
	 * @return Mapa con información de los <code>dataset</code> resultantes de la búsqueda.
	 * <br> Si no se ha realizado ninguna  búsqueda o no hay dataset pertinentes devolverá el valor <code>null</code>
	 * <br> Estructura de cada elemento del map:
	 * 		<br> . <b>key</b>: valor del atributo ID del elemento <code>dataset</code>con la cadena de la <code><em>URI</em></code>  
	 * 		<br> . <b>value</b>: Mapa con la información a extraer del <code>dataset</code>. Cada <code>key</code> tomará los valores <em>title</em>, <em>description</em> o <em>theme</em>, y <code>value</code> sus correspondientes valores.

	 * @return
	 *  - Map con información de los <code>dataset</code> resultantes de la búsqueda.
	 *  <br>
	 *  - null si no hay datasets pertinentes.  
	 */	
	@Override
	public Map<String, HashMap<String, String>> getDatasets() {
		// TODO 
		return hDatasets;
	}
	

	 //===========================================================
	 // Métodos a implementar de SAX DocumentHandler
	 //===========================================================
	
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
		// TODO 
		if(qName.equalsIgnoreCase("concept"))
		{
			inConcept = true;
			idTemp = attributes.getValue("id");
			if(level>0)
			{
				lConcepts.add(attributes.getValue("id"));
				level++;
			}
			if(inDataset)
			{
				if(lConcepts.contains(attributes.getValue("id")))
				{
					HashMap<String,String> datasetsContent = new HashMap<>();
					datasetsContent.putIfAbsent("title", titleTemp);
					datasetsContent.putIfAbsent("description", datasDescriptionTemp);
					datasetsContent.putIfAbsent("theme", themeTemp);
					hDatasets.putIfAbsent(idDatasetTemp, datasetsContent);
					//System.out.println("ID: " + idDatasetTemp + " " + "\n" + "<description>" + datasDescriptionTemp + "</description>" + "\n" + "<theme>"+themeTemp + "</theme>");
					idTemp = null;
					
				}
			}
			//System.out.println(idTemp);
		}

		
		else if(qName.equalsIgnoreCase("concepts") && inDataset)
		{
			inConcepts = true;
		}
		else if(qName.equalsIgnoreCase("datasets"))
			inDatasets = true;
		
		else if(qName.equalsIgnoreCase("dataset"))
		{
			inDataset = true;
			idDatasetTemp = attributes.getValue("id");
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		// TODO 
		if((contenidoElemento.toString().replaceAll("[\\n\\t ]","").equals(sCodigoConcepto)) && (inConcept))
		{
			conceptFound = true;
			lConcepts.add(idTemp);
			idTemp = null; //Para borrar lo que teniamos, y evitar valores repetidos?
			level = 1;
			categoryFound = true;
		}
		
		else if(qName.equalsIgnoreCase("label") && (categoryFound) && (level > 0))
		{
			sNombreCategoria = contenidoElemento.substring(6);
			categoryFound = false;
		}
		
		else if(qName.equalsIgnoreCase("concept") && level > 0)
		{
			level--;
			inConcept = (level > 0) ? true : false;
		}
		
		else if(qName.equalsIgnoreCase("title") && inDataset)
		{
			titleTemp = contenidoElemento.substring(4);
		}
		else if(qName.equalsIgnoreCase("theme") && inDataset)
		{
			themeTemp = contenidoElemento.substring(4);
		}
		else if(qName.equalsIgnoreCase("description") && inDataset)
		{
			datasDescriptionTemp = contenidoElemento.substring(4);
		}
		contenidoElemento.setLength(0);
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		// TODO 
		contenidoElemento.append(ch,start,length);
		
		
				
	}

}
