package net.syphlex.practice.manager.event.impl;

import net.syphlex.practice.manager.event.PracticeEvent;
import org.bukkit.configuration.file.YamlConfiguration;

public class SumoEvent extends PracticeEvent {

    public int teamSize = 1; // default size (can be max 3)

    public SumoEvent(String identifier, String displayName, YamlConfiguration config) {
        super(identifier, displayName, config);

        if (identifier.equalsIgnoreCase("sumo1v1")) {
            teamSize = 1;
        } else if (identifier.equalsIgnoreCase("sumo2v2")) {
            teamSize = 2;
        } else if (identifier.equalsIgnoreCase("sumo3v3")) {
            teamSize = 3;
        }


    }

    @Override
    public void run(){
        super.run();

        switch (eventState) {
            case ONGOING:

                if (round == 0) {


                }

                break;
        }
    }
}
