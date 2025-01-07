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

    public void onEnable(){
        File file = new File(Practice.get().getDataFolder(), "syphlex.yml");

        try {

            if (!file.exists()) {
                file.createNewFile();
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            config.options().copyDefaults(true);
            config.addDefault("main-spawn", "null");

            config.save(file);

            mainSpawn = StringUtil.deserializeLocation(config.getString("main-spawn"));

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

            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
