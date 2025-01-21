package net.syphlex.practice.manager.menu.impl.kiteditor;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Menu to select which kit to edit
 */
public class KitEditorSelectionMenu extends Menu {
    public KitEditorSelectionMenu() {
        super("Select a Ladder", 36);


        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        }

        int slot = 10;
        for (Ladder ladder : Practice.get().getLadderManager().getLadderMap().values()) {

            if (ladder.menuIcon != null) {

                ItemStack itemStack = ladder.menuIcon;
                ItemMeta itemMeta = itemStack.getItemMeta();

                itemMeta.addItemFlags(ItemFlag.values());

                List<String> lore = new ArrayList<>(Arrays.asList(
                        "&f&m----------------------",
                        "&aClick to manipulate your",
                        "&alayout for this kit.",
                        "&f&m----------------------"));

                itemMeta.setLore(StringUtil.CC(lore));
                itemStack.setItemMeta(itemMeta);

                inventory.setItem(slot, itemStack);
            }

            if (slot == 16) {
                slot = 21;
                continue;
            }

            slot++;
        }
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {

        final Profile profile = e.getProfile();

        int slot = 10;
        for (Ladder ladder : Practice.get().getLadderManager().getLadderMap().values()) {

            if (e.getSlot() == slot) {
                profile.getPlayer().closeInventory();
                profile.openMenu(new KitEditorMenu(ladder));

                profile.getPlayer().getInventory().clear();
                if (profile.getKitPresets().get(ladder) != null) {
                    profile.getPlayer().getInventory().setContents(profile.getKitPreset(ladder));
                } else {
                    profile.getPlayer().getInventory().setContents(ladder.getInventory());
                }
                return;
            }

            if (slot == 16) {
                slot = 21;
                continue;
            }

            slot++;
        }
    }
}
