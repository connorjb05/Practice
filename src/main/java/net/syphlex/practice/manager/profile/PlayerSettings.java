package net.syphlex.practice.manager.profile;

import lombok.Getter;
import net.syphlex.practice.util.ItemBuilder;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public enum PlayerSettings {
    DUEL_REQUEST("Duel Requests", new ItemBuilder()
            .setMaterial(Material.DIAMOND_SWORD)
            .setName("&6Duel Requests")
            .build()),
    PARTY_INVITES("Party Invites", new ItemBuilder()
            .setMaterial(Material.NAME_TAG)
            .setName("&6Party Invites")
            .build()),
    PRIVATE_MESSAGES("Private Messages", new ItemBuilder()
            .setMaterial(Material.PAPER)
            .setName("&6Private Messages")
            .build()),
    SCOREBOARD("Scoreboard", new ItemBuilder()
            .setMaterial(Material.PAINTING)
            .setName("&6Scoreboard")
            .build());

    private final String name;
    private final ItemStack menuIcon;

    PlayerSettings(String name, ItemStack menuIcon){
        this.name = name;
        this.menuIcon = menuIcon;
    }
}
