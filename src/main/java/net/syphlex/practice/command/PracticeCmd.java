package net.syphlex.practice.command;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.AbstractCmd;
import net.syphlex.practice.util.Messages;
import net.syphlex.practice.util.Permissions;
import net.syphlex.practice.util.StringUtil;

public class PracticeCmd extends AbstractCmd {
    public PracticeCmd(String command) {
        super(command);
    }

    @Override
    public void onCommand(Profile profile, String[] args) {


        if (args.length == 1 && args[0].equalsIgnoreCase("setmainspawn")) {

            if (!profile.hasPermission(Permissions.SET_SPAWN)) {
                profile.sendMessage(Messages.NO_PERMISSION);
                return;
            }

            profile.getPlayer().sendMessage(StringUtil.CC("&aYou have set the main server spawn."));
            Practice.get().getConfigManager().setMainSpawn(profile.getPlayer().getLocation());

        } else if (args.length == 1 && (args[0].equalsIgnoreCase("fastpotions")
                || args[0].equalsIgnoreCase("fastpot"))) {

            if (!profile.hasPermission(Permissions.ADMIN)) {
                profile.sendMessage(Messages.NO_PERMISSION);
                return;
            }

            Practice.get().getConfigManager().setFastPotions(!Practice.get().getConfigManager().isFastPotions());

            if (Practice.get().getConfigManager().isFastPotions()) {
                profile.sendMessage("&aYou have enabled fast potions.");
            } else {
                profile.sendMessage("&cYou have disabled fast potions.");
            }
        } else {
            // todo
        }
    }
}
