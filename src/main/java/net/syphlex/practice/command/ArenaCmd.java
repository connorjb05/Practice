package net.syphlex.practice.command;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.AbstractCmd;

public class ArenaCmd extends AbstractCmd {
    public ArenaCmd(String command) {
        super(command);
    }

    @Override
    public void onCommand(Profile profile, String[] args) {

        if (!profile.getPlayer().hasPermission("syphlex.arena")) {
            profile.sendMessage("&cNo permission.");
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

            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {

            profile.sendMessage("&6Arenas:");
            for (String arenaName : Practice.get().getArenaManager().getArenaMap().keySet()) {
                profile.sendMessage(" &7* &e" + arenaName);
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
            profile.sendMessage(" &7* " + Practice.PRIMARY_COLOR + "/arena create <arena>");
            profile.sendMessage(" &7* " + Practice.PRIMARY_COLOR + "/arena delete <arena>");
            profile.sendMessage(" &7* " + Practice.PRIMARY_COLOR + "/arena pos1 <arena>");
            profile.sendMessage(" &7* " + Practice.PRIMARY_COLOR + "/arena pos2 <arena>");
            profile.sendMessage(" &7* " + Practice.PRIMARY_COLOR + "/arena corner1 <arena>");
            profile.sendMessage(" &7* " + Practice.PRIMARY_COLOR + "/arena corner2 <arena>");
            profile.sendMessage(" &7* " + Practice.PRIMARY_COLOR + "/arena spectate <arena>");
            profile.sendMessage(" &7* " + Practice.PRIMARY_COLOR + "/arena addkit <kit> <arena>");
            profile.sendMessage(" &7* " + Practice.PRIMARY_COLOR + "/arena delkit <kit> <arena>");
            profile.sendMessage(" &7* " + Practice.PRIMARY_COLOR + "/arena list");
            profile.sendMessage(" ");
        }
    }
}
