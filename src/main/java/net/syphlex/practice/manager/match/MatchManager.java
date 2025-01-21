package net.syphlex.practice.manager.match;

import lombok.Getter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.manager.profile.objects.InventorySnapshot;
import net.syphlex.practice.util.TtlHashMap;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
public class MatchManager {

    private final Map<Ladder, List<Match>> matchMap = new HashMap<>();

    private final Map<UUID, InventorySnapshot> snapshotMap = new TtlHashMap<>(TimeUnit.MINUTES, 1);

    public void onEnable(){

        for (Ladder ladder : Practice.get().getLadderManager().getLadderMap().values()) {
            matchMap.put(ladder, new ArrayList<>());
        }

    }

    public void onDisable(){
        for (Ladder ladder : matchMap.keySet()) {
            for (Match match : matchMap.get(ladder)) {
                match.endMatch(true);
            }
        }
    }

    public void removeMatch(Match match){

        // open arena to the rest of the world!!!
        match.getArena().setOpen(true);
        matchMap.get(match.getLadder()).remove(match);
    }

    public int getInMatch(Ladder ladder){
        return matchMap.get(ladder).size() * 2;
    }

    public int getTotalInMatch(){
        return Practice.get().getQueueManager().getPlayersInMatch().size();
    }

}
