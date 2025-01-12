package net.syphlex.practice.command;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.manager.match.Match;
import net.syphlex.practice.manager.menu.impl.DuelMenu;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.manager.profile.objects.PlayerState;
import net.syphlex.practice.util.AbstractCmd;
import net.syphlex.practice.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;

public class DuelCmd extends AbstractCmd {
    public DuelCmd(String command) {
        super(command);
    }

    @Override
    public void onCommand(Profile profile, String[] args) {
        if (args.length == 1) {

            final Player t = Bukkit.getPlayer(args[0]);

            if (t == null) {
                profile.sendMessage(Messages.NOT_ONLINE);
                return;
            }

            final Profile target = Practice.get().getProfileManager().get(t);

            if (target.isInMatch() || target.getPlayerState() != PlayerState.IN_SPAWN) {
                profile.sendMessage("&cThat player is not at spawn.");
                return;
            }

            if (target.getDuelRequests().isUnderCooldown(profile)) {
                profile.sendMessage("&cYou must wait before sending another duel request.");
                return;
            }

            profile.openMenu(new DuelMenu(target));

        } else if (args.length == 2 && args[0].equalsIgnoreCase("accept")) {

            final Player requesterPlayer = Bukkit.getPlayer(args[1]);

            if (requesterPlayer == null) {
                profile.sendMessage(Messages.NOT_ONLINE);
                return;
            }

            final Profile requester = Practice.get().getProfileManager().get(requesterPlayer);

            if (!profile.getDuelRequests().isUnderCooldown(requester)) {
                profile.sendMessage("&cThis duel request has expired or does not exist.");
                return;
            }

            if (!profile.getDuelRequests().isRequestFrom(requester)) {
                profile.sendMessage("&cYou do not have any duel requests from this player.");
                return;
            }

            final Kit kit = profile.getDuelRequests().getRequestKit(requester);

            final Arena arena = Practice.get().getArenaManager().getFreeArena(kit);

            Practice.get().getMatchManager().getMatchMap()
                    .get(kit).add(new Match(
                            Collections.singletonList(profile),
                            Collections.singletonList(requester),
                            null, arena, kit, false, false));

            profile.sendMessage("&aYou accepted " + requester.getPlayer().getName() + "'s duel request...");
            requester.sendMessage("&a" + profile.getPlayer().getName() + " has accepted your duel request...");

        } else {
            profile.sendMessage("&cUsage: /duel <player>");
        }
    }
}
