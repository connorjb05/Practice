package net.syphlex.practice.manager.menu.impl.party;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class PartyMatchMenu extends Menu {

    public PartyMatchMenu() {
        super("Party Match Event", 27);

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        }

        inventory.setItem(12, new ItemBuilder()
                .setMaterial(Material.DIAMOND_AXE)
                .setName(Practice.PRIMARY_COLOR + "&lParty FFA Match")
                .setLore(Arrays.asList(
                        " ",
                        "&fClick to select a kit for a",
                        "&fparty ffa match.",
                        " "))
                .build());

        inventory.setItem(14, new ItemBuilder()
                .setMaterial(Material.DIAMOND_SWORD)
                .setName(Practice.PRIMARY_COLOR + "&lParty Split Match")
                .setLore(Arrays.asList(
                        " ",
                        "&fClick to select a kit for a",
                        "&fparty split match.",
                        " "))
                .build());
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {

        final Profile profile = e.getProfile();

        if (!profile.isInParty()) {
            return;
        }

        if (profile.getParty().getPartySize() <= 1) {
            profile.sendMessage("&cYour party is too small.");
            return;
        }

        switch (e.getSlot()) {
            case 12:
                // ffa match
                profile.openMenu(new PartyFFAMenu());
                break;
            case 14:
                // split match
                profile.openMenu(new PartySplitMenu());
                break;
        }
    }
}
