package net.syphlex.practice.util;

import lombok.experimental.UtilityClass;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.profile.Profile;
import org.bukkit.entity.Player;

@UtilityClass
public class InventoryUtil {

    public void setSpawnInventory(Player player) {

        if (player == null) {
            return;
        }

        final Profile profile = Practice.get().getProfileManager().get(player);

        if (profile == null) {
            return;
        }

        if (profile.isInQueue()) {
            setQueuedInventory(player);
            return;
        }

        if (profile.isInParty()) {
            setPartyInventory(player);
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.getInventory().setItem(0, ItemUtil.getQueueMatchItem());
        player.getInventory().setItem(3, ItemUtil.getEventHostItem());
        //player.getInventory().setItem(1, ItemUtil.getBotMatchItem());
        player.getInventory().setItem(5, ItemUtil.getCreatePartyItem());
        player.getInventory().setItem(6, ItemUtil.getLayoutEditorItem());
        player.getInventory().setItem(7, ItemUtil.getLeaderboardsItem());
        player.getInventory().setItem(8, ItemUtil.getSettingsItem());
    }

    public void setPartyInventory(Player player){

        if (player == null) {
            return;
        }

        final Profile profile = Practice.get().getProfileManager().get(player);

        if (!profile.isInParty()) {
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        if (profile.getParty().isLeader(profile)) {
            player.getInventory().setItem(0, ItemUtil.getPartyMatchItem());
            player.getInventory().setItem(1, ItemUtil.getPartyFightOtherPartyItem());
        }

        player.getInventory().setItem(4, ItemUtil.getPartyMemberListItem());
        player.getInventory().setItem(7, ItemUtil.getSettingsItem());
        player.getInventory().setItem(8, ItemUtil.getLeavePartyItem());
    }

    public void setQueuedInventory(Player player){

        if (player == null) {
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItem(8, ItemUtil.getLeaveQueueItem());
    }

}
