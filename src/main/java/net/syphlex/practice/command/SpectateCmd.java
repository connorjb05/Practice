package net.syphlex.practice.command;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.AbstractCmd;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpectateCmd extends AbstractCmd {
    public SpectateCmd(String command) {
        super(command);
    }

    @Override
    public void onCommand(Profile profile, String[] args) {

        if (profile.isInMatch()) {
            profile.sendMessage("&cYou cannot spectate a match while in a match.");
            return;
        }

        if (args.length == 1) {

            final Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                profile.sendMessage("&cMatch not found.");
                return;
            }

            final Profile targetProfile = Practice.get().getProfileManager().get(target);

            if (!targetProfile.isInMatch()) {
                profile.sendMessage("&cMatch not found.");
                return;
            }

            if (profile.isSpectatingMatch()) {
                profile.stopSpectatingMatch();
            } else {
                profile.startSpectatingMatch(targetProfile.getMatch());
            }

        } else {

        }
    }
}
