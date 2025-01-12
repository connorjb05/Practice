package net.syphlex.practice.manager.party;

import lombok.Getter;
import lombok.Setter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.InventoryUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Party {

    private final Profile leader;
    private final List<Profile> members = new ArrayList<>();

    private boolean allInvite = false;
    private boolean open = false;

    public Party(Profile leader) {
        this.leader = leader;
    }

    public void invite(Profile profile){
    }

    public void join(Profile profile){

        if (members.contains(profile)){
            return;
        }

        profile.sendMessage("&aYou have joined "
                + leader.getPlayer().getName() + "'s party.");

        members.add(profile);

        sendPartyMessage(Practice.TERTIARY_COLOR + "(Party) "
                + Practice.PRIMARY_COLOR + profile.getPlayer().getName()
                + Practice.SECONDARY_COLOR + " has joined the party.");
    }

    public void leave(Profile profile){

        leader.sendMessage("&c(Party) " + profile.getPlayer().getName()
                + " has left the party.");

        for (Profile members : members) {
            members.sendMessage("&c(Party) " + profile.getPlayer().getName()
                    + " has left the party.");
        }

        members.remove(profile);

        profile.sendMessage("&cYou have left "
                + leader.getPlayer().getName() + "'s party.");

        profile.setPartyChat(false);
        profile.setParty(null);
    }

    public void disband(){

        leader.setPartyChat(false);

        for (Profile members : members) {
            members.sendMessage("&cYou have left "
                    + leader.getPlayer().getName() + "'s party. &7(Disbanded)");

            InventoryUtil.setSpawnInventory(members.getPlayer());

            members.setParty(null);
            members.setPartyChat(false);
        }

        leader.setParty(null);
        members.clear();
    }

    public void showPartyMembers(Profile profile){
        for (Profile member : members) {
            if (member == profile) {
                continue;
            }
            profile.getPlayer().showPlayer(member.getPlayer());
        }
    }

    public void sendPartyMessage(String message){

        leader.sendMessage(message);

        for (Profile profile : members) {
            profile.sendMessage(message);
        }
    }

    public boolean isInParty(Profile profile){
        return leader == profile || members.contains(profile);
    }

    public int getPartySize(){
        return members.size() + 1;
    }

    public boolean isLeader(Profile profile){
        return profile == leader;
    }
}
