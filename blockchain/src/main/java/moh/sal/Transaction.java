package moh.sal;

public class Transaction {
    /**
     * From account
     */
    public Account from;

    /**
     * Sent amount
     */
    public double amountToSend;

    /**
     * To Account
     */
    public Account to;

    public Transaction(Account from, double amountToSend, Account to) {
        this.from = from;
        this.amountToSend = amountToSend;
        this.to = to;
    }

    public boolean transact() throws Exception {
        if(from.balance <= 0) {
            throw new Exception("Invalid amount in account " + from.user.name);
        }
        from.balance = from.balance - amountToSend;
        to.balance = to.balance + amountToSend;
        return true;
    }
}
