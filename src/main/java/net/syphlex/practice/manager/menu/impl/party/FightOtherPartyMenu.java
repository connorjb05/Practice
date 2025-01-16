package net.syphlex.practice.manager.menu.impl.party;

import net.syphlex.core.Core;
import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.menu.impl.DuelMenu;
import net.syphlex.practice.manager.party.Party;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.ItemBuilder;
import net.syphlex.practice.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class FightOtherPartyMenu extends Menu {

    private final List<Party> partyList;
    private final int page, endIndex, startIndex;

    public FightOtherPartyMenu(Profile profile, int page) {
        super("Fight Other Parties", 54);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15)); // Black Stained Glass Pane
        }

        // Fill the bottom row (slots 45 to 53)
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15)); // Black Stained Glass Pane
        }

        // Fill the right column (slots 8, 17, 26, 35, 44)
        for (int i = 8; i < 45; i += 9) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15)); // Black Stained Glass Pane
        }

        // Fill the left column (slots 0, 9, 18, 27, 36)
        for (int i = 0; i < 45; i += 9) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15)); // Black Stained Glass Pane
        }

        this.partyList = new ArrayList<>(Practice.get().getPartyManager().getPartyMap().values());
        this.partyList.remove(profile.getParty());

        int startIndex = (page - 1) * 28;
        int endIndex = Math.min(startIndex + 28, partyList.size());

        this.page = page;
        this.startIndex = startIndex;
        this.endIndex = endIndex;

        for (int i = startIndex; i < endIndex; i++) {

            int slot = 10 + (i - startIndex);

            Party party = partyList.get(i);

            ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1);
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

            skullMeta.setOwner(party.getLeader().getPlayer().getName());

            skullMeta.setDisplayName(StringUtil.CC(
                    Core.get().getPlayerDataManager()
                    .get(party.getLeader().getPlayer()).getRank().getColor()
                            + party.getLeader().getPlayer().getName()
                            + "'s " + Practice.QUATERNARY_COLOR + "Party &7("
                            + (party.getPartySize()) + ")"));

            itemStack.setItemMeta(skullMeta);

            inventory.setItem(slot, itemStack);
        }

        if (page > 1) {
            inventory.setItem(45, new ItemBuilder()
                    .setMaterial(Material.PAPER)
                    .setName(Practice.PRIMARY_COLOR + "Previous Page")
                    .build());
        }

        if (endIndex < partyList.size()) {
            inventory.setItem(53, new ItemBuilder()
                    .setMaterial(Material.PAPER)
                    .setName(Practice.PRIMARY_COLOR + "Next Page")
                    .build());
        }
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {

        switch (e.getSlot()) {
            case 45:
                if (page > 1) {
                    e.getProfile().openMenu(new FightOtherPartyMenu(e.getProfile(), page - 1));
                }
                break;
            case 53:
                if (endIndex < partyList.size()) {
                    e.getProfile().openMenu(new FightOtherPartyMenu(e.getProfile(), page + 1));
                }
                break;
        }

        for (int i = startIndex; i < endIndex; i++) {

            int slot = 10 + (i - startIndex);

            Party party = partyList.get(i);

            if (e.getSlot() == slot) {
                // display kit menu
                e.getProfile().openMenu(new DuelMenu(party.getLeader()));
                return;
            }
        }
    }
}
