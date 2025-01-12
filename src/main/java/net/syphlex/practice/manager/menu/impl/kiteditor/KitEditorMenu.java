package net.syphlex.practice.manager.menu.impl.kiteditor;

import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.InventoryUtil;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Edit layout inventory here
 */
public class KitEditorMenu extends Menu {

    private final Kit kit;

    public KitEditorMenu(Kit kit) {
        super("Layout Editor: " + kit.getName(), 27);

        this.kit = kit;

        setEditable(true);

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        }

        inventory.setItem(11, new ItemBuilder()
                .setMaterial(Material.WOOL)
                .setDurability((short) 13)
                .setName("&a&lSave")
                .build());

        inventory.setItem(13, new ItemBuilder()
                .setMaterial(Material.WOOL)
                .setDurability((short) 4)
                .setName("&e&lReset")
                .build());

        inventory.setItem(15, new ItemBuilder()
                .setMaterial(Material.WOOL)
                .setDurability((short) 14)
                .setName("&c&lCancel")
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
                    profile.getKitPresets().put(kit, profile.getPlayer().getInventory().getContents());
                    profile.sendMessage("&aSaved your " + kit.getName() + " kit layout.");
                    break;
                case 13:
                    profile.getPlayer().getInventory().setContents(kit.getInventory());
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
