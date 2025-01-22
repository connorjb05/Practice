package net.syphlex.practice.manager.ladder.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class GappleLadder extends Ladder {
    public GappleLadder(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setName(Practice.PRIMARY_COLOR + "&lGapple")
                .setMaterial(Material.GOLDEN_APPLE)
                .setGlowing(true)
                .build();

        inventory = new ItemStack[36];
        armor = new ItemStack[4];

        armor[0] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_BOOTS)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addVanillaEnchant(Enchantment.DURABILITY, 3)
                .addVanillaEnchant(Enchantment.PROTECTION_FALL, 4)
                .build();
        armor[1] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_LEGGINGS)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addVanillaEnchant(Enchantment.DURABILITY, 3)
                .build();
        armor[2] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_CHESTPLATE)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addVanillaEnchant(Enchantment.DURABILITY, 3)
                .build();
        armor[3] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_HELMET)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addVanillaEnchant(Enchantment.DURABILITY, 3)
                .build();

        inventory[0] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_SWORD)
                .addVanillaEnchant(Enchantment.DAMAGE_ALL, 5)
                .addVanillaEnchant(Enchantment.DURABILITY, 3)
                .addVanillaEnchant(Enchantment.FIRE_ASPECT, 2)
                .build();
        inventory[1] = new ItemBuilder()
                .setMaterial(Material.GOLDEN_APPLE)
                .setDurability((short)1)
                .setAmount(64)
                .build();
        inventory[2] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_HELMET)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addVanillaEnchant(Enchantment.DURABILITY, 3)
                .build();
        inventory[3] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_CHESTPLATE)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addVanillaEnchant(Enchantment.DURABILITY, 3)
                .build();
        inventory[4] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_LEGGINGS)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addVanillaEnchant(Enchantment.DURABILITY, 3)
                .build();
        inventory[5] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_BOOTS)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addVanillaEnchant(Enchantment.DURABILITY, 3)
                .addVanillaEnchant(Enchantment.PROTECTION_FALL, 4)
                .build();

        inventory[6] = new ItemBuilder()
                .setMaterial(Material.POTION)
                .setDurability((short)8197)
                .build();
        inventory[7] = new ItemBuilder()
                .setMaterial(Material.POTION)
                .setDurability((short)8194)
                .build();
    }
}
