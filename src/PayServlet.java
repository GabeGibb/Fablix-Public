import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.Date;
import java.text.SimpleDateFormat;

@WebServlet(name = "PayServlet", urlPatterns = "/api/pay")
public class PayServlet extends HttpServlet {
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
        String firstName = request.getParameter("first_name");
        String lastName = request.getParameter("last_name");
        String cardNumber = request.getParameter("card_number");

        String expiration = request.getParameter("expiration");
        if (expiration.equals("")){
            expiration = "1111-11-11";
        }

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement


            String query = "SELECT * " +
                    "FROM creditcards " +
                    " WHERE creditcards.id = ? " +
                    " AND creditcards.firstName = ? " +
                    " AND creditcards.lastName = ? " +
                    " AND creditcards.expiration = ?; " ;

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, cardNumber);
            statement.setString(2, firstName);
            statement.setString(3, lastName);

            statement.setString(4, expiration);
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            String dbCard = null;

            while (rs.next()) {
                dbCard = rs.getString("id");
            }

            System.out.println(dbCard);


            JsonObject responseJsonObject = new JsonObject();
            if (dbCard != null && cardNumber.equals(dbCard)) {
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

                HttpSession session = request.getSession();
                Date buyDate = new Date(session.getLastAccessedTime());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String strDate = formatter.format(buyDate);

                String customerId = session.getAttribute("customerId").toString();
                System.out.println(customerId);


                ArrayList<String> previousMovies = (ArrayList<String>) session.getAttribute("previousMovies");
                if (previousMovies == null) {
                    previousMovies = new ArrayList<String>();
                }

                JsonArray previousMoviesJsonArray = new JsonArray();
                previousMovies.forEach(previousMoviesJsonArray::add);

                for (int i = 0; i < previousMoviesJsonArray.size(); i++) {
                    String movieId = previousMoviesJsonArray.get(i).getAsString().split("#")[1];
                    String insert = "INSERT INTO sales (customerId, movieId, saleDate)\n" +
                            "VALUES (?, ?, ?);";

                    statement = conn.prepareStatement(insert);
                    statement.setInt(1, Integer.parseInt(customerId));
                    statement.setString(2, movieId);
                    statement.setString(3, strDate);
                    statement.execute();
                }

                session.setAttribute("previousMovies", null);





            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Payment Failed, Try Again");
                responseJsonObject.addProperty("message", "Payment Failed, Try Again"   );
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
//                if (!username.equals(dbUsername)) {
//                    responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
//                } else {
//                    responseJsonObject.addProperty("message", "incorrect password");
//                }
            }
            rs.close();
            statement.close();

            response.getWriter().write(responseJsonObject.toString());

            // Write JSON string to output
//            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        } finally {
            out.close();
        }


    }
}
