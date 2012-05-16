package com.almende.appservices.proxy;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.chap.memo.memoNodes.MemoNode;
import com.sun.syndication.feed.module.georss.GeoRSSModule;
import com.sun.syndication.feed.module.georss.SimpleModuleImpl;
import com.sun.syndication.feed.module.georss.W3CGeoModuleImpl;
import com.sun.syndication.feed.module.georss.GMLModuleImpl;
import com.sun.syndication.feed.module.georss.geometries.Position;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;

@Path("/geoRSS")
public class GeoRSSProxy {
	
	public GeoRSSProxy(){
		//generate example memo db
		MemoNode rootNode = MemoNode.getRootNode();
		if (rootNode.getChildByStringValue("geoRSS demo") == null){
			System.out.println("Generating test/demo data");
			MemoNode baseNode = rootNode.addChild(new MemoNode("geoRSS demo"));
			baseNode.addChild(new MemoNode("agent"))
					.setPropertyValue("name", "PoliceOfficer#324353")
					.setPropertyValue("lat", "77.58601")
					.setPropertyValue("lon", "-52.4484")
					.setPropertyValue("seen", new Long(new Date().getTime()).toString());
			baseNode.addChild(new MemoNode("agent"))
					.setPropertyValue("name", "AmbulanceMedic#3502")
					.setPropertyValue("lat", "77.585957")
					.setPropertyValue("lon", "-52.448461")
					.setPropertyValue("seen", new Long(new Date().getTime()).toString());
			baseNode.addChild(new MemoNode("agent"))
					.setPropertyValue("name", "Firefighter#324333")
					.setPropertyValue("lat", "77.585957")
					.setPropertyValue("lon", "-52.448461")
					.setPropertyValue("seen", new Long(new Date().getTime()).toString());
			MemoNode.flushDB();
		}
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
		}
		feed.setTitle("MemoGeoRSS");
		feed.setDescription("GeoRSS output stream for Memo");
		feed.setLink("http://memo-app-services.appspot.com/geoRSS");
		
		MemoNode baseNode = MemoNode.getRootNode().getChildByStringValue("geoRSS demo");
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

			entry.getModules().add(geoRSSModule);
			entries.add(entry);
		}
		
		StringWriter w = new StringWriter();
		SyndFeedOutput output = new SyndFeedOutput();
		try {
			output.output(feed, w);
		} catch (Exception e) {
			System.out.println("Error writing feed to string:"+e.getMessage());
			e.printStackTrace();
		}
		w.flush();
		return Response.ok(w.toString()).build();
	}

	
}
