package com.iemes.util;

import junit.framework.TestCase;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class PropertiesUtilsTest extends TestCase {
    public void testApplicationPropertiesAreAvailable() {
        assertNotNull(PropertiesUtils.getProperties());
        assertNotNull(PropertiesUtils.getProperties().getProperty("cpu"));
    }

    public void testJdbcPropertiesAreAvailable() {
        assertNotNull(PropertiesUtils.getjdbcProperties());
        assertNotNull(PropertiesUtils.getjdbcProperties().getProperty("jdbc.url"));
    }

    public void testPersistentConfigFileReadAndWrite() throws Exception {
        Path config = Files.createTempFile("iemes-config-", ".properties");
        String previous = System.getProperty("iemes.config.file");
        try {
            Files.write(config, "cpu=30\n".getBytes("ISO-8859-1"));
            System.setProperty("iemes.config.file", config.toString());
            assertEquals("30", PropertiesUtils.getProperties().getProperty("cpu"));
            PropertiesUtils.modifyProperties("cpu", "35");
            Properties stored = new Properties();
            try (java.io.InputStream input = Files.newInputStream(config)) {
                stored.load(input);
            }
            assertEquals("35", stored.getProperty("cpu"));
        } finally {
            if (previous == null) System.clearProperty("iemes.config.file");
            else System.setProperty("iemes.config.file", previous);
            Files.deleteIfExists(config);
        }
    }
}
