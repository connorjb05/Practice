package net.syphlex.practice.manager.match;

import lombok.Getter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;

import java.util.*;

@Getter
public class MatchManager {

    private final Map<Kit, List<Match>> matchMap = new HashMap<>();

    public void onEnable(){

        for (Kit kit : Practice.get().getKitManager().getKitMap().values()) {
            matchMap.put(kit, new ArrayList<>());
        }

    }

    public void onDisable(){
        for (Kit kit : matchMap.keySet()) {
            for (Match match : matchMap.get(kit)) {
                match.endMatch(true);
            }
        }
    }

    public void removeMatch(Match match){

        // open arena to the rest of the world!!!
        match.getArena().setOpen(true);

        matchMap.get(match.getKit()).remove(match);
    }

    public int getInMatch(Kit kit){
        return matchMap.get(kit).size() * 2;
    }

    public int getTotalInMatch(){
        return Practice.get().getQueueManager().getPlayersInMatch().size();
    }

}
