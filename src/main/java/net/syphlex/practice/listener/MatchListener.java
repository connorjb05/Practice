package net.syphlex.practice.listener;

import com.ngxdev.entity.PotionEffectAddEvent;
import net.syphlex.practice.Practice;
import net.syphlex.practice.event.ProfileDamageEvent;
import net.syphlex.practice.manager.ladder.impl.BoxingLadder;
import net.syphlex.practice.manager.ladder.impl.BridgeLadder;
import net.syphlex.practice.manager.ladder.impl.SumoLadder;
import net.syphlex.practice.manager.match.Match;
import net.syphlex.practice.manager.match.MatchState;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.manager.profile.objects.PlayerState;
import net.syphlex.practice.util.ItemUtil;
import net.syphlex.practice.util.PlayerUtil;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class MatchListener implements Listener {


    @EventHandler
    public void onProfileDamage(ProfileDamageEvent e){

        final Profile attacker = e.getAttacker();
        final Profile victim = e.getVictim();

        if (victim == null) {
            return;
        }

        if (victim.getPlayerState() == PlayerState.IN_SPAWN) {
            e.setCancelled(true);
            return;
        }

        // damage event
        if (attacker == null) {

            if (victim.isInMatch()) {

                final Match match = victim.getMatch();

                if (match.getLadder() instanceof SumoLadder
                        || match.getLadder() instanceof BoxingLadder
                        || match.getLadder() instanceof BridgeLadder) {

                    if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                            || e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                        return;
                    }

                    e.setCancelled(true);
                }
            }
            return;
        }

        // player vs player damage event

        if (victim.isInMatch()) {

            final Match match = victim.getMatch();

            if (match.getMatchState() != MatchState.ONGOING) {
                e.setCancelled(true);
                return;
            }

            if (!match.isFfa() && match.getPlayerTeam(attacker).contains(victim)) {
                e.setCancelled(true);
                return;
            }

            victim.setLastAttacker(attacker);

            if (match.getLadder() instanceof BridgeLadder) {

                if (victim.getPlayer().getHealth() - e.getFinalDamage() <= 0) {
                    e.setCancelled(true);

                    victim.getPlayer().setHealth(victim.getPlayer().getMaxHealth());

                    if (match.getAliveFromTeam(1).contains(victim)) {
                        victim.teleport(match.getArena().getPosition1());
                    } else {
                        victim.teleport(match.getArena().getPosition2());
                    }

                    PlayerUtil.resetPlayer(victim.getPlayer());
                    match.getLadder().giveKit(victim);
                    victim.getPlayer().updateInventory();

                    match.sendMatchMessage(Practice.PRIMARY_COLOR + victim.getPlayer().getName()
                            + Practice.QUATERNARY_COLOR + " was killed by "
                            + Practice.PRIMARY_COLOR + attacker.getPlayer().getName()
                            + Practice.QUATERNARY_COLOR + ".");

                    for (Profile profile : match.getOpponentList(victim)) {
                        profile.getPlayer().playSound(profile.getPlayer().getLocation(), Sound.ORB_PICKUP, 1, 1);
                    }
                }

            }

            if (match.getLadder() instanceof SumoLadder) {
                e.setDamage(0);
            }

            if (match.getLadder() instanceof BoxingLadder) {
                attacker.setHits(attacker.getHits() + 1);

                int maxHits = (100 * Math.max(
                        match.getAliveFromTeam(1).size(),
                        match.getAliveFromTeam(2).size()));

                if (attacker.getHits() >= maxHits) {
                    match.eliminate(victim);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e){

        final Player player = e.getPlayer();
        final Profile profile = Practice.get().getProfileManager().get(player);

        if (profile.isBuild() && player.getGameMode() == GameMode.CREATIVE){
            return;
        }

        if (profile.isInMatch()) {

            final Match match = profile.getMatch();

            if (match.getLadder() instanceof BridgeLadder) {

                if (!match.getArena().isLocationInsideArena(e.getBlock().getLocation())) {
                    e.setCancelled(true);
                    return;
                }

                match.getPlacedBlocks().add(e.getBlock());
                return;
            }

            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e){

        final Player player = e.getPlayer();
        final Profile profile = Practice.get().getProfileManager().get(player);

        if (profile.isBuild() && player.getGameMode() == GameMode.CREATIVE){
            return;
        }

        if (profile.isInMatch()) {

            final Match match = profile.getMatch();

            e.getBlock().getDrops().clear(); // Prevents any drops

            if (match.getLadder() instanceof BridgeLadder) {

                if (!match.getArena().isLocationInsideArena(e.getBlock().getLocation())
                        && !match.getPlacedBlocks().contains(e.getBlock())) {
                    e.setCancelled(true);
                    return;
                }

                match.getPlacedBlocks().remove(e.getBlock());
                return;
            }

            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {

        final Player p = e.getEntity();
        final Profile profile = Practice.get().getProfileManager().get(p);

        e.setDeathMessage(null);
        e.setDroppedExp(0);
        e.getDrops().clear();

        if (profile.isInMatch()) {

            Match match = profile.getMatch();

            if (match.getLadder() instanceof BridgeLadder) {

                p.spigot().respawn();

                p.setHealth(p.getMaxHealth());

                if (match.getAliveFromTeam(1).contains(profile)) {
                    profile.teleport(match.getArena().getPosition1());
                } else {
                    profile.teleport(match.getArena().getPosition2());
                }

                PlayerUtil.resetPlayer(profile.getPlayer());
                match.getLadder().giveKit(profile);
                profile.getPlayer().updateInventory();

                if (profile.getLastAttacker() == null) {
                    match.sendMatchMessage(Practice.PRIMARY_COLOR + profile.getPlayer().getName()
                            + Practice.QUATERNARY_COLOR + " died.");
                } else {
                    match.sendMatchMessage(Practice.PRIMARY_COLOR + profile.getPlayer().getName()
                            + Practice.QUATERNARY_COLOR + " was killed by "
                            + Practice.PRIMARY_COLOR + profile.getLastAttacker().getPlayer().getName()
                            + Practice.QUATERNARY_COLOR + ".");
                }

                for (Profile opponents : match.getOpponentList(profile)) {
                    opponents.getPlayer().playSound(profile.getPlayer().getLocation(), Sound.ORB_PICKUP, 1, 1);
                }

                return;
            }

            p.setBedSpawnLocation(p.getLocation(), true);

            match.eliminate(profile);

            Bukkit.getScheduler().runTaskLater(Practice.get(), () -> {
                p.spigot().respawn();
                p.getInventory().setItem(0, ItemUtil.getPlayAgainItem());
            }, 15L);
        }
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent e) {
        final Player p = e.getPlayer();
        final Profile profile = Practice.get().getProfileManager().get(p);

        if (profile.getPlayerState() == PlayerState.IN_SPAWN) {
            if (e.getTo().getY() <= 50) {
                profile.teleport(Practice.get().getConfigManager().getMainSpawn());
            }
        }

        if (profile.isInMatch()) {

            if (profile.getMatch().getMatchState() == MatchState.STARTING
                    && profile.getMatch().getLadder().isFreeze()) {
                // Prevent position change, but allow yaw and pitch changes
                Location from = e.getFrom();
                Location to = e.getTo();

                // If the position is changing (X, Y, Z), cancel the movement
                if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
                    // Set the to location to be the same as from (no position change)

                    Location oldSpot = from.clone();
                    //oldSpot.setYaw(to.getYaw());
                    //oldSpot.setPitch(to.getPitch());

                    e.setTo(oldSpot);
                }

                // Prevent movement in all directions (X, Y, Z), but allow yaw/pitch changes
                p.setVelocity(new Vector(0, 0, 0));
            }

            Match match = profile.getMatch();

            if (match.getLadder() instanceof BridgeLadder) {
                if (e.getTo().getY() <= match.getArena().getMinY() - 7) {

                    if (match.getProfileMap().get(profile).getX() == 1) {
                        profile.teleport(match.getArena().getPosition1());
                    } else {
                        profile.teleport(match.getArena().getPosition2());
                    }

                    match.sendMatchMessage(Practice.PRIMARY_COLOR + p.getName()
                            + Practice.QUATERNARY_COLOR + " died.");

                    for (Profile opponents : match.getOpponentList(profile)) {
                        opponents.getPlayer().playSound(profile.getPlayer().getLocation(), Sound.ORB_PICKUP, 1, 1);
                    }

                    PlayerUtil.resetPlayer(profile.getPlayer());
                    match.getLadder().giveKit(profile);
                    profile.getPlayer().updateInventory();
                }
            }

            if (match.getLadder() instanceof SumoLadder) {
                if (e.getTo().getY() <= match.getArena().getMinY()) {
                    match.eliminate(profile);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled=true, priority= EventPriority.NORMAL)
    public void PlayerTeleportEvent(PlayerTeleportEvent event) {
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
            Location location = event.getTo();
            location.setX((double)location.getBlockX() + 0.5);
            location.setY(location.getBlockY());
            location.setZ((double)location.getBlockZ() + 0.5);
            event.setTo(location);
        }
    }

    @EventHandler
    public void onPortalEvent(PlayerPortalEvent e){

        final Player player = e.getPlayer();
        final Profile profile = Practice.get().getProfileManager().get(player);

        e.setCancelled(true);

        if (profile.isInMatch()) {

            final Match match = profile.getMatch();

            // player scored
            if (match.getLadder() instanceof BridgeLadder) {
                // todo restart round
            }
        }
    }

    @EventHandler
    public void onConsumeItemEvent(PlayerItemConsumeEvent e){

        if (e.getItem().getType() != Material.GOLDEN_APPLE) {
            return;
        }

        final Player player = e.getPlayer();
        final Profile profile = Practice.get().getProfileManager().get(player);

        if (profile.isInMatch()) {
            if (profile.getMatch().getLadder() instanceof BridgeLadder) {
                profile.getPlayer().setHealth(profile.getPlayer().getMaxHealth());
            }
        }
    }

    @EventHandler
    public void onPotionEffectAddEvent(PotionEffectAddEvent e) {

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) e.getEntity();
        final Profile profile = Practice.get().getProfileManager().get(player);

        if (profile.isInMatch()) {

            if (profile.getMatch().getLadder() instanceof BridgeLadder) {

                if (e.getCause().equals(PotionEffectAddEvent.EffectCause.UNKNOWN)
                        && e.getEffect().getType().equals(PotionEffectType.REGENERATION)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onProjectileLaunchEvent(ProjectileLaunchEvent e){

        if (!(e.getEntity() instanceof Arrow)) {
            return;
        }

        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Player player = (Player) e.getEntity().getShooter();
        final Profile profile = Practice.get().getProfileManager().get(player);

        if (profile.isInMatch()) {
            if (profile.getMatch().getLadder() instanceof BridgeLadder) {
                new BukkitRunnable(){
                    @Override
                    public void run(){

                        if (!player.isOnline()) {
                            cancel();
                            return;
                        }

                        player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                    }
                }.runTaskLater(Practice.get(), 60L);
            }
        }
    }
}
