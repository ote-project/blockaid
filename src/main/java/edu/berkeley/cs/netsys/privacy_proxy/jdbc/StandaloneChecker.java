package edu.berkeley.cs.netsys.privacy_proxy.jdbc;

import java.sql.SQLException;
import java.util.Properties;

public class StandaloneChecker implements AutoCloseable {
    private final PrivacyConnection conn;

    private StandaloneChecker(PrivacyConnection conn) {
        this.conn = conn;
    }

    public boolean check(String query) throws SQLException {
        return conn.checkIndividualQuery(query);
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static StandaloneChecker create(String url, String user, String password) throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        PrivacyConnection conn = (PrivacyConnection) new PrivacyDriver().connect(url, props);
        if (conn == null) {
            throw new SQLException("URL not accepted: " + url);
        }
        return new StandaloneChecker(conn);
    }
}
