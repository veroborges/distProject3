<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
<style type="text/css">
html {
	height: 100%
}

body {
	height: 100%;
	margin: 0;
	padding: 0
}

#map_canvas {
	height: 100%;
	margin-top: 50px;
}
</style>
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

  }
  
  function getHistory(username){
	console.log(username.value);
    $.getJSON('UserLocationsServlet', {username: username.value}, function(data) {
		console.log(data);
		$.each(data.events, function(key, event) {
		  if (event.id != null && event.id != marker.eventid){
		  	marker.events.push(event);
		  }
		});
      });
 }
  
</script>

<body onload="initialize()">
	<h1>Event Tracker</h1>
	<a href="index.jsp" >Main</a>
	<form id="userHForm">
 	<label for='username'>Username:</label><br/><input type='text' id='username'/> <br/><br/>"
 	<input type='button' value='Get User History' onclick='getHistory(userHForm.username)'/>"
 	</form>"
	<div id="map_canvas" style="width: 500px; height: 500px"></div>
</body>
</html>