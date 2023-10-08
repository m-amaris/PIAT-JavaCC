import java.util.List;

public class Concept {
	private String id;
	private String code;
	private String label;
	private List<Concept> concepts;

	public Concept(String id, String code, String label, List<Concept> concepts) {
		this.id = id;
		this.code = code;
		this.label = label;
		this.concepts = concepts;
	}

	@Override
	public String toString() {
		return "concept:{ \n \tid:" + id+",\n"+"\tcode:" + code+",\n"+"\tlabel:" + label+",\n"+"\tconcepts:" + concepts+"\n"+"}\n";
	}

	public void addConcept(Concept concept) {
		concepts.add(concept);
	}
}
