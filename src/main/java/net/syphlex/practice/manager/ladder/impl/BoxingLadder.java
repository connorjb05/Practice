package net.syphlex.practice.manager.ladder.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BoxingLadder extends Ladder {
    public BoxingLadder(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setMaterial(Material.DIAMOND_CHESTPLATE)
                .setName(Practice.PRIMARY_COLOR + "&lBoxing")
                .build();

        inventory = new ItemStack[36];
        armor = new ItemStack[4];

        inventory[0] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_SWORD)
                .setUnbreakable(true)
                .addVanillaEnchant(Enchantment.DAMAGE_ALL, 10)
                .addVanillaEnchant(Enchantment.DURABILITY, 10)
                .build();

        potionEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }
}
