package piat.opendatasearch;
import java.util.List;

/**
 * A Java class that represents a concept.
 */
public class Concept {
    private final String id;
	private final String code;
	private final String label; 
    private final List<Concept> concepts;

    /**
     * Constructs a new Concept object.
     *
     * @param id        the unique identifier of the concept
     * @param code      the code of the concept
     * @param label     the label of the concept
     * @param concepts  the list of nested concepts
     */
    public Concept(String id, String code, String label, List<Concept> concepts) {
        this.id = id;
        this.code = code;
        this.label = label;
        this.concepts = concepts;
    }

    /**
     * Returns the code of the concept.
     *
     * @return the code of the concept
     */
    public final String getCode() {
        return code;
    }

    /**
     * Returns the unique identifier of the concept.
     *
     * @return the unique identifier of the concept
     */
    public final String getId() {
        return id;
    }

    /**
     * Returns the list of nested concepts.
     *
     * @return the list of nested concepts
     */
    public final List<Concept> getConcepts() {
        return concepts;
    }

    /**
	 * Returns a string representation of the Concept object, including its
	 * attributes.
	 *
	 * @return A string representation of the Concept object.
	 */
    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append(" \"concept\" : {\n");
        builder.append("   \"id\" : ").append(id).append(",\n");
        builder.append("   \"code\" : \"").append(code).append("\",\n");
        builder.append("   \"label\" : \"").append(label).append("\"");
        if (!(concepts.isEmpty())) {
            builder.append(",\n \"concepts\" : ").append(concepts).append(",");
        }
        builder.append("\n}\n}");
        return builder.toString();
    }
}