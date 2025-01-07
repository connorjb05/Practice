package net.syphlex.practice.command;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.AbstractCmd;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PartyCmd extends AbstractCmd {
    public PartyCmd(String command) {
        super(command);
    }

    @Override
    public void onCommand(Profile profile, String[] args) {

        if (args.length == 2) {

            final Player player = Bukkit.getPlayer(args[1]);

            if (player == null) {
                profile.sendMessage("&cThat player is not online.");
                return;
            }

            final Profile target = Practice.get().getProfileManager().get(player);

            if (args[0].equalsIgnoreCase("join")) {

                if (profile.isInParty()) {
                    profile.sendMessage("&cYou are already in a party.");
                    return;
                }

                if (!target.isInParty()) {
                    profile.sendMessage("&cThat party does not exist.");
                    return;
                }

                if (profile.getPartyInvitations().isEmpty() && !target.getParty().isOpen()) {
                    profile.sendMessage("&cYou do not have any party invitations.");
                    return;
                }

                if (!profile.getPartyInvitations().containsKey(target.getParty()) && !target.getParty().isOpen()) {
                    profile.sendMessage("&cThis party has not sent you an invitation.");
                    return;
                }

                if (target.isInMatch()) {
                    profile.sendMessage("&cYou must wait until the party match has ended to issue this.");
                    return;
                }

                Practice.get().getPartyManager().onJoin(profile, target.getParty()); // handle join

            } else if (args[0].equalsIgnoreCase("invite")) {

                if (!profile.isInParty()) {
                    profile.sendMessage("&cYou are not in a party.");
                    return;
                }

                if (!profile.getParty().isLeader(profile)
                        && !profile.getParty().isAllInvite()) {
                profile.sendMessage("&cParty all-invite must be enabled or you must be the party leader to invite players.");
                    return;
                }

                if (target.isInParty()) {
                    profile.sendMessage("&cThis player is already in a party.");
                    return;
                }

                if (profile.isInMatch()) {
                    profile.sendMessage("&cYou must wait until the party match has ended to issue this.");
                    return;
                }

                Practice.get().getPartyManager().onInvite(target, profile, profile.getParty());

            } else if (args[0].equalsIgnoreCase("kick")) {



            } else {
                // send help
            }
        } else if (args.length == 1) {

            if (!profile.isInParty()) {
                profile.sendMessage("&cYou are not in a party.");
                return;
            }

            if (args[0].equalsIgnoreCase("leave")) {

                Practice.get().getPartyManager().onLeaveOrDisband(profile);

            } else if (args[0].equalsIgnoreCase("chat")) {

                profile.setPartyChat(!profile.isPartyChat());

                profile.sendMessage((profile.isPartyChat() ?
                        "&aYou are now speaking in party chat." :
                        "&cYou are no longer speaking in party chat."));
            } else if (args[0].equalsIgnoreCase("open")) {

                if (!profile.getPlayer().hasPermission("syphlex.open.party")) {
                    profile.sendMessage("&cNo permission."); // todo store advertisement
                    return;
                }

                if (!profile.getParty().isLeader(profile)) {
                    profile.sendMessage("&cYou must be the leader of the party to issue this.");
                    return;
                }

                profile.getParty().setOpen(!profile.getParty().isOpen());

                if (profile.getParty().isOpen()) {
                    profile.sendMessage("&aYour party is now open and players can now join without an invitation.");
                } else {
                    profile.sendMessage("&cYour party is now closed and players cannot join without an invitation.");
                }
            } else {
                // send help
            }


        } else {
            // send help
        }
    }
}
