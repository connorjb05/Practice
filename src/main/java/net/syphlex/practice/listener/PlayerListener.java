package net.syphlex.practice.listener;

import com.ngxdev.entity.PotionEffectAddEvent;
import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.event.ProfileDamageEvent;
import net.syphlex.practice.manager.kit.impl.BoxingKit;
import net.syphlex.practice.manager.kit.impl.BridgeKit;
import net.syphlex.practice.manager.kit.impl.SoupKit;
import net.syphlex.practice.manager.kit.impl.SumoKit;
import net.syphlex.practice.manager.match.Match;
import net.syphlex.practice.manager.match.MatchState;
import net.syphlex.practice.manager.menu.impl.bot.BotMatchMenu;
import net.syphlex.practice.manager.menu.impl.LeaderboardsMenu;
import net.syphlex.practice.manager.menu.impl.kiteditor.KitEditorSelectionMenu;
import net.syphlex.practice.manager.menu.impl.party.FightOtherPartyMenu;
import net.syphlex.practice.manager.menu.impl.party.PartyMatchMenu;
import net.syphlex.practice.manager.menu.impl.SettingsMenu;
import net.syphlex.practice.manager.party.Party;
import net.syphlex.practice.manager.profile.objects.PlayerState;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.ItemUtil;
import net.syphlex.practice.util.Messages;
import net.syphlex.practice.util.Permissions;
import net.syphlex.practice.util.PlayerUtil;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;

public class PlayerListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e){

        final Player p = e.getPlayer();
        final Profile profile = Practice.get().getProfileManager().get(p);

