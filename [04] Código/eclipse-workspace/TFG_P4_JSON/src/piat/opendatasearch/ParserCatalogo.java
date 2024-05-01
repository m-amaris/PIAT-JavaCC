/* No modificar este fichero */
package piat.opendatasearch;

import java.util.List;

/**
 * An interface that should be implemented by the ManejadorXML class.
 *
 * This interface defines methods that are required for extracting information
 * from an open data catalog.
 * 
 * @author Miguel Amarís
 */
public interface ParserCatalogo {

	/**
     * Retrieves the value of the "label" element of the "concept" whose "code" element
     * matches the search criteria.
     *
     * @return The value of the "label" element of the "concept" whose "code" matches
     *         the search criteria, or null if the pertinent "concept" is not found or
     *         the information is not available.
     */
	public String getLabel();

	/**
     * Retrieves a list of information about "concepts" resulting from the search.
     *
     * Each element in the list contains the URI of the "concept."
     *
     * Relevant "concepts" are considered to be the "concept" whose code (the "code" element)
     * matches the search criteria and all descendant "concepts" under it.
     *
     * @return - A list of URIs of pertinent concepts.
     *         - null if no pertinent concepts are found.
     */
	public List<Concept> getConcepts();

	/**
     * Retrieves a map with information about "datasets" resulting from the search.
     *
     * If no search has been performed, or there are no pertinent datasets, it returns null.
     *
     * The structure of each map element:
     * - "key": The value of the ID attribute of the "dataset" element as a URI string.
     * - "value": A map with information to be extracted from the "dataset." Each "key"
     *   takes values like "title," "description," or "theme," and "value" holds the
     *   corresponding values.
     *
     * @return - A map with information about pertinent datasets.
     *         - null if no pertinent datasets are found.
     */
	public List<Dataset> getDatasets();

}