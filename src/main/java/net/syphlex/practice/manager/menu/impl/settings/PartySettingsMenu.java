package net.syphlex.practice.manager.menu.impl.settings;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.ItemBuilder;
import net.syphlex.practice.util.Messages;
import net.syphlex.practice.util.Permissions;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PartySettingsMenu extends Menu {

    public PartySettingsMenu(Profile profile) {
        super("Party Settings", 27);

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15));
        }

        inventory.setItem(12, new ItemBuilder()
                .setMaterial(Material.MAP)
                .setName(Practice.PRIMARY_COLOR + "&lOpen Party")
                .build());

        inventory.setItem(13, new ItemBuilder()
                .setMaterial(Material.BOOK_AND_QUILL)
                .setName(Practice.PRIMARY_COLOR + "&lAllow All-Invite")
                .build());

        inventory.setItem(14, new ItemBuilder()
                .setMaterial(Material.BOOK)
                .setName(Practice.PRIMARY_COLOR + "&lParty Size")
                .build());
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {

        final Profile profile = e.getProfile();

        switch (e.getSlot()) {
            case 12:

                if (!profile.hasPermission(Permissions.OPEN_PARTY)) {
                    profile.sendMessage(Messages.NO_PERMISSION);
                    profile.sendMessage(Messages.STORE_ADVERTISEMENT);
                    return;
                }

                // todo

                break;
            case 13:

                break;
            case 14:

                if (!profile.hasPermission(Permissions.OPEN_PARTY)) {
                    profile.sendMessage(Messages.NO_PERMISSION);
                    profile.sendMessage(Messages.STORE_ADVERTISEMENT);
                    return;
                }

                // todo left click to increase party size, right click to lower

                break;
        }
    }
}
