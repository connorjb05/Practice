package net.syphlex.practice.manager.menu.impl.bot;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BotMatchMenu extends Menu {

    public BotMatchMenu() {
        super("Duel a Bot", 27);

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15));
        }

        int slot = 10;
        for (Ladder ladder : Practice.get().getLadderManager().getLadderMap().values()) {

            if (ladder.menuIcon != null) {

                ItemStack itemStack = ladder.menuIcon;
                ItemMeta itemMeta = itemStack.getItemMeta();

                itemMeta.addItemFlags(ItemFlag.values());

                List<String> lore = new ArrayList<>(Arrays.asList(
                        "&f&m---------------------------",
                        "&aClick to select a difficulty.",
                        "&f&m---------------------------"));

                itemMeta.setLore(StringUtil.CC(lore));
                itemStack.setItemMeta(itemMeta);

                inventory.setItem(slot, itemStack);
            }

            slot++;
        }
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {

        int slot = 10;
        for (Ladder ladder : Practice.get().getLadderManager().getLadderMap().values()) {

            if (e.getSlot() == slot) {
                e.getProfile().openMenu(new BotDifficultyMenu(ladder));
                break;
            }

            slot++;
        }
    }
}
