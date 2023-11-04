package sax_example;

import java.util.List;

public class XMLOutput {

	private String sXMLPattern = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"\n"+"\t" 
			 + "<librosOferta>"+"\n" + "\t\t"
			 + "#LIBROS#" + "\t"
			 +"</librosOferta>";

	private String sLibroPattern= "<libro>"+"\n"+"\t\t\t"
				+"#LIBRO#" +"\t\t" 
				+"</libro>"+"\n\t\t";

	private String sContenidoPattern = "<titulo>"+"#TITULO#"+"</titulo>"+"\n\t\t\t"
					+"<autores>"+"#AUTORES#"+"</autores>\n";

	private String sContenidoLibro;
	private String sUnLibro;
	private String sLibrosOut;
	private String sXMLOut;


public String xmlOutput (List<Libro> librosL) {
	String aux=" ";
	for (Libro tmpLibro : librosL) {
    	List<String> autores = tmpLibro.getAuthors();
    	for (String autor : autores) {
    		aux= aux + autor + " ";
    	}
		sContenidoLibro= sTitle (tmpLibro.getTitle(),aux);
		aux="";
		sUnLibro = sLibro (sContenidoLibro);
		sLibrosOut+=sUnLibro;
	}
	sXMLOut = sXMLPattern.replace("#LIBROS#", sLibrosOut);
	return sXMLOut;
}

public String sTitle (String sTitulo, String sAutores) {
	String contenido = sContenidoPattern.replace("#TITULO#",sTitulo).replace("#AUTORES#",sAutores);
	return contenido;
}

public String sLibro(String unLibro) {
	String libro = sLibroPattern.replace ("#LIBRO#", unLibro);
	return libro;
}
}
