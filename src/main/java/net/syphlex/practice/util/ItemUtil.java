package net.syphlex.practice.util;

import lombok.experimental.UtilityClass;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class ItemUtil {

    public ItemStack getQueueMatchItem(){
        return new ItemBuilder()
                .setName(Practice.PRIMARY_COLOR + "Queue Match &7(Right Click)")
                .setMaterial(Material.IRON_SWORD)
                .setUnbreakable(true)
                .build();
    }

    public ItemStack getBotMatchItem(){
        return new ItemBuilder()
                .setName(Practice.PRIMARY_COLOR + "Duel a Bot &7(Right Click)")
                .setMaterial(Material.GOLD_SWORD)
                .setUnbreakable(true)
                .build();
    }

    public ItemStack getEventHostItem(){
        return new ItemBuilder()
                .setName(Practice.PRIMARY_COLOR + "Event Host &7(Right Click)")
                .setMaterial(Material.EYE_OF_ENDER)
                .build();
    }

    public ItemStack getCreatePartyItem(){
        return new ItemBuilder()
                .setName(Practice.PRIMARY_COLOR + "Create Party &7(Right Click)")
                .setMaterial(Material.NAME_TAG)
                .build();
    }

    public ItemStack getLeaderboardsItem(){
        return new ItemBuilder()
                .setMaterial(Material.ITEM_FRAME)
                .setName(Practice.PRIMARY_COLOR + "Leaderboards &7(Right Click)")
                .build();
    }

    public ItemStack getSettingsItem(){
        return new ItemBuilder()
                .setMaterial(Material.SKULL_ITEM)
                .setName(Practice.PRIMARY_COLOR + "Profile Settings &7(Right Click)")
                .build();
    }

    public ItemStack getPartyMatchItem(){
        return new ItemBuilder()
                .setMaterial(Material.IRON_AXE)
                .setName(Practice.PRIMARY_COLOR + "Start Party Match &7(Right Click)")
                .build();
    }

    public ItemStack getPartyFightOtherPartyItem(){
        return new ItemBuilder()
                .setMaterial(Material.DIAMOND_AXE)
                .setName(Practice.PRIMARY_COLOR + "Fight Other Party &7(Right Click)")
                .build();
    }

    public ItemStack getPartyMemberListItem(){
        return new ItemBuilder()
                .setMaterial(Material.PAPER)
                .setName(Practice.PRIMARY_COLOR + "Party Members &7(Right Click)")
                .build();
    }

    public ItemStack getPartySettingsItem(){
        return new ItemBuilder()
                .setMaterial(Material.DIODE)
                .setName(Practice.PRIMARY_COLOR + "Party Settings &7(Right Click)")
                .build();
    }

    public ItemStack getLeavePartyItem(){
        return new ItemBuilder()
                .setName("&cLeave Party &7(Right Click)")
                .setMaterial(Material.NETHER_STAR)
                .build();
    }

    public ItemStack getLeaveQueueItem(){
        return new ItemBuilder()
                .setName("&cLeave Queue &7(Right Click)")
                .setMaterial(Material.INK_SACK)
                .setDurability((short)1)
                .build();
    }

    public ItemStack getBookKit(){
        return new ItemBuilder()
                .setMaterial(Material.ENCHANTED_BOOK)
                .setName(Practice.PRIMARY_COLOR + "Default Kit")
                .build();
    }
}
