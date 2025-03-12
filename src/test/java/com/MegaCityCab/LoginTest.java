package com.MegaCityCab;
import com.megacitycab.dao.UserDAO;
import org.junit.Test;
import static org.junit.Assert.*;

public class LoginTest {

    UserDAO userDAO = new UserDAO();

    @Test
    public void testValidLogin() {
        assertTrue(userDAO.validateUser("tharusha1234", "tharusha1234"));
    }

    @Test
    public void testInvalidLogin() {
        assertFalse(userDAO.validateUser("askan", "askan@1234"));
    }
}
