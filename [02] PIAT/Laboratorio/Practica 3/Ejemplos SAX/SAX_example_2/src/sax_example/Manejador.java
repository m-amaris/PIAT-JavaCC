package sax_example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
 
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
public class Manejador extends DefaultHandler {

	private List<Libro> bookL=new ArrayList<>();
    private String tmpValue;
    private Libro bookTmp;
    SimpleDateFormat sdf= new SimpleDateFormat("yy-MM-dd");

    @Override
    public void startElement(String s, String s1, String elementName, Attributes attributes) throws SAXException {
        // if current element is book , create new book
        // clear tmpValue on start of element
 
        if (elementName.equalsIgnoreCase("libro")) {
            bookTmp = new Libro();
            bookTmp.setId(attributes.getValue("id"));
            bookTmp.setLang(attributes.getValue("lang"));
        }
        // if current element is publisher
        if (elementName.equalsIgnoreCase("publisher")) {
            bookTmp.setPublisher(attributes.getValue("country"));
        }
    }
    @Override
    public void endElement(String s, String s1, String element) throws SAXException {
        // if end of book element add to list
        if (element.equals("libro")) {
            bookL.add(bookTmp);
        }
        if (element.equalsIgnoreCase("isbn")) {
            bookTmp.setIsbn(tmpValue);
        }
        if (element.equalsIgnoreCase("title")) {
            bookTmp.setTitle(tmpValue);
        }
        if(element.equalsIgnoreCase("author")){
           bookTmp.getAuthors().add(tmpValue);
        }
        if(element.equalsIgnoreCase("price")){
            bookTmp.setPrice(Integer.parseInt(tmpValue));
        }
        if(element.equalsIgnoreCase("regDate")){
            try {
                bookTmp.setRegDate(sdf.parse(tmpValue));
            } catch (ParseException e) {
                System.out.println("date parsing error");
            }
        }
    }
    @Override
    public void characters(char[] ac, int i, int j) throws SAXException {
        tmpValue = new String(ac, i, j);
    }
    
    public List<Libro> getLibros() {
        return bookL;
    }
}