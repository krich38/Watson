import org.junit.Assert;
import org.junit.Test;
import org.watson.protocol.io.IncomingMessage;

import java.util.Arrays;

/**
 * @author Kyle Richards
 * @version 1.0
 */
public class TestMessageParse {
    @Test
    public void testMessageParse() {
        String msg = "Watson login username password";
        Assert.assertTrue(verify(msg, "username", "password"));
        Assert.assertFalse(verify(msg, "shouldntfind", "eitherofthese"));
        msg = "Watson login username password some random text more";
        Assert.assertTrue(verify(msg, "username", "password"));
    }

    public boolean verify(String msg, String username, String password) {
        String[] parts = msg.split(" ");
        System.out.println(Arrays.toString(parts));
        if (parts.length > 3) {

            if (parts[1].equals("login")) {
                final String text = msg.substring(msg.indexOf(parts[1]));

                parts = text.split(" ");
                String user = parts[1];
                String pass = parts[2];
                System.out.println(username + " : " + password);

                return user.equals(username) && pass.equals(password);

            }

        }
        return false;

    }
}
