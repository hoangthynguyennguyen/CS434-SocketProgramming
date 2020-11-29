package utils;

import java.util.Random;

public class Question {

    private int number01, number02, operatorIndex;
    private Random random;

    private String keyword;
    private String description;

    public Question(String keyword, String description) {
        this.random = new Random();
        this.number01 = 0;
        this.number02 = 0;
        this.operatorIndex = 0;
        this.keyword = keyword;
        this.description = description;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getDescription() {
        return description;
    }

    public void generateRandom() {
        operatorIndex = random.nextInt(5);
    }

    public int getNumber01() {
        return number01;
    }

    public int getNumber02() {
        return number02;
    }


    public int getOperatorIndex() {
        return operatorIndex;
    }
}
