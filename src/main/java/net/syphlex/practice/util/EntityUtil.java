package net.syphlex.practice.util;

import lombok.experimental.UtilityClass;
import net.syphlex.practice.manager.arena.Arena;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@UtilityClass
public class EntityUtil {

    public void removeEntitiesFromArena(Arena arena){

        if (arena.getSpectate() == null) {
            return;
        }

        for (Entity entity : arena.getSpectate().getWorld().getEntities()) {

            if (entity instanceof Player)
                continue;

            double distance = entity.getLocation().distanceSquared(arena.getSpectate());

            if (distance <= 2500) {
                entity.remove();
            }
        }
    }

}
