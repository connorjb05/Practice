package net.syphlex.practice.manager.ladder.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;

public class BuildUHCLadder extends Ladder {
    public BuildUHCLadder(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setMaterial(Material.LAVA_BUCKET)
                .setName(Practice.PRIMARY_COLOR + "Build UHC")
                .build();
    }
}
