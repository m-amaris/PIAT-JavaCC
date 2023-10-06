import java.util.List;

public class Dataset {
	private String id;
	private String title;
	private String description = "";
	private String keyword = "";
	private String theme = "";
	private String publisher = "";
	private List<Concept> idConcepts = null;

	public Dataset(String id, String title, String description, String keyword, String theme, String publisher,
			List<Concept> idConcepts) {
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
		String s = "Dataset{ id=" + id + " title= " + title;
		if (description != null) {
			s = s.concat(" description=" + description);
		}
		if (keyword != "") {
			s = s.concat(" keyword=" + keyword);
		}
		if (theme != "") {
			s = s.concat(" theme=" + theme);
		}
		if (publisher != "") {
			s = s.concat(" publisher=" + publisher);
		}
		if (idConcepts != null) {
			s = s.concat(" idConcepts=" + idConcepts);
		}
		s = s.concat("}\n");
		return s;
	}

	public void addIdConcept(Concept concept) {
		idConcepts.add(concept);
	}
}
