import java.util.ArrayList;
import java.util.List;

public class Concept {
	private String id;
	private String code = "";
	private String label = "";
	private List<Concept> concepts = new ArrayList<Concept>();

	public Concept(String id, String code, String label, List<Concept> concepts) {
		this.id = id;
		this.code = code;
		this.label = label;
		this.concepts = concepts;
	}

	public Concept(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		String s;
		if (code == "" && label == "" && concepts == null) {
			s = "idConcept:{ id=" + id+"}\n";
		} else {
			s = "concept:{ \n \tid=" + id+"\n";
		}
		if (code != "") {
			s = s.concat("\tcode=" + code+"\n");
		}
		if (label != "") {
			s = s.concat("\tlabel=" + label+"\n");
		}
		if (!concepts.isEmpty()) {
			s = s.concat("\tconcepts=" + concepts+"\n");
		}
		s = s.concat("}\n");
		return s;
	}

	public void addConcept(Concept concept) {
		concepts.add(concept);
	}
}
