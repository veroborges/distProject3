<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
<style type="text/css">
  html { height: 100% }
  body { height: 100%; margin: 0; padding: 0 }
  #map_canvas { height: 100%; margin-top: 50px;}
</style>
<script type="text/javascript"
    src="http://maps.googleapis.com/maps/api/js?key=AIzaSyDAJrNEEEeiN-_qU	KKhbcLjuCHAkv4VSoU&sensor=true">
</script>

<script src="http://code.jquery.com/jquery-latest.js"></script>
<script type="text/javascript">
  var map;
  var markers;
  var markerCounter;  
  
  function initialize() {
    var latlng = new google.maps.LatLng(40.4468314, -79.9479933);
    var myOptions = {
      zoom: 8,
      center: latlng,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById("map_canvas"),
        myOptions);

    markers =new Array();
    markerCounter = 0;
	
    google.maps.event.addListener(map, 'click', function(event) {
      addUserMarker(event.latLng);
    });
  }

function timedPing(markerId){
  var marker = markers[markerId];
  var lng = marker.getPosition().lng();
  var lat = marker.getPosition().lat();
  console.log("user:" + marker.username + " lng:" + lng + " lat:" + lat);

  t=setTimeout("timedPing(" + markerId +")",10000);

  $.getJSON('EventTrackerServlet',  {pUser : marker.username, pLat: lat, pLng: lng, pEventId: marker.eventid}, function(data) {
		console.log(data);
	  if (data.canCreateEvent == true){
			marker.canCreate = true;
		}
      
	  marker.events = data.events;   
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
		    events: null,
		    eventid: null,
		    infoWindow: new google.maps.InfoWindow()
		  });
	  
	  markers[markerCounter] = marker;
	  updateInfoWindow(markerCounter);
	  markerCounter++;
	  
	  marker.setMap(map); //needed after constructer?
    }
  
  function updateInfoWindow(markerId){
	  var marker = markers[markerId];
	  var html;
	  
	  if (marker.username == null){
		  html = "<form id='newUserF'>" +
	      "Username:<br/><input type='text' id='username'/> <br/><br/>" +
	      "Name:<br/><input type='text' id='name'/> <br/><br/>" +
	      "Password:<br/><input type='text' id='pwd'/><br/><br/>" +
	      "<input type='hidden' id='markerId' value='" + markerId + "'/>" +
	      "<input type='button' value='Save' onclick='saveData(newUserF)'/>"+
	      + "</form>";
	  }
	  else if (marker.canCreate == true){
		  html = "<form id='newEventF'>" +
	      "Event Name:<br/><input type='text' id='eventname'/><br/><br/>" +
	      "<input type='hidden' id='markerId' value='" + markerId + "'/>" +
	      "<input type='button' value='Create New Event' onclick='createEvent(newEventF)'/>" +
	      "</form>";
	  }else if (marker.canCreate == false && marker.events.length > 0){
		  html = "<form id='joinEventF'>" +
	      "Type:<select id='eventDrop'>" +
          "<option value='none' SELECTED>none</option>"
         
          for(var i = 0; i < marker.events.length(); i++){
          	html += "<option value='" + marker.events[i].id + "'>" +  marker.events[i].name + "</option>"
          }
          
          html += "</select><br/><br/>" +
	      "<input type='hidden' id='markerId' value='" + markerId + "'/>" +
	      "<input type='button' value='Create New Event' onclick='saveEvent(joinEventF)'/></form>";
	  }else{
		  html = marker.username;
	  }
	  
	google.maps.event.addListener(marker, "click", function() {
          marker.infoWindow.setContent(html);
          marker.infoWindow.open(map, marker);
    });
  }
  
  function saveData(form) { 
	  console.log(form.markerId.value);
	  var markerIdNum = parseInt(form.markerId.value);
	  var marker = markers[markerIdNum];

      $.post('EventTrackerServlet', {mUser : form.username.value, mName: form.name.value, mPass: form.pwd.value}, function(data) { 
          	console.log(data);
      		$('#message').html("User" + data + "Added");
            marker.username = data;            
            marker.infoWindow.close();
            marker.setTitle(marker.username);
            timedPing(markerIdNum);
    	});
    }
  function createEvent(form) { 
	  console.log(form.markerId.value);
	  var markerIdNum = parseInt(form.markerId.value);
	  var marker = markers[markerIdNum];
	  var lng = marker.getPosition().lng();
	  var lat = marker.getPosition().lat();
	  
	  console.log("user:" + marker.username + " lng:" + lng + " lat:" + lat + "new event:" + form.eventname.value);

	  $.post('EventTrackerServlet',  {cUser : marker.username, cLat: lat, cLng: lng, cEventName: form.eventname.value}, function(data) {
		   console.log(data);
		   updateInfoWindow(markerIdNum);
	  });
    }
	 
</script>
	<body onload="initialize()">
		<h1>Event Tracker</h1>

		<div id="map_canvas" style="width:500px; height:500px"></div>	
   		 <div id="message"></div>
	</body>
</html>