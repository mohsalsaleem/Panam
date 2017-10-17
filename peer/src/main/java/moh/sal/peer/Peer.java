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

public class Peer extends AbstractVerticle {
    private Blockchain blockchain = new Blockchain();

    private final HashMap<String, String> OPTIONS;
    private HttpClient httpClient;
    private List<PeerEntry> peerEntries = new ArrayList<>();
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
        vertx.createHttpServer().websocketHandler(serverWebSocket -> {
            if(!serverWebSocket.path().equals("/ws")) {
                serverWebSocket.reject();
            } else {
                serverWebSocket.frameHandler(webSocketFrame -> {
                    try {
                        handleMessage(serverWebSocket);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
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

    private void handleMessage(ServerWebSocket serverWebSocket) throws InvalidProtocolBufferException {
        serverWebSocket.frameHandler(webSocketFrame -> {
            if (webSocketFrame.isBinary() && webSocketFrame.isFinal()) {
                Buffer buffer = webSocketFrame.binaryData();
                byte[] arr = new byte[buffer.length()];
                buffer.getBytes(arr);

                Message message = null;
                try {
                    message = Message.parseFrom(arr);
                    switch (message.getType()) {
                        case MessageTypes.ADD_PEER:
                            PeerEntry peerEntry = new PeerEntry(message.getAddPeerMessage().getPort(), message.getAddPeerMessage().getIp());
                            peerEntries.add(peerEntry);
                        case MessageTypes.SEND_BLOCKS:
                            byte[] bytes = ObjectSerializer.serialize(this.blockchain);
                            Message.Blockchain blockchain = Message.Blockchain
                                    .newBuilder()
                                    .setBlockchain(ByteString.copyFrom(bytes))
                                    .build();

                            Message messageBuilder = Message
                                    .newBuilder()
                                    .setType(MessageTypes.SENDING_BLOCKS)
                                    .setBlockchainMessage(blockchain)
                                    .build();
                            serverWebSocket.writeFinalBinaryFrame(Buffer.buffer(messageBuilder.toByteArray()));
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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

    private class PeerEntry {
        int port;
        String ip;
        static final String PATH = "/ws";

        PeerEntry(int port, String ip) {
            this.port = port;
            this.ip = ip;
        }

        PeerEntry(String port, String ip) {
            this.port = Integer.parseInt(port);
            this.ip = ip;
        }

        @Override
        public boolean equals(Object object) {
            if(object instanceof PeerEntry) {
                PeerEntry peerEntry = (PeerEntry) object;
                return Objects.equals(peerEntry.ip, this.ip) && peerEntry.port == this.port;
            }
            return false;
        }
    }
}
