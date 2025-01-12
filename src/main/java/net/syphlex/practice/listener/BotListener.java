package net.syphlex.practice.listener;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;

public class BotListener implements Listener {

    private final Map<NPC, Long> lastHitTimes = new HashMap<>();
    private static final long ATTACK_COOLDOWN = 9L * 50L; // 1 second (1000ms) cooldown

    // 50ms * 20 ticks = 1000 ms

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {

        if (!(e.getDamager() instanceof Player)) {
            return;
        }

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        if (!CitizensAPI.getNPCRegistry().isNPC(e.getEntity())) {
            return;
        }

        NPC npc = CitizensAPI.getNPCRegistry().getNPC(e.getEntity());

        // Only process damage if the cooldown has passed
        long currentTime = System.currentTimeMillis();
        long lastHitTime = lastHitTimes.getOrDefault(npc, 0L);

        // If enough time has passed since the last hit, apply damage
        if (currentTime - lastHitTime > ATTACK_COOLDOWN) {
            lastHitTimes.put(npc, currentTime); // Update last hit time
            // Apply the damage to the NPC
        } else {
            // If the cooldown hasn't expired, cancel the event (prevent the NPC from being hit again)
            e.setCancelled(true);
        }
    }
}
