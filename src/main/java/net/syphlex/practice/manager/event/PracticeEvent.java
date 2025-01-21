package net.syphlex.practice.manager.event;

import lombok.Getter;
import lombok.Setter;
import net.syphlex.core.Core;
import net.syphlex.core.playerdata.PlayerData;
import net.syphlex.practice.Practice;
import net.syphlex.practice.event.ProfileDamageEvent;
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

    private final EventInfo info;
    private final YamlConfiguration config;

    // profile : (team #, alive?)
    private final Map<Profile, Pair<Integer, Boolean>> playerMap = new ConcurrentHashMap<>();
    private final Map<String, Object> configMap = new HashMap<>();

    private String displayName = "";

    private Location spectateLoc;

    private Profile host;

    public int gameTime = 60;
    public int round = 0;

    public int teamSize = 1; // default size (can be max 3)

    public EventState eventState = EventState.STARTING;

    public PracticeEvent(EventInfo info, YamlConfiguration config){
        this.info = info;
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

    public void startEvent(){}

    public void endEvent(){

        for (Profile profile : playerMap.keySet()) {
            profile.reset();
            profile.setLobbyInventory();
            profile.teleport(Practice.get().getConfigManager().getMainSpawn());
        }

        gameTime = 60;
        round = 0;
        teamSize = 1;
        eventState = EventState.STARTING;
        displayName = info.getDisplayName();

        playerMap.clear();
    }

    public void joinEvent(Profile profile){

        profile.setLastAttacker(null);
        playerMap.put(profile, new Pair<>(-1, true));

        profile.reset();
        profile.teleport(spectateLoc);

        sendEventMessage("&a" + profile.getPlayer().getName()
                + " has joined the event. &7(" + getEventAlive() + ")");
    }

    public void leaveEvent(Profile profile){

        profile.setLastAttacker(null);
        playerMap.remove(profile);

        sendEventMessage("&c" + profile.getPlayer().getName() + " has left the event.");

        profile.sendMessage("&cYou have left the event.");

        profile.reset();
        profile.setLobbyInventory();
        profile.teleport(Practice.get().getConfigManager().getMainSpawn());
    }

    public void spectateEvent(Profile profile){
        profile.sendMessage(Practice.QUATERNARY_COLOR + "You are now spectating the event.");
        playerMap.put(profile, new Pair<>(0, false));

        sendEventMessage(Messages.EVENT_CHAT_PREFIX.get()
                + Practice.PRIMARY_COLOR + profile.getPlayer().getName()
                + Practice.QUATERNARY_COLOR + " is spectating the event.");

        profile.reset();
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

    public boolean isSpectating(Profile profile) {
        if (!playerMap.containsKey(profile)) {
            return false;
        }
        return !playerMap.get(profile).getY();
    }

    public boolean isAlive(Profile profile){
        if (!playerMap.containsKey(profile)) {
            return false;
        }
        return playerMap.get(profile).getY();
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

                startEvent();

                if (gameTime-- == 60
                        || gameTime == 30
                        || gameTime == 3
                        || gameTime == 2
                        || gameTime == 1) {

                    String hostName = "&c&lConsole";

                    if (host != null) {
                        PlayerData data = Core.get().getPlayerDataManager().get(host.getPlayer());
                        hostName = data.getRank().getColor() + host.getPlayer().getName();
                    }

                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage(StringUtil.CC(Messages.EVENT_CHAT_PREFIX.get()
                            + Practice.PRIMARY_COLOR + hostName + " "
                            + Practice.QUATERNARY_COLOR + "is hosting a "
                            + Practice.PRIMARY_COLOR + displayName
                            + Practice.QUATERNARY_COLOR + " Event! &a(Click to join)"));
                    Bukkit.broadcastMessage(StringUtil.CC("&7&oEvent is starting in "
                            + gameTime + " seconds..."));
                    Bukkit.broadcastMessage(" ");
                }

                if (gameTime == 0) {
                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage(StringUtil.CC(Messages.EVENT_CHAT_PREFIX.get()
                            + Practice.QUATERNARY_COLOR
                            + "The event has started! &a(Click to spectate)"));
                    Bukkit.broadcastMessage(" ");
                    eventState = EventState.ONGOING;
                }
                break;
            case ONGOING:
                gameTime--;
                break;
            case ENDED:
                endEvent();
                break;
        }
    }

    @EventHandler
    public void onProfileDamageEvent(ProfileDamageEvent e) {

        final Profile profile = e.getVictim();

        if (profile == null) {
            return;
        }

        if (playerMap.containsKey(profile)) {

            if (eventState == EventState.STARTING || isSpectating(profile)) {
                //e.setCancelled(true);
                return;
            }
        }
    }

    public enum EventState {
        STARTING, ONGOING, ENDED;
    }
}
