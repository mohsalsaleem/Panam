package moh.sal.blockchain;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Blockchain extends LinkedList<Block> {

    private static final String HASH_ALGORITHM = "SHA-256";

    public Blockchain() {
        super();
    }

    private String generateHash(Block block) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
        String data = block.index + block.transactions.toString() + block.previousHash + block.nonce;
        byte[] hashBytes = messageDigest.digest(data.getBytes());
        return DatatypeConverter.printHexBinary(hashBytes);
    }

    private double calculateNonce(Block block) throws NoSuchAlgorithmException {
        String hash;
        MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
        for(double i = 0; i < Double.MAX_VALUE; i++) {
            String data = block.index + block.transactions.toString() + block.previousHash + i;
            byte[] hashBytes = messageDigest.digest(data.getBytes());
            hash = DatatypeConverter.printHexBinary(hashBytes);
            if(hash.substring(0, 4).equals("0000")) {
                return i;
            }
        }
        return -1;
    }

    private String generate64Zeros() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 64 ; i++) {
            stringBuilder.append("0");
        }
        return stringBuilder.toString();
    }

    private Block genesisBlock(Block block) throws NoSuchAlgorithmException {
        block.index = 1;
        block.previousHash = generate64Zeros();
        block.nonce = calculateNonce(block);
        block.hash = generateHash(block);
        return block;
    }

    private Block signBlock(Block block) throws NoSuchAlgorithmException {
        block.index = this.size();
        block.previousHash = this.get(this.size() - 1).hash;
        block.nonce = calculateNonce(block);
        block.hash = generateHash(block);
        return block;
    }

    @Override
    public void addFirst(Block block) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addLast(Block block) {
        super.addLast(block);
    }

    @Override
    public boolean contains(Object object) {
        if(object instanceof Block) {
            Block block = (Block)object;
            for(Block _block: this) {
                if(_block.hash.equals(block.hash)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean add(Block block) {
        try {
            if(super.size() == 0) {
                block = genesisBlock(block);
            } else {
                block = signBlock(block);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return super.add(block);
    }

    @Override
    public boolean addAll(Collection<? extends Block> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Block> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Block set(int index, Block element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, Block element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        if(o instanceof Block) {
            Block block = (Block)o;
            for (int i = 0; i < this.size(); i++) {
                if(block.hash.equals(this.get(i).hash)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Block removeFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Block removeLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Block remove(int index) {
        throw new UnsupportedOperationException();
    }
}
