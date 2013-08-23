package org.jboss.tools.browser.cli;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;

import org.eclipse.jetty.websocket.WebSocket;

/**
 * Sends expressions to the connected browser and synchronously returns the result of their evaluation.
 * 
 * @see BrowserCliWebSocket#evaluate(String, int)
 * @author Yahor Radtsevich (yradtsevich)
 */
public class BrowserCliWebSocket implements WebSocket.OnTextMessage {

	private Connection connection;
	private ServletContext context;
	private Map<String, BlockingQueue<String>> resultMap = new ConcurrentHashMap<>();

	public BrowserCliWebSocket(ServletContext context) {
		this.context = context;
	}
	
	@Override
	public void onOpen(Connection connection) {
		this.connection = connection;
		Set<BrowserCliWebSocket> browserCliWebSockets = getBrowserCliWebSocketsList();
		browserCliWebSockets.add(this);
		System.out.println("A client connected.");
	}

	@SuppressWarnings("unchecked")
	private Set<BrowserCliWebSocket> getBrowserCliWebSocketsList() {
		return (Set<BrowserCliWebSocket>) context.getAttribute("org.jboss.tools.browser.cli.BrowserCliWebSockets");
	}

	@Override
	public void onClose(int closeCode, String message) {
		Set<BrowserCliWebSocket> browserCliWebSockets = getBrowserCliWebSocketsList();
		browserCliWebSockets.remove(this);
		System.out.println("A client disconnected.");
	}

	@Override
	public void onMessage(String data) {
		int separatorIndex = data.indexOf(';');
		String id = data.substring(0, separatorIndex);
		String result = data.substring(separatorIndex + 1);
		
		BlockingQueue<String> resultQueue = resultMap.get(id);
		if (resultQueue != null) {
			resultQueue.offer(result);
		} else {
			System.err.println("Unknown/outdated message: " + data);
		}
	}

	public String evaluate(String expression, int timeout) throws IOException, InterruptedException {
		String id = Double.toString(Math.random());
		connection.sendMessage(id + ";" + expression);
		BlockingQueue<String> resultQueue = new ArrayBlockingQueue<>(1);
		resultMap.put(id, resultQueue);
		try {
			String result = resultQueue.poll(timeout, TimeUnit.MILLISECONDS);
			return result;
		} finally {
			resultMap.remove(id);
		}
	}
}
