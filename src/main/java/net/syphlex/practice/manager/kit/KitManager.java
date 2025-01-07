package net.syphlex.practice.manager.kit;

import lombok.Getter;
import net.syphlex.practice.manager.kit.impl.*;

import java.util.HashMap;
import java.util.Map;

@Getter
public class KitManager {

    private final Map<String, Kit> kitMap = new HashMap<>();

    public void onEnable(){
        kitMap.put("NoDebuff", new NoDebuffKit("NoDebuff"));
        kitMap.put("BedFight", new BedFightKit("BedFight"));
        kitMap.put("Sumo", new SumoKit("Sumo"));
        kitMap.put("Boxing", new BoxingKit("Boxing"));
        kitMap.put("Bridges", new BridgeKit("Bridges"));
        kitMap.put("Combo", new ComboKit("Combo"));
        kitMap.put("Bow", new BowKit("Bow"));
    }

    public boolean kitExists(String kitName){
        return kitMap.containsKey(kitName);
    }
}
