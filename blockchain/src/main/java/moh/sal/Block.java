package moh.sal;

public class Block {
    /**
     * Hash code of the current block
     */
    public String hash;

    /**
     * Hash code of previous block
     */
    public String previousHash;

    /**
     * Transactions happened in the current block
     */
    public Transactions transactions;

    /**
     * Index of current block
     */
    public int index;

    /**
     * Nonce of the current block
     */
    public int nonce;
}
