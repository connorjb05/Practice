package net.syphlex.practice.manager.kit.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class BowKit extends Kit {
    public BowKit(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setMaterial(Material.BOW)
                .setName(Practice.PRIMARY_COLOR + "Bow")
                .build();

        armor = new ItemStack[4];
        inventory = new ItemStack[36];

        armor[0] = new ItemBuilder()
                .setMaterial(Material.LEATHER_BOOTS)
                .build();
        armor[1] = new ItemBuilder()
                .setMaterial(Material.LEATHER_LEGGINGS)
                .build();
        armor[2] = new ItemBuilder()
                .setMaterial(Material.LEATHER_CHESTPLATE)
                .build();
        armor[3] = new ItemBuilder()
                .setMaterial(Material.LEATHER_HELMET)
                .build();

        inventory[0] = new ItemBuilder()
                .setMaterial(Material.BOW)
                .addVanillaEnchant(Enchantment.ARROW_DAMAGE, 1)
                .addVanillaEnchant(Enchantment.ARROW_INFINITE, 1)
                .build();
        inventory[1] = new ItemBuilder()
                .setMaterial(Material.POTION)
                .setDurability((short)16421)
                .build();
        inventory[2] = new ItemBuilder()
                .setMaterial(Material.POTION)
                .setDurability((short)16421)
                .build();
        inventory[3] = new ItemBuilder()
                .setMaterial(Material.COOKED_BEEF)
                .setAmount(64)
                .build();
        inventory[4] = new ItemBuilder()
                .setMaterial(Material.ARROW)
                .build();
    }
}
