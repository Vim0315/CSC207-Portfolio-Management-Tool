package entities.users;

import usecase.portfolio.Portfolio;
import org.junit.*;

import static org.junit.Assert.*;

public class TestUser {
    private User user;
    private Portfolio portfolio;

    @Before
    public void setUp() {
        user = new User("test");
        portfolio = new Portfolio();
    }

    @After
    public void tearDown() {
        user = null;
    }

    @Test(timeout = 50)
    public void testUserGetName() {
        assertEquals(user.getName(), "test");
    }

    @Test(timeout = 50)
    public void testUserSetName(){
        user.setName("Bob");
        assertEquals("Bob", user.getName());
    }

    @Test(timeout = 50)
    public void testUserSetPortfolio(){
        user.setUserPortfolio(portfolio);
        assert(user.getUserPortfolio().equals(portfolio));
    }

    @Test(timeout = 500)
    public void testisBanned(){
        user.setBanned(true);
        assert(user.isBanned());
    }


}