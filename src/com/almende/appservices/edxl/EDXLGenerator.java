package com.almende.appservices.edxl;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;



@Path("/status")
public class EDXLGenerator {
	static  SAXBuilder builder = new SAXBuilder();
	final String pattern = "yyyy-MM-dd'T'hh:mm:ssZ";
	final SimpleDateFormat sdf = new SimpleDateFormat(pattern);	    
	
	final static HashMap<String,Namespace> namespaces = new HashMap<String,Namespace>();
	
	public EDXLGenerator(){
		namespaces.put("xsi", Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"));
		namespaces.put("rm", Namespace.getNamespace("rm", "urn:oasis:names:tc:emergency:EDXL:RM:1.0"));
		namespaces.put("xpil", Namespace.getNamespace("xpil","urn:oasis:names:tc:ciq:xpil:3"));
		namespaces.put("xnl", Namespace.getNamespace("xnl","urn:oasis:names:tc:ciq:xnl:3"));
		namespaces.put("xal",Namespace.getNamespace("xal","urn:oasis:names:tc:ciq:xal:3"));
		namespaces.put("geo-oasis", Namespace.getNamespace("geo-oasis","urn:oasis:names:tc:emergency:EDXL:HAVE:1.0:geo-oasis"));
	}
	
	public Element setElementWithPath(Element from, String[] path, String value){
		try {
			Element elem = from;
			Element parent = elem;
			for (String tag : path){
				String[] parts = tag.split(":");
				Namespace ns=null;
				if (parts.length>1){
					tag = parts[1];
					ns = namespaces.get(parts[0]);
				}
				elem = (Element) elem.getChild(tag,ns);
				if (elem != null){
					parent=elem;
				} else {
					elem = new Element(tag,ns);
					parent.addContent(elem);
					parent=elem;
				}
			}
			if (value != null) elem.setText(value);
			return elem;
		} catch (Exception e){}
		return null;
	}
	
	
	public Document genResourceDeploymentStatus(){
		final String template = 
			  "<ReportResourceDeploymentStatus>"
			  	+"<MessageContentType>ReportResourceDeploymentStatus</MessageContentType>"
			  +"</ReportResourceDeploymentStatus>"
		;
		Document document = null;
		try {
		    document = builder.build(new InputSource(new StringReader(template)));
		    Element root = document.getRootElement();
		    root.setNamespace(Namespace.getNamespace("urn:oasis:names:tc:emergency:EDXL:RM:1.0:msg"));
		    root.setAttribute(
		    	    new Attribute("schemaLocation",
		    	    		"urn:oasis:names:tc:emergency:EDXL:RM:1.0:msg EDXL-RMReportResourceDeploymentStatus.xsd",
		    	    	namespaces.get("xsi")));
		    for (Entry<String,Namespace> ns : namespaces.entrySet()){
		    	root.addNamespaceDeclaration(ns.getValue());
		    }
		    
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return document;
	}
	
	@GET
	@Produces("application/xml text/plain")
	public Response getDoc(){
		Document doc = genResourceDeploymentStatus();
		Element root = doc.getRootElement();
		setElementWithPath(root, new String[]{"MessageID"}, "ludo1");
		setElementWithPath(root, new String[]{"SendDateTime"}, sdf.format(new Date(System.currentTimeMillis())));
		setElementWithPath(root, new String[]{"OriginatingMessageID"},"ludo0");
		setElementWithPath(root, new String[]{"ContectInformation","ContactRole"},"Sender");
		setElementWithPath(root, new String[]{"ResourceInformation","ResourceInfoElementID"},"001");
		Element respInfo = setElementWithPath(root, new String[]{"ResourceInformation","ResponseInformation"},null);
		setElementWithPath(respInfo,new String[]{"rm:PrecedingResourceInfoElementID"},"001");
		setElementWithPath(respInfo, new String[]{"rm:ResponseType"},"Accept");
		
		Element typeStructure = setElementWithPath(root, new String[]{"ResourceInformation","Resource","TypeStructure"},null);
		setElementWithPath(typeStructure,new String[]{"rm:ValueListURN"},"urn:x-hazard:vocab:resourceTypes");
		setElementWithPath(typeStructure, new String[]{"rm:Value"},"Small Animal Sheltering Team");
		
		XMLOutputter outputter = new XMLOutputter();
        return Response.ok(outputter.outputString(doc)).build();
	}
}
