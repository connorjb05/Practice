package net.syphlex.practice.manager.menu.impl.settings;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class SettingsMenu extends Menu {

    public SettingsMenu() {
        super("Settings", 27);

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15));
        }

        inventory.setItem(12, new ItemBuilder()
                .setMaterial(Material.NETHER_STAR)
                .setName(Practice.PRIMARY_COLOR + "&lParty Settings")
                .setLore(Arrays.asList(
                        " ",
                        "&fClick to edit the settings",
                        "&fof your party.",
                        ""))
                .build());

        inventory.setItem(14, new ItemBuilder()
                .setMaterial(Material.SKULL_ITEM)
                .setName(Practice.PRIMARY_COLOR + "&lProfile Settings")
                .setLore(Arrays.asList(
                        " ",
                        "&fClick to edit your",
                        "&fprofile settings.",
                        ""))
                .build());
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {

        switch (e.getSlot()) {
            case 12:
                e.getProfile().openMenu(new PartySettingsMenu(e.getProfile()));
                break;
            case 14:
                e.getProfile().openMenu(new ProfileSettingsMenu(e.getProfile()));
                break;
        }
    }
}
