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

	public String getCode() {
		return code;
	}

	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		String s = "\n{\n \"concept\" : { \n \"id\" : " + id + ",\n \"code\" : \"" + code + "\",\n \"label\" : \"" + label + "\"";
		if (!(concepts.isEmpty())) {
			s = s.concat(",\n \"concepts\" : " + concepts + ",");
		}
		s = s.concat("}\n}");
		return s;
	}
	
}