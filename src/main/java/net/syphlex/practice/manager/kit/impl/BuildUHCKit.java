package net.syphlex.practice.manager.kit.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;

public class BuildUHCKit extends Kit {
    public BuildUHCKit(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setMaterial(Material.LAVA_BUCKET)
                .setName(Practice.PRIMARY_COLOR + "Build UHC")
                .build();
    }
}
