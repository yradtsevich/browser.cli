package org.jboss.tools.browser.cli;

import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Main class, should be run as {@code java org.jboss.tools.browser.cli.App path/to/web}.
 * <P/>
 * Then go to {@link http://localhost:8087/todoExample/} in Chrome. And try to enter 
 * the following commands into the {@code App} terminal:
 * <code><pre>
 * listAngularPropertiesAndMethods('html>body>div:nth-of-type(1)>ul>li>span')
 * listAngularPropertiesAndMethods('html>body>div:nth-of-type(2)>ul>li>span')
 * </pre></code>
 * 
 *  @author Yahor Radtsevich (yradtsevich)
 */
public class App {
    public static void main(String[] args) throws Throwable {
    	Server server = new Server(8087);
    	
    	ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    	servletHandler.addServlet(BrowserCliWebSocketServlet.class, "/cli");
    	
    	Set<BrowserCliWebSocket> webSockets = new CopyOnWriteArraySet<>();
    	servletHandler.getServletContext().setAttribute("org.jboss.tools.browser.cli.BrowserCliWebSockets", webSockets);
    	
    	ResourceHandler resourceHandler = new ResourceHandler();
    	resourceHandler.setDirectoriesListed(true);
    	resourceHandler.setResourceBase(args.length == 0 ? "./web" : args[0]);
    	
    	HandlerList handlerList = new HandlerList();
    	handlerList.setHandlers(new Handler[]{servletHandler, resourceHandler});
    	server.setHandler(handlerList);
        server.start();
        
        try (Scanner in = new Scanner(System.in)) {
	        StringBuffer commandBuffer = new StringBuffer();
	        while (in.hasNextLine()) {
	        	String nextLine = in.nextLine();
	        	commandBuffer.append(nextLine);
	        	
	        	if (!nextLine.endsWith("\\")) {
	        		executeAndPrintResult(webSockets, commandBuffer.toString());
	        		commandBuffer = new StringBuffer(); // clear buffer
	        	} else {
	        		commandBuffer.setCharAt(commandBuffer.length() - 1, '\n');
	        	}
	        }
        }
        
        server.join();
    }

	private static void executeAndPrintResult(Set<BrowserCliWebSocket> webSockets, String command)
			throws InterruptedException {
		Iterator<BrowserCliWebSocket> webSocketsIterator = webSockets.iterator();
		if (webSocketsIterator.hasNext()) {
			BrowserCliWebSocket webSocket = webSocketsIterator.next(); // use only the first one
			
			try {
				long start = System.nanoTime();
				String result = webSocket.evaluate(command, 100);
				long stop = System.nanoTime();
				System.out.format("%s [computed in %.3fms]%n", result, (stop - start) / 1e6);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No clients connected.");
		}
	}
}
