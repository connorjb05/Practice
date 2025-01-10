package net.syphlex.practice.manager.bot;

import lombok.Getter;

@Getter
public enum BotDifficulty {
    EASY(2.0, 9),
    MODERATE(2.5, 13),
    HARD(3.0, 17);

    private final double reach;
    private final int cps;

    BotDifficulty(final double reach, final int cps){
        this.reach = reach;
        this.cps = cps;
    }
}
