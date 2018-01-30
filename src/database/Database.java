package database;

import java.sql.*;

public class Database {

    private Connection con;
    private String url;
    private String databaseUsername;
    private String databasePassword;

    //Constructor.
    public Database(String url, String databaseUsername, String databasePassword) {
        this.url = url;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
    }

    public Database() {

    }

    //Method for connecting our database.
    public void connectDatabase() throws SQLException {
        con = DriverManager.getConnection(url, databaseUsername, databasePassword);

        System.out.println("Connected to database.");
    }

    //Creating necessary tables if they don't already exist.
    public void setUp() {

        try {
            Statement statement = con.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS gamenifo (qnumber VARCHAR(80) PRIMARY KEY NOT NULL, question VARCHAR(80) NOT NULL, firstanswer VARCHAR(80) NOT NULL, secondanswer VARCHAR(80) NOT NULL, thirdanswer VARCHAR(80) NOT NULL, correctanswer VARCHAR(80) NOT NULL, result VARCHAR(10), used VARCHAR(10));");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS playerdata (username VARCHAR(80) PRIMARY KEY NOT NULL, password VARCHAR(80) NOT NULL);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS userdata (username VARCHAR(80) PRIMARY KEY NOT NULL, password VARCHAR(80) NOT NULL);");
            statement.executeUpdate("DELETE FROM userdata;");
            statement.executeUpdate("INSERT INTO userdata (username, password) VALUES ('jessica', 'hemligt');");
            statement.executeUpdate("DELETE FROM playerdata;");
            statement.executeUpdate("INSERT INTO playerdata (username, password) VALUES ('Djossix', 'awesome');");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Add created user to our database table 'playerdata'.
    public boolean createPlayer(String username, String password) {

        username = username.toLowerCase();

        PreparedStatement statement;
        try {
            statement = con.prepareStatement("INSERT INTO playerdata VALUES (?, ?)");
            statement.setString(1, username);
            statement.setString(2, password);

            statement.execute();
            return true;

        } catch (SQLException e) {
            System.err.println("Player already exists.");
            return false;
        }
    }

    //Allows logon for player if information is correct.
    public boolean logOnPlayer(String username, String password) {

        username = username.toLowerCase();
        //password = password.toLowerCase(); // Security risk!!

        try {
            PreparedStatement statement = con.prepareStatement
                    ("SELECT username FROM playerdata WHERE LOWER(username) = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.err.println("Wrong credentials.");
            return false;
        }
    }

    //Allows logon for admin if information is correct.
    public boolean logOnAdmin(String username, String password) {

        username = username.toLowerCase();

        try {
            PreparedStatement statement = con.prepareStatement
                    ("SELECT username FROM userdata WHERE LOWER(username) = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.err.println("Wrong credentials.");
            return false;
        }
    }

    //Checks if player exists in our database.
    public boolean playerExistenz(String username) {

        username = username.toLowerCase();

        try {
            PreparedStatement statement = con.prepareStatement("SELECT username FROM playerdata WHERE LOWER(username) = ?");
            statement.setString(1, username);

            ResultSet rs = statement.executeQuery();
            return rs.next();

        } catch  (SQLException e) {
            System.err.println("Could not retreive data.");
            return false;
        }
    }

    //Removes player if supplied information is correct.
    //Ths method was created just in case, but never used.
    public boolean removePlayer(String username, String password) {

        username = username.toLowerCase();
        password = password.toLowerCase();

        try {
            PreparedStatement statement = con.prepareStatement
                    ("DELETE FROM players WHERE username = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);

            return statement.executeUpdate() > 0;

        } catch (SQLException e ) {
            System.err.println("Wrong credentials.");
            return false;
        }
    }

    //Returns number of existing questions in our database.
    public int nrOfQuestions() {

        int questions = 0;
        try {
            PreparedStatement statement = con.prepareStatement("SELECT qnumber FROM gameinfo");
            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                ++questions;
            }

            if(questions == 0) {
                System.out.println("No available data.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }

    //Returns number of correct answers.
    public int gameResult() {

        int result = 0;
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM gameinfo WHERE result = 'r'");
            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                ++result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    //Set result - 'r' for Right or 'w' for Wrong.
    public void setResult(String answer, String qNumber) {

        try {
            PreparedStatement statement = con.prepareStatement("UPDATE gameinfo SET result = ? WHERE qNumber = ?");
            statement.setString(1, answer);
            statement.setString(2, qNumber);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Add question to our database 'gameinfo'.
    public void addQuestion(String qNr, String question, String ansOne, String ansTwo, String ansThree, String corrAns) {

        try {
            PreparedStatement statement = con.prepareStatement
                    ("INSERT INTO gameinfo (qnumber, question, firstanswer, secondanswer, thirdanswer, correctanswer) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, qNr);
            statement.setString(2, question);
            statement.setString(3, ansOne);
            statement.setString(4, ansTwo);
            statement.setString(5, ansThree);
            statement.setString(6, corrAns);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Check if answer is correct or not.
    public boolean checkAnswer(String questionNr, String answer) {

        try {
            PreparedStatement statement = con.prepareStatement
                    ("SELECT * FROM gameinfo WHERE qnumber = ? AND correctanswer = ?");
            statement.setString(1, questionNr);
            statement.setString(2, answer);


            ResultSet rs = statement.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.err.println("Wrong answer.");
            return false;
        }
    }

    //Get a random question with it's answers.
    public QuizHandler getQuiz() {

        String qNumber = null, question = null, ansOne = null, ansTwo = null, ansThree = null;

        try {
            PreparedStatement statement = con.prepareStatement
                    ("SELECT qnumber, question, firstanswer, secondanswer, thirdanswer FROM gameinfo WHERE used is null ORDER BY random() LIMIT 1;");
            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                qNumber = rs.getString(1);
                question = rs.getString(2);
                ansOne = rs.getString(3);
                ansTwo = rs.getString(4);
                ansThree = rs.getString(5);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new QuizHandler(qNumber, question, ansOne, ansTwo, ansThree);
    }

    //Mark a question as used to avoid getting it more than once during a game.
    public void markAsUsed(String qNumber) {

        try {
            PreparedStatement statement = con.prepareStatement("UPDATE gameinfo SET used = 'yes' WHERE qnumber = ?");

            statement.setString(1, qNumber);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //This resets our columns 'result' and 'used' to null in the beginning of a new game.
    public void newGame() {

        try {
            PreparedStatement statement = con.prepareStatement("UPDATE gameinfo SET result = null, used = null;");
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Close our database when we're done.
    public boolean closeDatabase() {
        try {
            con.close();
            System.out.println("Database closed.");
        } catch (SQLException e) {
            System.err.println("Error: Failed to close database.");
            return false;
        }
        return false;
    }
}
