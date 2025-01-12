package net.syphlex.practice.manager.menu.impl.party;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.manager.kit.impl.BridgeKit;
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

public class PartyFFAMenu extends Menu {
    public PartyFFAMenu() {
        super("Party FFA Match", 27);

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        }

        int slot = 10;
        for (Kit kit : Practice.get().getKitManager().getKitMap().values()) {

            if (kit.menuIcon != null) {

                ItemStack itemStack = kit.menuIcon;
                ItemMeta itemMeta = itemStack.getItemMeta();

                itemMeta.addItemFlags(ItemFlag.values());

                List<String> lore = new ArrayList<>(Arrays.asList(
                        " ",
                        "&aClick to start a party ffa match.",
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
        for (Kit kit : Practice.get().getKitManager().getKitMap().values()) {

            if (e.getSlot() == slot) {

                Arena arena = Practice.get().getArenaManager().getFreeArena(kit);

                // no arena was found!
                if (arena == null) {
                    party.sendPartyMessage("&cNo arena found.");
                    return;
                }

                if (kit instanceof BridgeKit) {
                    profile.sendMessage("&cThis kit cannot be selected for FFA Party matches");
                    return;
                }

                Practice.get().getMatchManager().getMatchMap()
                                .get(kit).add(new Match(
                                        null, null, party, arena, kit,
                                false, true));

                profile.getPlayer().closeInventory();
            }

            slot++;
        }
    }
}
