package net.syphlex.practice.manager.ladder.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SumoLadder extends Ladder {
    public SumoLadder(String name) {
        super(name);

        freeze = true;

        menuIcon = new ItemBuilder()
                .setMaterial(Material.ANVIL)
                .setName(Practice.PRIMARY_COLOR + "&lSumo")
                .build();

        // empty inventory
        inventory = new ItemStack[36];
        armor = new ItemStack[4];
    }
}
