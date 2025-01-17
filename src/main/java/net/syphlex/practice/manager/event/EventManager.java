package net.syphlex.practice.manager.event;

import lombok.Getter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.event.impl.SumoEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class EventManager {

    private final List<PracticeEvent> events = new ArrayList<>();

    private PracticeEvent activeEvent = null;

    private BukkitTask eventTask;

    public void onEnable(){

        File file = new File(Practice.get().getDataFolder(), "events.yml");

        try {
            if (!file.exists()) {
            file.createNewFile();
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            config.options().copyDefaults(true);

            events.add(new SumoEvent(EventInfo.SUMO1V1, config));

            config.save(file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDisable(){

        File file = new File(Practice.get().getDataFolder(), "events.yml");

        try {

            if (!file.exists()) {
                file.createNewFile();
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            for (PracticeEvent event : events) {
                for (Map.Entry<String, Object> entry : event.getConfigMap().entrySet()) {
                    config.set(entry.getKey(), entry.getValue());
                }
            }

            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (eventTask == null) {
            return;
        }

    }

    public void hostEvent(PracticeEvent e){

        if (activeEvent != null) {
            return;
        }

        activeEvent = e;
        startTask();
    }

    public void endEvent(){

        if (activeEvent == null) {
            return;
        }

        activeEvent.endEvent();
        eventTask.cancel();

        activeEvent = null;
    }

    public void startTask(){
        eventTask = new BukkitRunnable(){
            @Override
            public void run(){
                if (activeEvent != null) {
                    activeEvent.run();
                }
            }
        }.runTaskTimer(Practice.get(), 0L, 20L);
    }
}
