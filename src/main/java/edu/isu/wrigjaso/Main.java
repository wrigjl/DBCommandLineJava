
// This is a Java program that connects to a PostgreSQL database using JDBC.
// It requires a JSON file for database connection properties.
// The program creates a schema, a table, inserts data, and retrieves it.
// It also demonstrates the use of prepared statements to prevent SQL injection.

package edu.isu.wrigjaso;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Main {
    /**
     * Main method to run the application.
     * It establishes a connection to a PostgreSQL database using JDBC.
     * If the connection fails, it prints the error message and exits with status 1.
     * If successful, it prints the current working directory.
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Connection conn = null;

        try {
            conn = getDatabaseConnection();
            System.out.println("Connection established successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("Current directory: " + System.getProperty("user.dir"));
        DatabaseMetaData metaData = conn.getMetaData();
        System.out.println("Server: " + metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion());
        System.out.println("Driver: " + metaData.getDriverName() + " " + metaData.getDriverVersion());

        // Create the "example" schema if it doesn't already exist, and switch to it.
        conn.createStatement().execute("CREATE SCHEMA IF NOT EXISTS example");
        conn.createStatement().execute("SET search_path TO example");

        // Create a test table in the "example" schema, and ensure it is empty.
        conn.createStatement()
                .execute("CREATE TABLE IF NOT EXISTS test1 (id SERIAL PRIMARY KEY, name VARCHAR(50), age INTEGER)");
        conn.createStatement().execute("DELETE FROM test1");

        // The data here comes from wikipedia. Age here reflects the age of the actor
        // when they
        // started playing the role of Dr. Who.
        // https://en.wikipedia.org/wiki/The_Doctor
        insertUser(conn, "William Hartnell", 58);
        insertUser(conn, "Patrick Troughton", 49);
        insertUser(conn, "Jon Pertwee", 54);
        insertUser(conn, "Tom Baker", 47);
        insertUser(conn, "Peter Davison", 32);
        insertUser(conn, "Colin Baker", 43);
        insertUser(conn, "Sylvester McCoy", 46);
        insertUser(conn, "Paul McGann", 36);
        insertUser(conn, "Christopher Eccleston", 41);
        insertUser(conn, "David Tennant", 38);
        insertUser(conn, "Matt Smith", 31);
        insertUser(conn, "Peter Capaldi", 59);
        insertUser(conn, "Jodie Whittaker", 40);
        insertUser(conn, "David Tennant", 52);
        insertUser(conn, "Ncuti Gatwa", 32);

        PreparedStatement ps = conn.prepareStatement("SELECT id, name, age FROM test1");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            int age = rs.getInt("age");
            System.out.println("Row: id=" + id + ", name=" + name + ", age=" + age);
        }

        ps = conn.prepareStatement("SELECT MAX(age) AS max_age FROM test1");
        rs = ps.executeQuery();
        if (rs.next()) {
            int maxAge = rs.getInt("max_age");
            System.out.println("Maximum age of starting Dr. Who: " + maxAge);
        }

        ps = conn.prepareStatement("SELECT age from test1 WHERE name = ?");
        ps.setString(1, "Ncuti Gatwa");
        rs = ps.executeQuery();
        if (rs.next()) {
            int age = rs.getInt("age");
            System.out.println("Mr. Gatwa's age when starting Dr. Who: " + age);
        }

        conn.close();
    }

    private static void insertUser(Connection conn, String name, int age) throws SQLException {
        // Prepared statements are used to avoid SQL injection attacks.
        // You should NEVER use string concatenation to build SQL queries when user
        // input is involved.
        PreparedStatement ps = conn.prepareStatement("INSERT INTO test1 (name, age) VALUES (?, ?)");
        ps.setString(1, name);
        ps.setInt(2, age);
        ps.executeUpdate();

        // NOTE: when doing many inserts, you should use batch processing for
        // performance.
        // Singleton inserts like this are fine for small amounts of data.
        ps.close();
    }

    static Connection getDatabaseConnection() throws SQLException {
        Properties props = new Properties();

        String filePath = "dbconnection.json";
        String url = "jdbc:postgresql://";
        String host = "localhost";
        String database = null;
        try {
            String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));
            org.json.JSONObject jsonObject = new org.json.JSONObject(content);

            // this is not optional, you must use SSL.
            props.put("sslmode", "require");

            try {
                props.put("user", jsonObject.getString("username"));
            } catch (org.json.JSONException e) {
            }
            ;
            try {
                props.put("password", jsonObject.getString("password"));
            } catch (org.json.JSONException e) {
            }
            ;

            try {
                database = jsonObject.getString("database");
            } catch (org.json.JSONException e) {
                try {
                    database = jsonObject.getString("username");
                } catch (org.json.JSONException e2) {
                    // If neither "database" nor "db" is found, we will exit.
                    System.err.println("Database name not found in JSON file.");
                    System.exit(1);
                }
            }
            try {
                host = jsonObject.getString("hostname");
            } catch (org.json.JSONException e) {
                host = "localhost";
            }
        } catch (Exception e) {
            System.err.println("Error reading database connection properties: " + e.getMessage());
            System.exit(1);
        }

        Connection conn = DriverManager.getConnection(url + host + "/" + database, props);
        return conn;
    }
}