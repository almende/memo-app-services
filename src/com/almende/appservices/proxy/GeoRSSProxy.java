package com.almende.appservices.proxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.jdom.Element;

import com.chap.memo.memoNodes.MemoNode;
import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.georss.GMLModuleImpl;
import com.sun.syndication.feed.module.georss.GeoRSSModule;
import com.sun.syndication.feed.module.georss.SimpleModuleImpl;
import com.sun.syndication.feed.module.georss.W3CGeoModuleImpl;
import com.sun.syndication.feed.module.georss.geometries.Position;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.WireFeedOutput;

@Path("/geoRSS")
public class GeoRSSProxy {
	static MemoNode baseNode; 
	
	public GeoRSSProxy(){
		baseNode = MemoNode.getRootNode().getChildByStringValue("Memo-appservices demo").getChildByStringValue("agents");
	}
	
	@SuppressWarnings("unchecked")
	@GET
	@Produces("application/rss+xml")
	public Response getRSS(@QueryParam("feedType") String feedType,@QueryParam("georssType") String rssType){
		SyndFeed feed = new SyndFeedImpl();
		if (feedType != null && !feedType.equals("")){
			feed.setFeedType(feedType);
		} else {
			feed.setFeedType("atom_0.3");
			feedType="atom_0.3";
		}
		feed.setTitle("MemoGeoRSS");
		feed.setDescription("GeoRSS output stream for Memo");
		feed.setLink("http://memo-app-services.appspot.com/geoRSS");
		
		List<SyndEntry> entries = feed.getEntries();		
		for (MemoNode agent: baseNode.getChildren()){
			SyndEntry entry = new SyndEntryImpl();
			entry.setTitle(agent.getPropertyValue("name"));
			Date date= new Date();
			date.setTime(new Long(agent.getPropertyValue("seen")));
			entry.setPublishedDate(date);
			entry.setLink("http://memo-app-services.appspot.com/agent/"+agent.getId().toString());
			
			GeoRSSModule geoRSSModule=new SimpleModuleImpl();
			if (rssType != null){
				if (rssType.equals("gml")){
					geoRSSModule = new GMLModuleImpl();
				} else if (rssType.equals("w3c")){
					geoRSSModule = new W3CGeoModuleImpl();
				}
			}
			Position newPos = new Position();
			newPos.setLatitude(new Double(agent.getPropertyValue("lat")));
			newPos.setLongitude(new Double(agent.getPropertyValue("lon")));
			geoRSSModule.setPosition(newPos);
			
			List<Module> modules = entry.getModules();
			modules.add(geoRSSModule);
			
			List<Element> foreignMarkup = new ArrayList<Element>();
			Element myElement = new Element("type");
			myElement.setText(agent.getPropertyValue("resType"));
			foreignMarkup.add(myElement);
			
			myElement = new Element("state");
			myElement.setText(agent.getPropertyValue("state"));
			foreignMarkup.add(myElement);
			
			myElement = new Element("taskDescription");
			myElement.setText(agent.getPropertyValue("taskDescription"));
			foreignMarkup.add(myElement);

			entry.setForeignMarkup(foreignMarkup);
	
			entries.add(entry);
		}
	
		WireFeedOutput woutput = new WireFeedOutput();
	    WireFeed wirefeed = feed.createWireFeed(feedType);
	    String result = "";
		try {
			
			result = woutput.outputString(wirefeed);
		} catch (Exception e) {
			System.out.println("Error writing feed to string:"+e.getMessage());
			e.printStackTrace();
		}
		return Response.ok(result).build();
	}
	
}
