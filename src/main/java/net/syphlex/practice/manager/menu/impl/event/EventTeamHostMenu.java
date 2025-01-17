package net.syphlex.practice.manager.menu.impl.event;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.event.PracticeEvent;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EventTeamHostMenu extends Menu {

    private final PracticeEvent event;

    public EventTeamHostMenu(PracticeEvent event) {
        super("Host Event", 27);

        this.event = event;

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15));
        }

        int slot = 10;
        for (int i = 1; i < 4; i++) {

            ItemStack itemStack = event.getInfo().getMenuIcon();
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(StringUtil.CC("&c" + i + "v" + i + " " + event.getInfo().getDisplayName()));
            itemStack.setItemMeta(itemMeta);
            itemStack.setAmount(i);

            inventory.setItem(slot, itemStack);

            slot++;
        }
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {
        int slot = 10;
        for (int i = 1; i < 4; i++) {

            if (e.getSlot() == slot) {

                String displayName = event.getInfo().getDisplayName();

                event.teamSize = i;
                event.setDisplayName(event.teamSize + "v" + event.teamSize + " " + displayName);
                Practice.get().getEventManager().hostEvent(event);
                break;
            }

            slot++;
        }
    }
}
