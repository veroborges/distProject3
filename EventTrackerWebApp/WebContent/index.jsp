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
    map = new google.maps.Map(document.getElementById("map_canvas"),
        myOptions);

    markers =new Array();
    markerCounter = 0;
	
    google.maps.event.addListener(map, 'dblclick', function(event) {
      addUserMarker(event.latLng);
    });
    
    $.getJSON('EventTrackerServlet', function(data) {
		console.log(data);
		$.each(data, function(key, shard) {
			  drawShardBoundary(shard.latmin, shard.latmax, shard.lngmin, shard.lngmax);
			});
  });
    
  }
  
  function drawShardBoundary(latmin, latmax, lngmin, lngmax){
	  var bounds = google.maps.LatLngBounds(new google.maps.LatLng(latmin, lngmin), new google.maps.LatLng(latmax, lngmax));

	  var rectangle = new google.maps.Rectangle();
	  
	  var rectOptions = {
		      strokeColor: "#FF0000",
		      strokeOpacity: 0.8,
		      strokeWeight: 2,
		      fillColor: "#FF0000",
		      fillOpacity: 0.35,
		      map: map,
		      bounds: bounds
		    };
	  
		    rectangle.setOptions(rectOptions);
  }
  

function timedPing(markerId){
  var marker = markers[markerId];
  var lng = marker.getPosition().lng();
  var lat = marker.getPosition().lat();
  //console.log("user:" + marker.username + " lng:" + lng + " lat:" + lat);

  t=setTimeout("timedPing(" + markerId +")",10000);

  $.getJSON('EventTrackerServlet',  {pUser : marker.username, pLat: lat, pLng: lng, pEventId: marker.eventid}, function(data) {
		//console.log(data);
		
		marker.events = [];
		
		if (data.canCreateEvent == true){
			marker.canCreate = true;
		}else{
		  marker.canCreate = false;
	  	}
	 	
		$.each(data.events, function(key, event) {
		  if (event.id != null && event.id != marker.eventid){
		  	marker.events.push(event);
		  }
		});
      	
		updateInfoWindow(markerId);
  });
  
 }

  function addUserMarker(latlng){
	  var marker = new google.maps.Marker({
		    position: latlng,
		    map: map,
		    draggable:true,
		    username: null,
		    canCreate: false,
		    events: new Array(),
		    eventid: null,
		    eventname : null,
		    infoWindow: new google.maps.InfoWindow({maxWidth: 10})
		  });
	  

	  markers[markerCounter] = marker;
	  updateInfoWindow(markerCounter);
	  markerCounter++;
	  
	  marker.setMap(map); //needed after constructer?
	
  	  google.maps.event.addListener(marker, "click", function() {
          marker.infoWindow.open(map, marker);
    	});
    }
  
  function updateInfoWindow(markerId){
	  var marker = markers[markerId];
	  var html = "";
	  if (marker.username == null && document.getElementById("newUserF" + markerId) == null){
		  html = "<form id=\"newUserF" + markerId+ "\">"+
 	      "<input type='text' id='username' placeholder='username'/> <br/><br/>"+
 	      "<input type='text' id='name' placeholder='name'/> <br/><br/>" +
 	      "<input type='text' id='pwd' placeholder='password'/><br/><br/>" +
	      "<input type='hidden' id='markerId' value='" + markerId + "'/>" +
 	      "<input type='button' class='button' value='Create New User' onclick='saveData(newUserF" + markerId + ")'/>"+
 			"</form>";
	  } 
	  else{
		  if (marker.canCreate == true && marker.eventid == null && document.getElementById("newEventF" + markerId) == null){
		  	html += "<form id=\"newEventF" + markerId + "\">"+
	 	      "<input type='text' id='eventname' placeholder='eventname'/> <br/><br/>"+
		      "<input type='hidden' id='markerId' value='" + markerId + "'/>" +
	 	      "<input type='button' class='button' value='Create New Event' onclick='createNewEvent(newEventF" + markerId + ")'/>"+
	 			"</form>"
		  }
		  
		 if (marker.events.length > 0 && marker.eventid == null && document.getElementById("joinEventF" + markerId) == null){
		 	 html += "<form class='joinF' id=\"joinEventF" + markerId + "\">" + "<select id='events'>" +
		 		"<option value='none'>Join Existing Event</option>";
         
          		for(var i = 0; i < marker.events.length; i++){
          		html += "<option value='" + marker.events[i].id + "'>" +  marker.events[i].name + "</option>";
          		}
          
          		html += "</select><br/><br/>" +
	      		"<input type='hidden' id='markerId' value='" + markerId + "'/>" +
	      		"<input type='button' class='button' value='Join Event' onclick='joinEvent(joinEventF" + markerId + ")'/></form>";
	      	}
		 }
	
	if (html != ""){
	marker.infoWindow.setContent(html);
    marker.infoWindow.open(map, marker);
	}
    
    google.maps.event.addListener(marker, "click", function() {
        marker.infoWindow.open(map, marker);
  });
  }
  
  function saveData(form) { 
	  //console.log(form.markerId.value);
	  var markerIdNum = parseInt(form.markerId.value);
	  var marker = markers[markerIdNum];

      $.post('EventTrackerServlet', {mUser : form.username.value, mName: form.name.value, mPass: form.pwd.value}, function(data) { 
          	//console.log(data);
      		$('#message').html("User" + data + "Added");
            marker.username = data;            
            marker.infoWindow.close();
            marker.setTitle(marker.username);
            timedPing(markerIdNum);
            
            marker.infoWindow.setContent("<h4>Welcome,  " + marker.username + "</h4>");
            
    	});
    }
  
  function createNewEvent(form) { 
	  //console.log(form.markerId.value);
	  var markerIdNum = parseInt(form.markerId.value);
	  var marker = markers[markerIdNum];
	  var lng = marker.getPosition().lng();
	  var lat = marker.getPosition().lat();
	  
	  //console.log("user:" + marker.username + " lng:" + lng + " lat:" + lat + "new event:" + form.eventname.value);

	  $.post('EventTrackerServlet',  {cUser : marker.username, cLat: lat, cLng: lng, cEventName: form.eventname.value}, function(data) {
		  //console.log(data);
		   marker.eventid = data.id;
		   //console.log(data.id);
		   marker.eventname = data.name;
		   marker.infoWindow.close();
		   marker.infoWindow.setContent("<h4>You just joined " + marker.eventname + "</h4>");
		
	  });
    }
  
  function joinEvent(form) { 
	  //console.log(form.markerId.value);
	  var markerIdNum = parseInt(form.markerId.value);
	  var marker = markers[markerIdNum];
	  //console.log("Selected:" + form.events.selectedIndex);
	  //console.log(marker.events);
	  marker.eventid = marker.events[form.events.selectedIndex-1].id;
	  marker.eventname = marker.events[form.events.selectedIndex-1].name;
	  marker.events = [];
	  marker.infoWindow.close();
	  marker.infoWindow.setContent("<h4>You just joined " + marker.eventname + "</h4>");
    }
	 
</script>
<body onload="initialize()">
	<div id="header_wrapper">
		<h1>Event Tracker Demo</h1>
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
	<div id="message"></div>
</body>
</html>