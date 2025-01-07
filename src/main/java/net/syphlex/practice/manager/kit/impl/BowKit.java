package net.syphlex.practice.manager.kit.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;

public class BowKit extends Kit {
    public BowKit(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setMaterial(Material.BOW)
                .setName(Practice.PRIMARY_COLOR + "Bow")
                .build();
    }
}
