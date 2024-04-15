package piat.opendatasearch;

/**
 * A Java class that represents an idConcept. An idConcept is an entity that is
 * uniquely identified by an id.
 */
public class IdConcept {
	private String id;
 
	/**
	 * Constructs a new IdConcept object.
	 * 
	 * @param id The id of the IdConcept.
	 */
	public IdConcept(String id) {
		this.id = id;
	}

	/**
	 * Returns the id of the IdConcept.
	 * 
	 * @return The id of the IdConcept.
	 */
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "{ \"idConcept\" : { \n \"id\" : " + id + "}\n}";
	}
}
