package org.univaq.swa.auleweb.aulewebrest.security;

import jakarta.ws.rs.core.UriInfo;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthHelpers {

    private static AuthHelpers instance = null;
    private final JWTHelpers jwt;

    Class c = Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/auleweb?serverTimezone=Europe/Rome", "aulewebsite", "aulewebpass");

    public AuthHelpers() throws SQLException, ClassNotFoundException {
        jwt = JWTHelpers.getInstance();
    }

    public boolean authenticateUser(String email, String password) throws NoSuchAlgorithmException, SQLException {
        try ( PreparedStatement stmt = con.prepareStatement("SELECT password FROM amministratore WHERE email=?")) {
            stmt.setString(1, email);

            try ( ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return checkHashSHA(password, rs.getString(1));
            }

        }
    }

    public String issueToken(UriInfo context, String email) {
        return jwt.issueToken(context, email);
    }

    public void updateToken(String email, String token) throws SQLException {
        try ( PreparedStatement stmt2 = con.prepareStatement("UPDATE amministratore SET token=? WHERE email=?")) {
            stmt2.setString(1, token);
            stmt2.setString(2, email);
            stmt2.executeUpdate();
        }
    }

    public void revokeToken(String token) throws SQLException {

        try ( PreparedStatement stmt = con.prepareStatement("UPDATE amministratore SET token = NULL WHERE token=?")) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        }

    }

    public String validateToken(String token) {
        return jwt.validateToken(token);
    }

    //check the input hash with the password hash in the MySQL DB
    private boolean checkHashSHA(String input, String hash) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        String hashtext = no.toString(16);

        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }

        return hashtext.equals(hash);
    }

    public static AuthHelpers getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new AuthHelpers();
        }
        return instance;
    }

}
