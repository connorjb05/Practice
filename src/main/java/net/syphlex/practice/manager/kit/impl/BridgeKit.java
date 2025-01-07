package net.syphlex.practice.manager.kit.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.util.ItemBuilder;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;

public class BridgeKit extends Kit {
    public BridgeKit(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setMaterial(Material.STAINED_CLAY)
                .setName(Practice.PRIMARY_COLOR + "Bridges")
                .build();

    }
}
