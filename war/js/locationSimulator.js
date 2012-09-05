(function($) {
	
	var init = function(dom_obj,options){
		return new function(){
			var agent = this;
			agent.myDomObj = dom_obj;
			agent.directionsDisplay = null;
			agent.geocoder = null;
			agent.directionsService = null;
			agent.settings = {
				startTime : 0,
				startAddress : "",
				endAddress : "",
				map: null
			};
			agent.dyn = {
				startPos : null,
				endPos : null,
				duration : 0,
				route : null,
				marker : null,
				steps : []
			};
			this.methods = {
				init : function(options) {
					agent.settings.startTime = new Date().getTime();
					$.extend(agent.settings, options);
					if (agent.settings.map == null){
						var mapOptions = {
							center: new google.maps.LatLng(47.075765,9.392449),
							zoom: 15,
							mapTypeId: google.maps.MapTypeId.SATELLITE
						};
						agent.myDomObj.append("<div class='map'></div>");
						agent.settings.map = new google.maps.Map(agent.myDomObj.find(".map").get(0),
								mapOptions);
					}
					if (agent.directionsDisplay == null){
						agent.directionsDisplay = new google.maps.DirectionsRenderer();
						agent.geocoder = new google.maps.Geocoder();
						agent.directionsService = new google.maps.DirectionsService();						
					}
					agent.geocoder.geocode({
						'address' : agent.settings.startAddress
					}, function(results, status) {
						if (status == google.maps.GeocoderStatus.OK) {
							agent.dyn.startPos = results[0].geometry.location;					
						}
					});
					agent.geocoder.geocode({
						'address' : agent.settings.endAddress
					}, function(results, status) {
						if (status == google.maps.GeocoderStatus.OK) {
							agent.dyn.endPos = results[0].geometry.location;
						}
					});
				},
				getCurrentPos : function(now) {
					if (!now){
						now = new Date().getTime();	
					}
					var lastStep = null;
					for ( var i = 0; i < agent.dyn.steps.length; i++) {
						var step = agent.dyn.steps[i];
						if (step.time < now){
							lastStep = step;
							continue;
						}
						if (lastStep == null) return step.position;
						var lat1 = lastStep.position.lat();
						var lng1 = lastStep.position.lng();
						var lat2 = step.position.lat();
						var lng2 = step.position.lng();
						var part = (now-lastStep.time)/(step.time - lastStep.time);
						var newLat = lat1 + (lat2-lat1)*part;
						var newLng = lng1 + (lng2-lng1)*part;
						return new google.maps.LatLng(newLat,newLng);
					}
					if (i == 0) return agent.dyn.startPos;
					return agent.dyn.endPos;
				},
				showDirections : function() {
					if (agent.dyn.startPos != null
							&& agent.dyn.endPos != null) {
						agent.directionsDisplay.setMap(agent.settings.map);
						var request = {
							origin : agent.dyn.startPos,
							destination : agent.dyn.endPos,
							travelMode : google.maps.TravelMode.DRIVING
						};
						agent.directionsService.route(request, function(result, status) {
							if (status == google.maps.DirectionsStatus.OK) {
								agent.dyn.route = result;
								agent.directionsDisplay.setDirections(result);
								agent.methods.convertRouteToTimeLookup();
							}
						});
					}
				},
				showCurrentPos : function() {
					if (agent.dyn.startPos != null
							&& agent.dyn.endPos != null) {
						var currentPos = agent.methods.getCurrentPos();
						
						var mOptions = {
							position : currentPos,
							map : agent.settings.map,
							labelContent: agent.myDomObj.attr("id"),
						    labelAnchor: new google.maps.Point(22, 0),
						    labelClass: "labels", // the CSS class for the label
						    labelStyle: {opacity: 0.75}
						}
						if (agent.dyn.marker == null) {
							agent.dyn.marker = new MarkerWithLabel(mOptions);
						} else {
							agent.dyn.marker.setPosition(currentPos);
						}
					}
				},
				convertRouteToTimeLookup : function() {
					var time = agent.settings.startTime;
					agent.dyn.route.routes[0].legs[0].steps.map(function(step) {
						var duration = step.duration.value;
						var distance = step.distance.value;
						var totalMovedDegrees = 0;
						var lastPos = step.start_point;
						step.lat_lngs.map(function(pos) {
							if (pos != lastPos) {
								totalMovedDegrees += Math.sqrt(Math.abs(Math.pow(pos.lat()
										- lastPos.lat(), 2)
										+ Math.pow(pos.lng() - lastPos.lng(), 2)));
							}
							lastPos = pos;
						});
						var speed = totalMovedDegrees / duration;
						lastPos = step.start_point;
						step.lat_lngs.map(function(pos) {
							if (pos != lastPos) {
								var movedDegrees = Math.sqrt(Math.abs(Math.pow(pos.lat()
										- lastPos.lat(), 2)
										+ Math.pow(pos.lng() - lastPos.lng(), 2)));
								var duration = movedDegrees / speed;
								time += duration * 1000;
								agent.dyn.steps.push({
									time : time,
									position : pos
								});
							}
							lastPos = pos;
						});
					});
				}		
			};			
		}
	}
	$.fn.locationSimulator = function(options) {
		if (typeof google == "undefined"){
			console.log("Couldn't find google's API, no internet connection?");
			return;
		}
		var result=init(this,arguments);
		result.methods.init.apply(this, arguments);
		return result;
	}
})(jQuery);
