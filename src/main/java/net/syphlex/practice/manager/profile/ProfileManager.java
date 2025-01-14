package net.syphlex.practice.manager.profile;

import lombok.Getter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.manager.profile.objects.PlayerSettings;
import net.syphlex.practice.util.InventoryUtil;
import net.syphlex.practice.util.ItemUtil;
import net.syphlex.practice.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ProfileManager {

    private final Map<UUID, Profile> profileMap = new ConcurrentHashMap<>();

    public void onEnable() {

        File dir = new File(Practice.get().getDataFolder(), "/userdata/");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        Bukkit.getOnlinePlayers().forEach(this::join);
    }

    public void onDisable() {
        profileMap.values().forEach(p -> quit(p.getPlayer()));
    }

    public void join(Player player) {
        Profile profile = new Profile(player);
        profileMap.put(player.getUniqueId(), profile);

        PlayerUtil.resetPlayer(player);
        profile.teleport(Practice.get().getConfigManager().getMainSpawn());
        InventoryUtil.setSpawnInventory(player);

        Practice.get().getThreadManager().getService().execute(() -> {

            File dir = new File(Practice.get().getDataFolder(), "/userdata/");

            if (!dir.exists()) {
                dir.mkdirs();
            }

            synchronized (profile.getFileLock()) {

                File userFile = new File(dir, player.getUniqueId().toString() + ".yml");

                try {

                    if (!userFile.exists()) {
                        createUserFile(userFile);
                    }

                    YamlConfiguration config = YamlConfiguration.loadConfiguration(userFile);

                    profile.setSetting(PlayerSettings.DUEL_REQUEST, config.getBoolean("settings.duel_requests"));
                    profile.setSetting(PlayerSettings.PARTY_INVITES, config.getBoolean("settings.party_invites"));
                    profile.setSetting(PlayerSettings.PRIVATE_MESSAGES, config.getBoolean("settings.private_messages"));
                    profile.setSetting(PlayerSettings.SCOREBOARD, config.getBoolean("settings.scoreboard"));
                    profile.setSetting(PlayerSettings.GLOBAL_CHAT, config.getBoolean("settings.global_chat"));
                    profile.setSetting(PlayerSettings.IN_MATCH_CHAT, config.getBoolean("settings.in_match_chat"));

                    if (config.contains("kit-presets") && config.getConfigurationSection("kit-presets") != null) {
                        for (String kitName : config.getConfigurationSection("kit-presets").getKeys(false)) {

                            Kit kit = Practice.get().getKitManager().getKitMap().get(kitName);

                            if (config.contains("kit-presets." + kitName + ".preset")) {
                                List<String> inventoryData = config.getStringList(
                                        "kit-presets." + kitName + ".preset");

                                ItemStack[] inventory = ItemUtil.deserializeItemStack(inventoryData);

                                profile.getKitPresets().put(kit, inventory);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void quit(Player player) {
        Profile profile = profileMap.remove(player.getUniqueId());

        if (profile.isInMatch()) {
            profile.getMatch().eliminate(profile);
        }

        if (profile.isInParty()) {
            Practice.get().getPartyManager().onLeaveOrDisband(profile);
        }

        Practice.get().getQueueManager().dequeue(profile);

        if (profile.isInParty()) {
            Practice.get().getPartyManager().onLeaveOrDisband(profile);
        }

        Practice.get().getThreadManager().getService().execute(() -> {

            File dir = new File(Practice.get().getDataFolder(), "/userdata/");

            if (!dir.exists()) {
                dir.mkdirs();
            }

            synchronized (profile.getFileLock()) {

                File userFile = new File(dir, player.getUniqueId().toString() + ".yml");

                try {

                    if (!userFile.exists()) {
                        createUserFile(userFile);
                        return;
                    }

                    YamlConfiguration config = YamlConfiguration.loadConfiguration(userFile);

                    config.set("settings.duel_requests", profile.getSetting(PlayerSettings.DUEL_REQUEST));
                    config.set("settings.party_invites", profile.getSetting(PlayerSettings.PARTY_INVITES));
                    config.set("settings.private_messages", profile.getSetting(PlayerSettings.PRIVATE_MESSAGES));
                    config.set("settings.scoreboard", profile.getSetting(PlayerSettings.SCOREBOARD));
                    config.set("settings.global_chat", profile.getSetting(PlayerSettings.GLOBAL_CHAT));
                    config.set("settings.in_match_chat", profile.getSetting(PlayerSettings.IN_MATCH_CHAT));

                    for (Map.Entry<Kit, ItemStack[]> entry : profile.getKitPresets().entrySet()) {

                        Kit kit = entry.getKey();
                        ItemStack[] inventory = entry.getValue();

                        config.set("kit-presets." + kit.getName() + ".preset",
                                ItemUtil.serializeItemStack(inventory));
                    }

                    config.save(userFile);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void createUserFile(File userFile) {
        try {
            userFile.createNewFile();

            YamlConfiguration config = YamlConfiguration.loadConfiguration(userFile);

            config.set("settings.duel_requests", true);
            config.set("settings.party_invites", true);
            config.set("settings.private_messages", true);
            config.set("settings.scoreboard", true);
            config.set("settings.global_chat", true);
            config.set("settings.in_match_chat", true);

            config.save(userFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Profile get(Player p) {
        return profileMap.get(p.getUniqueId());
    }
}

