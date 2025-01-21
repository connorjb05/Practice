package net.syphlex.practice.manager.menu.impl.settings;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.profile.objects.PlayerSettings;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ProfileSettingsMenu extends Menu {

    private final Profile profile;

    public ProfileSettingsMenu(Profile profile) {
        super("Profile Settings", 27);

        this.profile = profile;

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15));
        }

        for (PlayerSettings setting : PlayerSettings.values()) {

            boolean settingEnabled = profile.getSetting(setting);

            ItemStack itemStack = setting.getMenuIcon().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(StringUtil.CC(itemMeta.getDisplayName()
                    + " &7(" + (settingEnabled ? "On)" : "Off)")));

            List<String> lore = new ArrayList<>();

            if (settingEnabled) {

                // setting is enabled

                lore.add("&f&m---------------------------");
                lore.add(" ");
                lore.addAll(setting.getDescription());
                lore.add(" ");
                lore.add(Practice.PRIMARY_COLOR + " » " + Practice.SECONDARY_COLOR + "Enabled: &aYes");
                lore.add(" ");
                lore.add("&cClick to disable this setting.");
                lore.add("&f&m---------------------------");
            } else {

                // setting is disabled

                lore.add("&f&m---------------------------");
                lore.add(" ");
                lore.addAll(setting.getDescription());
                lore.add(" ");
                lore.add(Practice.PRIMARY_COLOR + " » " + Practice.SECONDARY_COLOR + "Enabled: &cNo");
                lore.add(" ");
                lore.add("&aClick to enable this setting.");
                lore.add("&f&m---------------------------");
            }

            itemMeta.setLore(StringUtil.CC(lore));
            itemStack.setItemMeta(itemMeta);

            inventory.setItem(setting.getSlot(), itemStack);
        }
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {

        for (PlayerSettings setting : PlayerSettings.values()) {

            if (e.getSlot() == setting.getSlot()) {

                profile.setSetting(setting, !profile.getSetting(setting));

                boolean settingEnabled = profile.getSetting(setting);

                ItemStack itemStack = setting.getMenuIcon().clone();
                ItemMeta itemMeta = itemStack.getItemMeta();

                itemMeta.setDisplayName(StringUtil.CC(itemMeta.getDisplayName()
                        + " &7(" + (settingEnabled ? "On)" : "Off)")));

                List<String> lore = new ArrayList<>();

                if (settingEnabled) {

                    // setting is enabled

                    lore.add("&f&m---------------------------");
                    lore.add(" ");
                    lore.addAll(setting.getDescription());
                    lore.add(" ");
                    lore.add(Practice.PRIMARY_COLOR + " » " + Practice.SECONDARY_COLOR + "Enabled: &aYes");
                    lore.add(" ");
                    lore.add("&cClick to disable this setting.");
                    lore.add("&f&m---------------------------");

                    profile.sendMessage("&aYou have enabled your " + setting.getName() + ".");
                } else {

                    // setting is disabled

                    lore.add("&f&m---------------------------");
                    lore.add(" ");
                    lore.addAll(setting.getDescription());
                    lore.add(" ");
                    lore.add(Practice.PRIMARY_COLOR + " » " + Practice.SECONDARY_COLOR + "Enabled: &cNo");
                    lore.add(" ");
                    lore.add("&aClick to enable this setting.");
                    lore.add("&f&m---------------------------");

                    profile.sendMessage("&cYou have disabled your " + setting.getName() + ".");
                }

                itemMeta.setLore(StringUtil.CC(lore));
                itemStack.setItemMeta(itemMeta);

                inventory.setItem(setting.getSlot(), itemStack);
                break;
            }
        }
    }
}
