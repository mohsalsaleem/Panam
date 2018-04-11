package moh.sal.peer;

import io.vertx.core.Vertx;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author mohsal
 */
public class Runner {

    public static HashMap<String, String> commandLineOptionsParser(String args[]) throws Exception {

        System.out.println(Arrays.toString(args));

        if(args.length == 0) {
            throw new Exception("No options present.");
        }
        HashMap<String, String> options = new HashMap<>();

        String[] host = args[0].split("=");
        if(host.length < 2) {
            throw new Exception("Malformed host options");
        }
        if(!host[0].equals("HOST")) {
            throw new Exception("First option needs to be HOST");
        }

        options.put("HOST", host[1]);

        if(args.length > 1) {
            String[] peers = args[1].split("=");
            if(peers.length < 2) {
                throw new Exception("Malformed host options");
            }
            if(!peers[0].equals("PEERS")) {
                throw new Exception("Second option needs to be PEERS");
            }

            options.put("PEERS", peers[1]);
        }

        return options;
    }

    public static void main(String[] args) {
        try {
            HashMap<String, String> options = commandLineOptionsParser(args);
            System.out.println("options");
            Vertx vertx = Vertx.vertx();
            vertx.deployVerticle(new Peer(options));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
