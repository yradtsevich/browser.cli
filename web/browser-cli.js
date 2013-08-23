/**
 * Connects to ws://localhost:8087/cli and listens to the comands sent 
 * from org.jboss.tools.browser.cli.BrowserCliWebSocket.
 * 
 * @author Yahor Radtsevich (yradtsevich)
 */
(function() {
	var websocket = new WebSocket("ws://localhost:8087/cli");
	websocket.onmessage = function(evt) {
		var request = evt.data;

		var separatorIndex = request.indexOf(';');
		var id = request.substr(0, separatorIndex);
		var command = request.substr(separatorIndex + 1);
		var result;
		try {
			result = window.eval(command);
		} catch (e) {
			result = "Error: " + e.message; 
		}

		websocket.send(id + ';' + result);
	};
}());
