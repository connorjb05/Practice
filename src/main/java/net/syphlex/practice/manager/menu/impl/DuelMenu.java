package net.syphlex.practice.manager.menu.impl;

import net.syphlex.core.Core;
import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.profile.Profile;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DuelMenu extends Menu {

    private final Profile target;

    public DuelMenu(Profile target) {
        super("Select a Kit", 36);

        this.target = target;

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        }

        int slot = 10;
        for (Ladder ladder : Practice.get().getLadderManager().getLadderMap().values()) {

            if (ladder.menuIcon != null) {

                ItemStack itemStack = ladder.menuIcon;
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setLore(null);
                itemMeta.addItemFlags(ItemFlag.values());
                itemStack.setItemMeta(itemMeta);

                inventory.setItem(slot, itemStack);
            }

            if (slot == 16) {
                slot = 21;
                continue;
            }

            slot++;
        }
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {

        final Profile profile = e.getProfile();

        int slot = 10;
        for (Ladder ladder : Practice.get().getLadderManager().getLadderMap().values()) {

            if (e.getSlot() == slot) {

                target.getDuelRequests().addRequest(profile, ladder);

                profile.getPlayer().closeInventory();

                profile.sendMessage(" ");
                profile.sendMessage(Practice.PRIMARY_COLOR + "&lDuel Request");
                profile.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.QUATERNARY_COLOR + "To: "
                        + Practice.PRIMARY_COLOR
                        + Core.get().getPlayerDataManager().get(target.getPlayer()).getRank().getColor()
                        + target.getPlayer().getName() + (target.isInParty() ? "'s"
                        + Practice.QUATERNARY_COLOR + " Party" : ""));
                profile.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.QUATERNARY_COLOR + "Ladder: "
                        + Practice.PRIMARY_COLOR + ladder.getName());
                profile.sendMessage(" ");

                if (target.isInParty()) {


                    target.sendMessage(" ");
                    target.sendClickableMessage(
                            Practice.PRIMARY_COLOR + "&lDuel Request &7(Click to accept)",
                            "duel accept " + profile.getPlayer().getName());
                    target.sendClickableMessage(
                            Practice.PRIMARY_COLOR + " » " + Practice.QUATERNARY_COLOR + "From: "
                                    + Practice.PRIMARY_COLOR
                                    + Core.get().getPlayerDataManager().get(profile.getPlayer()).getRank().getColor()
                                    + profile.getPlayer().getName() + (profile.isInParty() ? "'s"
                                    + Practice.QUATERNARY_COLOR + " Party" : ""),
                            "duel accept " + profile.getPlayer().getName());
                    target.sendClickableMessage(
                            Practice.PRIMARY_COLOR + " » " + Practice.QUATERNARY_COLOR + "Party Size: "
                                    + Practice.PRIMARY_COLOR
                                    + (profile.isInParty() ? profile.getParty().getPartySize() : 1),
                            "duel accept " + profile.getPlayer().getName());
                    target.sendClickableMessage(
                            Practice.PRIMARY_COLOR + " » " + Practice.QUATERNARY_COLOR + "Ladder: "
                                    + Practice.PRIMARY_COLOR + ladder.getName(),
                            "duel accept " + profile.getPlayer().getName());
                    target.sendMessage(" ");

                    for (Profile members : target.getParty().getMembers()) {
                        members.sendMessage(" ");
                        members.sendMessage(Practice.PRIMARY_COLOR + "&lDuel Request");
                        members.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.QUATERNARY_COLOR + "From: "
                                + Practice.PRIMARY_COLOR
                                + Core.get().getPlayerDataManager().get(profile.getPlayer()).getRank().getColor()
                                + profile.getPlayer().getName() + (profile.isInParty() ? "'s"
                                + Practice.QUATERNARY_COLOR + " Party" : ""));
                        members.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.QUATERNARY_COLOR + "Party Size: "
                                + Practice.PRIMARY_COLOR
                                + (profile.isInParty() ? profile.getParty().getPartySize() : 1));
                        members.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.QUATERNARY_COLOR + "Ladder: "
                                + Practice.PRIMARY_COLOR + ladder.getName());
                        members.sendMessage(" ");
                    }

                } else {

                    if (profile.isInParty()) {
                        for (Profile members : profile.getParty().getMembers()) {
                            members.sendMessage(" ");
                            members.sendMessage(Practice.PRIMARY_COLOR + "&lDuel Request");
                            members.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.QUATERNARY_COLOR + "To: "
                                    + Practice.PRIMARY_COLOR
                                    + Core.get().getPlayerDataManager().get(target.getPlayer()).getRank().getColor()
                                    + target.getPlayer().getName() + (target.isInParty() ? "'s"
                                    + Practice.QUATERNARY_COLOR + " Party" : ""));
                            members.sendMessage(Practice.PRIMARY_COLOR + " » " + Practice.QUATERNARY_COLOR + "Ladder: "
                                    + Practice.PRIMARY_COLOR + ladder.getName());
                            members.sendMessage(" ");
                        }
                    }

                    target.sendMessage(" ");
                    target.sendClickableMessage(
                            Practice.PRIMARY_COLOR + "&lDuel Request &7(Click to accept)",
                            "duel accept " + profile.getPlayer().getName());
                    target.sendClickableMessage(
                            Practice.PRIMARY_COLOR + " » " + Practice.QUATERNARY_COLOR + "From: "
                                    + Practice.PRIMARY_COLOR
                                    + Core.get().getPlayerDataManager().get(profile.getPlayer()).getRank().getColor()
                                    + profile.getPlayer().getName() + (profile.isInParty() ? "'s"
                                    + Practice.QUATERNARY_COLOR + " Party" : ""),
                            "duel accept " + profile.getPlayer().getName());
                    target.sendClickableMessage(
                            Practice.PRIMARY_COLOR + " » " + Practice.QUATERNARY_COLOR + "Ladder: "
                                    + Practice.PRIMARY_COLOR + ladder.getName(),
                            "duel accept " + profile.getPlayer().getName());
                    target.sendMessage(" ");
                }

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
