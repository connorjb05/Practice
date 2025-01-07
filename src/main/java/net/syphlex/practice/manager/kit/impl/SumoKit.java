package net.syphlex.practice.manager.kit.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.util.ItemBuilder;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SumoKit extends Kit {
    public SumoKit(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setMaterial(Material.ANVIL)
                .setName(Practice.PRIMARY_COLOR + "Sumo")
                .build();

        // empty inventory
        inventory = new ItemStack[36];
        armor = new ItemStack[4];
    }
}
