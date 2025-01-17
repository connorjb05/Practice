package net.syphlex.practice.manager.event;

import lombok.Getter;
import lombok.Setter;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@Getter
public enum EventInfo {
    SUMO1V1("sumo", "Sumo", new ItemBuilder()
            .setMaterial(Material.ANVIL)
            .setName("&cSumo")
            .build(), Arrays.asList(""));

    private final String identifier;
    private final String displayName;
    private final ItemStack menuIcon;
    private final List<String> description;

    EventInfo(String identifier, String displayName, ItemStack menuIcon, List<String> description){
        this.identifier = identifier;
        this.displayName = displayName;
        this.menuIcon = menuIcon;
        this.description = description;
    }
}
