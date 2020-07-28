//********************************************************************************
// Classe  servPdf.java
//********************************************************************************

package es.udl.asic.sakaiproject.tool.einahelpdesk;

import org.sakaiproject.tool.api.Session; 
import org.sakaiproject.tool.cover.SessionManager;
 
//import org.sakaiproject.api.kernel.session.Session;
//import org.sakaiproject.api.kernel.session.cover.SessionManager;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Driver;
import org.apache.fop.messaging.MessageHandler;
import org.apache.xerces.dom.DocumentImpl;

import org.w3c.dom.*;


public class servPdf extends HttpServlet {
	// Metode get del servlet que ens ha de servir la imatge
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		byte[] data = null; // array de bytes per obtenir la imatge

		//Obtenim la sessio
		Session session = SessionManager.getCurrentSession();
		
		//Obtenim el atribut ident de la sessio que es l'identificador
		Document document = (Document) session.getAttribute("document");
		String unaSolaAssistencia = (String) session.getAttribute("unaSolaAssistencia");

		Element llista = document.getDocumentElement();
		
		Element ellogo = document.createElement("logo");
		ellogo.appendChild(document.createTextNode(getFileName("udl.gif")));
		llista.appendChild(ellogo);	
		
				
		response.setContentType("application/pdf");
		if(unaSolaAssistencia.equals("una")){		generatePDF(document,"unaassistencia.xsl",response.getOutputStream());
		//System.out.println("UNA");
		}
		else if (unaSolaAssistencia.equals("moltes")){	generatePDF(document,"llistaassistencies.xsl",response.getOutputStream());
		//System.out.println("ES LA LLISTA");
		}
		
			
		if (document!=null){
			System.out.println("El doc NO es null");
		}
		else {
			System.out.println("El doc SI es null");
		}
		
		session.removeAttribute("document");
		System.out.println("servlet-document");
	}
	
	
	private void generatePDF(Document doc, String xslFileName, OutputStream streamOut){
		
		Driver driver = new Driver();
		driver.setOutputStream(streamOut);
		driver.setRenderer(Driver.RENDER_PDF);

		Transformer transformer = null;
		try{
			transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(getFileName(xslFileName)));
		}
		catch (TransformerException e){
			e.printStackTrace();
			System.out.println("Error al transformar");
			return;
		}


		Source x = new DOMSource(doc);

		try{
			transformer.transform(x, new SAXResult(driver.getContentHandler()));
		}
		catch (TransformerException e1){
			System.out.println("Error al transformar2");
			e1.printStackTrace();
			
			return;
		}
	}


	public String getFileName(String str){
		ServletContext servletContext = getServletContext();
		return servletContext.getRealPath("/einahelpdesk/" + str);
	
	}
	

	
}
