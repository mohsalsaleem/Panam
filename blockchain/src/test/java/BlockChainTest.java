import moh.sal.blockchain.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockChainTest {
    Blockchain blockchain;
    List<User> users;
    List<Account> accounts;
    Transactions transactions = new Transactions();

    @Before
    public void initialize() {
        blockchain = new Blockchain();
        accounts = new ArrayList<Account>();
        users = new ArrayList<User>() {
         @Override
         public boolean contains(Object o) {
             if(o instanceof User) {
                 for (User user: this) {
                    if(user.id == ((User) o).id) {
                        return true;
                    }
                 }
             }
             return false;
         }
        };
    }

    @Test
    public void checkBlockchainEmptyness() {
        Assert.assertEquals(0, this.blockchain.size());
    }

    @Test
    public void createUser() {
        int userIndex = this.users.size();
        User user = new User("Saleem", userIndex);

        Assert.assertEquals(user.name, "Saleem");
        Assert.assertEquals(user.id,userIndex);
    }

    @Test
    public void addUsers() {
        for(int i = 0; i < 5; i++) {
            int userIndex = this.users.size();
            User user = new User("Saleem" + i, userIndex);

            this.users.add(user);

            Assert.assertEquals(this.users.get(userIndex).id, user.id);
            Assert.assertEquals(this.users.get(userIndex).name, "Saleem" + i);
        }
        Assert.assertEquals(5, this.users.size());
    }

    @Test
    public void createAccounts() {
        for (User user: users) {
            Account account = new Account(user, 1000);
            accounts.add(account);
        }

        for (Account account: accounts) {
            Assert.assertEquals(true, users.contains(account.user));
        }
    }

    @Test
    public void createTransactions() throws Exception {
        Random random = new Random();
        for(int i = 0; i < this.accounts.size(); i++) {

            Account from = accounts.get(i);
            Account to = (i == this.accounts.size() - 1 ? accounts.get(0) : accounts.get(i + 1));

            Account fromBeforeTransaction = new Account(from.user, from.balance);
            Account toBeforeTransaction = new Account(to.user, to.balance);

            double amountToTransfer = random.nextInt(100 - 1 + 1) + 1;

            Transaction transaction = new Transaction(from, amountToTransfer, to);
            if(transaction.transact()) {
                transactions.add(transaction);
            }
            Assert.assertEquals(true, from.balance != fromBeforeTransaction.balance && to.balance != toBeforeTransaction.balance);
        }
    }

    @Test
    public void createBlockchain() {
        Block block = new Block(this.transactions);
        Blockchain blockchain = new Blockchain();
        blockchain.add(block);
        Assert.assertEquals(1, blockchain.size());
    }
}
