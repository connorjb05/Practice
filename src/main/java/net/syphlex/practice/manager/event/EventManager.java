package net.syphlex.practice.manager.event;

import net.syphlex.practice.Practice;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class EventManager {

    private PracticeEvent activeEvent = null;

    private BukkitTask eventTask;

    public void onEnable(){

    }

    public void onDisable(){

        if (eventTask == null) {
            return;
        }

    }

    public void startTask(){
        eventTask = new BukkitRunnable(){
            @Override
            public void run(){
                if (activeEvent != null) {

                }
            }
        }.runTaskTimer(Practice.get(), 0L, 20L);
    }
}
