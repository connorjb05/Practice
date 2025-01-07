package net.syphlex.practice.manager.kit.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.util.ItemBuilder;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;

public class BedFightKit extends Kit {
    public BedFightKit(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setMaterial(Material.BED)
                .setName(Practice.PRIMARY_COLOR + "Bed Fight")
                .build();
    }
}
