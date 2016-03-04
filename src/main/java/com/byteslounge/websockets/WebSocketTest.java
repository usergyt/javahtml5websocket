package com.byteslounge.websockets;

import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpSession;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket")
public class WebSocketTest {
	
    private static final Set<WebSocketTest> connections = new CopyOnWriteArraySet<WebSocketTest>();
    private Session session;
	@OnMessage
    public void onMessage(String message, Session session) 
    	throws IOException, InterruptedException {
 		// Print the client message for testing purposes
 		
		// Send the first message to the client
		//session.getBasicRemote().sendText("This is the first server message");
		
		// Send 3 messages to the client every 5 seconds
		int sentMessages = 0;
		
	/*	while(sentMessages < 3){
			Thread.sleep(5000);
			session.getBasicRemote().
				sendText("This is an intermediate server message. Count: " 
					+ sentMessages+" 你说："+message);
			sentMessages++;
		}*/
  		session.getBasicRemote().sendText(new Date().toLocaleString());
		
		session.getBasicRemote().sendText(message);
		
    	WebSocketTest.broadCast(  message);

		// Send a final message to the client
		//session.getBasicRemote().sendText("This is the last server message");
    }
    /**
     * 发送或广播信息
     * 
     * @param message
     */
    private static void broadCast(String message) {
        for (WebSocketTest chat : connections) {
            try {
                synchronized (chat) {
                    chat.session.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                connections.remove(chat);
                try {
                    chat.session.close();
                } catch (IOException e1) {
                }
//                WebSockets.broadCast(String.format("System> %s %s", chat.nickName,
//                        " has bean disconnection."));
            }
        }
    }
    
	@OnOpen
    public void onOpen (Session session) {
		this.session=session;
		connections.add(this);
		
        System.out.println("Client connected"+session);
    }

    @OnClose
    public void onClose () {
    	System.out.println("Connection closed");
    }
}
