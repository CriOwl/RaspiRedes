package com.smarts.Comunications;
import javax.websocket.*;

@ClientEndpoint
public class SocketsS {
    
    private Session session;
    
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("open");
        this.session = session;
    }
    
    @OnMessage
    public void onMessage(String message) {
        System.out.println("Mensaje recibido: " + message);
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error: " + throwable.getMessage());
    }
    
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Conexión cerrada: " + reason);
    }
    
    public void sendMessage(String msg) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(msg);
        } else {
            System.out.println("No hay sesión activa para enviar mensajes");
        }
    }
}