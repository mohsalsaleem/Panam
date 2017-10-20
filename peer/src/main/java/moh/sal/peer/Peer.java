package moh.sal.peer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.vertx.core.http.WebSocketFrame;
import moh.sal.blockchain.Blockchain;
import moh.sal.peer.communication.ObjectSerializer;
import moh.sal.peer.communication.PeerCom;
import moh.sal.peer.communication.PeerCom.*;
import moh.sal.utils.Serializer;

public class Peer extends AbstractVerticle {
    static Blockchain blockchain = new Blockchain();
    static List<PeerEntry> peerEntries = new ArrayList<>();

    private final HashMap<String, String> OPTIONS;
    private HttpClient httpClient;
    private Host me;

    Peer(HashMap<String, String> options) {
        this.OPTIONS = options;
        try {
            if(!OPTIONS.containsKey("HOST")) throw new RuntimeException("Host not present");
            me = setHost(this.OPTIONS.get("HOST"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() throws Exception {
        System.out.println("Starting..");
        httpClient = vertx.createHttpClient();
        vertx.createHttpServer().websocketHandler((ServerWebSocket serverWebSocket) -> {
            System.out.println("Connection received.");
            if(!serverWebSocket.path().equals("/ws")) {
                System.out.println("Connection request rejected.");
                serverWebSocket.reject();
            } else {
                serverWebSocket.frameHandler(new MessageHandler(serverWebSocket));

                serverWebSocket.closeHandler(closeHandler -> {
                   System.out.println("Closed");
                });
            }
        }).listen(me.port);

        if(OPTIONS.containsKey("PEERS")) {
            List<PeerEntry> peerEntryList = parsePeers(OPTIONS.get("PEERS"));
            for (PeerEntry peerEntry: peerEntryList) {
                httpClient.websocket(peerEntry.port, peerEntry.ip, PeerEntry.PATH, webSocket -> {
                    peerEntryList.add(peerEntry);
                    performInitialSteps(webSocket);
                }, throwable -> System.out.println("Could not connect to " + peerEntry.ip + ":" + peerEntry.port + throwable.getMessage()));
            }
            peerEntries.addAll(peerEntryList);
        }
    }

    private Host setHost(String hostString) throws Exception {
        String[] hostStringArray = hostString.split(":");
        if(hostStringArray.length != 2) {
            throw new Exception("Invalid host");
        }
        return new Host(hostStringArray[1], hostStringArray[0]);
    }

    private void performInitialSteps(WebSocket webSocket) {
        Message.AddPeer.Builder addPeerBuilder = Message.AddPeer.newBuilder();
        addPeerBuilder.setIp(me.ip);
        addPeerBuilder.setPath(PeerEntry.PATH);
        addPeerBuilder.setPort(me.port);

        Message.Builder messageBuilder = Message.newBuilder();
        messageBuilder.setType(MessageTypes.ADD_PEER);

        Message.AddPeer addPeer = addPeerBuilder.build();
        messageBuilder.setAddPeerMessage(addPeer);

        Message message = messageBuilder.build();
        byte[] messageBytes = message.toByteArray();
        webSocket.writeFinalBinaryFrame(Buffer.buffer(messageBytes));

        System.out.println(MessageTypes.ADD_PEER + " message sent.");
    }

    private List<PeerEntry> parsePeers(String peers) {
        List<PeerEntry> peerEntries = new ArrayList<>();
        if(peers.length() == 0) {
            return peerEntries;
        }
        String[] peerList = peers.split(",");
        for (String peer: peerList) {
            String[] singlePeer = peer.split(":");
            if(singlePeer.length > 0) {
                PeerEntry peerEntry = new PeerEntry(singlePeer[1], singlePeer[0]);
                peerEntries.add(peerEntry);
            }
        }
        return peerEntries;
    }

    private class Host {
        int port;
        String ip;

        Host (int port, String ip) {
            this.ip = ip;
            this.port = port;
        }

        Host (String port, String ip) {
            this.port = Integer.parseInt(port);
            this.ip = ip;
        }
    }
}
