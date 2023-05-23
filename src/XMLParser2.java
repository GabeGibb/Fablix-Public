import jakarta.servlet.ServletConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import java.sql.Connection;



public class XMLParser2 {
    Document dom;

    private Connection conn;

    private int attempts;
    private int fails;

    private String inserts;

    private int movieIdNum;
    private int starIdNum;

    private int genreIdNum;

    private HashMap<String, String> movieIds;
    private HashMap<String, String> starIds;
    private HashMap<String, String> genreIds;

    public void runExample() {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println(1);
            // Connect to the test database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb",
                    Parameters.username, Parameters.password);
            System.out.println(1);

            String query;
            PreparedStatement statement;
            ResultSet rs;
            //START SQL

            query = "select max(id) from movies;";
            // Perform the query

            statement = conn.prepareStatement(query);
            System.out.println(1);
            rs = statement.executeQuery();
            String maxId = "";
            if (rs.next()){
                maxId = rs.getString("max(id)");
            }
            movieIdNum = Integer.parseInt(maxId.substring(2, maxId.length()));

            query = "select max(id) from stars;";
            // Perform the query

            statement = conn.prepareStatement(query);
            rs = statement.executeQuery();
            String maxIdS = "";
            if (rs.next()){
                maxIdS = rs.getString("max(id)");
            }
            starIdNum = Integer.parseInt(maxIdS.substring(2, maxIdS.length()));

            query = "select max(id) from genres;";
            // Perform the query

            statement = conn.prepareStatement(query);
            rs = statement.executeQuery();
            String maxIdG = "";
            if (rs.next()){
                maxIdG = rs.getString("max(id)");
            }
            genreIdNum = Integer.parseInt(maxIdG);


        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        this.movieIds = new HashMap<String, String>();
        this.starIds = new HashMap<String, String>();
        this.genreIds = new HashMap<String, String>();

        inserts = "";
        attempts = 0;
        fails = 0;
        System.out.println("Starting parsing 2");
        System.out.println("Starting main");
        parseXmlFile("mains243.xml");
        parseMovies();

        System.out.println("Starting actors");
        parseXmlFile("actors63.xml");
        parseActors();

        System.out.println("Starting casts");
        parseXmlFile("casts124.xml");
        parseCasts();
        System.out.println("Total objects attempted to parse: " + attempts);
        System.out.println("Total objects failed to parse AND add to the database: " + fails);



        String[] insertsArr = this.inserts.split("\n");


        for (int i = 0; i < insertsArr.length; i++){
            try{
                Statement statement = conn.createStatement();
                statement.execute(insertsArr[i]);

                System.out.println("success " + i + " / " + insertsArr.length);
            }catch(Exception e){
                System.out.println(e.getMessage());
//                System.out.println(insertsArr[i]);
            }
        }
        System.out.println("DONE");

    }

    private void parseXmlFile(String fileName) {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse(fileName);

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseMovies() {

        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("film");
        for (int i = 0; i < nodeList.getLength(); i++) {
            attempts++;
            // get the employee element
            Element element = (Element) nodeList.item(i);

            // get the Employee object
            try{
                String title = getTextValue(element, "t");
                String year = getTextValue(element, "year");
                String director = getTextValue(element, "dirn");
                String genre = getTextValue(element, "cat");
                if(genre != null && genre.indexOf(" ") != -1){
                    genre = genre.substring(0, genre.indexOf(" "));
                }

                if (title == null || year == null || director == null){
                    fails++;
                    continue;
                }
                if (genre == null){
                    genre = "Unknown";
                }


                //SQL
                String query;
                Statement statement;
                ResultSet rs;

                String genreId = "";

                if (!genreIds.containsKey(genre)){
                    query = "select name,id from genres where genres.name = '" + genre + "';";

                    statement = conn.createStatement();

//                    statement.setString(1, genre);

                    rs = statement.executeQuery(query);

                    String dbGenre = "";
                    if (rs.next()){

                        dbGenre = rs.getString("name");
                        genreId = rs.getString("id");
                    }
                    rs.close();
                    statement.close();

                    //If genre not in database
                    if (dbGenre == ""){
                        this.inserts += " INSERT INTO genres (name) values('" + genre + "'); \n";
                        genreIdNum++;
                        genreId = Integer.toString(genreIdNum);
                    }

                    genreIds.put(genre, genreId);
                }
                else{

                    genreId = this.genreIds.get(genre);
                }


                //
                this.movieIdNum++;
                String newMaxId = "tt" + Integer.toString(this.movieIdNum);
                //genres inserted movie id found, lets add movie
                this.inserts += " INSERT INTO movies values('" + newMaxId + "', '" + title + "', " + year +", '" + director + "'); \n";

                this.movieIds.put(title, newMaxId);


                this.inserts += " insert into genres_in_movies values(" + genreId + ", '" + newMaxId + "'); \n";
                this.inserts += " insert into ratings values('" + newMaxId + "', 0, 0); \n";

//                System.out.println("1");
            }catch(Exception e){
                fails++;
                System.out.println(e);

            }


        }
    }

    private void parseActors() {
        // get the document root Element
        System.out.println("start actor parsing for real ");
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("actor");
        for (int i = 0; i < nodeList.getLength(); i++) {
            attempts++;
            // get the employee element
            Element element = (Element) nodeList.item(i);

            // get the Employee object
            try{
                String star = getTextValue(element, "stagename");
                String year = getTextValue(element, "dob");

                if (star == null || year ==  null){
                    fails++;
                    System.out.println("continue");
                    continue;
                }


                this.starIdNum++;
                String newMaxId = "nm" + Integer.toString(this.starIdNum);

                this.inserts += " INSERT INTO stars values('" + newMaxId + "', '" + star + "', NULL); \n";

                if (year == null || year == ""){
                    this.inserts += " INSERT INTO stars values('" + newMaxId + "', '" + star + "', NULL); \n";
                }else{
                    this.inserts += " INSERT INTO stars values('" + newMaxId + "', '" + star + "', " + year + "); \n";
                }

                this.starIds.put(star, newMaxId);

                System.out.println("2");

            }catch(Exception e){
                fails++;
                System.out.println(e.getMessage());
            }
        }
    }


    private void parseCasts() {
        // get the document root Element
        System.out.println("start cast parsing for real ");
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("m");
        for (int i = 0; i < nodeList.getLength(); i++) {
            attempts++;
            // get the employee element
            Element element = (Element) nodeList.item(i);

            // get the Employee object
            try{
                String title = getTextValue(element, "t");
                String actor = getTextValue(element, "a");

                if (title == null || actor == null){
                    fails++;
                    System.out.println("continue");
                    continue;
                }

                String movieId = this.movieIds.get(title);
                String starsId = this.starIds.get(actor);

                if (movieId == null || starsId == null){
                    fails++;
                    continue;
                }

                this.inserts += " insert into stars_in_movies values('" +starsId + "', '" + movieId+ "'); ";

                System.out.println("3");

            }catch(Exception e){
                fails++;
                System.out.println(e.getMessage());
            }
        }
    }



    private String getTextValue(Element element, String tagName) {
        String textVal = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            // here we expect only one <Name> would present in the <Employee>
            textVal = nodeList.item(0).getFirstChild().getNodeValue();

        }
        return textVal;
    }



    public static void main(String[] args) {
        // create an instance
        XMLParser2 domParser= new XMLParser2();

        // call run example
        domParser.runExample();
    }

}
