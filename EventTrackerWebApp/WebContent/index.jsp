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
  var events;
  
  
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

/*   function timedPing(markerId){

  marker = markers[markerId];
  t=setTimeout("timedPing(markerId)",1000);

  $.getJSON('EventTrackerServlet', function(data) { //add parameters to pass (lat, lng, eventid, username)
	$.each(data, function(i, event) {
		if (event.id == null && event.location == null){
			marker.canCreate = true;
		}
		else{
      	  marker.events.push(event);
		}      
	});
  
	  updateInfoWindow(markerCounter);
  });
  
  } */

  function addUserMarker(latlng){
	  var marker = new google.maps.Marker({
		    position: latlng,
		    map: map,
		    draggable:true,
		    username: null,
		    canCreate: false,
		    events: null,
		    eventid: null
		  });
	  
	  markers[markerCounter] = marker;
	  updateInfoWindow(markerCounter);
	  markerCounter++;
	  
	  marker.setMap(map); //needed after constructer?
    }
  
  function updateInfoWindow(markerId){
	  marker = markers[markerId];
	  var html;
	  var infoWindow = new google.maps.InfoWindow();
	  
	  if (marker.username == null){
		  html = "<form id='newUser'><table>" +
	      "<tr><td>Username:</td> <td><input type='text' id='username'/> </td> </tr>" +
	      "<tr><td>Name:</td> <td><input type='text' id='name'/> </td> </tr>" +
	      "<tr><td>Password:</td> <td><input type='text' id='pwd'/> </td> </tr>" +
	      "<input type='hidden' id='markerId' value='" + markerId + "'/>" +
	      "<tr><td></td><td><input type='button' value='Save' onclick='saveData(newUser)'/></td></tr>"+
	      + "</table></form>";
	  }
	  
	  else if (marker.canCreate == false && marker.events.length > 0){
		  html = "<table>" +
	      "<tr><td>Type:</td> <td><select id='joinEvent'>" +
          "<option value='none' SELECTED>none</option>"
         
          for(var i = 0; i < marker.events.length(); i++){
          	html += "<option value='" + marker.events[i].id + "'>" +  marker.events[i].name + "</option>"
          }
          
          html += "</select> </td></tr>" +
	      "<input type='hidden' id='markerId' value='" + markerId + "'/>" +
	      "<tr><td></td><td><input type='button' value='Create New Event' onclick='saveEvent(markerId)'/></td></tr>";
	  }
	  else if (marker.canCreate == true){
		  html = "<table>" +
	      "<tr><td>Event Name:</td> <td><input type='text' id='eventname'/> </td> </tr>" +
	      "<input type='hidden' id='markerId' value='" + markerId + "'/>" +
	      "<tr><td></td><td><input type='button' value='Create New Event' onclick='createEvent(markerId)'/>" +
	      "</td></tr>";
	  }
	  
	google.maps.event.addListener(marker, "click", function() {
          infoWindow.setContent(html);
          infoWindow.open(map, marker);
    });
  }
  
  function saveData(form) { 
	  console.log(form.markerId.value);
	  var marker = markers[parseInt(form.markerId.value)];

      $.post('EventTrackerServlet', {mUser : form.username.value, mName: form.name.value, mPass: form.pwd.value}, function(data) { //add form name to pass or the parameters
    	    marker.closeInfoWindow();
            document.getElementById("message").innerHTML = "User" + data + "Added";
            console.log(data);
            //set marker username
            timedPing(markerId);
    	});
    }
	 
   
/*   function addUser(form){
	  console.log(form);
	  var lat = form.lat.value;
	  var lon = form.lon.value;
	  var point = new google.maps.LatLng(form.lat.value, form.lon.value);
	  addMarker(point, "You are here!", "userMarker");
	
	  timedPing(form.username.value, lat, lon);
  } */

</script>
	<body onload="initialize()">
		<h1>Event Tracker</h1>

		<div id="map_canvas" style="width:500px; height:500px"></div>	
<!-- 		 <div id="message"></div>
		<div id=event_list></div> -->
	</body>
</html>