/*******
 * 
 * Ejemplo de uso de SAX.
 * Asignatura: PIAT.
 * Curso: 2020/2021
 * Autores: Javier Malagón, Gregorio RUbio
 * 
 *******/


package sax_example;

import java.io.IOException;


import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

public class inicio {

    public static void main(String[] args)throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
    	Manejador handler = new Manejador();
        try {
        	SAXParser saxParser = factory.newSAXParser();
        	saxParser.parse("./src/libreria.xml", handler);
        } catch (ParserConfigurationException e) {
            System.out.println("ParserConfig error");
        } catch (SAXException e) {
            System.out.println("SAXException : xml not well formed");
        } catch (IOException e) {
            System.out.println("IO error");
        } 
       
        List<Libro> librosL= handler.getLibros();
       
        Output output = new Output();
        XMLOutput xmlOutput = new XMLOutput();
        
        
        System.out.println ("******** Salida básica ********");
        System.out.println (output.basicOutput (librosL));
        /********** 
        Ejemplo de salida basica desde la clase principal. 
        Se recomienda utilizar la calse Output
        
        for (Libro tmpLibro : librosL) {
            System.out.println(tmpLibro.toString());
        }
         *******/
        
        System.out.println ("\n\n******** Salida texto ********");
        System.out.println (output.textOutput(librosL));
        /*****************
         Ejemplo de salida texto desde la clase principal. 
        Se recomienda utilizar la calse Output
        
       for (Libro tmpLibro : librosL) {
        	System.out.println ("Titulo: " + tmpLibro.getTitle());
         	System.out.println ("Precio: " + tmpLibro.getPrice());
        	System.out.println ("ISBN: " + tmpLibro.getIsbn());
        	List<String> autores = tmpLibro.getAuthors();
        	for (String autor : autores) {
        		System.out.println ("Autor: " + autor);
        	}
        }
        ******************/
        
        System.out.println ("\n\n******** Salida XML ********");
        System.out.println (xmlOutput.xmlOutput(librosL));
    }  
}
