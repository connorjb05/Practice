package net.syphlex.practice.manager.profile;

import lombok.Getter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.match.MatchState;
import net.syphlex.practice.util.InventoryUtil;
import net.syphlex.practice.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ProfileManager {

    private final Map<UUID, Profile> profileMap = new ConcurrentHashMap<>();

    public void onEnable(){
        Bukkit.getOnlinePlayers().forEach(this::join);
    }

    public void onDisable(){
        profileMap.values().forEach(p -> quit(p.getPlayer()));
    }

    public void join(Player player){
        Profile profile = new Profile(player);
        profileMap.put(player.getUniqueId(), profile);

        PlayerUtil.resetPlayer(player);
        profile.teleport(Practice.get().getConfigManager().getMainSpawn());
        InventoryUtil.setSpawnInventory(player);
    }

    public void quit(Player player){
        Profile profile = profileMap.remove(player.getUniqueId());

        if (profile.isInMatch()) {
            profile.getMatch().eliminate(profile);
        }

        if (profile.isInParty()) {
            Practice.get().getPartyManager().onLeaveOrDisband(profile);
        }

        Practice.get().getQueueManager().dequeue(profile);

        if (profile.isInParty()){
            Practice.get().getPartyManager().onLeaveOrDisband(profile);
        }
    }

    public Profile get(Player p){
        return profileMap.get(p.getUniqueId());
    }
}
