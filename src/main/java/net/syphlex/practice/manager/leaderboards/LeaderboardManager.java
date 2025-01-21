package net.syphlex.practice.manager.leaderboards;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.EloUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LeaderboardManager {

    private final Map<Ladder, List<LeaderboardPlayer>> leaderboardMap = new ConcurrentHashMap<>();
    private final Map<Ladder, Map<UUID, LeaderboardPlayer>> playerDataMap = new ConcurrentHashMap<>();

    public void onEnable(){

        // load leaderboards for each kit/ladder
        for (Ladder ladder : Practice.get().getLadderManager().getLadderMap().values()) {
            leaderboardMap.put(ladder, new ArrayList<>());
            playerDataMap.put(ladder, new ConcurrentHashMap<>());
        }

        // load player data into cache maps
        for (Ladder ladder : leaderboardMap.keySet()) {

            try {

                File dir = new File(Practice.get().getDataFolder(), "/leaderboards/");

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(dir, ladder.getName() + ".yml");

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
                        int elo = Integer.parseInt(split[3]);

                        LeaderboardPlayer leaderboardPlayer = new LeaderboardPlayer(uuid, elo, wins, loses);

                        playerDataMap.get(ladder).put(uuid, leaderboardPlayer);
                    }
                }

            } catch (Exception e ){
                e.printStackTrace();
            }
        }

        Practice.get().getLogger().info("Successfully loaded leaderboards for all kits.");

        // start refresh clock
        new BukkitRunnable(){
            @Override
            public void run(){

                for (Ladder ladder : leaderboardMap.keySet()) {

                    Map<UUID, LeaderboardPlayer> playerData = playerDataMap.get(ladder);

                    List<LeaderboardPlayer> leaderboardList = playerData.values().stream()
                            .sorted((p1, p2) -> Integer.compare(p2.getElo(), p1.getElo()))
                            .limit(10)
                            .collect(Collectors.toList());

                    leaderboardMap.put(ladder, leaderboardList);
                }

            }
        }.runTaskTimerAsynchronously(Practice.get(), 0L, 36000L); // 30 minutes
    }

    public void onDisable(){
        for (Ladder ladder : playerDataMap.keySet()) {

            try {

                File dir = new File(Practice.get().getDataFolder(), "/leaderboards/");

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(dir, ladder.getName() + ".yml");

                if (!file.exists()) {
                    file.createNewFile();
                    continue;
                }

                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

                for (LeaderboardPlayer leaderboardPlayer : playerDataMap.get(ladder).values()) {
                    config.createSection(leaderboardPlayer.getUuid().toString()
                            + ";" + leaderboardPlayer.getWins()
                            + ";" + leaderboardPlayer.getLoses()
                            + ";" + leaderboardPlayer.getElo());
                }

                config.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Practice.get().getLogger().info("Successfully saved and stored leaderboards for all kits.");
    }

    public LeaderboardPlayer getLeaderboardPlayer(Ladder ladder, UUID uuid){
        return playerDataMap.get(ladder).get(uuid);
    }

    public List<LeaderboardPlayer> getLeaderboard(Ladder ladder){
        return leaderboardMap.getOrDefault(ladder, Collections.emptyList());
    }

    public void updateLeaderboardPlayer(Ladder ladder, Profile profile, int wins, int loses){

        UUID uuid = profile.getPlayer().getUniqueId();

        int opponentElo = getElo(profile.getMatch().getOpponents(profile).getAsList().get(0).getPlayer().getUniqueId(), ladder);

        int profileElo = getElo(uuid, ladder);

        int eloChange = EloUtil.calculateEloChange(profileElo, opponentElo, loses == 0);

        playerDataMap.computeIfAbsent(ladder, k -> new ConcurrentHashMap<>())
                .compute(uuid, (key, existing) -> {
                    if (existing == null) {
                        return new LeaderboardPlayer(uuid, 1000 + eloChange, wins, loses);
                    }
                    existing.setWins(existing.getWins() + wins);
                    existing.setLoses(existing.getLoses() + loses);
                    existing.setElo(existing.getElo() + eloChange);
                    return existing;
                });
    }

    public int getElo(UUID uuid, Ladder ladder){
        if (!playerDataMap.get(ladder).containsKey(uuid)) {
            return 1000;
        }
        return playerDataMap.get(ladder).get(uuid).getElo();
    }
}
