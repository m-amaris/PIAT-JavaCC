public class IdConcept {
	private String id;
	
	public IdConcept(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "idConcept:{ id=" + id+"}\n";
	}

	public String getId() {
		return id;
	}
	
}
