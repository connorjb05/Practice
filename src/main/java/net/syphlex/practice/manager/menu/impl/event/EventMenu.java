package net.syphlex.practice.manager.menu.impl.event;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.event.PracticeEvent;
import net.syphlex.practice.manager.menu.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EventMenu extends Menu {
    public EventMenu() {
        super("Host Event", 27);

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        }

        int i = 10;
        for (PracticeEvent event : Practice.get().getEventManager().getEvents()) {
            inventory.setItem(i, event.getInfo().getMenuIcon());
            i++;
        }
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {
        int i = 10;
        for (PracticeEvent event : Practice.get().getEventManager().getEvents()) {
            if (e.getSlot() == i) {
                if (event.getInfo().getIdentifier().equalsIgnoreCase("sumo")) {
                    e.getProfile().openMenu(new EventTeamHostMenu(event));
                } else {

                }
                break;
            }
            i++;
        }
    }
}
