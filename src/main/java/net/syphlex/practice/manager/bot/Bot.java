package net.syphlex.practice.manager.bot;

import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.LookClose;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.manager.ladder.impl.NoDebuffLadder;
import net.syphlex.practice.manager.profile.Profile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


@Getter
public class Bot extends Profile {

    private final NPC npc;
    private final BotDifficulty botDifficulty;
    private boolean strafingRight = true;

    private Player currentTarget = null;

    public Bot(BotDifficulty difficulty) {
        super(null);

        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        this.npc = registry.createNPC(org.bukkit.entity.EntityType.PLAYER, "Bot");

        this.botDifficulty = difficulty;

        // Add traits for bot behavior
        this.npc.addTrait(LookClose.class); // Makes the bot look at nearby players
        configureDifficulty(difficulty);
    }

    @Override
    public void teleport(Location location){

        if (!npc.isSpawned()) {
            npc.spawn(location);
            return;
        }

        npc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    private void configureDifficulty(BotDifficulty difficulty) {
        // Adjust bot settings based on difficulty
        switch (difficulty) {
            case EASY:
                // Example: slower movement, less aggressive
                break;
            case MODERATE:
                // Example: balanced stats
                break;
            case HARD:
                // Example: faster movement, more aggressive
                break;
        }
    }

    public void spawn(Location location) {
        npc.spawn(location);
    }

    public void moveTo(Location location) {
        if (npc.isSpawned()) {
            npc.getNavigator().setTarget(location);
        }
    }

    public void attack(Player target) {
        if (npc.isSpawned()) {
            // Example attack logic
            npc.getNavigator().setTarget(target.getLocation());
        }
    }

    public void despawn() {
        if (npc.isSpawned()) {
            npc.despawn();
        }
    }

    public boolean isSpawned() {
        return npc.isSpawned();
    }

    public void startBot(Ladder ladder){
        if (ladder instanceof NoDebuffLadder) {
            executeNoDebuffLogic();
        }
    }

    public void executeNoDebuffLogic() {
        if (!npc.isSpawned()) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!npc.isSpawned()) {
                    cancel();
                    return;
                }

                for (Entity entities : npc.getEntity().getNearbyEntities(16, 8, 16)) {

                    if (!(entities instanceof Player)
                            || CitizensAPI.getNPCRegistry().isNPC(entities)) {
                        continue;
                    }

                    currentTarget = (Player) entities;
                }

                // Strafing logic
                Location botLocation = npc.getEntity().getLocation();
                Location targetLocation = currentTarget.getLocation();

                double strafeDistance = botDifficulty.getReach() / 2; // Strafing based on reach
                Location strafeLocation = botLocation.clone();

                if (strafingRight) {
                    strafeLocation.setX(botLocation.getX() + strafeDistance);
                } else {
                    strafeLocation.setX(botLocation.getX() - strafeDistance);
                }

                strafingRight = !strafingRight; // Alternate strafing direction
                npc.getNavigator().setTarget(strafeLocation);

                // Check health and throw potions
                double botHealth = ((Player) npc.getEntity()).getHealth();
                if (botHealth < 10.0) { // Health threshold for potion usage
                    throwHealingPotion(botLocation);
                }

                // Aggressive attack logic
                double distance = botLocation.distance(targetLocation);
                if (distance <= botDifficulty.getReach()) {
                    attackPlayer(currentTarget, botDifficulty);
                } else {
                    npc.getNavigator().setTarget(targetLocation);
                }
            }
        }.runTaskTimer(CitizensAPI.getPlugin(), 0L, 20L); // Run every second
    }

    private void attackPlayer(Player target, BotDifficulty difficulty) {
        int minCps = difficulty.getMinCps();
        int maxCps = difficulty.getMaxCps();

        int attackDelay = 20 / (minCps + (int) (Math.random() * (maxCps - minCps))); // Random CPS
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!npc.isSpawned() || !target.isOnline()) {
                    cancel();
                    return;
                }

                double distance = npc.getEntity().getLocation().distance(target.getLocation());
                if (distance > difficulty.getReach()) {
                    cancel();
                    return;
                }

                target.damage(1.0, npc.getEntity()); // Deal damage to the player
            }
        }.runTaskTimer(CitizensAPI.getPlugin(), 0L, attackDelay);
    }

    private void throwHealingPotion(Location location) {
        if (!npc.isSpawned()) return;

        ItemStack potion = new ItemStack(Material.POTION, 1, (short) 16421); // Instant health II potion
        npc.getEntity().getWorld().dropItem(location, potion);

        // Simulate the bot using the potion (expandable for visual effects, etc.)
        // Optional: apply healing
        ((Player) npc.getEntity()).setHealth(
                Math.min(((Player) npc.getEntity()).getMaxHealth(),
                        ((Player) npc.getEntity()).getHealth() + 8.0)); // Heal by 4 hearts
    }
}

