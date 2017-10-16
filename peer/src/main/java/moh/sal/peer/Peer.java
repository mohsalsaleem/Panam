package moh.sal.peer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.ServerWebSocket;

import java.util.HashMap;

public class Peer extends AbstractVerticle {

    public final HashMap<String, String> OPTIONS;
    public static HashMap<String, ServerWebSocket> peers = new HashMap<>();
    private HttpClient httpClient;

    public Peer(HashMap<String, String> options) {
        this.OPTIONS = options;
    }

    @Override
    public void start() throws Exception {
        System.out.println("Starting..");
        httpClient = vertx.createHttpClient();
        vertx.createHttpServer().websocketHandler(serverWebSocket -> {
            System.out.println("Path: " + serverWebSocket.path());
            if(!serverWebSocket.path().equals("/ws")) {
                serverWebSocket.reject();
            } else {
                serverWebSocket.frameHandler(webSocketFrame -> {
                    System.out.println(webSocketFrame.textData());
                });
            }
        }).listen(Integer.parseInt(OPTIONS.get("PORT")));

        if(OPTIONS.containsKey("PEERS")) {
            httpClient.websocket(3001, "localhost", "/ws", webSocket -> {
               webSocket.handler(dataHandler -> {
                   System.out.println("Connected");
               });
            });
        }

    }
}
