package net.syphlex.practice.manager.arena.block;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public class WrappedBlockState {
    private final Material material;
    private final byte data;

    public WrappedBlockState(Material material, byte data) {
        this.material = material;
        this.data = data;
    }
}