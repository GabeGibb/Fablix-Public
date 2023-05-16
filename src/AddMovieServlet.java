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

@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/add-movie")
public class AddMovieServlet extends HttpServlet {
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
        String title = request.getParameter("movie-title");
        String year = request.getParameter("movie-year");
        String director = request.getParameter("movie-director");
        String rating = request.getParameter("movie-rating");
        String star = request.getParameter("movie-star");
        String genre = request.getParameter("movie-genre");


        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            String query = "select name from stars where stars.name = ?";

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, star);
            ResultSet rs = statement.executeQuery();

            String dbStar = "";
            while (rs.next()){
                dbStar = rs.getString("name");
            }
            rs.close();


            //IF STAR NOT IN DATABASE
            if (dbStar == ""){
                query = "select max(id) from stars;";
                // Perform the query
                statement = conn.prepareStatement(query);
                rs = statement.executeQuery();
                String maxId = "";
                while (rs.next()){
                    maxId = rs.getString("max(id)");
                }
                rs.close();
                int newMaxIdNum = Integer.parseInt(maxId.substring(2, maxId.length())) + 1;
                String newMaxId = "nm" + Integer.toString(newMaxIdNum);


                String insertStar = "INSERT INTO stars values(?, ?, null);";
                statement = conn.prepareStatement(insertStar);

                statement.setString(1, newMaxId);
                statement.setString(2, star);
//                statement.setNull(3, Types.INTEGER);

                statement.execute();
            }

            query = "select name from genres where genres.name = ?";

            statement = conn.prepareStatement(query);
            statement.setString(1, genre);

            rs = statement.executeQuery();

            String dbGenre = "";
            while (rs.next()){
                dbGenre = rs.getString("name");
            }
            rs.close();

            //If genre not in database
            if (dbGenre == ""){
                String insertStar = "INSERT INTO genres (name) values(?);";
                statement = conn.prepareStatement(insertStar);
                statement.setString(1, genre);
                statement.execute();
            }

            query = "select * from movies " +
                    "where movies.title = ? " +
                    " and movies.year = ? " +
                    " and movies.director = ? ;";

            statement = conn.prepareStatement(query);
            statement.setString(1, title);
            statement.setInt(2, Integer.parseInt(year));
            statement.setString(3, director);

            rs = statement.executeQuery();

            int count = 0;
            while (rs.next()){
                count++;
            }
            if (count != 0){
                throw new Exception("Movie already exists");
            }


            query = "select max(id) from movies;";
            // Perform the query
            statement = conn.prepareStatement(query);
            rs = statement.executeQuery();
            String maxId = "";
            while (rs.next()){
                maxId = rs.getString("max(id)");
            }
            int newMaxIdNum = Integer.parseInt(maxId.substring(2, maxId.length())) + 1;
            String newMaxId = "tt" + Integer.toString(newMaxIdNum);


            String insertMovie = "CALL add_movie(?, ?, ?, ?, ?, ?, ?)";
            statement = conn.prepareStatement(insertMovie);
            statement.setString(1, newMaxId);
            statement.setString(2, title);
            statement.setInt(3, Integer.parseInt(year));
            statement.setString(4, director);
            statement.setFloat(5, Float.parseFloat(rating));
            statement.setString(6, star);
            statement.setString(7, genre);
            statement.execute();


            statement.close();


            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output


            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {

        }
        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */

    }
}
