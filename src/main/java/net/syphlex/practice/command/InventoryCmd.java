package net.syphlex.practice.command;

import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.AbstractCmd;

import java.util.UUID;
import java.util.regex.Pattern;

public class InventoryCmd extends AbstractCmd {

    private static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    private static final Pattern UUID_PATTERN = Pattern.compile(UUID_REGEX);

    public InventoryCmd(String command) {
        super(command);
    }

    @Override
    public void onCommand(Profile profile, String[] args) {
        if (args.length == 2) {

            try {

                UUID matchUUID = UUID.fromString(args[0]);

                UUID playerUUID = UUID.fromString(args[1]);

                // POst match inventories does not contain the targets inventory data
                if (!profile.getPostMatchInventories().containsKey(matchUUID)
                        || !profile.getPostMatchInventories().get(matchUUID).getInventories().containsKey(playerUUID)) {
                    profile.sendMessage("&cThat inventory no longer exists or has expired.");
                    return;
                }

                // close previous inventory, open intended players inventory
                profile.getPlayer().closeInventory();
                profile.getPlayer().openInventory(
                        profile.getPostMatchInventories().get(matchUUID)
                                .getInventories().get(playerUUID).getInventory());

            } catch (Exception e) {
                // empty catch block
            }
        }
    }
}
