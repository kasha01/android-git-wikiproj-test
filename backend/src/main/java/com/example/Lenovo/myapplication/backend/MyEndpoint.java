/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.example.Lenovo.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.utils.SystemProperty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "myApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.Lenovo.example.com",
                ownerName = "backend.myapplication.Lenovo.example.com",
                packagePath = ""
        )
)
public class MyEndpoint {
    private Connection con;
    private Statement st;
    private ResultSet rs;


    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name) {
        MyBean response = new MyBean();
        response.setData("Hiiiii, " + name);

        return response;
    }

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "sayWiki")
    public MyBean sayWiki() throws SQLException {
        MyBean response = new MyBean();
        String line = "From_";

        String url = null;
        try {
            if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
                line = line + "PORDUCTION_";
                Class.forName("com.mysql.jdbc.GoogleDriver");
                url = "jdbc:google:mysql://mytestproject-1812:mysql56/test?user=root";
            } else {
                line = line + "DEV_";
                // Local MySQL instance to use during development.
                Class.forName("com.mysql.jdbc.Driver");
                //con = DriverManager.getConnection("jdbc:mysql://localhost:3306/TEST", "root", "isql1");
                url = "jdbc:mysql://localhost:3306/TEST?user=root&password=isql1";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return response;
        }

        try {
            con = DriverManager.getConnection(url);
            st = con.createStatement();
            rs = st.executeQuery("select Tag from wikibubbletag where TagId = 22");
            while (rs.next()) {
                line = line + rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setData("SQL Error");
        }
        finally {
            con.close();
        }

        response.setData(line);
        return response;
    }
}
