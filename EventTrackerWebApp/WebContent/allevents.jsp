<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
<title>Event Tracker</title>
<link type="text/css"  rel="stylesheet" href="style.css">

<script type="text/javascript"
	src="http://code.jquery.com/jquery-latest.js"></script>

<script type="text/javascript"
	src="http://maps.googleapis.com/maps/api/js?key=AIzaSyDAJrNEEEeiN-_qU	KKhbcLjuCHAkv4VSoU&sensor=true">
</script>

<script type="text/javascript">
  var map;
  var markers;
  var markerCounter;  
  
  function initialize() {
    var latlng = new google.maps.LatLng(40.4468314, -79.9479933);
    var myOptions = {
      zoom: 8,
      center: latlng,
      mapTypeId: google.maps.MapTypeId.ROADMAP,	
      disableDoubleClickZoom: true
    };
    
    markers = new Array();
    
    map = new google.maps.Map(document.getElementById("map_canvas"),
        myOptions);
    
    google.maps.event.addListener(map, 'tilesloaded', function(){
    	getNewMarkers();
    });
    
    google.maps.event.addListener(map, 'bounds_changed', function(){
    	clearMarkers();
    	getNewMarkers();
    });
    
  }
  
  function getNewMarkers(){
	    var boundsNE = map.getBounds().getNorthEast();
	    var boundsSW = map.getBounds().getSouthWest();

	    $.post('AllEventsServlet', {lat2: boundsNE.lat(), lat1: boundsSW.lat(),lng2: boundsNE.lng(), lng1: boundsSW.lng()}, function(data) {
	    	console.log(data);
	    		$.each(data, function(key, event) {
	    			addMarker(event);
	    		});
	          });   
  }
  
  function clearMarkers() {
	  for (var i = 0; i < markers.length; i++){
		  markers[i].setMap(null);
	  }
  }
  
  function addMarker(event){
	  latlng = new google.maps.LatLng(event.location.lat, event.location.lng);
	  var marker = new google.maps.Marker({
		    position: latlng,
		    map: map,
		    eventid: event.id,
		    title : event.name,
		    infoWindow: new google.maps.InfoWindow()
		  });
	  
	  marker.setMap(map);
	  marker.infoWindow.setContent("<h4>"+ marker.title + "</h4>")
	  markers.push(marker);	  

  	  google.maps.event.addListener(marker, "click", function() {
          marker.infoWindow.open(map, marker);
    	});
    }
</script>

<body onload="initialize()">
	<div id="header_wrapper">
		<h1>World Events</h1>
		<nav id="main-nav">
			<ul>
				<li><a href="index.jsp" class="current">Home</a></li>
				<li><a href="userevents.jsp">User Event History</a></li>
				<li><a href="allevents.jsp">World Events</a></li>
			</ul>
		</nav>
	</div>
	<div id="map_wrapper">
		<div id="map_canvas"></div>
	</div>
</body>
</html>