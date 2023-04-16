import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();

            String query = "SELECT * \n" +
                    "FROM ratings, movies\n" +
                    "WHERE movies.id = ratings.movieId\n" +
                    "ORDER BY ratings.rating DESC\n" +
                    "LIMIT 20";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");
                String rating = rs.getString("rating");

                String genreQ = "SELECT genres.name \n" +
                        "FROM genres, movies, genres_in_movies\n" +
                        "WHERE movies.id = genres_in_movies.movieId\n" +
                        "AND genres_in_movies.genreId = genres.id\n" +
                        "AND movies.title = '" + title + "'";
                Statement genreStatement = conn.createStatement();
                ResultSet genreR = genreStatement.executeQuery(genreQ);
                JsonArray genres = new JsonArray();
                while (genreR.next()){
                    genres.add(genreR.getString("name"));
                }



                String starQ = "SELECT stars.name, stars.id \n" +
                        "FROM stars, movies, stars_in_movies\n" +
                        "WHERE movies.id = stars_in_movies.movieId\n" +
                        "AND stars_in_movies.starId = stars.id\n" +
                        "AND movies.title = '" + title + "'";
                Statement starStatement = conn.createStatement();
                ResultSet starR = starStatement.executeQuery(starQ);
                JsonArray stars = new JsonArray();
                JsonArray starsId = new JsonArray();
                while (starR.next()){
                    stars.add(starR.getString("name"));
                    starsId.add(starR.getString("id"));
                }



//                String genres = rs.getString("birthYear");
//                String stars = rs.getString("birthYear");
//                String rating = rs.getString("birthYear");
                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", id);
                jsonObject.addProperty("movie_title", title);
                jsonObject.addProperty("movie_year", year);
                jsonObject.addProperty("movie_director", director);
                jsonObject.add("movie_genres", genres);
                jsonObject.add("movie_stars", stars);
                jsonObject.add("movie_stars_id", starsId);
                jsonObject.addProperty("movie_rating", rating);
                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
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

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
