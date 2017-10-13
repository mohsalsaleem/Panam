package moh.sal;

public class Account {
    /**
     * The user this account belongs to.
     */
    public User user;

    /**
     * Balance in the account.
     */
    public double balance;

    public Account(User user, double balance) {
        this.user = user;
        this.balance = balance;
    }
}
