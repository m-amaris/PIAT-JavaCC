import java.util.List;

/**
 * @author
 *
 */

public class ManejadorXML implements ParserCatalogo {
	

	/**
	 * @param sCodigoConcepto código de la categoría a procesar
	 * @throws ParserConfigurationException
	 */
	private  List < Dataset > datasets;
	private  List < Concept > concepts;
	
	public ManejadorXML(List < Dataset > datasets, List < Concept > concepts){
		this.datasets = datasets;
		this.concepts = concepts;
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
		return null;
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
	public List< Concept > getConcepts() {
		return this.concepts;
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
	public List < Dataset > getDatasets() {
		return this.datasets;
	}

}
