package net.syphlex.practice.manager.leaderboards;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class LeaderboardPlayer {
    private final UUID uuid;
    private int wins;
    private int loses;

    public String getUsername(){
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer == null) {
            return "N/A";
        }
        return offlinePlayer.getName();
    }

    public float getWLR(){
        float wlr = (float)Math.min(wins, 1) / (float)Math.min(loses, 1);
        return Math.round(wlr * 100) / 100.0f;
    }
}
