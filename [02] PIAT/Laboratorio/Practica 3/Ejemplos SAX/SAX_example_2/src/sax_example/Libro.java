package sax_example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Libro {
    String lang;
    String title;
    String id;
    String isbn;
    Date regDate;
    String publisher;
    int price;
    List<String> authors;
    public Libro(){
        authors=new ArrayList<String>();
    }
  
    public String getLang () {
    	return lang;
    }
    
    public String getTitle () {
    	return title;
    }   
    
    public String getId () {
    	return id;
    }    
    
    public String getIsbn () {
    	return isbn;
    }    
    
    public Date getDate () {
    	return regDate;
    }
    
    public String getPublisher () {
    	return publisher;
    }    
    
    public int getPrice () {
    	return price;
    }    
    
    public List<String> getAuthors () {
    	return authors;
    }   
    
    public void setLang (String lang) {
    	this.lang=lang;
    }
    
    public void setTitle (String title) {
    	this.title=title;
    }
    
    public void setId (String id) {
    	this.id=id;
    }
    public void setIsbn (String isbn) {
    	this.isbn=isbn;
    }
    
    public void setRegDate (Date regDate) {
    	this.regDate=regDate;
    }
    
    public void setPublisher (String publisher) {
    	this.publisher=publisher;
    }   
    
    public void setPrice (int price) {
    	this.price=price;
    }   

    public void setAuthors (List<String> authors) {
    	this.authors=authors;
    }   
    
    public String toString (){
    	return ("[" + title + " "+ price + " "+ authors + "]");
    }   
    
    
}