package net.syphlex.practice.manager.config;

import lombok.Getter;
import lombok.Setter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.util.StringUtil;
import net.syphlex.practice.Practice;
import net.syphlex.practice.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
@Setter
public class ConfigManager {

    private Location mainSpawn;

    private boolean fastPotions;

    private double potionSpeed = 1.0;


    public void onEnable(){
        File file = new File(Practice.get().getDataFolder(), "syphlex.yml");

        try {

            if (!file.exists()) {
                file.createNewFile();
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            config.options().copyDefaults(true);
            config.addDefault("main-spawn", "null");
            config.addDefault("fast-potions", true);
            config.addDefault("potion-speed", 1.0);

            config.save(file);

            mainSpawn = StringUtil.deserializeLocation(config.getString("main-spawn"));
            fastPotions = config.getBoolean("fast-potions");
            potionSpeed = config.getDouble("potion-speed");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDisable() {
        try {
            File file = new File(Practice.get().getDataFolder(), "syphlex.yml");

            if (!file.exists()) {
                file.createNewFile();
                return;
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            config.set("main-spawn", StringUtil.serializeLocation(mainSpawn));
            if (config.getBoolean("fast-potions") != fastPotions) {
                config.set("fast-potions", fastPotions);
            }

            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
