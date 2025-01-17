package net.syphlex.practice.manager.ladder;

import lombok.Getter;
import net.syphlex.practice.manager.ladder.impl.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class LadderManager {

    private final Map<String, Ladder> ladderMap = new LinkedHashMap<>();

    public void onEnable(){
        ladderMap.put("NoDebuff", new NoDebuffLadder("NoDebuff"));
        ladderMap.put("BedFight", new BedFightLadder("BedFight"));
        ladderMap.put("Sumo", new SumoLadder("Sumo"));
        ladderMap.put("Boxing", new BoxingLadder("Boxing"));
        ladderMap.put("Bridge", new BridgeLadder("Bridge"));
        ladderMap.put("Combo", new ComboLadder("Combo"));
        ladderMap.put("Bow", new BowLadder("Bow"));
        ladderMap.put("Soup", new SoupLadder("Soup"));
        ladderMap.put("BuildUHC", new BuildUHCLadder("Build UHC"));
        ladderMap.put("Gapple", new GappleLadder("Gapple"));
    }

    public boolean kitExists(String kitName){
        return ladderMap.containsKey(kitName);
    }
}
