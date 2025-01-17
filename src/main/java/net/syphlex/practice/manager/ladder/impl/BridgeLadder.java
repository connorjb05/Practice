package net.syphlex.practice.manager.ladder.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class BridgeLadder extends Ladder {


    public BridgeLadder(String name) {
        super(name);

        freeze = true;

        menuIcon = new ItemBuilder()
                .setMaterial(Material.STAINED_CLAY)
                .setDurability((short)11)
                .setName(Practice.PRIMARY_COLOR + "Bridge")
                .build();

        armor = new ItemStack[4];
        inventory = new ItemStack[36];

        inventory[0] = new ItemBuilder()
                .setMaterial(Material.IRON_SWORD)
                .setUnbreakable(true)
                .build();
        inventory[1] = new ItemBuilder()
                .setMaterial(Material.DIAMOND_PICKAXE)
                .addVanillaEnchant(Enchantment.DIG_SPEED, 2)
                .setUnbreakable(true)
                .build();
        inventory[2] = new ItemBuilder()
                .setMaterial(Material.STAINED_CLAY)
                .setAmount(64)
                .build();
        inventory[3] = new ItemBuilder()
                .setMaterial(Material.STAINED_CLAY)
                .setAmount(64)
                .build();
        inventory[4] = new ItemBuilder()
                .setMaterial(Material.BOW)
                .setUnbreakable(true)
                .build();
        inventory[5] = new ItemBuilder()
                .setMaterial(Material.GOLDEN_APPLE)
                .setAmount(8)
                .build();
        inventory[6] = new ItemBuilder()
                .setMaterial(Material.ARROW)
                .build();
    }
}
