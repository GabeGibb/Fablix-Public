//import jakarta.servlet.ServletConfig;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;
//
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import javax.sql.DataSource;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import java.io.IOException;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//import java.sql.Connection;
//
//public class XMLParser {
//    Document dom;
//
//    private Connection conn;
//
//    private int attempts;
//    private int fails;
//
//    private String inserts;
//
//    public void runExample() {
//        try{
//            Class.forName("com.mysql.cj.jdbc.Driver");
//
//            // Connect to the test database
//            conn = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false",
//                    Parameters.username, Parameters.password);
//
//
//        }catch(Exception e){
//
//        }
//
//        inserts = "BEGIN; ";
//        attempts = 0;
//        fails = 0;
//        System.out.println("Starting parsing");
//        System.out.println("Starting main");
//        parseXmlFile("mains243.xml");
//        parseMovies();
//
//        System.out.println("Starting actors");
//        parseXmlFile("actors63.xml");
//        parseActors();
//
//        System.out.println("Starting casts");
//        parseXmlFile("casts124.xml");
//        parseCasts();
//        System.out.println("Total objects attempted to parse: " + attempts);
//        System.out.println("Total objects failed to parse AND add to the database: " + fails);
//
//    }
//
//    private void parseXmlFile(String fileName) {
//        // get the factory
//        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//        try {
//
//            // using factory get an instance of document builder
//            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//
//            // parse using builder to get DOM representation of the XML file
//            dom = documentBuilder.parse(fileName);
//
//        } catch (ParserConfigurationException | SAXException | IOException error) {
//            error.printStackTrace();
//        }
//    }
//
//    private void parseMovies() {
//        // get the document root Element
//        Element documentElement = dom.getDocumentElement();
//
//        // get a nodelist of employee Elements, parse each into Employee object
//        NodeList nodeList = documentElement.getElementsByTagName("film");
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            attempts++;
//            // get the employee element
//            Element element = (Element) nodeList.item(i);
//
//            // get the Employee object
//            try{
//                String title = getTextValue(element, "t");
//                String year = getTextValue(element, "year");
//                String director = getTextValue(element, "dirn");
//                String genre = getTextValue(element, "cat");
//                if(genre != null && genre.indexOf(" ") != -1){
//                    genre = genre.substring(0, genre.indexOf(" "));
//                }
//
//                if (title == null || year == null || director == null){
//                    fails++;
//                    continue;
//                }
//                if (genre == null){
//                    genre = "Unknown";
//                }
//
//                String query;
//                PreparedStatement statement;
//                ResultSet rs;
//                //START SQL
////                query = "select * from movies " +
////                        "where movies.title = ? " +
////                        " and movies.year = ? " +
////                        " and movies.director = ? ;";
////
////                statement = conn.prepareStatement(query);
////                statement.setString(1, title);
////                statement.setInt(2, Integer.parseInt(year));
////                statement.setString(3, director);
////
////                rs = statement.executeQuery();
////
////                int count = 0;
////                while (rs.next()){
////                    count++;
////                }
////                if (count != 0){
////                    throw new Exception("Movie already exists");
////                }
//
//                query = "select max(id) from movies;";
//                // Perform the query
//                statement = conn.prepareStatement(query);
//                rs = statement.executeQuery();
//                String maxId = "";
//                if (rs.next()){
//                    maxId = rs.getString("max(id)");
//                }
//                int newMaxIdNum = Integer.parseInt(maxId.substring(2, maxId.length())) + 1;
//                String newMaxId = "tt" + Integer.toString(newMaxIdNum);
//
//                //
//                query = "select name from genres where genres.name = ?";
//
//                statement = conn.prepareStatement(query);
//                statement.setString(1, genre);
//
//                rs = statement.executeQuery();
//
//                String dbGenre = "";
//                if (rs.next()){
//                    dbGenre = rs.getString("name");
//                }
//                rs.close();
//
//                //If genre not in database
//                if (dbGenre == ""){
//                    String insertStar = "INSERT INTO genres (name) values(?);";
//                    statement = conn.prepareStatement(insertStar);
//                    statement.setString(1, genre);
//                    statement.execute();
//                }
//
//                //genres inserted movie id found, lets add movie
//                query = "INSERT INTO movies values(?, ?, ?, ?);";
//
//                statement = conn.prepareStatement(query);
//                statement.setString(1, newMaxId);
//                statement.setString(2, title);
//                statement.setInt(3, Integer.parseInt(year));
//                statement.setString(4, director);
//                statement.execute();
//
//                query = "select genres.id from genres where genres.name = ?; ";
//
//                statement = conn.prepareStatement(query);
//                statement.setString(1, genre);
//
//                rs = statement.executeQuery();
//                String genreId = "";
//                if (rs.next()){
//                    genreId = rs.getString("id");
//                }
//                if (true){
//                    continue;
//                }
//                query = "insert into genres_in_movies values(?, ?);";
//                statement = conn.prepareStatement(query);
//                statement.setString(1, genreId);
//                statement.setString(2, newMaxId);
//                statement.execute();
//
//                query = "insert into ratings values(?, 0, 0);";
//                statement = conn.prepareStatement(query);
//                statement.setString(1, newMaxId);
//                statement.execute();
//
//                rs.close();
//                statement.close();
//
//            }catch(Exception e){
//                fails++;
////                System.out.println(e.getMessage());
//
//            }
//
//
//        }
//    }
//
//    private void parseActors() {
//        // get the document root Element
//        Element documentElement = dom.getDocumentElement();
//
//        // get a nodelist of employee Elements, parse each into Employee object
//        NodeList nodeList = documentElement.getElementsByTagName("actor");
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            attempts++;
//            // get the employee element
//            Element element = (Element) nodeList.item(i);
//
//            // get the Employee object
//            try{
//                String star = getTextValue(element, "stagename");
//                String year = getTextValue(element, "dob");
//
//                if (star == null || year ==  null){
//                    fails++;
//                    continue;
//                }
//
//                String query = "select max(id) from stars;";
//
//
//                // Perform the query
//                PreparedStatement statement = conn.prepareStatement(query);
//
//                ResultSet rs = statement.executeQuery();
//
//                String maxId = "";
//                if (rs.next()){
//                    maxId = rs.getString("max(id)");
//                }
//
//                int newMaxIdNum = Integer.parseInt(maxId.substring(2, maxId.length())) + 1;
//                String newMaxId = "nm" + Integer.toString(newMaxIdNum);
//
//                rs.close();
//                if (true){
//                    continue;
//                }
//                String insertStar = "INSERT INTO stars values(?, ?, ?);";
//
//                statement = conn.prepareStatement(insertStar);
//
//                statement.setString(1, newMaxId);
//                statement.setString(2, star);
//                if (year == null || year == ""){
//                    statement.setNull(3, Types.INTEGER);
//                }else{
//                    statement.setInt(3, Integer.parseInt(year));
//                }
//                statement.execute();
//
//                statement.close();
////                System.out.println("success!!!!!!!!!!!");
//
//            }catch(Exception e){
//                fails++;
////                System.out.println(e.getMessage());
//            }
//        }
//    }
//
//
//    private void parseCasts() {
//        // get the document root Element
//        Element documentElement = dom.getDocumentElement();
//
//        // get a nodelist of employee Elements, parse each into Employee object
//        NodeList nodeList = documentElement.getElementsByTagName("m");
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            attempts++;
//            // get the employee element
//            Element element = (Element) nodeList.item(i);
//
//            // get the Employee object
//            try{
//                String title = getTextValue(element, "t");
//                String actor = getTextValue(element, "a");
//
//                if (title == null || actor == null){
//                    fails++;
//                    continue;
//                }
//
//                String query = "select movies.id from movies where movies.title = ?;";
//                PreparedStatement statement = conn.prepareStatement(query);
//                statement.setString(1, title);
//
//                ResultSet rs = statement.executeQuery();
//                String movieId = "";
//                if (rs.next()){
//                    movieId = rs.getString("id");
//                }
//
//
//                query = "select stars.id from stars where stars.name = ?;";
//                statement = conn.prepareStatement(query);
//                statement.setString(1, actor);
//
//                rs = statement.executeQuery();
//                String starsId = "";
//                if (rs.next()){
//                    starsId = rs.getString("id");
//                }
//
//
////                System.out.println(movieId + starsId);
//                if (movieId == "" || starsId == ""){
//                    fails++;
//                    continue;
//                }
//                if (true){
//                    continue;
//                }
//                query = "insert into stars_in_movies values(?, ?);";
//                statement = conn.prepareStatement(query);
//
//                statement.setString(1, starsId);
//                statement.setString(2, movieId);
//
//                statement.execute();
//                statement.close();
////                System.out.println("Success!!!!!!!!!!!!!!!");
//
//            }catch(Exception e){
//                fails++;
////                System.out.println(e.getMessage());
//            }
//        }
//    }
//
//
//
//    private String getTextValue(Element element, String tagName) {
//        String textVal = null;
//        NodeList nodeList = element.getElementsByTagName(tagName);
//        if (nodeList.getLength() > 0) {
//            // here we expect only one <Name> would present in the <Employee>
//            textVal = nodeList.item(0).getFirstChild().getNodeValue();
//
//        }
//        return textVal;
//    }
//
//
//
//    public static void main(String[] args) {
//        // create an instance
//        XMLParser domParser= new XMLParser();
//
//        // call run example
//        domParser.runExample();
//    }
//
//}
