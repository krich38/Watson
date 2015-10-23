import org.junit.Assert;
import org.junit.Test;
import org.watson.core.handler.io.DatabaseAdapter;
import org.watson.module.user.UserAccess;

import javax.xml.crypto.Data;

/**
 * @author Kyle Richards
 * @version 1.0
 */
public class TestDatabaseAdapter {
    @Test
    public void testDatabaseAdapter() {
        if(DatabaseAdapter.establishConnection()) {
            UserAccess expected = UserAccess.FULL_USER;
            UserAccess returned = DatabaseAdapter.authenticateUser("Kyle", "yamaha38");
            Assert.assertEquals(expected, returned);
        }
    }
}
