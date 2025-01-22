package net.syphlex.practice.manager.ladder.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class BowLadder extends Ladder {
    public BowLadder(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setMaterial(Material.BOW)
                .setName(Practice.PRIMARY_COLOR + "&lBow")
                .build();

        armor = new ItemStack[4];
        inventory = new ItemStack[36];

        armor[0] = new ItemBuilder()
                .setMaterial(Material.CHAINMAIL_BOOTS)
                .setUnbreakable(true)
                .build();
        armor[1] = new ItemBuilder()
                .setMaterial(Material.CHAINMAIL_LEGGINGS)
                .setUnbreakable(true)
                .build();
        armor[2] = new ItemBuilder()
                .setMaterial(Material.CHAINMAIL_CHESTPLATE)
                .setUnbreakable(true)
                .build();
        armor[3] = new ItemBuilder()
                .setMaterial(Material.CHAINMAIL_HELMET)
                .setUnbreakable(true)
                .build();

        inventory[0] = new ItemBuilder()
                .setMaterial(Material.BOW)
                //.addVanillaEnchant(Enchantment.ARROW_DAMAGE, 1)
                .addVanillaEnchant(Enchantment.ARROW_KNOCKBACK, 1)
                .addVanillaEnchant(Enchantment.ARROW_INFINITE, 1)
                .setUnbreakable(true)
                .build();
        inventory[1] = new ItemBuilder()
                .setMaterial(Material.ENDER_PEARL)
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
