package sax_example;

import java.util.List;


public class Output {

	private String basicOut=" ";
	private StringBuffer textOut = new StringBuffer();

	public String basicOutput(List<Libro> librosL){
		 for (Libro tmpLibro : librosL) {
	            basicOut += tmpLibro.toString();	
		 }
		return basicOut;
	}
	
	public StringBuffer textOutput (List<Libro> librosL) {
		for (Libro tmpLibro : librosL) {
			textOut.append(tmpLibro.getTitle()).append("\n").append(tmpLibro.getPrice()).append("\n");
        	textOut.append(tmpLibro.getIsbn()).append("\n");
        	List<String> autores = tmpLibro.getAuthors();
        	for (String autor : autores) {
        		textOut.append(autor).append("  ");
        	}
        	textOut.append ("\n\n");			
		}
		return textOut;
	}

}
