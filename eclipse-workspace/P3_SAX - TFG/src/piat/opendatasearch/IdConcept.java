package piat.opendatasearch;
public class IdConcept {
	private String id;
	
	public IdConcept(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "{ \"idConcept\" : { \n \"id\" : " + id + "}\n}";
	}

	public String getId() {
		return id;
	}
	
}
