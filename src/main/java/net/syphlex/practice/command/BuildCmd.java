package net.syphlex.practice.command;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.*;
import org.bukkit.GameMode;

public class BuildCmd extends AbstractCmd {
    public BuildCmd(String command) {
        super(command);
    }

    @Override
    public void onCommand(Profile profile, String[] args) {

        if (!profile.hasPermission(Permissions.BUILD)) {
            profile.sendMessage(Messages.NO_PERMISSION);
            return;
        }

        profile.setBuild(!profile.isBuild());

        if (profile.isBuild()) {
            profile.getPlayer().setGameMode(GameMode.CREATIVE);
            profile.sendMessage("&aYou have enabled your building permissions.");
        } else {
            profile.reset();
            profile.setLobbyInventory();
            profile.teleport(Practice.get().getConfigManager().getMainSpawn());
            profile.getPlayer().setGameMode(GameMode.SURVIVAL);

            profile.sendMessage("&cYou have disabled your building permissions.");
        }
    }
}
