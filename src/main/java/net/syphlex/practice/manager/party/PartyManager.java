package net.syphlex.practice.manager.party;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.InventoryUtil;
import net.syphlex.practice.util.PlayerUtil;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.InventoryUtil;
import net.syphlex.practice.util.PlayerUtil;

import java.util.HashMap;
import java.util.Map;

public class PartyManager {

    // leader, and their party
    private final Map<Profile, Party> partyMap = new HashMap<>();

    public void onCreate(Profile profile){

        if (isInParty(profile)) {
            profile.sendMessage("&cYou are already in a party.");
            return;
        }

        Party party = new Party(profile);
        partyMap.putIfAbsent(profile, party);
        profile.setParty(party);

        profile.sendMessage("&7");
        profile.sendMessage("&aYou have successfully created a party.");
        profile.sendMessage("&7");

        InventoryUtil.setPartyInventory(profile.getPlayer());
    }

    public void onLeaveOrDisband(Profile profile) {

        if (!isInParty(profile)) {
            profile.sendMessage("&cYou are not in a party.");
            return;
        }

        if (profile.getParty().isLeader(profile)) {

            profile.getParty().disband();

            partyMap.remove(profile);

            profile.sendMessage("&7");
            profile.sendMessage("&cYou have successfully disbanded your party.");
            profile.sendMessage("&7");

        } else {

            profile.getParty().leave(profile);

        }


        InventoryUtil.setSpawnInventory(profile.getPlayer());
    }

    public void onJoin(Profile profile, Party party){

        if (profile.isInQueue()) {
            profile.sendMessage("&cYou cannot join a party while in a queue.");
            return;
        }

        if (party.isOpen()) {

            profile.setParty(party);
            party.join(profile);

            profile.getPartyInvitations().clear();

            InventoryUtil.setPartyInventory(profile.getPlayer());

            return;
        }

        if (!profile.getPartyInvitations().containsKey(party)) {
            profile.sendMessage("&cYou were not invited or the invite to this party has expired.");
            return;
        }

        long lastInviteTime = profile.getPartyInvitations().get(party);

        if (Math.abs(System.currentTimeMillis() - lastInviteTime) >= 30000) {
            profile.sendMessage("&cThis party invite has expired.");
            profile.getPartyInvitations().remove(party);
            return;
        }

        profile.setParty(party);
        party.join(profile);

        profile.getPartyInvitations().clear();

        InventoryUtil.setPartyInventory(profile.getPlayer());
    }

    public void onInvite(Profile profile, Profile inviter, Party party){

        if (profile.getPartyInvitations().containsKey(party)) {

            long lastInviteTime = profile.getPartyInvitations().get(party);

            if (Math.abs(System.currentTimeMillis() - lastInviteTime) <= 30000) {
                inviter.sendMessage("&cYou must wait before sending another party invitation.");
                return;
            }
        }

        inviter.sendMessage("&aYou have invited "
                + profile.getPlayer().getName() + " to your party.");

        profile.getPartyInvitations().put(party, System.currentTimeMillis());

        party.sendPartyMessage("&7(Party) " + Practice.PRIMARY_COLOR
                + profile.getPlayer().getName() + Practice.SECONDARY_COLOR + " was invited to the party.");

        profile.sendMessage(" ");
        PlayerUtil.sendClickableText(profile.getPlayer(),
                "&7(Party) " + Practice.PRIMARY_COLOR + "You" + Practice.SECONDARY_COLOR + " were invited to "
                        + Practice.PRIMARY_COLOR + party.getLeader().getPlayer().getName() + "'s "
                        + Practice.SECONDARY_COLOR + "party. &a(Click to join)",
                "party join " + party.getLeader().getPlayer().getName());
        profile.sendMessage(" ");
    }

    public boolean isInParty(Profile profile){

        if (partyMap.isEmpty()){
            return false;
        }

        if (partyMap.containsKey(profile)){
            return true;
        }

        for (Party party : partyMap.values()) {
            if (party.isInParty(profile)) {
                return true;
            }
        }

        return false;
    }
}
