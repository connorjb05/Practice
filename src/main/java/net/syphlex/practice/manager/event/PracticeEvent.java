package net.syphlex.practice.manager.event;

import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PracticeEvent {

    private final Map<Profile, Pair<Integer, Boolean>> playerMap = new HashMap<>();

    private final List<Profile> spectators = new ArrayList<>();

    private final int gameTime = 60;

    public void startEvent(){

    }

    public void endEvent(){

    }

    public abstract void run();
}
