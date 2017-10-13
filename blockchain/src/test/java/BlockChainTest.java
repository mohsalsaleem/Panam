import moh.sal.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BlockChainTest {
    Blockchain blockchain;
    List<User> users;
    List<Account> accounts;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void initialize() {
        blockchain = new Blockchain();
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

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
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
        for (User user: this.users) {
            Account account = new Account(user, 1000);
            this.accounts.add(account);
        }

        System.out.println(this.accounts.size());

        for (Account account: this.accounts) {
            System.out.println(account.balance);
            Assert.assertEquals(true, this.users.contains(account.user));
        }
    }
}
