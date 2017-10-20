package moh.sal.peer;

import com.google.protobuf.ByteString;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketFrame;
import moh.sal.blockchain.Blockchain;
import moh.sal.peer.communication.PeerCom;
import moh.sal.utils.Serializer;

import javax.xml.ws.handler.Handler;
import java.io.IOException;

public class MessageHandler implements io.vertx.core.Handler<WebSocketFrame> {
    private ServerWebSocket webSocket;

    public MessageHandler(ServerWebSocket webSocket) {
        this.webSocket = webSocket;
    }

    @Override
    public void handle(WebSocketFrame webSocketFrame) {
            System.out.println("Frame received.");
            if (webSocketFrame.isBinary() && webSocketFrame.isFinal()) {
                Buffer buffer = webSocketFrame.binaryData();
                byte[] arr = new byte[buffer.length()];
                buffer.getBytes(arr);

                PeerCom.Message message;
                try {
                    message = PeerCom.Message.parseFrom(arr);
                    System.out.println("Received message type: " + message.getType());
                    switch (message.getType()) {
                        case MessageTypes.ADD_PEER:
                            System.out.println("Request to add Peer: " + message.getAddPeerMessage().getIp() + ":" + message.getAddPeerMessage().getPort());
                            PeerEntry peerEntry = new PeerEntry(message.getAddPeerMessage().getPort(), message.getAddPeerMessage().getIp());
                            PeerCom.Message peerAddedMessage;
                            if(Peer.peerEntries.add(peerEntry)) {
                                peerAddedMessage = PeerCom.Message.newBuilder().setType(MessageTypes.ADD_PEER_REPLY_SUCCESS).build();
                            } else {
                                peerAddedMessage = PeerCom.Message.newBuilder().setType(MessageTypes.ADD_PEER_REPLY_FAILURE).build();
                            }
                            System.out.println("Peer added successfully");
                            this.webSocket.writeFinalBinaryFrame(Buffer.buffer(peerAddedMessage.toByteArray()));
                            break;
                        case MessageTypes.SEND_BLOCKS:
                            System.out.println("Request to send blocks");
                            byte[] bytes = Serializer.serialize(Peer.blockchain);
                            PeerCom.Message.Blockchain blockchain = PeerCom.Message.Blockchain
                                    .newBuilder()
                                    .setBlockchain(ByteString.copyFrom(bytes))
                                    .build();

                            PeerCom.Message messageBuilder = PeerCom.Message
                                    .newBuilder()
                                    .setType(MessageTypes.SENDING_BLOCKS)
                                    .setBlockchainMessage(blockchain)
                                    .build();
                            this.webSocket.writeFinalBinaryFrame(Buffer.buffer(messageBuilder.toByteArray()));
                            break;
                        case MessageTypes.ADD_PEER_REPLY_SUCCESS:
                            System.out.println("Peer added success message received.");
                            PeerCom.Message sendBlockMessage = PeerCom.Message
                                    .newBuilder()
                                    .setType(MessageTypes.SEND_BLOCKS)
                                    .build();
                            byte[] sendBlockMessageBytes = sendBlockMessage.toByteArray();

                            System.out.println(MessageTypes.SEND_BLOCKS + " message sent.");
                            this.webSocket.writeFinalBinaryFrame(Buffer.buffer(sendBlockMessageBytes));
                            break;
                        case MessageTypes.ADD_PEER_REPLY_FAILURE:
                            System.out.println("Failure to add peer");
                        case MessageTypes.SENDING_BLOCKS:
                            System.out.println("Received blocks");
                            ByteString byteString = message.getBlockchainMessage().getBlockchain();
                            byte[] bytes1 = new byte[byteString.size()];
                            byteString.copyTo(bytes1, 0);
                            Peer.blockchain = (Blockchain) Serializer.deserialize(bytes1);
                            System.out.println(Peer.blockchain.size());
                            break;
                        default:
                            System.out.println("Invalid Message");

                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
    }
}
