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
import jakarta.servlet.annotation.WebInitParam;


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

        String urlTitle = request.getParameter("title");
        String urlYear = request.getParameter("year");
        String urlDirector = request.getParameter("director");
        String urlStar = request.getParameter("star");
        String urlGenre = request.getParameter("genre");


        String urlStartNumber = request.getParameter("start");
        if (urlStartNumber == null){
            urlStartNumber = "0";
        }

        String urlNumber = request.getParameter("n");
        if (urlNumber == null){
            urlNumber = "25";
        }


        String qOptions = "";

        if(urlYear != "" && urlYear != null){
            qOptions += " AND movies.year = " + urlYear + " ";
        }if(urlDirector != "" && urlDirector != null){
            qOptions += " AND movies.director LIKE '%" + urlDirector + "%' ";
        }if(urlStar != "" && urlStar != null){
            qOptions += " AND stars.name LIKE '%" + urlStar + "%' ";
        }if(urlGenre != "" && urlGenre != null){
            qOptions += " AND genres.name = '" + urlGenre + "' ";
        }if (urlTitle != "" && urlTitle != null){
            if (urlTitle.startsWith("*")){
//                if (urlTitle.equals("*")){
//                    qOptions += " AND movies.title LIKE '%[^a-zA-Z0-9]%' ";
//                }else{
                qOptions += " AND movies.title LIKE '" + urlTitle.substring(1) + "%' ";
//                }
            }else{
                qOptions += " AND movies.title LIKE '%" + urlTitle + "%' ";
            }
        }

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();

            String query = "SELECT DISTINCT movies.id, movies.title, movies.year, movies.director, ratings.rating \n" +
                    "FROM ratings, movies, stars, stars_in_movies, genres, genres_in_movies\n" +
                    "WHERE movies.id = ratings.movieId\n" + qOptions +
                    "AND genres_in_movies.genreId = genres.id\n" +
                    "AND genres_in_movies.movieId = movies.id\n" +
                    "AND stars.id = stars_in_movies.starId\n" +
                    "AND stars_in_movies.movieId = movies.id\n" +
                    "ORDER BY ratings.rating DESC\n" +
                    "LIMIT " + urlStartNumber + ", " + urlNumber + ";";
            if (urlTitle != null && urlTitle.equals("*")){
                query = "SELECT DISTINCT movies.id, movies.title, movies.year, movies.director, ratings.rating\n" +
                        "FROM ratings, movies\n" +
                        "WHERE movies.id = ratings.movieId\n" +
                        "ORDER BY movies.title ASC\n" +
                        "LIMIT 11;";
            }

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

                String genreQ = "SELECT genres.name, genres.id \n" +
                        "FROM genres, movies, genres_in_movies\n" +
                        "WHERE movies.id = genres_in_movies.movieId\n" +
                        "AND genres_in_movies.genreId = genres.id\n" +
                        "AND movies.title = '" + title + "' " +
                        "ORDER BY genres.name ASC;";

                Statement genreStatement = conn.createStatement();
                ResultSet genreR = genreStatement.executeQuery(genreQ);
                JsonArray genres = new JsonArray();
//                JsonArray genresId = new JsonArray();
                while (genreR.next()){
                    genres.add(genreR.getString("name"));
//                    genresId.add(genreR.getString("id"));
                }



                String starQ = "SELECT stars.name, stars.id \n" +
                        "FROM stars, movies, stars_in_movies\n" +
                        "WHERE movies.id = stars_in_movies.movieId\n" +
                        "AND stars_in_movies.starId = stars.id\n" +
                        "AND movies.title = '" + title + "' ";
//                        "ORDER BY COUNT(movies.id) DESC";
                Statement starStatement = conn.createStatement();
                ResultSet starR = starStatement.executeQuery(starQ);
                JsonArray stars = new JsonArray();
                JsonArray starsId = new JsonArray();
                while (starR.next()){
                    stars.add(starR.getString("name"));
                    starsId.add(starR.getString("id"));
                }


                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", id);
                jsonObject.addProperty("movie_title", title);
                jsonObject.addProperty("movie_year", year);
                jsonObject.addProperty("movie_director", director);
                jsonObject.add("movie_genres", genres);
//                jsonObject.add("movie_genres_id", genresId);
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
