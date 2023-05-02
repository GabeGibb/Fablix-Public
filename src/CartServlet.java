import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "IndexServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        JsonObject responseJsonObject = new JsonObject();


        ArrayList<String> previousMovies = (ArrayList<String>) session.getAttribute("previousMovies");
        if (previousMovies == null) {
            previousMovies = new ArrayList<String>();
        }
        // Log to localhost log
        request.getServletContext().log("getting " + previousMovies.size() + " items");
        JsonArray previousMoviesJsonArray = new JsonArray();
        previousMovies.forEach(previousMoviesJsonArray::add);
        responseJsonObject.add("previousMovies", previousMoviesJsonArray);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String remove = request.getParameter("remove");

        String movie = request.getParameter("title");
        String id = request.getParameter("id");

        movie += "#" + id;
        System.out.println(movie);
        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        ArrayList<String> previousMovies = (ArrayList<String>) session.getAttribute("previousMovies");
        if (previousMovies == null) {

            previousMovies = new ArrayList<String>();
            previousMovies.add(movie);
            session.setAttribute("previousMovies", previousMovies);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousMovies) {
                if (remove == null){
                    previousMovies.add(movie);
                }
                else{
                    previousMovies.remove(movie);
                }
            }
        }

        JsonObject responseJsonObject = new JsonObject();

        JsonArray previousMoviesJsonArray = new JsonArray();
        previousMovies.forEach(previousMoviesJsonArray::add);
        responseJsonObject.add("previousMovies", previousMoviesJsonArray);

        response.getWriter().write(responseJsonObject.toString());
    }
}
