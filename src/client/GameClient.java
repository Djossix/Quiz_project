package client;

import database.QuizHandler;

import java.io.*;
import java.net.Socket;
import java.util.*;
import database.*;

public class GameClient {

    public static void main(String[] args) throws ClassNotFoundException {

        Scanner sc = new Scanner(System.in);

        while (true) {
            try {

                Socket client = new Socket("localhost", 8088); //Create a socket on client-side.

                //Create in-and output-streams for Strings and Objects.
                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                DataInputStream in = new DataInputStream(client.getInputStream());
                ObjectInputStream objIn = new ObjectInputStream(client.getInputStream());

                System.out.println(in.readUTF());

                String menuOne = sc.nextLine();
                out.writeUTF(menuOne);

                //Code for the different selections which server has given.
                if (menuOne.equals("L") || menuOne.equals("l")) {

                    System.out.println(in.readUTF());
                    String menuTwo = sc.nextLine();

                    out.writeUTF(menuTwo);

                    if (menuTwo.equals("P") || menuTwo.equals("p")) {
                        System.out.print("\nUsername: ");
                        String userName = sc.nextLine();
                        System.out.print("Password: ");
                        String userPwd = sc.nextLine();

                        out.writeUTF(userName);
                        out.writeUTF(userPwd);

                        System.out.println(in.readUTF());

                        String menuPlayer = sc.nextLine();
                        out.writeUTF(menuPlayer);

                        Database db = new Database();

                        //Here's the code for the actual game which is put in a for-loop.
                        //The loop-size equals number of questions available.
                        if (menuPlayer.equals("S") || menuPlayer.equals("s")) {

                            QuizHandler qh = null;
                            int nrOfQuestions = Integer.parseInt(in.readUTF());


                            for (int i = 0; i < nrOfQuestions; i++) {

                                qh = (QuizHandler) objIn.readObject();

                                System.out.println("\nQuestion: " + qh.getQuestion());
                                System.out.println("1. " + qh.getAnsOne());
                                System.out.println("2. " + qh.getAnsTwo());
                                System.out.println("3. " + qh.getAnsThree());
                                System.out.print("\nAnswer: ");

                                String answer = sc.nextLine();

                                out.writeUTF(answer);

                                System.out.println(in.readUTF());
                            }

                            System.out.println(in.readUTF());

                        } else if (menuPlayer.equals("Q") || menuPlayer.equals("q")) {
                            client.close();
                            break;
                        } else {
                            client.close();
                            break;
                        }

                    //With admin-login we can add more questions to our Quiz!
                    } else if (menuTwo.equals("A") || menuTwo.equals("a")) {
                        System.out.print("\nUsername: ");
                        String adminName = sc.nextLine();
                        System.out.print("Password: ");
                        String adminPwd = sc.nextLine();

                        out.writeUTF(adminName);
                        out.writeUTF(adminPwd);

                        System.out.println(in.readUTF());
                        String menuAdmin = sc.nextLine();

                        out.writeUTF(menuAdmin);

                        if (menuAdmin.equals("A") || menuAdmin.equals("a")) {

                            System.out.println("\nQuestion: ");
                            String q = sc.nextLine();

                            System.out.println("Option 1: ");
                            String ansOne = sc.nextLine();

                            System.out.println("Option 2: ");
                            String ansTwo = sc.nextLine();

                            System.out.println("Option 3: ");
                            String ansThree = sc.nextLine();

                            System.out.println("Right answer: ");
                            String corrAns = sc.nextLine();

                            out.writeUTF(q);
                            out.writeUTF(ansOne);
                            out.writeUTF(ansTwo);
                            out.writeUTF(ansThree);
                            out.writeUTF(corrAns);

                        } else if (menuAdmin.equals("L") || menuAdmin.equals("l")) {
                            client.close();
                            break;
                        } else {
                            client.close();
                            break;
                        }

                    } else {
                        System.out.println(in.readUTF());
                        break;
                    }

                //If a new player-account is needed.
                } else if (menuOne.equals("C") || menuOne.equals("c")) {

                    System.out.print("\nEnter desired username: ");
                    String newPlayer = sc.nextLine();

                    System.out.print("Enter desired password: ");
                    String newPwd = sc.nextLine();

                    out.writeUTF(newPlayer);
                    out.writeUTF(newPwd);

                    System.out.println(in.readUTF());

                } else if (menuOne.equals("E") || menuOne.equals("e")) {    //Code to exit the connection.
                    client.close();
                    break;

                } else {
                    System.out.println(in.readUTF());
                }


            } catch (EOFException e) {

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}