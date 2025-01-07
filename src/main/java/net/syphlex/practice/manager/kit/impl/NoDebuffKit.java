package net.syphlex.practice.manager.kit.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.util.ItemBuilder;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class NoDebuffKit extends Kit {
    public NoDebuffKit(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setName(Practice.PRIMARY_COLOR + "NoDebuff")
                .setAmount(1)
                .setMaterial(Material.POTION)
                .setDurability((short)16421)
                .build();

        inventory = new ItemStack[36];
        armor = new ItemStack[4];

        // Armor setup (Protection 2 Diamond Armor)
        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        helmet.addEnchantment(Enchantment.DURABILITY, 3);

        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        chestplate.addEnchantment(Enchantment.DURABILITY, 3);

        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        leggings.addEnchantment(Enchantment.DURABILITY, 3);

        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        boots.addEnchantment(Enchantment.DURABILITY, 3);
        boots.addEnchantment(Enchantment.PROTECTION_FALL, 4);

        armor[0] = boots;
        armor[1] = leggings;
        armor[2] = chestplate;
        armor[3] = helmet;

        // Weapon and essential items
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
        sword.addEnchantment(Enchantment.DURABILITY, 3);
        sword.addEnchantment(Enchantment.FIRE_ASPECT, 2);
        inventory[0] = sword; // Sword
        inventory[1] = new ItemStack(Material.ENDER_PEARL, 16); // Ender Pearls
        inventory[2] = new ItemStack(Material.POTION, 1, (short)8226); // Speed Potion
        inventory[3] = new ItemStack(Material.POTION, 1, (short)8259); // Fire Resistance Potion
        inventory[8] = new ItemStack(Material.COOKED_BEEF, 64); // Steak

        // Health potions and remaining speed potions
        ItemStack healthPotion = new ItemStack(Material.POTION, 1, (short)16421);
        for (int i = 0; i < 36; i++) {
            if (inventory[i] == null) {
                inventory[i] = healthPotion;
            }
        }

        // Add 3 speed potions to the far right (slots 27-29)
        inventory[17] = new ItemStack(Material.POTION, 1, (short)8226);
        inventory[26] = new ItemStack(Material.POTION, 1, (short)8226);
        inventory[35] = new ItemStack(Material.POTION, 1, (short)8226);
    }
}
