package net.syphlex.practice.manager.kit.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.util.ItemBuilder;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BridgeKit extends Kit {
    public BridgeKit(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setMaterial(Material.STAINED_CLAY)
                .setName(Practice.PRIMARY_COLOR + "Bridges")
                .build();

        armor = new ItemStack[4];
        inventory = new ItemStack[36];

        inventory[0] = new ItemBuilder()
                .setMaterial(Material.IRON_SWORD)
                .build();
        inventory[1] = new ItemBuilder()
                .setMaterial(Material.STAINED_CLAY)
                .setAmount(64)
                .build();
        inventory[2] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_PICKAXE)
                .build();
    }
}
