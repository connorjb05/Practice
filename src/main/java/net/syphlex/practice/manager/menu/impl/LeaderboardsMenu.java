package net.syphlex.practice.manager.menu.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.manager.leaderboards.LeaderboardPlayer;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.util.StringUtil;
import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.leaderboards.LeaderboardPlayer;
import net.syphlex.practice.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/***
 * Menu displaying all the kits and their top 10 players
 */
public class LeaderboardsMenu extends Menu {
    public LeaderboardsMenu() {
        super("Leaderboards", 27);

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15));
        }

        int slot = 10;
        for (Kit kit : Practice.get().getKitManager().getKitMap().values()) {

            if (kit.menuIcon != null) {

                ItemStack itemStack = kit.menuIcon;
                ItemMeta itemMeta = itemStack.getItemMeta();

                itemMeta.addItemFlags(ItemFlag.values());

                int inMatch = Practice.get().getMatchManager().getInMatch(kit);
                int inQueue = Practice.get().getQueueManager().getInQueue(kit);

                List<String> lore = new ArrayList<>();

                int place = 1;
                for (LeaderboardPlayer leaderboardPlayer : Practice.get().getLeaderboardManager().getLeaderboard(kit)) {
                    lore.add(Practice.PRIMARY_COLOR + place + ". " + Practice.SECONDARY_COLOR
                            + leaderboardPlayer.getUsername() + "&7: " + Practice.PRIMARY_COLOR
                            + leaderboardPlayer.getElo() + " Elo");
                    place++;
                }

                if (lore.isEmpty()) {
                    lore.add(" ");
                    lore.add("&cNo data...");
                    lore.add(" ");
                } else {
                    lore.add(0, "&f&m---------------------");
                    lore.add(lore.size(), "&f&m---------------------");
                }

                itemMeta.setLore(StringUtil.CC(lore));
                itemStack.setItemMeta(itemMeta);

                itemStack.setAmount(Math.min(Math.max(inMatch, 1), 64));

                inventory.setItem(slot, itemStack);
            }

            slot++;
        }
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {}
}
