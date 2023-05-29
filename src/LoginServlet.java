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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
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
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        String isMobile = request.getParameter("mobile");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement


            String query = "SELECT customers.email, customers.password, customers.id \n" +
                    "FROM customers\n" +
                    "WHERE customers.email = ?;";

            // Perform the query
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            String dbUsername = "";
            String dbPassword = "";
            String dbId = "";
            while (rs.next()) {
                dbUsername = rs.getString("email");
                dbPassword = rs.getString("password");
                dbId = rs.getString("id");
            }
            System.out.println(dbPassword);
            rs.close();
            statement.close();

            boolean recaptchaGood = true;
            if (isMobile == null){
                recaptchaGood = RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            }else{
                recaptchaGood = true;
            }



            JsonObject responseJsonObject = new JsonObject();
            if (username.equals(dbUsername) && new StrongPasswordEncryptor().checkPassword(password, dbPassword) && !dbUsername.equals("")
                    && !dbPassword.equals("") && recaptchaGood) {


                // Login success:

                // set this user into the session
                request.getSession().setAttribute("user", new User(username));
                request.getSession().setAttribute("customerId", dbId);

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                if (!username.equals(dbUsername) || username.equals("")) {
                    responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
                } else if (!new StrongPasswordEncryptor().checkPassword(password, dbPassword)) {
                    responseJsonObject.addProperty("message", "incorrect password");
                }
                else{
                    responseJsonObject.addProperty("message", "recaptcha failed");
                }
            }



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