        if (profile.isPartyChat()) {

            // this just fixes any 'bugs' that may occur
            if (!profile.isInParty()) {
                profile.setPartyChat(false);
                return;
            }

            e.setCancelled(true);

            profile.getParty().sendPartyMessage(
                    "&7(Party) " + Practice.TERTIARY_COLOR + p.getName() + ": "
                            + Practice.PRIMARY_COLOR + e.getMessage());
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent e){

        if (!(e.getPlayer() instanceof Player)) {
            return;
        }

        final Player p = (Player) e.getPlayer();
        final Profile profile = Practice.get().getProfileManager().get(p);

        if (profile.isInMenu()) {
            profile.closeMenu();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClickEvent(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        final Player p = (Player) e.getWhoClicked();
        final Profile profile = Practice.get().getProfileManager().get(p);

        if (!profile.isInMenu()) {

            if (profile.isBuild() && p.getGameMode() == GameMode.CREATIVE){
                return;
            }

            if (profile.getPlayerState() == PlayerState.IN_SPAWN) {
                e.setCancelled(true);
            }

            return;
        }

        e.setCancelled(!profile.getMenu().isEditable());

        MenuClickEvent clickEvent = new MenuClickEvent(profile, e.getRawSlot(), e.getClick(), true);
        profile.getMenu().onClickEvent(clickEvent);

        if (clickEvent.isCancelled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemDropEvent(PlayerDropItemEvent e){

        final Player p = e.getPlayer();
        final Profile profile = Practice.get().getProfileManager().get(p);

        if (profile.isBuild() && p.getGameMode() == GameMode.CREATIVE){
            return;
        }

        switch (profile.getPlayerState()) {
            case IN_SPAWN:
            case IN_EVENT:
                e.setCancelled(true);
                break;
        }

        if (profile.isInMatch()) {

            if (profile.getMatch().getKit() instanceof BridgeKit) {
                e.setCancelled(true);
                return;
            }

            // blocks players from dropping their sword in soup match
            if (profile.getMatch().getKit() instanceof SoupKit) {
                if (e.getItemDrop() != null
                        && e.getItemDrop().getItemStack() != null
                        && e.getItemDrop().getItemStack().getType().name().contains("SWORD")) {
                    e.setCancelled(true);
                }
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(Practice.get(), () -> {
                e.getItemDrop().remove();
            }, 100L);
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e){

        final Player p = e.getPlayer();
        final Profile profile = Practice.get().getProfileManager().get(p);

        if (profile.hasPermission("syphlex.admin")) {
            return;
        }

        if (profile.isInMatch()) {
            e.setCancelled(true);
            profile.sendMessage("&cYou cannot issue commands while in a match.");
        }
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent e){

        if (!(e.getEntity() instanceof Player)){
            return;
        }

        final Player p = (Player) e.getEntity();
        final Profile profile = Practice.get().getProfileManager().get(p);

        if (profile.getPlayerState() == PlayerState.IN_MATCH) {

            if (profile.isInMatch()) {
                if (profile.getMatch().getKit() instanceof SumoKit
                        || profile.getMatch().getKit() instanceof BoxingKit
                        || profile.getMatch().getKit() instanceof BridgeKit
                        || profile.getMatch().getKit() instanceof SoupKit) {
                    e.setFoodLevel(20);
                    p.setSaturation(20f);
                    e.setCancelled(true);
                }
            }

            return;
        }

        p.setFoodLevel(20);
        p.setSaturation(20f);
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        final Player p = (Player) e.getEntity();
        final Profile profile = Practice.get().getProfileManager().get(p);

        ProfileDamageEvent profileDamageEvent = new ProfileDamageEvent(
                null, profile, e.getCause(), e.getDamage(), e.getFinalDamage());
        Bukkit.getPluginManager().callEvent(profileDamageEvent);

        if (profileDamageEvent.isCancelled()) {
            e.setCancelled(true);
        }

        if (profile.getPlayerState() == PlayerState.IN_SPAWN) {
            e.setCancelled(true);
        }

        if (profile.isInMatch()) {

            final Match match = profile.getMatch();

            if (match.getKit() instanceof SumoKit
                    || match.getKit() instanceof BoxingKit
                    || match.getKit() instanceof BridgeKit) {

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

        final Player player = (Player) e.getEntity();
        final Profile profile = Practice.get().getProfileManager().get(player);
        final Profile damagerProfile = Practice.get().getProfileManager().get(damager);

        ProfileDamageEvent profileDamageEvent = new ProfileDamageEvent(
                damagerProfile,
                profile, e.getCause(), e.getDamage(), e.getFinalDamage());

        if (profileDamageEvent.isCancelled()) {
            e.setCancelled(true);
        }

        if (profile.getPlayerState() == PlayerState.IN_SPAWN) {
            e.setCancelled(true);
        }

        //final Profile damagerProfile = Practice.get().getProfileManager().get(damager);

        if (profile.isInMatch()) {

            final Match match = profile.getMatch();

            if (match.getMatchState() != MatchState.ONGOING) {
                e.setCancelled(true);
            }

            // friendly fire patch
            if (!match.isFfa() && match.getTeamAlive(damagerProfile).contains(profile)) {
                e.setCancelled(true);
                return;
            }

            profile.setLastAttacker(damagerProfile);

            /*
            Handles a players death whilst in a bridge duel
             */
            if (match.getKit() instanceof BridgeKit) {

                if (player.getHealth() - e.getFinalDamage() <= 0) {

                    e.setDamage(0);
                    e.setCancelled(true);

                    player.setHealth(player.getMaxHealth());

                    if (match.getTeamOne().containsKey(profile)) {
                        profile.teleport(match.getArena().getPosition1());
                    } else {
                        profile.teleport(match.getArena().getPosition2());
                    }

                    PlayerUtil.resetPlayer(player);
                    match.getKit().giveKit(profile);
                    player.updateInventory();

                    match.getTeamAlive(profile).forEach(players -> {
                        players.sendMessage(Practice.PRIMARY_COLOR + profile.getPlayer().getName()
                                + Practice.SECONDARY_COLOR + " was killed by "
                                + Practice.PRIMARY_COLOR + damagerProfile.getPlayer().getName()
                                + Practice.SECONDARY_COLOR + ".");
                    });

                    match.getAliveOpponents(profile).forEach(opponents -> {
                        opponents.getPlayer().playSound(opponents.getPlayer().getLocation(),
                                Sound.ORB_PICKUP, 1, 1);
                        opponents.sendMessage(Practice.PRIMARY_COLOR + profile.getPlayer().getName()
                                + Practice.SECONDARY_COLOR + " was killed by "
                                + Practice.PRIMARY_COLOR + damagerProfile.getPlayer().getName()
                                + Practice.SECONDARY_COLOR + ".");
                    });
                }
                return;
            }

            if (match.getKit() instanceof SumoKit
                    || match.getKit() instanceof BoxingKit) {
                e.setDamage(0);
            }

            if (match.getKit() instanceof BoxingKit) {

                damagerProfile.setHits(damagerProfile.getHits() + 1);

                int maxHits = (100 * Math.max(
                        match.getTeamOne().size(),
                        match.getTeamTwo().size()));

                if (damagerProfile.getHits() >= maxHits) {
                    match.eliminate(profile);
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

            if (match.getKit() instanceof BridgeKit) {

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

            if (match.getKit() instanceof BridgeKit) {

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

            if (match.getKit() instanceof BridgeKit) {

                p.spigot().respawn();

                match.getAliveOpponents(profile).forEach(opponents -> {
                    opponents.getPlayer().playSound(opponents.getPlayer().getLocation(),
                            Sound.ORB_PICKUP, 1, 1);
                    opponents.sendMessage(Practice.PRIMARY_COLOR + p.getName()
                            + Practice.SECONDARY_COLOR + " was killed by "
                            + Practice.PRIMARY_COLOR + profile.getLastAttacker().getPlayer().getName()
                            + Practice.SECONDARY_COLOR + ".");
                });

                match.getTeamAlive(profile).forEach(players -> {
                    players.sendMessage(Practice.PRIMARY_COLOR + p.getName()
                            + Practice.SECONDARY_COLOR + " was killed by "
                            + Practice.PRIMARY_COLOR + profile.getLastAttacker().getPlayer().getName()
                            + Practice.SECONDARY_COLOR + ".");
                });

                if (match.getTeamOne().containsKey(profile)) {
                    profile.teleport(match.getArena().getPosition1());
                } else {
                    profile.teleport(match.getArena().getPosition2());
                }

                PlayerUtil.resetPlayer(p);
                match.getKit().giveKit(profile);
                p.updateInventory();

                return;
            }

            p.setBedSpawnLocation(p.getLocation(), true);

            match.eliminate(profile);

            Bukkit.getScheduler().runTaskLater(Practice.get(), () -> {

                if (match.getAlivePlayers(match.getProfileMap()).size() > 2) {
                    match.getProfileMap().forEach((profiles, alive) -> {

                        // hide player that just died from alive players
                        if (profiles != profile && alive) {
                            //PlayerUtil.hidePlayer(profiles.getPlayer(), profile.getPlayer());
                            //profiles.getPlayer().hidePlayer(profile.getPlayer());
                        }

                        // show player that just died to all dead players
                        if (profiles != profile && !alive) {
                            //profiles.getPlayer().showPlayer(profile.getPlayer());
                            //PlayerUtil.showPlayer(profiles.getPlayer(), profile.getPlayer());
                        }

                    });
                }

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

            if (profile.getMatch().getMatchState() == MatchState.STARTING) {
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

            if (match.getKit() instanceof BridgeKit) {
                if (e.getTo().getY() <= match.getArena().getMinY() - 7) {

                    if (match.getTeamOne().containsKey(profile)) {
                        profile.teleport(match.getArena().getPosition1());
                    } else {
                        profile.teleport(match.getArena().getPosition2());
                    }

                    match.getTeamAlive(profile).forEach(players -> {
                        players.sendMessage(Practice.PRIMARY_COLOR + p.getName()
                                + Practice.SECONDARY_COLOR + " fell in the void.");
                    });

                    match.getAliveOpponents(profile).forEach(opponents -> {
                        opponents.getPlayer().playSound(opponents.getPlayer().getLocation(),
                                Sound.ORB_PICKUP, 1, 1);
                        opponents.sendMessage(Practice.PRIMARY_COLOR + p.getName()
                                + Practice.SECONDARY_COLOR + " fell in the void.");
                    });

                    PlayerUtil.resetPlayer(profile.getPlayer());
                    match.getKit().giveKit(profile);
                    profile.getPlayer().updateInventory();
                }
            }

            if (match.getKit() instanceof SumoKit) {
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
            if (match.getKit() instanceof BridgeKit) {

                if (match.isPreparing()) {
                    return;
                }

                if (match.isTeamOne(profile)) {

                    if (e.getFrom().distanceSquared(match.getArena().getPosition1()) <
                            e.getFrom().distanceSquared(match.getArena().getPosition2())) {

                        profile.teleport(match.getArena().getPosition1());

                        match.getTeamAlive(profile).forEach(players -> {
                            players.sendMessage(Practice.PRIMARY_COLOR + player.getName()
                                    + Practice.SECONDARY_COLOR + " fell in the void.");
                        });

                        match.getAliveOpponents(profile).forEach(opponents -> {
                            opponents.getPlayer().playSound(opponents.getPlayer().getLocation(),
                                    Sound.ORB_PICKUP, 1, 1);
                            opponents.sendMessage(Practice.PRIMARY_COLOR + player.getName()
                                    + Practice.SECONDARY_COLOR + " fell in the void.");
                        });

                        PlayerUtil.resetPlayer(profile.getPlayer());
                        match.getKit().giveKit(profile);
                        profile.getPlayer().updateInventory();
                        return;
                    } else {
                        match.setTeamOneScore(match.getTeamOneScore() + 1);
                        profile.teleport(match.getArena().getPosition1());
                    }
                } else {
                    if (e.getFrom().distanceSquared(match.getArena().getPosition2()) <
                            e.getFrom().distanceSquared(match.getArena().getPosition1())) {

                        profile.teleport(match.getArena().getPosition2());

                        match.getTeamAlive(profile).forEach(players -> {
                            players.sendMessage(Practice.PRIMARY_COLOR + player.getName()
                                    + Practice.SECONDARY_COLOR + " fell in the void.");
                        });

                        match.getAliveOpponents(profile).forEach(opponents -> {
                            opponents.getPlayer().playSound(opponents.getPlayer().getLocation(),
                                    Sound.ORB_PICKUP, 1, 1);
                            opponents.sendMessage(Practice.PRIMARY_COLOR + player.getName()
                                    + Practice.SECONDARY_COLOR + " fell in the void.");
                        });

                        PlayerUtil.resetPlayer(profile.getPlayer());
                        match.getKit().giveKit(profile);
                        profile.getPlayer().updateInventory();
                        return;
                    } else {
                        match.setTeamTwoScore(match.getTeamTwoScore() + 1);
                        profile.teleport(match.getArena().getPosition2());
                    }
                }

                // restart the match
                match.startMatch();

                if (match.getTeamTwoScore() < 5 && match.getTeamOneScore() < 5) {
                    match.getProfileMap().keySet().forEach(players -> {

                        players.sendTitle(Practice.PRIMARY_COLOR
                                        + profile.getPlayer().getName() + " scored!",
                                0, 10, 5);

                        players.sendMessage(Practice.PRIMARY_COLOR + "&lScore:");
                        players.sendMessage(Practice.PRIMARY_COLOR + " » "
                                + Practice.SECONDARY_COLOR + "Team One: "
                                + Practice.PRIMARY_COLOR + match.getTeamOneScore() + "/5");
                        players.sendMessage(Practice.PRIMARY_COLOR + " » "
                                + Practice.SECONDARY_COLOR + "Team Two: "
                                + Practice.PRIMARY_COLOR + match.getTeamTwoScore() + "/5");
                    });
                }
            }

            return;
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
            if (profile.getMatch().getKit() instanceof BridgeKit) {
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

            if (profile.getMatch().getKit() instanceof BridgeKit) {

                if (e.getCause().equals(PotionEffectAddEvent.EffectCause.UNKNOWN)
                        && e.getEffect().getType().equals(PotionEffectType.REGENERATION)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e){
        final Player p = e.getPlayer();

        e.setJoinMessage(null);

        Practice.get().getProfileManager().join(p);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e){
        final Player p = e.getPlayer();

        e.setQuitMessage(null);

        Practice.get().getProfileManager().quit(p);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {

        final Player p = e.getPlayer();
        final Profile profile = Practice.get().getProfileManager().get(p);
        final Action a = e.getAction();

        if (profile.isBuild() && p.getGameMode() == GameMode.CREATIVE){
            return;
        }

        if (profile.getPlayerState() == PlayerState.IN_SPAWN) {

            switch (a) {
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:

                    e.setCancelled(true);

                    final ItemStack item = p.getItemInHand();

                    if (item.isSimilar(ItemUtil.getQueueMatchItem())) {

                        // open queue match menu

                        profile.openMenu(Practice.get().getMenuManager().getQueueMatchMenu());
                    } else if (item.isSimilar(ItemUtil.getBotMatchItem())) {

                        // open bot match menu

                        profile.openMenu(new BotMatchMenu());

                    } else if (item.isSimilar(ItemUtil.getEventHostItem())) {

                        // event host item

                        if (!profile.hasPermission(Permissions.EVENT_HOST)) {
                            profile.sendMessage(Messages.NO_PERMISSION);
                            profile.sendMessage(Messages.STORE_ADVERTISEMENT);
                            return;
                        }

                        profile.sendMessage("&cLol you got scammed cuz this is still in development... ;)");

                    } else if (item.isSimilar(ItemUtil.getCreatePartyItem())) {

                        // create party

                        Practice.get().getPartyManager().onCreate(profile);

                    } else if (item.isSimilar(ItemUtil.getLayoutEditorItem())) {

                        // layout editor menu item

                        profile.openMenu(new KitEditorSelectionMenu());

                    } else if (item.isSimilar(ItemUtil.getLeaderboardsItem())) {

                        // leaderboards

                        profile.openMenu(new LeaderboardsMenu());

                    } else if (item.isSimilar(ItemUtil.getSettingsItem())) {

                        // settings

                        profile.openMenu(new SettingsMenu(profile));

                    } else if (item.isSimilar(ItemUtil.getPartyMatchItem())) {

                        // start a party match

                        profile.openMenu(new PartyMatchMenu());

                    } else if (item.isSimilar(ItemUtil.getPartyFightOtherPartyItem())) {

                        // duel other parties

                        //profile.sendMessage("&cNot setup yet...");
                        profile.openMenu(new FightOtherPartyMenu(profile, 1));

                    } else if (item.isSimilar(ItemUtil.getPartyMemberListItem())) {

                        // member list

                        if (!profile.isInParty()) {
                            profile.sendMessage("&cYou are not in a party.");
                            return;
                        }

                        final Party party = profile.getParty();

                        profile.sendMessage("&f&m---------------------");
                        profile.sendMessage(Practice.PRIMARY_COLOR + "&l"
                                + party.getLeader().getPlayer().getName() + "'s party: &7("
                                + party.getPartySize() + ")");
                        profile.sendMessage(" ");
                        profile.sendMessage(Practice.TERTIARY_COLOR + "Members:");
                        for (Profile member : party.getMembers()) {
                            profile.sendMessage("&f* " + Practice.PRIMARY_COLOR + member.getPlayer().getName());
                        }
                        profile.sendMessage("&f&m---------------------");

                    } else if (item.isSimilar(ItemUtil.getLeavePartyItem())) {

                        // leave party

                        Practice.get().getPartyManager().onLeaveOrDisband(profile);

                    } else if (item.isSimilar(ItemUtil.getLeaveQueueItem())) {

                        // leave queue

                        Practice.get().getQueueManager().dequeue(profile);
                    }

                    break;
            }
        } else if (profile.getPlayerState() == PlayerState.IN_MATCH) {
            switch (a) {
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:

                    if (e.getItem() != null && e.getItem().getType() == Material.ENDER_PEARL) {

                        long now = System.currentTimeMillis();
                        long lastPearlUseTime = profile.getLastPearlUseTime();

                        double difference = (double) (now - lastPearlUseTime);

                        if (lastPearlUseTime == -1) {
                            difference = now;
                        }

                        if (difference < 15000.0) {
                            profile.sendMessage("&cEnderpearl cooldown: "
                                    + new DecimalFormat(".#").format(15.0 - difference / 1000.0)
                                    + " seconds.");
                            e.setCancelled(true);
                            p.updateInventory();
                            break;
                        }

                        profile.startEnderpearlCooldown();
                        profile.setLastPearlUseTime(now);
                    }

                    if (profile.isInMatch()) {
                        if (profile.getMatch().getKit() instanceof SoupKit
                                && e.getItem() != null
                                && e.getItem().getType() == Material.MUSHROOM_SOUP
                                && p.getHealth() < p.getMaxHealth()) {
                            p.setHealth(Math.min(p.getHealth() + 7, p.getMaxHealth()));
                            e.getItem().setType(Material.BOWL);
                        }
                    }

                    final ItemStack itemStack = p.getItemInHand();

                    if (profile.isInMatch()) {
                        if (itemStack.isSimilar(ItemUtil.getBookKit(profile.getMatch().getKit()))) {
                            profile.getMatch().getKit().giveKit(profile);
                        }
                        break;
                    }

                    break;
            }
        }

        if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
            if (e.getItem() != null && e.getItem().isSimilar(ItemUtil.getPlayAgainItem())) {
                if (profile.isInQueue()) {
                    Practice.get().getQueueManager().dequeue(profile);
                } else {
                    Practice.get().getQueueManager().queue(profile, profile.getLastMatchKit());
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
            if (profile.getMatch().getKit() instanceof BridgeKit) {
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
