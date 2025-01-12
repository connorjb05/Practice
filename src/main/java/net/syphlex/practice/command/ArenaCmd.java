package net.syphlex.practice.command;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.AbstractCmd;
import net.syphlex.practice.util.Messages;
import net.syphlex.practice.util.Permissions;
import org.bukkit.ChatColor;

public class ArenaCmd extends AbstractCmd {
    public ArenaCmd(String command) {
        super(command);
    }

    @Override
    public void onCommand(Profile profile, String[] args) {

        if (!profile.hasPermission(Permissions.ARENA)) {
            profile.sendMessage(Messages.NO_PERMISSION);
            return;
        }

        if (args.length == 2) {

            String arenaName = args[1];

            if (args[0].equalsIgnoreCase("create")) {

                if (Practice.get().getArenaManager().arenaExists(arenaName)){
                    profile.sendMessage("&cAn arena with that name already exists.");
                    return;
                }

                profile.sendMessage("&aYou have successfully created an arena named "
                        + arenaName + ".");
                Practice.get().getArenaManager().getArenaMap()
                        .put(arenaName, new Arena(arenaName));

            } else if (args[0].equalsIgnoreCase("delete")) {

                if (!Practice.get().getArenaManager().arenaExists(arenaName)) {
                    profile.sendMessage("&cCould not find an arena with that name.");
                    return;
                }


                profile.sendMessage("&cYou have deleted " + arenaName + ".");
                Practice.get().getArenaManager().getArenaMap().remove(arenaName);

            } else if (args[0].equalsIgnoreCase("pos1")) {

                if (!Practice.get().getArenaManager().arenaExists(arenaName)) {
                    profile.sendMessage("&cCould not find an arena with that name.");
                    return;
                }

                profile.sendMessage("&aYou have successfully set position 1 for "
                        + arenaName + ".");
                Practice.get().getArenaManager().getArenaMap().get(arenaName)
                        .setPosition1(profile.getPlayer().getLocation());

            } else if (args[0].equalsIgnoreCase("pos2")) {

                if (!Practice.get().getArenaManager().arenaExists(arenaName)) {
                    profile.sendMessage("&cCould not find an arena with that name.");
                    return;
                }

                profile.sendMessage("&aYou have successfully set position 2 for "
                        + arenaName + ".");
                Practice.get().getArenaManager().getArenaMap().get(arenaName)
                        .setPosition2(profile.getPlayer().getLocation());

            } else if (args[0].equalsIgnoreCase("corner1")) {

                if (!Practice.get().getArenaManager().arenaExists(arenaName)) {
                    profile.sendMessage("&cCould not find an arena with that name.");
                    return;
                }

                profile.sendMessage("&aYou have successfully set corner 1 for "
                        + arenaName + ".");
                Practice.get().getArenaManager().getArenaMap().get(arenaName)
                        .setCorner1(profile.getPlayer().getLocation());

            } else if (args[0].equalsIgnoreCase("corner2")) {

                if (!Practice.get().getArenaManager().arenaExists(arenaName)) {
                    profile.sendMessage("&cCould not find an arena with that name.");
                    return;
                }

                profile.sendMessage("&aYou have successfully set corner 2 for "
                        + arenaName + ".");
                Practice.get().getArenaManager().getArenaMap().get(arenaName)
                        .setCorner2(profile.getPlayer().getLocation());

            } else if (args[0].equalsIgnoreCase("spectate")) {

                if (!Practice.get().getArenaManager().arenaExists(arenaName)) {
                    profile.sendMessage("&cCould not find an arena with that name.");
                    return;
                }

                profile.sendMessage("&aYou have successfully set the spectate location for "
                        + arenaName + ".");
                Practice.get().getArenaManager().getArenaMap().get(arenaName)
                        .setSpectate(profile.getPlayer().getLocation());

            } else if (args[0].equalsIgnoreCase("teleport")) {

                if (!Practice.get().getArenaManager().arenaExists(arenaName)) {
                    profile.sendMessage("&cCould not find an arena with that name.");
                    return;
                }

                if (Practice.get().getArenaManager().getArenaMap().get(arenaName).getSpectate() == null) {
                    profile.sendMessage("&cCould not find a location to teleport to.");
                    return;
                }

                profile.sendMessage("&aTeleporting to " + arenaName + "...");
                profile.teleport(Practice.get().getArenaManager().getArenaMap().get(arenaName).getSpectate());

            } else if (args[0].equalsIgnoreCase("kits")) {

                if (!Practice.get().getArenaManager().arenaExists(arenaName)) {
                    profile.sendMessage("&cCould not find an arena with that name.");
                    return;
                }

                if (Practice.get().getArenaManager().getArenaMap().get(arenaName).getKits().isEmpty()) {
                    profile.sendMessage(Practice.PRIMARY_COLOR + "&lKits: &cNone");
                    return;
                }

                profile.sendMessage(Practice.PRIMARY_COLOR + "&lKits:");
                for (Kit kit : Practice.get().getArenaManager().getArenaMap().get(arenaName).getKits()) {
                    profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                            + Practice.SECONDARY_COLOR + ChatColor.stripColor(kit.getName()));
                }
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {

            if (Practice.get().getArenaManager().getArenaMap().isEmpty()) {
                profile.sendMessage(Practice.PRIMARY_COLOR + "&lArenas: &cNone");
                return;
            }

            profile.sendMessage(Practice.PRIMARY_COLOR + "&lArenas:");
            for (String arenaName : Practice.get().getArenaManager().getArenaMap().keySet()) {
                profile.sendMessage( Practice.PRIMARY_COLOR + " » " + Practice.SECONDARY_COLOR + arenaName
                        + ": " + (Practice.get().getArenaManager().getArenaMap().get(arenaName).isOpen() ? "&aOpen" : "&cIn Use"));
            }

        } else if (args.length == 3 && args[0].equalsIgnoreCase("addkit")) {

            String arenaName = args[2];
            String kitName = args[1];

            if (!Practice.get().getArenaManager().arenaExists(arenaName)) {
                profile.sendMessage("&cCould not find an arena with that name.");
                return;
            }

            if (!Practice.get().getKitManager().kitExists(kitName)) {
                profile.sendMessage("&cCould not find a kit with that name.");
                return;
            }

            final Arena arena = Practice.get().getArenaManager().getArenaMap().get(arenaName);
            final Kit kit = Practice.get().getKitManager().getKitMap().get(kitName);

            if (arena.getKits().contains(kit)) {
                profile.sendMessage("&cThis arena already has this kit added.");
                return;
            }

            profile.sendMessage("&aYou have successfully added the "
                    + kitName + " kit to " + arenaName + ".");
            arena.getKits().add(kit);


        } else if (args.length == 3 && args[0].equalsIgnoreCase("delkit")) {

            String arenaName = args[2];
            String kitName = args[1];

            if (!Practice.get().getArenaManager().arenaExists(arenaName)) {
                profile.sendMessage("&cCould not find an arena with that name.");
                return;
            }

            if (!Practice.get().getKitManager().kitExists(kitName)) {
                profile.sendMessage("&cCould not find a kit with that name.");
                return;
            }

            final Arena arena = Practice.get().getArenaManager().getArenaMap().get(arenaName);
            final Kit kit = Practice.get().getKitManager().getKitMap().get(kitName);

            if (!arena.getKits().contains(kit)) {
                profile.sendMessage("&cThis arena does not have this kit added.");
                return;
            }

            profile.sendMessage("&cYou have successfully removed the " + kitName
                    + " kit from " + arenaName + ".");
            arena.getKits().remove(kit);

        } else {
            profile.sendMessage(" ");
            profile.sendMessage(Practice.PRIMARY_COLOR + "&lArena Help:");
            profile.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.TERTIARY_COLOR + "/arena create" + Practice.SECONDARY_COLOR + " <arena>");
            profile.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.TERTIARY_COLOR + "/arena delete" + Practice.SECONDARY_COLOR + " <arena>");
            profile.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.TERTIARY_COLOR + "/arena pos1" + Practice.SECONDARY_COLOR + " <arena>");
            profile.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.TERTIARY_COLOR + "/arena pos2" + Practice.SECONDARY_COLOR + " <arena>");
            profile.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.TERTIARY_COLOR + "/arena corner1" + Practice.SECONDARY_COLOR + " <arena>");
            profile.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.TERTIARY_COLOR + "/arena corner2" + Practice.SECONDARY_COLOR + " <arena>");
            profile.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.TERTIARY_COLOR + "/arena spectate" + Practice.SECONDARY_COLOR + " <arena>");
            profile.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.TERTIARY_COLOR + "/arena addkit" + Practice.SECONDARY_COLOR + " <kit> <arena>");
            profile.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.TERTIARY_COLOR + "/arena delkit" + Practice.SECONDARY_COLOR + " <kit> <arena>");
            profile.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.TERTIARY_COLOR + "/arena kits" + Practice.SECONDARY_COLOR + " <arena>");
            profile.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.TERTIARY_COLOR + "/arena list");
            profile.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.TERTIARY_COLOR + "/arena teleport" + Practice.SECONDARY_COLOR + " <arena>");
            profile.sendMessage(" ");
        }
    }
}
