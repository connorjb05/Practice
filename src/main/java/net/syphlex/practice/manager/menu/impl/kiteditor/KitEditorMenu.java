package net.syphlex.practice.manager.menu.impl.kiteditor;

import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.InventoryUtil;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Edit layout inventory here
 */
public class KitEditorMenu extends Menu {

    private final Ladder ladder;

    public KitEditorMenu(Ladder ladder) {
        super("Layout Editor: " + ladder.getName(), 27);

        this.ladder = ladder;

        setEditable(true);

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        }

        inventory.setItem(11, new ItemBuilder()
                .setMaterial(Material.WOOL)
                .setDurability((short) 13)
                .setName("&a&lSave")
                .setLore(Arrays.asList(
                        "&7Save your personal changes.",
                        "&7",
                        "&aClick to save."))
                .build());

        inventory.setItem(13, new ItemBuilder()
                .setMaterial(Material.WOOL)
                .setDurability((short) 4)
                .setName("&e&lReset")
                .setLore(Arrays.asList(
                        "&7Reset any changes made.",
                        "&7",
                        "&eClick to reset."))
                .build());

        inventory.setItem(15, new ItemBuilder()
                .setMaterial(Material.WOOL)
                .setDurability((short) 14)
                .setName("&c&lCancel")
                .setLore(Arrays.asList(
                        "&7Close menu without saving.",
                        "&7",
                        "&cClick to cancel."))
                .build());
    }

    @Override
    public void onCloseEvent(Profile profile){
        InventoryUtil.setSpawnInventory(profile.getPlayer());
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {

        final Profile profile = e.getProfile();

        if (e.getSlot() < 27) {
            e.setCancelled(true);

            switch (e.getSlot()) {
                case 11:
                    profile.saveKitPreset(ladder, profile.getPlayer().getInventory().getContents());
                    //profile.getKitPresets().put(kit, profile.getPlayer().getInventory().getContents());
                    profile.sendMessage("&aSaved your " + ladder.getName() + " kit layout.");
                    profile.sendMessage("&7You may close out of the menu.");
                    break;
                case 13:
                    profile.getPlayer().getInventory().setContents(ladder.getInventory());
                    break;
                case 15:
                    profile.getPlayer().closeInventory();
                    break;
            }

            return;
        } else {
            e.setCancelled(false);
        }
    }
}
