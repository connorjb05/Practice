package net.syphlex.practice.manager.ladder.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;

public class BedFightLadder extends Ladder {
    public BedFightLadder(String name) {
        super(name);

        menuIcon = new ItemBuilder()
                .setMaterial(Material.BED)
                .setName(Practice.PRIMARY_COLOR + "Bed Fight")
                .build();
    }
}
