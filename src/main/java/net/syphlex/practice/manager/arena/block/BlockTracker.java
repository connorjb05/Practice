package net.syphlex.practice.manager.arena.block;

import lombok.Getter;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.util.Pair;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BlockTracker {
    // Block -> New Material + Data, Old Material + Data
    private final Map<Block, Pair<WrappedBlockState, WrappedBlockState>> changedBlocks = new HashMap<>();

    public BlockTracker(Arena arena) {
        // Constructor logic (if needed)
    }

    public void addBlockChange(Block block, Material newMaterial, byte newData, Material oldMaterial, byte oldData) {
        WrappedBlockState newState = new WrappedBlockState(newMaterial, newData);
        WrappedBlockState oldState = new WrappedBlockState(oldMaterial, oldData);
        changedBlocks.put(block, new Pair<>(newState, oldState));

        // Handle paired bed block
        if (oldMaterial == Material.BED_BLOCK) {
            Block pairedBlock = getPairedBedBlock(block);
            if (pairedBlock != null && !changedBlocks.containsKey(pairedBlock)) {
                changedBlocks.put(
                        pairedBlock,
                        new Pair<>(new WrappedBlockState(Material.AIR, (byte) 0),
                                new WrappedBlockState(pairedBlock.getType(), pairedBlock.getData()))
                );
            }
        }
    }

    public void clear() {
        for (Map.Entry<Block, Pair<WrappedBlockState, WrappedBlockState>> entry : changedBlocks.entrySet()) {
            Block block = entry.getKey();
            WrappedBlockState oldState = entry.getValue().getY();

            if (block.getType() != oldState.getMaterial() || block.getData() != oldState.getData()) {
                block.setType(oldState.getMaterial());
                block.setData(oldState.getData());
            }
        }
        changedBlocks.clear();
    }

    public Block getPairedBedBlock(Block block) {
        BlockFace[] directions = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
        for (BlockFace direction : directions) {
            Block relative = block.getRelative(direction);
            if (relative.getType() == Material.BED_BLOCK) {
                return relative;
            }
        }
        return null;
    }
}
