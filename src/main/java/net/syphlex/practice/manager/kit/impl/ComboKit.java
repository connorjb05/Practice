package net.syphlex.practice.manager.kit.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.util.ItemBuilder;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ComboKit extends Kit {
    public ComboKit(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setMaterial(Material.RAW_FISH)
                .setDurability((short)3)
                .setName(Practice.PRIMARY_COLOR + "Combo")
                .build();

        inventory = new ItemStack[36];
        armor = new ItemStack[4];

        armor[0] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_BOOTS)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10)
                .addVanillaEnchant(Enchantment.DURABILITY, 10)
                .build();
        armor[1] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_LEGGINGS)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10)
                .addVanillaEnchant(Enchantment.DURABILITY, 10)
                .build();
        armor[2] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_CHESTPLATE)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10)
                .addVanillaEnchant(Enchantment.DURABILITY, 10)
                .build();
        armor[3] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_HELMET)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10)
                .addVanillaEnchant(Enchantment.DURABILITY, 10)
                .build();

        inventory[0] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_SWORD)
                .addVanillaEnchant(Enchantment.DAMAGE_ALL, 3)
                .addVanillaEnchant(Enchantment.DURABILITY, 10)
                .build();
        inventory[1] = new ItemBuilder()
                .setMaterial(Material.ENDER_PEARL)
                .setAmount(16)
                .build();
        inventory[2] = new ItemBuilder()
                .setMaterial(Material.GOLDEN_APPLE)
                .setDurability((short)1)
                .setAmount(64)
                .build();
        inventory[3] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_HELMET)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10)
                .addVanillaEnchant(Enchantment.DURABILITY, 10)
                .build();
        inventory[4] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_BOOTS)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10)
                .addVanillaEnchant(Enchantment.DURABILITY, 10)
                .build();
        inventory[5] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_LEGGINGS)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10)
                .addVanillaEnchant(Enchantment.DURABILITY, 10)
                .build();
        inventory[6] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_CHESTPLATE)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10)
                .addVanillaEnchant(Enchantment.DURABILITY, 10)
                .build();

        inventory[7] = new ItemBuilder()
                .setMaterial(Material.POTION)
                .setDurability((short)8226)
                .build();
        inventory[16] = new ItemBuilder()
                .setMaterial(Material.POTION)
                .setDurability((short)8226)
                .build();
        inventory[25] = new ItemBuilder()
                .setMaterial(Material.POTION)
                .setDurability((short)8226)
                .build();
        inventory[34] = new ItemBuilder()
                .setMaterial(Material.POTION)
                .setDurability((short)8226)
                .build();
        //inventory[7] = new ItemBuilder()
        //        .setMaterial(Material.POTION)
        //        .setDurability((short)8265)
        //        .build();
    }
}
