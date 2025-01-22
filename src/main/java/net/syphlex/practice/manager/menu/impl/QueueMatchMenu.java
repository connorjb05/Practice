package net.syphlex.practice.manager.menu.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.manager.ladder.impl.BedFightLadder;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueueMatchMenu extends Menu {

    private final BukkitTask task;

    public QueueMatchMenu() {
        super("Queue Match", 36);

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15));
        }

        task = new BukkitRunnable(){
            @Override
            public void run(){
                int slot = 10;
                for (Ladder ladder : Practice.get().getLadderManager().getLadderMap().values()) {

                    if (ladder.menuIcon != null) {

                        ItemStack itemStack = ladder.menuIcon;
                        ItemMeta itemMeta = itemStack.getItemMeta();

                        itemMeta.addItemFlags(ItemFlag.values());

                        int inMatch = Practice.get().getMatchManager().getInMatch(ladder);
                        int inQueue = Practice.get().getQueueManager().getInQueue(ladder);

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

                    if (slot == 16) {
                        slot = 21;
                        continue;
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
        for (Ladder ladder : Practice.get().getLadderManager().getLadderMap().values()) {

            if (e.getSlot() == slot) {

                Practice.get().getQueueManager().queue(profile, ladder);
                profile.getPlayer().closeInventory();
                profile.sendSound(Sound.WOOD_CLICK);

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
