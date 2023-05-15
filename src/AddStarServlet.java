import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "AddStarServlet", urlPatterns = "/api/add-star")
public class AddStarServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {


        String star = request.getParameter("star");
        String year = request.getParameter("birth-year");



        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            String query = "select max(id) from stars;";


            // Perform the query
            PreparedStatement statement = conn.prepareStatement(query);

            ResultSet rs = statement.executeQuery();

            String maxId = "";
            while (rs.next()){
                maxId = rs.getString("max(id)");
            }

            int newMaxIdNum = Integer.parseInt(maxId.substring(2, maxId.length())) + 1;
            String newMaxId = "nm" + Integer.toString(newMaxIdNum);

            rs.close();
            System.out.println(1);
            String insertStar = "INSERT INTO stars values(?, ?, ?);";

            statement = conn.prepareStatement(insertStar);

            statement.setString(1, newMaxId);
            statement.setString(2, star);
            if (year == null || year == ""){
                statement.setNull(3, Types.INTEGER);
            }else{
                statement.setInt(3, Integer.parseInt(year));
            }


            System.out.println(2);
            statement.execute();
            System.out.println(3);

            statement.close();


            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */

    }
}
