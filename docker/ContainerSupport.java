import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.Properties;

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
            initial.setProperty("fromEmail", "");
            initial.setProperty("toEmail", "");
            initial.setProperty("emailName", "");
            initial.setProperty("emailPassword", "");
            write(saved, initial);
        }
        Files.copy(saved, config, StandardCopyOption.REPLACE_EXISTING);
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
