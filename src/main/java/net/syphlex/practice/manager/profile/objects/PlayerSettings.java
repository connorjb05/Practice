package net.syphlex.practice.manager.profile.objects;

import lombok.Getter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public enum PlayerSettings {
    DUEL_REQUEST("Duel Requests", new ItemBuilder()
            .setMaterial(Material.DIAMOND_SWORD)
            .setName(Practice.PRIMARY_COLOR + "&lDuel Requests")
            .build(), Arrays.asList(
            "&7Allow players to send you",
            "&7duel requests.")),
    PARTY_INVITES("Party Invites", new ItemBuilder()
            .setMaterial(Material.NAME_TAG)
            .setName(Practice.PRIMARY_COLOR + "&lParty Invites")
            .build(), Arrays.asList(
            "&7Allow parties to send you",
            "&7party invitations.")),
    PRIVATE_MESSAGES("Private Messages", new ItemBuilder()
            .setMaterial(Material.BOOK_AND_QUILL)
            .setName(Practice.PRIMARY_COLOR + "&lPrivate Messages")
            .build(), Arrays.asList(
            "&7Allow players to privately",
            "&7message you.")),
    SCOREBOARD("Scoreboard", new ItemBuilder()
            .setMaterial(Material.PAINTING)
            .setName(Practice.PRIMARY_COLOR + "&lScoreboard")
            .build(), Collections.singletonList(
            "&7Toggle your sidebar visibility.")),
    GLOBAL_CHAT("Global Chat", new ItemBuilder()
            .setMaterial(Material.PAPER)
            .setName(Practice.PRIMARY_COLOR + "&lGlobal Chat")
            .build(), Collections.singletonList("&7See public messages.")),
    IN_MATCH_CHAT("In Match Chat", new ItemBuilder()
            .setMaterial(Material.MAP)
            .setName(Practice.PRIMARY_COLOR + "&lIn Match Chat")
            .build(), Collections.singletonList("&7See in-match messages.")),
    KILL_EFFECTS("Kill Effects", new ItemBuilder()
            .setMaterial(Material.SKULL_ITEM)
            .setName(Practice.PRIMARY_COLOR + "&lKill Effects")
            .build(), Arrays.asList(
            "&7Effects that play after",
            "&7wins or kills.",
            "&7",
            "&cRequires <rank> rank."));

    private final String name;
    private final ItemStack menuIcon;
    private final List<String> description;

    PlayerSettings(String name, ItemStack menuIcon, List<String> description) {
        this.name = name;
        this.menuIcon = menuIcon;
        this.description = description;
    }
}
