package net.syphlex.practice.manager.ladder.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class OneTapLadder extends Ladder {
    public OneTapLadder(String name) {
        super(name);


        menuIcon = new ItemBuilder()
                .setMaterial(Material.LEASH)
                .setName(Practice.PRIMARY_COLOR + "One Tap")
                .build();

        inventory = new ItemStack[36];
        armor = new ItemStack[4];

        inventory[0] = new ItemBuilder()
                .setMaterial(Material.WOOL)
                .setAmount(64)
                .build();
    }
}
