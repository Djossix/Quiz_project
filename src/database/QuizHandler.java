package database;

public class QuizHandler implements java.io.Serializable {

    private String qNumber, question, ansOne, ansTwo, ansThree;

    //Creating a class for holding values for getQuiz.
    public QuizHandler(String qNr, String q, String ansOne, String ansTwo, String ansThree) {

        this.qNumber = qNr;
        this.question = q;
        this.ansOne = ansOne;
        this.ansTwo = ansTwo;
        this.ansThree = ansThree;
    }

    public String getqNumber() {
        return this.qNumber;
    }

    public String getQuestion() {
        return this.question;
    }

    public String getAnsOne() {
        return this.ansOne;
    }

    public String getAnsTwo() {
        return this.ansTwo;
    }

    public String getAnsThree() {
        return this.ansThree;
    }

}
