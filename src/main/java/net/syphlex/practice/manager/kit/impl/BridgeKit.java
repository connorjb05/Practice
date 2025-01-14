package net.syphlex.practice.manager.kit.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.util.ItemBuilder;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class BridgeKit extends Kit {


    public BridgeKit(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setMaterial(Material.STAINED_CLAY)
                .setName(Practice.PRIMARY_COLOR + "Bridge")
                .build();

        armor = new ItemStack[4];
        inventory = new ItemStack[36];

        inventory[0] = new ItemBuilder()
                .setMaterial(Material.IRON_SWORD)
                .setUnbreakable(true)
                .build();
        inventory[2] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_PICKAXE)
                .addVanillaEnchant(Enchantment.DIG_SPEED, 2)
                .setUnbreakable(true)
                .build();
        inventory[4] = new ItemBuilder()
                .setMaterial(Material.BOW)
                .setUnbreakable(true)
                .build();
        inventory[6] = new ItemBuilder()
                .setMaterial(Material.ARROW)
                .build();
        inventory[5] = new ItemBuilder()
                .setMaterial(Material.GOLDEN_APPLE)
                .setAmount(8)
                .build();
    }
}
