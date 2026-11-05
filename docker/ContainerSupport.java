import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.sql.*;
import java.util.Properties;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/** Container-only configuration; both Spring and legacy ConfigUtils read the same JDBC file. */
public final class ContainerSupport {
    public static void main(String[] args) throws Exception {
        if (args.length == 1 && "health".equals(args[0])) {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://127.0.0.1:8080/login.shtml").openConnection();
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.setInstanceFollowRedirects(false);
            try {
                if (connection.getResponseCode() != 200) System.exit(1);
            } finally { connection.disconnect(); }
            return;
        }
        Path classes = Paths.get("/usr/local/tomcat/webapps/ROOT/WEB-INF/classes");
        String password = System.getenv("IEMES_DB_PASSWORD");
        if (password == null || password.isEmpty()) throw new IllegalArgumentException("IEMES_DB_PASSWORD is required");
        Path jdbcFile = classes.resolve("jdbc.properties");
        Properties jdbc = read(jdbcFile);
        jdbc.setProperty("jdbc.driverClass", "com.mysql.cj.jdbc.Driver");
        jdbc.setProperty("jdbc.url", "jdbc:mysql://db:3306/iemes_v1?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false");
        jdbc.setProperty("jdbc.username", "iemes");
        jdbc.setProperty("jdbc.password", password);
        write(jdbcFile, jdbc);
        Path config = classes.resolve("config.properties");
        Path saved = Paths.get("/var/lib/iemes/config.properties");
        Files.createDirectories(saved.getParent());
        if (!Files.exists(saved)) {
            Properties initial = read(config);
            // Do not send local development alerts to historical recipients.
            initial.setProperty("fromEmail", "iemes@example.test");
            initial.setProperty("toEmail", "operator@example.test");
            initial.setProperty("emailName", "");
            initial.setProperty("emailPassword", "");
            write(saved, initial);
        }
        Files.copy(saved, config, StandardCopyOption.REPLACE_EXISTING);
        replaceSeedAdminPassword(jdbc, System.getenv("IEMES_ADMIN_PASSWORD"));
    }
    private static void replaceSeedAdminPassword(Properties jdbc, String password) throws Exception {
        if (password == null || password.length() < 16 || "change-this-local-admin-password".equals(password)) {
            throw new IllegalArgumentException("Set a unique IEMES_ADMIN_PASSWORD of at least 16 characters in .env");
        }
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(jdbc.getProperty("jdbc.url"), jdbc.getProperty("jdbc.username"), jdbc.getProperty("jdbc.password"));
             PreparedStatement select = connection.prepareStatement("SELECT password FROM ly_user WHERE id = 3 AND accountName = 'admin'")) {
            try (ResultSet result = select.executeQuery()) {
                if (!result.next() || !"f397cd3b3e73b0b735d040b323af4081".equals(result.getString(1))) return;
            }
            String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
            String hash = new SimpleHash("md5", password, ByteSource.Util.bytes("admin" + salt), 2).toHex();
            try (PreparedStatement update = connection.prepareStatement("UPDATE ly_user SET password = ?, credentialsSalt = ? WHERE id = 3 AND accountName = 'admin' AND password = ?")) {
                update.setString(1, hash);
                update.setString(2, salt);
                update.setString(3, "f397cd3b3e73b0b735d040b323af4081");
                update.executeUpdate();
            }
        }
    }
    private static Properties read(Path file) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(file)) { properties.load(input); }
        return properties;
    }
    private static void write(Path file, Properties properties) throws IOException {
        try (OutputStream output = Files.newOutputStream(file)) { properties.store(output, "IE-MES container configuration"); }
    }
}
