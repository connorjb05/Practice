package net.syphlex.practice.listener;

//import com.ngxdev.entity.PotionEffectAddEvent;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.arena.block.WrappedBlockState;
import net.syphlex.practice.manager.ladder.impl.BedFightLadder;
import net.syphlex.practice.manager.ladder.impl.BoxingLadder;
import net.syphlex.practice.manager.ladder.impl.BridgeLadder;
import net.syphlex.practice.manager.ladder.impl.SumoLadder;
import net.syphlex.practice.manager.match.Match;
import net.syphlex.practice.manager.match.MatchState;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.manager.profile.objects.PlayerState;
import net.syphlex.practice.util.ItemUtil;
import net.syphlex.practice.util.Pair;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MatchListener implements Listener {

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        final Player p = (Player) e.getEntity();
        final Profile victim = Practice.get().getProfileManager().get(p);

        if (victim.getPlayerState() == PlayerState.IN_SPAWN) {
            e.setCancelled(true);
            return;
        }

        if (victim.isInMatch()) {

            final Match match = victim.getMatch();

            if (match.getLadder() instanceof SumoLadder
                    || match.getLadder() instanceof BoxingLadder
                    || match.getLadder() instanceof BridgeLadder
                    || match.getLadder() instanceof BedFightLadder) {

                if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                        || e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                    return;
                }

                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player damager = null;

        if (e.getDamager() instanceof Projectile) {
            if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
                damager = (Player) ((Projectile) e.getDamager()).getShooter();
            }
        }

        if (e.getDamager() instanceof Player) {
            damager = (Player) e.getDamager();
        }

        // dont know da fuq where this damage came from cuh
        if (damager == null) {
            return;
        }

        final Player victimPlayer = (Player) e.getEntity();
        final Profile victim = Practice.get().getProfileManager().get(victimPlayer);
        final Profile attacker = Practice.get().getProfileManager().get(damager);

        if (victim.getPlayerState() == PlayerState.IN_SPAWN) {
            e.setCancelled(true);
            return;
        }

        if (attacker.getPlayerState() == PlayerState.SPECTATING_MATCH
                || victim.getPlayerState() == PlayerState.SPECTATING_MATCH) {
            e.setCancelled(true);
            return;
        }

        if (victim.isInMatch()) {

            final Match match = victim.getMatch();

            if (match.getMatchState() != MatchState.ONGOING) {
                e.setCancelled(true);
                return;
            }

            if (!match.isFfa() && match.getTeam(attacker).getTeamMap().containsKey(victim)) {
                e.setCancelled(true);
                return;
            }

            victim.setLastAttacker(attacker);

            attacker.setHits(attacker.getHits() + 1);

            if (victim.getCombo() > victim.getLongestCombo()) {
                victim.setLongestCombo(victim.getCombo());
            }
            victim.setCombo(0);

            attacker.setCombo(attacker.getCombo() + 1);

            if (attacker.getCombo() > attacker.getLongestCombo()) {
                attacker.setLongestCombo(attacker.getCombo());
            }

            if (match.getLadder() instanceof BridgeLadder) {

                if (victim.getPlayer().getHealth() - e.getFinalDamage() <= 0) {
                    e.setCancelled(true);

                    match.getTeam(victim).setupPlayer(victim, match);

                    String victimColor = match.getTeam(victim).getTeamColor();
                    String attackerColor = match.getTeam(attacker).getTeamColor();

                    match.sendMatchMessage(victimColor + victim.getPlayer().getName()
                            + Practice.QUATERNARY_COLOR + " was killed by "
                            + attackerColor + attacker.getPlayer().getName()
                            + Practice.QUATERNARY_COLOR + ".");

                    match.getTeam(attacker).sendSound(Sound.ORB_PICKUP);
                }

            }

            if (match.getLadder() instanceof BedFightLadder) {

                if (victim.getPlayer().getHealth() - e.getFinalDamage() <= 0) {
                    e.setCancelled(true);

                    match.handleBedFightRespawn(victim);

                    String victimColor = match.getTeam(victim).getTeamColor();
                    String attackerColor = match.getTeam(attacker).getTeamColor();

                    boolean hasBed = match.getTeam(victim).isHasBed();

                    match.sendMatchMessage(victimColor + victim.getPlayer().getName()
                            + Practice.QUATERNARY_COLOR + " was killed by "
                            + attackerColor + attacker.getPlayer().getName()
                            + Practice.QUATERNARY_COLOR + ". " + (!hasBed ? "&b&lFINAL KILL!" : ""));
                }
            }

            if (match.getLadder() instanceof SumoLadder) {
                e.setDamage(0);
            }

            if (match.getLadder() instanceof BoxingLadder) {

                e.setDamage(0);

                int maxHits = (100 * Math.max(
                        match.getTeamOne().getAliveCount(),
                        match.getTeamTwo().getAliveCount()));

                if (attacker.getHits() >= maxHits) {
                    match.eliminate(victim);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlaceEvent(BlockPlaceEvent e){

        final Player player = e.getPlayer();
        final Profile profile = Practice.get().getProfileManager().get(player);

        if (profile.isBuild() && player.getGameMode() == GameMode.CREATIVE){
            return;
        }

        if (profile.isInMatch()) {

            final Match match = profile.getMatch();

            if (match.getLadder() instanceof BridgeLadder
                    || match.getLadder() instanceof BedFightLadder) {
                if (match.getMatchState() == MatchState.STARTING) {
                    e.setCancelled(true);
                    return;
                }

                if (!match.getArena().isLocationInsideArena(e.getBlock().getLocation())) {
                    e.setCancelled(true);
                    return;
                }

                if (match.getBlockTracker().getChangedBlocks().containsKey(e.getBlock())) {
                    WrappedBlockState oldState = match.getBlockTracker().getChangedBlocks().get(e.getBlock()).getY();
                    match.getBlockTracker().getChangedBlocks().put(e.getBlock(),
                            new Pair<>(new WrappedBlockState(e.getBlock().getType(), e.getBlock().getData()), oldState));
                    return;
                }

                match.getBlockTracker().addBlockChange(
                        e.getBlock(),
                        e.getBlock().getType(),
                        e.getBlock().getData(),
                        Material.AIR,
                        (byte) 0
                );
                return;
            }

            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(BlockBreakEvent e){

        final Player player = e.getPlayer();
        final Profile profile = Practice.get().getProfileManager().get(player);

        if (profile.isBuild() && player.getGameMode() == GameMode.CREATIVE){
            return;
        }

        if (profile.isInMatch()) {

            final Match match = profile.getMatch();

            //e.getBlock().getDrops().clear(); // Prevents any drops

            if (match.getLadder() instanceof BridgeLadder) {

                if (!match.getArena().isLocationInsideArena(e.getBlock().getLocation())
                        || !e.getBlock().getType().name().contains("STAINED_CLAY")) {
                    e.setCancelled(true);
                    return;
                }

                match.getBlockTracker().addBlockChange(
                        e.getBlock(),
                        Material.AIR,
                        (byte) 0,
                        e.getBlock().getType(),
                        e.getBlock().getData()
                );
                return;
            }

            if (match.getLadder() instanceof BedFightLadder) {

                if (!match.getArena().isLocationInsideArena(e.getBlock().getLocation())) {
                    e.setCancelled(true);
                    return;
                }

                if (e.getBlock().getType() == Material.WOOL
                        || e.getBlock().getType() == Material.ENDER_STONE
                        || e.getBlock().getType() == Material.WOOD
                        || e.getBlock().getType() == Material.BED_BLOCK) {

                    match.getBlockTracker().addBlockChange(
                            e.getBlock(),
                            Material.AIR,
                            (byte) 0,
                            e.getBlock().getType(),
                            e.getBlock().getData()
                    );

                    // bed break
                    if (e.getBlock().getType() == Material.BED_BLOCK) {

                        e.getBlock().getDrops().clear();

                        Block pairedBlock = match.getBlockTracker().getPairedBedBlock(e.getBlock());

                        // Track both parts of the bed
                        match.getBlockTracker().addBlockChange(
                                e.getBlock(),
                                Material.AIR,
                                (byte) 0,
                                e.getBlock().getType(),
                                e.getBlock().getData()
                        );

                        if (pairedBlock != null) {
                            match.getBlockTracker().addBlockChange(
                                    pairedBlock,
                                    Material.AIR,
                                    (byte) 0,
                                    pairedBlock.getType(),
                                    pairedBlock.getData()
                            );
                        }

                        e.getBlock().setType(Material.AIR);

                        // Bed break logic
                        if (match.getTeamOne().isInTeam(profile)) {
                            if (e.getBlock().getLocation().distanceSquared(match.getArena().getPosition1()) <
                                    e.getBlock().getLocation().distanceSquared(match.getArena().getPosition2())) {
                                e.setCancelled(true);
                                return;
                            }
                        } else {
                            if (e.getBlock().getLocation().distanceSquared(match.getArena().getPosition2()) <
                                    e.getBlock().getLocation().distanceSquared(match.getArena().getPosition1())) {
                                e.setCancelled(true);
                                return;
                            }
                        }

                        e.getBlock().getLocation().getWorld().strikeLightningEffect(e.getBlock().getLocation());
                        // Update match bed states and notify teams

                        match.getOpponents(profile).setHasBed(false);

                        match.sendMatchMessage(match.getOpponents(profile).getTeamColor()
                                + match.getOpponents(profile).getTeamName() + "'s "
                                + Practice.QUATERNARY_COLOR + " bed was broken by "
                                + match.getTeam(profile).getTeamColor()
                                + match.getTeam(profile).getTeamName()
                                + Practice.QUATERNARY_COLOR + "!");

                        match.playMatchSound(Sound.ENDERDRAGON_GROWL);

                        match.getOpponents(profile).sendTitle(
                                "&cBED DESTROYED!",
                                "&eYou will no longer respawn.");
                    }

                    // todo end stone/wood?
                    return;
                }
                e.setCancelled(true);
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

                match.getTeam(profile).setupPlayer(profile, match);

                if (profile.getLastAttacker() == null) {
                    match.sendMatchMessage(
                            match.getTeam(profile).getTeamColor()
                            + profile.getPlayer().getName()
                            + Practice.QUATERNARY_COLOR + " fell in the void.");
                } else {
                    match.sendMatchMessage(match.getTeam(profile).getTeamColor()
                            + profile.getPlayer().getName()
                            + Practice.QUATERNARY_COLOR + " was killed by "
                            + match.getTeam(profile.getLastAttacker()).getTeamColor()
                            + profile.getLastAttacker().getPlayer().getName()
                            + Practice.QUATERNARY_COLOR + ".");
                }

                match.getOpponents(profile).sendSound(Sound.ORB_PICKUP);
                return;
            }

            if (match.getLadder() instanceof BedFightLadder) {

                p.spigot().respawn();

                String victimColor = match.getTeam(profile).getTeamColor();
                boolean hasBed = match.getTeam(profile).isHasBed();

                if (profile.getLastAttacker() == null) {

                    match.sendMatchMessage(victimColor + profile.getPlayer().getName()
                            + Practice.QUATERNARY_COLOR + " fell in the void. " + (!hasBed ? "&b&lFINAL KILL!" : ""));
                } else {
                    String attackerColor = match.getTeam(profile.getLastAttacker()).getTeamColor();

                    match.sendMatchMessage(victimColor + profile.getPlayer().getName()
                            + Practice.QUATERNARY_COLOR + " was killed by "
                            + attackerColor + profile.getLastAttacker().getPlayer().getName()
                            + Practice.QUATERNARY_COLOR + ". " + (!hasBed ? "&b&lFINAL KILL!" : ""));
                }

                match.handleBedFightRespawn(profile);
            }

            p.setBedSpawnLocation(p.getLocation(), true);

            match.eliminate(profile);

            Bukkit.getScheduler().scheduleSyncDelayedTask(Practice.get(), () -> {
                try {
                    Object nmsPlayer = e.getEntity().getClass().getMethod("getHandle", new Class[0]).invoke(e.getEntity(), new Object[0]);
                    Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);
                    Class<?> EntityPlayer2 = Class.forName(String.valueOf(nmsPlayer.getClass().getPackage().getName()) + ".EntityPlayer");
                    Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
                    minecraftServer.setAccessible(true);
                    Object mcserver = minecraftServer.get(con);
                    Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList", new Class[0]).invoke(mcserver, new Object[0]);
                    Method moveToWorld = playerlist.getClass().getMethod("moveToWorld", EntityPlayer2, Integer.TYPE, Boolean.TYPE);
                    moveToWorld.invoke(playerlist, nmsPlayer, 0, false);


                    match.addSpectator(profile);
                    p.getInventory().setItem(0, ItemUtil.getPlayAgainItem());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
            return;
        } else if (profile.getPlayerState() == PlayerState.SPECTATING_MATCH) {
            if (e.getTo().distanceSquared(profile.getSpectatingMatch().getArena().getSpectate()) > 5625
                    || e.getTo().getY() < profile.getSpectatingMatch().getArena().getMinY() - 7) {
                profile.teleport(profile.getSpectatingMatch().getArena().getSpectate());
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
                handleBridgeMatch(profile, e);
            }

            if (match.getLadder() instanceof BedFightLadder) {
                handleBedFightMatch(profile, e);
            }

            if (match.getLadder() instanceof SumoLadder) {
                if (e.getTo().getY() <= match.getArena().getMinY()) {
                    match.eliminate(profile);
                }
            }
        }
    }

    private void handleBedFightMatch(Profile profile, PlayerMoveEvent e){

        final Match match = profile.getMatch();

        if (e.getTo().getY() <= match.getArena().getMinY() - 7) {

            String victimColor = match.getTeam(profile).getTeamColor();
            boolean hasBed = match.getTeam(profile).isHasBed();

            if (profile.getLastAttacker() == null) {

                match.sendMatchMessage(victimColor + profile.getPlayer().getName()
                        + Practice.QUATERNARY_COLOR + " fell in the void. " + (!hasBed ? "&b&lFINAL KILL!" : ""));
            } else {

                String attackerColor = match.getTeam(profile.getLastAttacker()).getTeamColor();

                match.sendMatchMessage(victimColor + profile.getPlayer().getName()
                        + Practice.QUATERNARY_COLOR + " was killed by "
                        + attackerColor + profile.getLastAttacker().getPlayer().getName()
                        + Practice.QUATERNARY_COLOR + ". " + (!hasBed ? "&b&lFINAL KILL!" : ""));
            }

            match.handleBedFightRespawn(profile);
        }

    }

    private void handleBridgeMatch(Profile profile, PlayerMoveEvent e){

        final Match match = profile.getMatch();

        if (e.getTo().getY() <= match.getArena().getMinY() - 7) {
            respawnBridgePlayer(profile);
        }

        // player scored
        if (e.getTo().getBlock().getType() == Material.ENDER_PORTAL) {

            if (match.getTeamOne().isInTeam(profile)) {
                if (e.getTo().distanceSquared(match.getArena().getPosition1()) <
                        e.getTo().distanceSquared(match.getArena().getPosition2())) {
                    respawnBridgePlayer(profile);
                    return;
                }
            } else {
                if (e.getTo().distanceSquared(match.getArena().getPosition2()) <
                        e.getTo().distanceSquared(match.getArena().getPosition1())) {
                    respawnBridgePlayer(profile);
                    return;
                }
            }

            if (!match.getTeam(profile).isHasJustScored()) {

                match.getTeam(profile).setHasJustScored(true);

                int teamNumber = match.getTeamOne().getTeamMap().containsKey(profile) ? 1 : 2;

                String teamColor = teamNumber == 1
                        ? match.getTeamOne().getTeamColor()
                        : match.getTeamTwo().getTeamColor();

                if (teamNumber == 1) {
                    match.getTeamOne().setScore(match.getTeamOne().getScore() + 1);
                    match.sendMatchTitle(teamColor + profile.getPlayer().getName() + " "
                                    + Practice.QUATERNARY_COLOR + " scored!",
                            match.getTeamOne().getTeamColor() + match.getTeamOne().getScore()
                                    + Practice.QUATERNARY_COLOR + " - "
                                    + match.getTeamTwo().getTeamColor() + match.getTeamTwo().getScore());
                } else {
                    match.getTeamTwo().setScore(match.getTeamTwo().getScore() + 1);
                    match.sendMatchTitle(teamColor + profile.getPlayer().getName() + " "
                                    + Practice.QUATERNARY_COLOR + " scored!",
                            match.getTeamTwo().getTeamColor() + match.getTeamTwo().getScore()
                                    + Practice.QUATERNARY_COLOR + " - "
                                    + match.getTeamOne().getTeamColor() + match.getTeamOne().getScore());
                }

                match.sendMatchMessage(teamColor + profile.getPlayer().getName()
                        + Practice.QUATERNARY_COLOR + " has scored!");

                match.sendMatchMessage("&7&m------------------------");
                match.sendMatchMessage(" ");
                match.sendMatchMessage(Practice.QUATERNARY_COLOR + "&lScore:");
                match.sendMatchMessage(Practice.PRIMARY_COLOR + " » "
                        + match.getTeamOne().getTeamColor() + "Team One: &f"
                        + match.getTeamOne().getScore());
                match.sendMatchMessage(Practice.PRIMARY_COLOR + " » "
                        + match.getTeamTwo().getTeamColor() + "Team Two: &f"
                        + match.getTeamTwo().getScore());
                match.sendMatchMessage(" ");
                match.sendMatchMessage("&7&m------------------------");


                if (match.getTeamOne().getScore() >= 5) {
                    match.sendMatchTitle(match.getTeamOne().getTeamColor() + "Red Team &fWon!",
                            match.getTeamOne().getTeamColor() + match.getTeamOne().getScore()
                                    + Practice.QUATERNARY_COLOR + "-"
                                    + match.getTeamTwo().getTeamColor() + match.getTeamTwo().getScore());
                    match.endMatch(false);
                    return;
                } else if (match.getTeamTwo().getScore() >= 5) {
                    match.sendMatchTitle(match.getTeamTwo().getTeamColor() + "Blue Team &fWon!",
                            match.getTeamTwo().getTeamColor() + match.getTeamTwo().getScore()
                                    + Practice.QUATERNARY_COLOR + "-"
                                    + match.getTeamOne().getTeamColor() + match.getTeamOne().getScore());
                    match.endMatch(false);
                    return;
                }

                match.getTeamOne().setupPlayers(match);
                match.getTeamTwo().setupPlayers(match);

                match.setMatchState(MatchState.STARTING);
                match.setDuration(5);

                match.setMatchTask(new BukkitRunnable() {
                    @Override
                    public void run() {
                        match.setDuration(match.getDuration() - 1);

                        if (match.getDuration() > 0 && match.getDuration() <= 3) {
                            match.playMatchSound(Sound.CLICK);
                            match.sendMatchTitle("&c" + match.getDuration(), "&c");
                        } else if (match.getDuration() <= 0){
                            match.playMatchSound(Sound.FIREWORK_BLAST);
                            match.sendMatchTitle("&c", "&c");
                            match.setMatchState(MatchState.ONGOING);
                            profile.getMatchTeam().setHasJustScored(false);
                            cancel();
                            match.setMatchTask(null);
                        }
                    }
                }.runTaskTimer(Practice.get(), 0L, 20L));
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
        }
    }

    private void respawnBridgePlayer(Profile profile) {

        Match match = profile.getMatch();

        String teamColor = "";

        if (match.getTeamOne().isInTeam(profile)) {
            profile.teleport(match.getArena().getPosition1());
            teamColor = match.getTeamOne().getTeamColor();
        } else {
            profile.teleport(match.getArena().getPosition2());
            teamColor = match.getTeamTwo().getTeamColor();
        }

        match.sendMatchMessage(teamColor + profile.getPlayer().getName()
                + Practice.QUATERNARY_COLOR + " fell in the void.");

        match.getOpponents(profile).sendSound(Sound.ORB_PICKUP);

        profile.getMatchTeam().setupPlayer(profile, match);
        profile.setLastAttacker(null);
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
                }.runTaskLater(Practice.get(), 80L);
            }
        }
    }

    @EventHandler
    public void onProjectileHitEvent(ProjectileHitEvent e){

        if (e.getEntity() == null) {
            return;
        }

        if (!(e.getEntity() instanceof Arrow)) {
            return;
        }

        ((Player)e.getEntity().getShooter()).sendMessage("" + e.getEntity().getLocation().getBlock().getType().name());
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e){

        final Player player = e.getPlayer();
        final Profile profile = Practice.get().getProfileManager().get(player);

        switch (e.getAction()) {
            case RIGHT_CLICK_AIR:
                if (profile.isInMatch()) {
                    profile.setSwings(profile.getSwings() + 1);
                }
                break;
        }
    }

    @EventHandler
    public void onEntityRegainHealthEvent(EntityRegainHealthEvent e) {

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) e.getEntity();

        if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED
                || e.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC) {

            final Profile profile = Practice.get().getProfileManager().get(player);

            if (profile.isInMatch()) {
                if (profile.getMatch().getLadder() instanceof BridgeLadder) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
