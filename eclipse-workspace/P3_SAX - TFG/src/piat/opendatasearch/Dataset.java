package piat.opendatasearch;
import java.util.List;

public class Dataset {
	private String id;
	private String title;
	private String description = "";
	private String keyword = "";
	private String theme = "";
	private String publisher = "";
	private List<IdConcept> idConcepts = null;

	public Dataset(String id, String title, String description, String keyword, String theme, String publisher,
			List<IdConcept> idConcepts) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.keyword = keyword;
		this.theme = theme;
		this.publisher = publisher;
		this.idConcepts = idConcepts;
	}

	@Override
	public String toString() {
		String s = "dataset:{ \n id:" + id + ",\n title: " + title+",";
		if (description != null) {
			s = s.concat("\n description:" + description+",");
		}
		if (keyword != "") {
			s = s.concat("\n keyword:" + keyword+",");
		}
		if (theme != "") {
			s = s.concat("\n theme:" + theme+",");
		}
		if (publisher != "") {
			s = s.concat("\n publisher:" + publisher+",");
		}
		if (idConcepts != null) {
			s = s.concat("\n idConcepts:" + idConcepts+",");
		}
		s = s.concat("}\n");
		return s;
	}

	public void addIdConcept(IdConcept idConcept) {
		idConcepts.add(idConcept);
	}

	public List<IdConcept> getIdConcepts() {
		return idConcepts;
	}
	
}
