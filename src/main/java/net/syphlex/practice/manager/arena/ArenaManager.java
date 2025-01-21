package net.syphlex.practice.manager.arena;

import lombok.Getter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class ArenaManager {

    private final Random random = new Random();

    private File file;
    private FileConfiguration config;

    @Getter
    private final Map<String, Arena> arenaMap = new TreeMap<>();

    public void onEnable() {
        try {

            file = new File(Practice.get().getDataFolder(), "arenas.yml");

            if (!file.exists()) {
                file.createNewFile();
            }

            config = YamlConfiguration.loadConfiguration(file);

            for (String arenaName : config.getConfigurationSection("").getKeys(false)) {

                Location pos1 = StringUtil.deserializeLocation(config.getString(arenaName + ".pos1"));
                Location pos2 = StringUtil.deserializeLocation(config.getString(arenaName + ".pos2"));
                Location corner1 = StringUtil.deserializeLocation(config.getString(arenaName + ".corner1"));
                Location corner2 = StringUtil.deserializeLocation(config.getString(arenaName + ".corner2"));
                Location spectate = StringUtil.deserializeLocation(config.getString(arenaName + ".spectate"));
                List<Ladder> ladderList = StringUtil.deserializeKits(config.getStringList(arenaName + ".kits"));

                Arena arena = new Arena(arenaName);
                arena.setPosition1(pos1);
                arena.setPosition2(pos2);
                arena.setCorner1(corner1);
                arena.setCorner2(corner2);
                arena.setSpectate(spectate);
                arena.getLadders().addAll(ladderList);

                arenaMap.put(arenaName, arena);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDisable(){

        if (arenaMap.isEmpty()) {
            return;
        }

        try {

            if (!file.exists()) {
                file.createNewFile();
                return;
            }

            Set<String> arenasInConfig = config.getKeys(false);

            for (String configArena : arenasInConfig) {
                if (!arenaMap.containsKey(configArena)) {
                    config.set(configArena, null);
                }
            }

            for (Arena arena : arenaMap.values()) {
                config.set(arena.getName() + ".pos1", StringUtil.serializeLocation(arena.getPosition1()));
                config.set(arena.getName() + ".pos2", StringUtil.serializeLocation(arena.getPosition2()));
                config.set(arena.getName() + ".corner1", StringUtil.serializeLocation(arena.getCorner1()));
                config.set(arena.getName() + ".corner2", StringUtil.serializeLocation(arena.getCorner2()));
                config.set(arena.getName() + ".spectate", StringUtil.serializeLocation(arena.getSpectate()));
                config.set(arena.getName() + ".kits", StringUtil.serializeKits(arena.getLadders()));
            }

            config.save(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean arenaExists(String arenaName){
        return arenaMap.containsKey(arenaName);
    }

    public Arena getFreeArena(Ladder ladder) {

        if (arenaMap.isEmpty()) {
            Practice.get().getLogger().log(Level.SEVERE, "No arenas were found when attempting match creation!");
            return null;
        }

        List<Arena> availableMaps = new ArrayList<>();
        for (Arena arena : arenaMap.values()) {
            if (arena.getLadders().contains(ladder) && arena.isOpen()) {
                availableMaps.add(arena);
            }
        }

        if (availableMaps.isEmpty()) {
            Practice.get().getLogger().log(Level.SEVERE, "No arenas were found when attempting match creation!");
            return null;
        }

        return availableMaps.get(random.nextInt(availableMaps.size()));
    }
}
