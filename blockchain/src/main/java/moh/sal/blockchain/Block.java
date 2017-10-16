package moh.sal.blockchain;

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
    public double nonce;

    public Block(Transactions transactions) {
        this.transactions = transactions;
    }

    /**
     * Get Hash code
     * @return String
     */
    public String getHash() {
        return hash;
    }

    /**
     * Set hash
     * @param hash
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * Get the previous block's hash
     * @return
     */
    public String getPreviousHash() {
        return previousHash;
    }

    /**
     * Set previous block's hash
     * @param previousHash
     */
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public Transactions getTransactions() {
        return transactions;
    }

    public void setTransactions(Transactions transactions) {
        this.transactions = transactions;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getNonce() {
        return nonce;
    }

    public void setNonce(double nonce) {
        this.nonce = nonce;
    }
}
