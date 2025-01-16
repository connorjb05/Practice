package net.syphlex.practice.manager.kit.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class SoupKit extends Kit {
    public SoupKit(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setName(Practice.PRIMARY_COLOR + "Soup")
                .setAmount(1)
                .setMaterial(Material.MUSHROOM_SOUP)
                .build();

        inventory = new ItemStack[36];
        armor = new ItemStack[4];

        armor[0] = new ItemBuilder()
                .setMaterial(Material.IRON_BOOTS)
                .setUnbreakable(true)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .build();
        armor[1] = new ItemBuilder()
                .setMaterial(Material.IRON_LEGGINGS)
                .setUnbreakable(true)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .build();
        armor[2] = new ItemBuilder()
                .setMaterial(Material.IRON_CHESTPLATE)
                .setUnbreakable(true)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .build();
        armor[3] = new ItemBuilder()
                .setMaterial(Material.IRON_HELMET)
                .setUnbreakable(true)
                .addVanillaEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .build();

        inventory[0] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_SWORD)
                .setUnbreakable(true)
                .addVanillaEnchant(Enchantment.DAMAGE_ALL, 1)
                .build();

        inventory[1] = new ItemBuilder()
                .setMaterial(Material.POTION)
                .setDurability((short)8258)
                .build();

        for (int i = 2; i < inventory.length; i++) {
            inventory[i] = new ItemBuilder()
                    .setMaterial(Material.MUSHROOM_SOUP)
                    .build();
        }
    }
}