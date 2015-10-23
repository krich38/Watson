import org.junit.Assert;
import org.junit.Test;
import org.watson.module.ServerProperties;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author Kyle Richards
 * @version 1.0
 */

public class TestConfigurationLoading {
    @Test
    public void testConfigLoading() {
        File[] files = new File("servers/").listFiles();
        try {
            Assert.assertTrue(files != null);
            Yaml yaml = new Yaml();
            for (File f : files) {

                ServerProperties server = yaml.loadAs(new FileInputStream(f), ServerProperties.class);


            }
            Assert.assertTrue(true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Assert.assertFalse(true);
        }

    }

}
