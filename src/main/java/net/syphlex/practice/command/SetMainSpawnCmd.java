package net.syphlex.practice.command;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.AbstractCmd;
import net.syphlex.practice.util.Messages;
import net.syphlex.practice.util.Permissions;
import net.syphlex.practice.util.StringUtil;

public class SetMainSpawnCmd extends AbstractCmd {
    public SetMainSpawnCmd(String command) {
        super(command);
    }

    @Override
    public void onCommand(Profile profile, String[] args) {

        if (!profile.hasPermission(Permissions.SET_SPAWN)) {
            profile.sendMessage(Messages.NO_PERMISSION);
            return;
        }

        profile.getPlayer().sendMessage(StringUtil.CC("&aYou have set the main server spawn."));
        Practice.get().getConfigManager().setMainSpawn(profile.getPlayer().getLocation());

    }
}
