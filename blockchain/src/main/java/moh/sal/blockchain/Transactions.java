package moh.sal.blockchain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
public class Transactions extends ArrayList<Transaction> {
    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        String string = "";
        try {
            string = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return string;
    }
}
