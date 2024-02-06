/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package caffeshopmanagementsystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import static oracle.net.aso.C05.e;

/**
 *
 * @author atepg
 */
public class database {

    public static Connection connectDB() {

        try {

            Class.forName("com.mysql.jdbc.Driver");

            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/cafe", "root", ""); //LINK YOUR DATABASE
            return connect;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
