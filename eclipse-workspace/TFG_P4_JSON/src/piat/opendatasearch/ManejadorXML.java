package piat.opendatasearch;
import java.util.List;

/**
 * A class that implements the ParserCatalogo interface for handling XML data.
 *
 * This class is responsible for processing XML data related to open data
 * catalogs. It implements the ParserCatalogo interface, which defines methods
 * for extracting relevant information from the catalog data.
 *
 * @author Miguel Amarís
 */

public class ManejadorXML implements ParserCatalogo {
	

	/**
     * Constructs an instance of the ManejadorXML class with the provided datasets
     * and concepts.
     *
     * @param datasets  A list of datasets to be processed.
     * @param concepts  A list of concepts relevant to the data processing.
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
     * Retrieves the label of the concept that matches the search criteria.
     *
     * @return The value of the "label" element of the concept whose "code" matches
     *         the search criteria, or null if the relevant concept or label
     *         information is not found.
     */
	@Override
	public String getLabel() {
		return null;
	}

	/**
     * Retrieves a list of concepts that match the search criteria.
     *
     * @return A list of URIs representing concepts that match the search criteria,
     *         or null if no pertinent concepts are found.
     */
	@Override
	public List< Concept > getConcepts() {
		return this.concepts;
	}

	/**
     * Retrieves information about datasets that match the search criteria.
     *
     * @return A list of datasets with information about "title," "description," and
     *         "theme" based on the search criteria, or null if no pertinent
     *         datasets are found. Each dataset is represented as a map with its
     *         unique ID as the key and information as the value.
     */
	@Override
	public List < Dataset > getDatasets() {
		return this.datasets;
	}

}
