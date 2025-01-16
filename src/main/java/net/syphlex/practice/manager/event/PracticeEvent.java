package net.syphlex.practice.manager.event;

import lombok.Getter;
import lombok.Setter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public abstract class PracticeEvent implements Listener {

    private final String identifier, displayName;
    private final YamlConfiguration config;

    // profile : (team #, alive?)
    private final Map<Profile, Pair<Integer, Boolean>> playerMap = new ConcurrentHashMap<>();
    private final Map<String, Object> configMap = new HashMap<>();

    private Location spectateLoc;

    public int gameTime = 60;
    public int round = 0;

    public EventState eventState = EventState.STARTING;

    public PracticeEvent(String identifier, String displayName, YamlConfiguration config){
        this.identifier = identifier;
        this.displayName = displayName;
        this.config = config;

        Bukkit.getPluginManager().registerEvents(this, Practice.get());
    }

    public void addConfigValue(String path, Object value){
        config.addDefault(path, value);
        configMap.put(path, value);
    }

    public void setConfigValue(String path, Object value){
        configMap.put(path, value);
    }

    public void endEvent(){

        for (Profile profile : playerMap.keySet()) {
            PlayerUtil.resetPlayer(profile.getPlayer());
            InventoryUtil.setSpawnInventory(profile.getPlayer());
            profile.teleport(Practice.get().getConfigManager().getMainSpawn());
        }

        gameTime = 60;
        round = 0;
        eventState = EventState.STARTING;

        playerMap.clear();
    }

    public void joinEvent(Profile profile){

    }

    public void leaveEvent(Profile profile){

    }

    public void spectateEvent(Profile profile){
        profile.sendMessage(Practice.QUATERNARY_COLOR + "You are now spectating the event.");
        playerMap.put(profile, new Pair<>(0, false));

        PlayerUtil.resetPlayer(profile.getPlayer());
        profile.teleport(spectateLoc);

        profile.getPlayer().getInventory().setItem(8, ItemUtil.getLeaveEventItem());
    }

    public void eliminate(Profile profile, Profile eliminated){

        spectateEvent(eliminated);

        if (profile == null) {
            sendEventMessage(Messages.EVENT_CHAT_PREFIX.get()
                    + Practice.PRIMARY_COLOR + eliminated.getPlayer().getName()
                    + Practice.QUATERNARY_COLOR + " was eliminated. &7(" + getEventAlive().size() + ")");
            return;
        }

        sendEventMessage(Messages.EVENT_CHAT_PREFIX.get()
                + Practice.PRIMARY_COLOR + eliminated.getPlayer().getName()
                + Practice.QUATERNARY_COLOR + " was eliminated by "
                + Practice.PRIMARY_COLOR + profile.getPlayer().getName()
                + Practice.QUATERNARY_COLOR + ". &7(" + getEventAlive().size() + ")");
    }

    public void setTeam(Profile profile, int teamNumber){
        playerMap.put(profile, new Pair<>(teamNumber, true));
    }

    public List<Profile> getTeam(int teamNumber){
        List<Profile> playerList = new ArrayList<>();
        for (Profile profile : playerMap.keySet()) {
            if (playerMap.get(profile).getX() == teamNumber) {
                playerList.add(profile);
            }
        }
        return playerList;
    }

    public List<Profile> getTeamAlive(int teamNumber){
        List<Profile> playerList = new ArrayList<>();
        for (Map.Entry<Profile, Pair<Integer, Boolean>> entry : playerMap.entrySet()) {
            if (entry.getValue().getX() == teamNumber && entry.getValue().getY()) {
                playerList.add(entry.getKey());
            }
        }
        return playerList;
    }

    public List<Profile> getEventAlive(){
        List<Profile> playerList = new ArrayList<>();
        for (Map.Entry<Profile, Pair<Integer, Boolean>> entry : playerMap.entrySet()) {
            if (entry.getValue().getY()) {
                playerList.add(entry.getKey());
            }
        }
        return playerList;
    }

    public void sendEventMessage(String message){
        for (Profile profile : playerMap.keySet()){
            profile.sendMessage(message);
        }
    }

    public void run(){
        switch (eventState) {
            case STARTING:

                if (gameTime-- == 60
                        || gameTime == 30
                        || gameTime == 3
                        || gameTime == 2
                        || gameTime == 1) {
                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage(StringUtil.CC(Messages.EVENT_CHAT_PREFIX.get()
                            + Practice.PRIMARY_COLOR + "<player> "
                            + Practice.QUATERNARY_COLOR + "is hosting a "
                            + Practice.PRIMARY_COLOR + "<event> Event"
                            + Practice.QUATERNARY_COLOR + "! &a(Click to join)"));
                    Bukkit.broadcastMessage(" ");
                }

                if (gameTime == 0) {
                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage(StringUtil.CC(Messages.EVENT_CHAT_PREFIX.get()
                            + Practice.QUATERNARY_COLOR
                            + "The event has started! &a(Click to join)"));
                    Bukkit.broadcastMessage(" ");
                    eventState = EventState.ONGOING;
                }
                break;
            case ENDED:
                endEvent();
                break;
        }
    }

    @EventHandler
    public void onEntityDamageEntityEvent(EntityDamageByEntityEvent e){
    }

    public enum EventState {
        STARTING, ONGOING, ENDED;
    }
}
