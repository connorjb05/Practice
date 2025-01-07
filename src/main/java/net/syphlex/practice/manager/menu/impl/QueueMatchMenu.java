package net.syphlex.practice.manager.menu.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.StringUtil;
import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueueMatchMenu extends Menu {

    public QueueMatchMenu() {
        super("Queue Match", 27);

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15));
        }

        new BukkitRunnable(){
            @Override
            public void run(){
                int slot = 10;
                for (Kit kit : Practice.get().getKitManager().getKitMap().values()) {

                    if (kit.menuIcon != null) {

                        ItemStack itemStack = kit.menuIcon;
                        ItemMeta itemMeta = itemStack.getItemMeta();

                        itemMeta.addItemFlags(ItemFlag.values());

                        int inMatch = Practice.get().getMatchManager().getInMatch(kit);
                        int inQueue = Practice.get().getQueueManager().getInQueue(kit);

                        List<String> lore = new ArrayList<>(Arrays.asList(
                                "&f&m-------------------",
                                Practice.PRIMARY_COLOR + " » "
                                        + Practice.SECONDARY_COLOR + "In Match: "
                                        + Practice.PRIMARY_COLOR + inMatch,
                                Practice.PRIMARY_COLOR + " » "
                                        + Practice.SECONDARY_COLOR + "In Queue: "
                                        + Practice.PRIMARY_COLOR + inQueue,
                                "",
                                "&aClick to queue.",
                                "&f&m-------------------"));

                        itemMeta.setLore(StringUtil.CC(lore));
                        itemStack.setItemMeta(itemMeta);

                        itemStack.setAmount(Math.min(Math.max(inMatch, 1), 64));

                        inventory.setItem(slot, itemStack);
                    }

                    slot++;
                }
            }
        }.runTaskTimer(Practice.get(), 0L, 40L);
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {

        final Profile profile = e.getProfile();

        int slot = 10;
        for (Kit kit : Practice.get().getKitManager().getKitMap().values()) {

            if (e.getSlot() == slot) {
                Practice.get().getQueueManager().queue(profile, kit);
                profile.getPlayer().closeInventory();
            }

            slot++;
        }
    }


}
