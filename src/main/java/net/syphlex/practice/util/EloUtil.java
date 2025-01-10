package net.syphlex.practice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EloUtil {

    private static final int K_FACTOR = 30;

    private double calculateExpectedScore(int profileElo, int opponentElo){
        return  1.0 / (1 + Math.pow(10, (opponentElo - profileElo) / 400.0));
    }


    public int calculateEloChange(int profileElo, int opponentElo, boolean won){

        double expectedScore = 1.0 / (1 + Math.pow(10, (opponentElo - profileElo) / 400.0));

        double actualScore = won ? 1.0 : 0.0;

        return (int) (K_FACTOR * (actualScore - expectedScore));
    }
}
