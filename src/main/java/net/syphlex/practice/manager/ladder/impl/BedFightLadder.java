package net.syphlex.practice.manager.ladder.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class BedFightLadder extends Ladder {
    public BedFightLadder(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setMaterial(Material.BED)
                .setName(Practice.PRIMARY_COLOR + "Bed Fight")
                .build();

        inventory = new ItemStack[36];
        armor = new ItemStack[4];

        inventory[0] =  new ItemBuilder()
                .setMaterial(Material.WOOD_SWORD)
                .setUnbreakable(true)
                .build();

        inventory[1] = new ItemBuilder()
                .setMaterial(Material.WOOL)
                .setAmount(64)
                .build();

        inventory[2] = new ItemBuilder()
                .setMaterial(Material.SHEARS)
                .setUnbreakable(true)
                .build();

        inventory[3] = new ItemBuilder()
                .setMaterial(Material.WOOD_PICKAXE)
                .addVanillaEnchant(Enchantment.DIG_SPEED, 1)
                .setUnbreakable(true)
                .build();

        inventory[4] = new ItemBuilder()
                .setMaterial(Material.WOOD_AXE)
                .addVanillaEnchant(Enchantment.DIG_SPEED, 1)
                .setUnbreakable(true)
                .build();
    }
}
