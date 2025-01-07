package net.syphlex.practice.manager.leaderboards;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.Practice;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class LeaderboardManager {

    private final Map<Kit, List<LeaderboardPlayer>> leaderboardMap = new ConcurrentHashMap<>();
    private final Map<Kit, Map<UUID, LeaderboardPlayer>> playerDataMap = new ConcurrentHashMap<>();

    public void onEnable(){

        // load leaderboards for each kit/ladder
        for (Kit kit : Practice.get().getKitManager().getKitMap().values()) {
            leaderboardMap.put(kit, new ArrayList<>());
            playerDataMap.put(kit, new ConcurrentHashMap<>());
        }

        // load player data into cache maps
        for (Kit kit : leaderboardMap.keySet()) {

            try {

                File dir = new File(Practice.get().getDataFolder(), "/userdata/");

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(dir, kit.getName() + ".yml");

                if (!file.exists()) {
                    file.createNewFile();
                }

                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

                if (config.get("") != null) {
                    for (String section : config.getConfigurationSection("").getKeys(false)) {

                        String[] split = section.split(";");

                        UUID uuid = UUID.fromString(split[0]);
                        int wins = Integer.parseInt(split[1]);
                        int loses = Integer.parseInt(split[2]);

                        LeaderboardPlayer leaderboardPlayer = new LeaderboardPlayer(uuid, wins, loses);

                        playerDataMap.get(kit).put(uuid, leaderboardPlayer);
                    }

                    Practice.get().getLogger().log(Level.INFO,
                            "Successfully loaded player data for the "
                                    + kit.getName() + " kit!");
                }

            } catch (Exception e ){
                e.printStackTrace();
            }
        }

        // start refresh clock
        new BukkitRunnable(){
            @Override
            public void run(){

                for (Kit kit : leaderboardMap.keySet()) {

                    Map<UUID, LeaderboardPlayer> playerData = playerDataMap.get(kit);

                    List<LeaderboardPlayer> leaderboardList = playerData.values().stream()
                            .sorted((p1, p2) -> Integer.compare(p2.getWins(), p1.getWins()))
                            .limit(10)
                            .collect(Collectors.toList());

                    leaderboardMap.put(kit, leaderboardList);
                }

            }
        }.runTaskTimerAsynchronously(Practice.get(), 0L, 36000L); // 30 minutes
    }

    public void onDisable(){
        for (Kit kit : playerDataMap.keySet()) {

            try {

                File dir = new File(Practice.get().getDataFolder(), "/userdata/");

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(dir, kit.getName() + ".yml");

                if (!file.exists()) {
                    file.createNewFile();
                    continue;
                }

                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

                for (LeaderboardPlayer leaderboardPlayer : playerDataMap.get(kit).values()) {
                    config.createSection(leaderboardPlayer.getUuid().toString()
                            + ";" + leaderboardPlayer.getWins()
                            + ";" + leaderboardPlayer.getLoses());
                }

                config.save(file);

                Practice.get().getLogger().log(Level.INFO,
                        "Successfully stored player data for the "
                                + kit.getName() + " kit!");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<LeaderboardPlayer> getLeaderboard(Kit kit){
        return leaderboardMap.getOrDefault(kit, Collections.emptyList());
    }

    public void updateLeaderboardPlayer(Kit kit, Player player, int wins, int loses){
        UUID uuid = player.getUniqueId();
        playerDataMap.computeIfAbsent(kit, k -> new ConcurrentHashMap<>())
                .compute(uuid, (key, existing) -> {
                    if (existing == null) {
                        return new LeaderboardPlayer(uuid, wins, loses);
                    }
                    existing.setWins(existing.getWins() + wins);
                    existing.setLoses(existing.getLoses() + loses);
                    return existing;
                });
    }
}
