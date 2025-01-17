package net.syphlex.practice.manager.menu.impl.party;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.manager.match.Match;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.party.Party;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartySplitMenu extends Menu {
    public PartySplitMenu() {
        super("Party Split Match", 27);

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        }

        int slot = 10;
        for (Ladder ladder : Practice.get().getLadderManager().getLadderMap().values()) {

            if (ladder.menuIcon != null) {

                ItemStack itemStack = ladder.menuIcon;
                ItemMeta itemMeta = itemStack.getItemMeta();

                itemMeta.addItemFlags(ItemFlag.values());

                List<String> lore = new ArrayList<>(Arrays.asList(
                        " ",
                        "&aClick to start a party split match.",
                        " "));

                itemMeta.setLore(StringUtil.CC(lore));
                itemStack.setItemMeta(itemMeta);

                inventory.setItem(slot, itemStack);
            }

            slot++;
        }
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {

        final Profile profile = e.getProfile();

        if (!profile.isInParty()) {
            return;
        }

        final Party party = profile.getParty();

        int slot = 10;
        for (Ladder ladder : Practice.get().getLadderManager().getLadderMap().values()) {

            if (e.getSlot() == slot) {

                Arena arena = Practice.get().getArenaManager().getFreeArena(ladder);

                // no arena was found!
                if (arena == null) {
                    party.sendPartyMessage("&cNo arena found.");
                    return;
                }

                Practice.get().getMatchManager().getMatchMap()
                        .get(ladder).add(new Match(null, null,
                                party, arena, ladder, false, false));

                profile.getPlayer().closeInventory();
            }

            slot++;
        }
    }
}