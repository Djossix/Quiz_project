package server;

import java.io.*;
import java.net.*;

import database.*;

import java.sql.SQLException;

public class GamePlay implements Runnable {

    Socket connectedPlayer;
    DataInputStream in;
    DataOutputStream out;
    ObjectOutputStream outObj;
    String url = "jdbc:postgresql://localhost:5432/postgres";
    String username = "postgres";
    String password = "Lx0e1utY";

    //Constructor for GamePlay.
    //Create in-and output-streams, both for Strings and Objects.
    public GamePlay(Socket connectedPlayer) {
        this.connectedPlayer = connectedPlayer;
        try {
            this.in = new DataInputStream(this.connectedPlayer.getInputStream());
            this.out = new DataOutputStream(this.connectedPlayer.getOutputStream());
            this.outObj = new ObjectOutputStream(this.connectedPlayer.getOutputStream());
        } catch (IOException ex) {

        }
    }


    @Override
    public void run() {
        while (true) {
            try {

                //Create object of Database and connects database.
                Database database = new Database(url, username, password);
                database.connectDatabase();
                database.setUp();

                //Get local address from connected client.
                InetAddress host = InetAddress.getLocalHost();
                System.out.println("Client " + host + " has connected.");

                out.writeUTF("Get ready for the ultimate Dota2 Quiz!\n\n[L]og in\n[C]reate account\n[E]xit\n\n");

                String menuOne = in.readUTF();

                //Code for the different selections that the client can make.
                if (menuOne.equals("L") || menuOne.equals("l")) {

                    out.writeUTF("\n[P]layer\n[A]dmin\n\n");
                    String menuTwo = in.readUTF();

                    //Check login details and existence.
                    //If user not already exist and login details is correct, user will be logged in.
                    if (menuTwo.equals("P") || menuTwo.equals("p")) {

                        String userPlayer = in.readUTF();
                        String pwdPlayer = in.readUTF();

                        if (database.playerExistenz(userPlayer) != true) {
                            out.writeUTF("\nPlayer does not exist.\nPlease create an account and try again.\n\n");
                            break;
                        } else if (database.logOnPlayer(userPlayer, pwdPlayer) != true) {
                            out.writeUTF("\nUsername or password is incorrect.");
                            break;

                        } else {
                            out.writeUTF("\nLogin successful!\n\n[S]tart game\n[Q]uit\n\n");

                            String menuPlayer = in.readUTF();

                            //Here is the game code for the Quiz.
                            if (menuPlayer.equals("S") || menuPlayer.equals("s")) {

                                QuizHandler qh;
                                database.newGame();

                                //Get number of questions to use as max-size for the for-loop.
                                int nrOfQuestions = database.nrOfQuestions();
                                out.writeUTF(String.valueOf(nrOfQuestions));

                                for (int i = 0; i < nrOfQuestions; i++) {

                                    qh = database.getQuiz();
                                    outObj.writeObject(qh);


                                    String answer = in.readUTF();

                                    if (database.checkAnswer(qh.getqNumber(), answer) != true) {
                                        String result = "w";
                                        database.setResult(result, qh.getqNumber());
                                        out.writeUTF("\nSorry, wrong answer.\n\n");
                                        database.markAsUsed(qh.getqNumber());
                                    } else {
                                        String result = "r";
                                        database.setResult(result, qh.getqNumber());
                                        out.writeUTF("\nYay, correct answer.\n\n");
                                        database.markAsUsed(qh.getqNumber());
                                    }

                                    outObj.flush();

                                }

                                String result = String.valueOf(database.gameResult());
                                String nrQuestions = String.valueOf(database.nrOfQuestions());

                                out.writeUTF("\nYour result: " + result + "/" + nrQuestions + "\n\n");


                            } else if (menuPlayer.equals("Q") || menuPlayer.equals("q")) {
                                connectedPlayer.close();
                                database.closeDatabase();
                                break;
                            } else {
                                connectedPlayer.close();
                                database.closeDatabase();
                                break;
                            }
                        }

                    //Login code for admin.
                    //Code to create new questions and add them to the Quiz.
                    } else if (menuTwo.equals("A") || menuTwo.equals("a")) {

                        String userAdmin = in.readUTF();
                        String pwdAdmin = in.readUTF();

                        if (database.logOnAdmin(userAdmin, pwdAdmin) == true) {

                            out.writeUTF("\nYou're now logged in as administrator.\n\n[A]dd question\n[L]og out\n\n");

                            String menuAdmin = in.readUTF();

                            if (menuAdmin.equals("A") || menuAdmin.equals("a")) {

                                int qCount = (database.nrOfQuestions() + 1);
                                String qNr = String.valueOf(qCount);

                                String question = in.readUTF();
                                String ansOne = in.readUTF();
                                String ansTwo = in.readUTF();
                                String ansThree = in.readUTF();
                                String corrAns = in.readUTF();

                                database.addQuestion(qNr, question, ansOne, ansTwo, ansThree, corrAns);

                            } else if (menuAdmin.equals("L") || menuAdmin.equals("l")) {
                                connectedPlayer.close();
                                database.closeDatabase();
                                break;

                            } else {
                                out.writeUTF("\nInvalid choice.\n\n");
                                connectedPlayer.close();
                                database.closeDatabase();
                                break;
                            }

                        } else {
                            out.writeUTF("\nIncorrect username or password.\n\n");
                            break;
                        }


                    } else {
                        out.writeUTF("\nInvalid choice!\n\n");
                    }

                } else if (menuOne.equals("C") || menuOne.equals("c")) {

                    String newPlayer = in.readUTF();
                    String newPwd = in.readUTF();

                    if (database.createPlayer(newPlayer, newPwd) != true) {
                        out.writeUTF("\nThe requested username does already exist.\n\n");
                        break;
                    } else {
                        out.writeUTF("\nYour account has been created.\nPlease login to start the quiz.\n\n");
                    }



                } else if (menuOne.equals("E") || menuOne.equals("e")) {     //Code to exit the connection.
                    connectedPlayer.close();
                    database.closeDatabase();
                    break;

                } else {
                    out.writeUTF("\nInvalid choice!\n\n");
                }

                out.flush();

            } catch (EOFException e) {


            } catch (IOException e) {
                e.printStackTrace();
                break;

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        try {
            connectedPlayer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

