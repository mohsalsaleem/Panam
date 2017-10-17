package moh.sal.peer;

import io.vertx.core.Vertx;

import java.util.HashMap;

/**
 * @author mohsal
 */
public class Runner {

    public static HashMap<String, String> commandLineOptionsParser(String args[]) throws Exception {
        HashMap<String, String> options = new HashMap<>();
        if(args.length == 0) {
            throw new Exception("No options present.");
        }
        String[] optionsString = args[0].split(",");
        if(optionsString.length == 0) {
            throw new Exception("No options present");
        }

        for (String string: optionsString) {
            String[] option = string.split("=");
            if(option.length < 2) {
                throw new Exception("Malformed options");
            }
            options.put(option[0], option[1]);
        }
        return options;
    }

    public static void main(String[] args) {
        try {
            HashMap<String, String> options = commandLineOptionsParser(args);
            Vertx vertx = Vertx.vertx();
            vertx.deployVerticle(new Peer(options));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
