package moh.sal.peer;

import java.util.Objects;

class PeerEntry {
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
