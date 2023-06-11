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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    private static String pastUrl = "";
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
        long tsStartTime = System.nanoTime();

        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();
        String fullUrl;
        String newUrl = "";
        if (queryString == null) {
            fullUrl = requestURL.toString();
            newUrl = pastUrl;
        } else {
            fullUrl = requestURL.append('?').append(queryString).toString();
        }

        fullUrl = fullUrl.replace("api/movies", "movies.html");

        pastUrl = fullUrl;


        ArrayList<String[]> q1Params = new ArrayList<String[]>();

        response.setContentType("application/json"); // Response mime type

        String urlTitle = request.getParameter("title");
        String urlYear = request.getParameter("year");
        String urlDirector = request.getParameter("director");
        String urlStar = request.getParameter("star");
        String urlGenre = request.getParameter("genre");

        String fullText = request.getParameter("full-text");


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
            qOptions += " AND movies.year = ? ";
            q1Params.add(new String[]{urlYear, "int"});
        }if(urlDirector != "" && urlDirector != null){
            qOptions += " AND movies.director LIKE ? ";
            q1Params.add(new String[]{"%" + urlDirector + "%", "string"});
        }if(urlStar != "" && urlStar != null){
            qOptions += " AND stars.name LIKE ? ";
            q1Params.add(new String[]{"%" + urlStar + "%", "string"});
        }if(urlGenre != "" && urlGenre != null){
            qOptions += " AND genres.name = ? ";
            q1Params.add(new String[]{urlGenre, "string"});
        }if (urlTitle != "" && urlTitle != null){
            if (fullText == null){
                if (urlTitle.startsWith("*")){
                    qOptions += " AND movies.title LIKE ? ";
                    q1Params.add(new String[]{urlTitle.substring(1) + "%", "string"});
                }
                else{
                    qOptions += " AND movies.title LIKE ? ";
                    q1Params.add(new String[]{"%" + urlTitle + "%", "string"});
                }
            }else if (fullText != null){
                qOptions += " AND MATCH(movies.title) AGAINST (? IN BOOLEAN MODE) ";
                String words[] = urlTitle.split(" ");
                String param = "";
                for (int i = 0; i < words.length; i++){
                    param += "+";
                    param += words[i];
                    param += "* ";
                }

                q1Params.add(new String[]{param, "string"});

//                System.out.println(param);
            }

        }


        String sortT = request.getParameter("ordert");
        String sortR = request.getParameter("orderr");
        String first = request.getParameter("first");
        String order = " ORDER BY ";

        if (sortT == null || sortR == null || first == null){
            order += "ratings.rating DESC, movies.title ASC ";
        }else{
            if (first.equals("Title")){
                order += "movies.title " + sortT + ", ratings.rating " + sortR + " ";
            }else{
                order += "ratings.rating " + sortR + ", movies.title " + sortT + " ";
            }
        }

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        long tjStartTime = System.nanoTime();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            String query = "SELECT DISTINCT movies.id, movies.title, movies.year, movies.director, ratings.rating \n" +
                    "FROM ratings, movies, stars, stars_in_movies, genres, genres_in_movies\n" +
                    "WHERE movies.id = ratings.movieId\n" + qOptions +
                    "AND genres_in_movies.genreId = genres.id\n" +
                    "AND genres_in_movies.movieId = movies.id\n" +
                    "AND stars.id = stars_in_movies.starId\n" +
                    "AND stars_in_movies.movieId = movies.id\n" +
                    order +
                    "LIMIT " + urlStartNumber + ", " + urlNumber + ";";

            if (urlTitle != null && urlTitle.equals("*")){
                query = "SELECT DISTINCT movies.id, movies.title, movies.year, movies.director, ratings.rating\n" +
                        "FROM ratings, movies\n" +
                        "WHERE movies.id = ratings.movieId\n" +
                        "ORDER BY movies.title " +
                        "LIMIT 11;";
            }
            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);
            if (urlTitle != null && urlTitle.equals("*")){

            }
            else{
                String type;
                String value;
                for(int i = 0; i < q1Params.size(); i++){
                    value = q1Params.get(i)[0];
                    type = q1Params.get(i)[1];
                    if (type == "string"){
                        statement.setString(i+1, value);
                    }else if (type == "int"){
                        statement.setInt(i+1, Integer.parseInt(value));
                    }
                }
            }


            // Perform the query
            ResultSet rs = statement.executeQuery();


            JsonArray jsonArray = new JsonArray();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("new_url", newUrl);
            jsonArray.add(jsonObject);
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
                        "AND movies.title = ? " +
                        "ORDER BY genres.name ASC;";

                PreparedStatement genreStatement = conn.prepareStatement(genreQ);
                genreStatement.setString(1, title);

                ResultSet genreR = genreStatement.executeQuery();
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
                        "AND movies.title = ? " +
                        "ORDER BY stars.name;";
//                        "ORDER BY COUNT(movies.id) DESC";
                PreparedStatement starStatement = conn.prepareStatement(starQ);
                starStatement.setString(1, title);
                ResultSet starR = starStatement.executeQuery();
                JsonArray stars = new JsonArray();
                JsonArray starsId = new JsonArray();
                while (starR.next()){
                    stars.add(starR.getString("name"));
                    starsId.add(starR.getString("id"));
                }


                // Create a JsonObject based on the data we retrieve from rs
                jsonObject = new JsonObject();
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
        long tsEndTime = System.nanoTime();
        long tsTime = tsEndTime - tsStartTime;
        long tjTime = tsEndTime - tjStartTime;
        String fileText = "TS " + tsTime + " TJ " + tjTime;

        String contextPath = request.getServletContext().getRealPath("/");
        String xmlFilePath=contextPath+"\\search_test.txt";
//        File myfile = new File(xmlFilePath);

        try
        {
            FileWriter fw = new FileWriter(xmlFilePath,true); //the true will append the new data
            fw.write(fileText + "\n");//appends the string to the file
            fw.close();
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }

//        myfile.createNewFile();

    }
}
