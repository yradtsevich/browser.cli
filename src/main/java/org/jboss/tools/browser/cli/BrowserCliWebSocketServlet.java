package org.jboss.tools.browser.cli;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

/**
 * @author Yahor Radtsevich (yradtsevich)
 */
public class BrowserCliWebSocketServlet extends WebSocketServlet {

	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
		return new BrowserCliWebSocket(getServletContext());
	}
}
