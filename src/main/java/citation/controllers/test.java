package citation.controllers;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import citation.models.Citation;

public class test {
	public static void main(String[] args) throws JsonProcessingException {
		/*String data = ""; 
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder builder;  
		Document doc = null;
	    try {
			data = new String(Files.readAllBytes(Paths.get("src/main/resources/wonef-fscore-0.1.xml")));
			builder = factory.newDocumentBuilder();  
			doc = builder.parse(new InputSource(new StringReader(data)));  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println(doc.getFirstChild().getNodeName());*/
		
		String str= "Tu es un sorcier Harry"; 
		str = Normalizer.normalize(str, Normalizer.Form.NFD);
		str = str.replaceAll("[^\\p{ASCII}]", "").replaceAll("[^a-zA-Z0-9]", " ");
		System.out.println(str.trim());
		Citation c = new Citation();
		c.setBookId(1);
		ObjectMapper op = new ObjectMapper();
		System.out.println(op.writeValueAsString(c));
	}
	
}
