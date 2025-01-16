package net.syphlex.practice.manager.menu.impl.kiteditor;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.kit.Kit;
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
        super("Select a Kit", 36);


        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        }

        int slot = 10;
        for (Kit kit : Practice.get().getKitManager().getKitMap().values()) {

            if (kit.menuIcon != null) {

                ItemStack itemStack = kit.menuIcon;
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
        for (Kit kit : Practice.get().getKitManager().getKitMap().values()) {

            if (e.getSlot() == slot) {
                profile.getPlayer().closeInventory();
                profile.openMenu(new KitEditorMenu(kit));

                profile.getPlayer().getInventory().clear();
                if (profile.getKitPresets().get(kit) != null) {
                    profile.getPlayer().getInventory().setContents(profile.getKitPreset(kit));
                } else {
                    profile.getPlayer().getInventory().setContents(kit.getInventory());
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
