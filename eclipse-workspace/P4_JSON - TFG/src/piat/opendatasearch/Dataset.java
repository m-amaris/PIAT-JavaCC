package piat.opendatasearch;

import java.util.List;

public class Dataset {
	private final String id;
	private final String title;
	private final String description; 
	private final String keyword;
	private final String theme;
	private final String publisher;
	private final List<IdConcept> idConcepts;

	/**
	 * Constructs a new Dataset object.
	 *
	 * @param id          the unique identifier of the dataset
	 * @param title       the title of the dataset
	 * @param description the description of the dataset
	 * @param keyword     the keyword associated with the dataset
	 * @param theme       the theme associated with the dataset
	 * @param publisher   the publisher of the dataset
	 * @param idConcepts  the list of unique identifier concepts associated with the dataset
	 */
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

	/**
	 * Adds an IdConcept to the list of unique identifier concepts associated with
	 * the dataset.
	 *
	 * @param idConcept the IdConcept to add
	 */
	public final void addIdConcept(IdConcept idConcept) {
		idConcepts.add(idConcept);
	}

	/**
	 * Returns the list of unique identifier concepts associated with the dataset.
	 *
	 * @return the list of unique identifier concepts associated with the dataset
	 */
	public final List<IdConcept> getIdConcepts() {
		return idConcepts;
	}

	/**
	 * Returns the unique identifier of the dataset.
	 *
	 * @return the unique identifier of the dataset
	 */
	public final String getId() {
		return id;
	}

	/**
	 * Returns the title of the dataset.
	 *
	 * @return the title of the dataset
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * Returns the description of the dataset.
	 *
	 * @return the description of the dataset
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * Returns the keyword associated with the dataset.
	 *
	 * @return the keyword associated with the dataset
	 */
	public final String getKeyword() {
		return keyword;
	}

	/**
	 * Returns the theme associated with the dataset.
	 *
	 * @return the theme associated with the dataset
	 */
	public final String getTheme() {
		return theme;
	}

	/**
	 * Returns the publisher of the dataset.
	 *
	 * @return the publisher of the dataset
	 */
	public final String getPublisher() {
		return publisher;
	}

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n{\n \"dataset\" : { \n \"id\" : ").append(id).append(",\n \"title\" : \"").append(title)
				.append("\"");

		if (description != null) {
			sb.append(",\n \"description\" : \"").append(description).append("\"");
		}
		if (!keyword.isEmpty()) {
			sb.append(",\n \"keyword\" : \"").append(keyword).append("\"");
		}
		if (!theme.isEmpty()) {
			sb.append(",\n \"theme\" : \"").append(theme).append("\"");
		}
		if (!publisher.isEmpty()) {
			sb.append(",\n \"publisher\" : \"").append(publisher).append("\"");
		}
		if (!idConcepts.isEmpty()) {
			sb.append(",\n \"idConcepts\" : ").append(idConcepts);
		}
		sb.append("}\n}");
		return sb.toString();
	}

}
