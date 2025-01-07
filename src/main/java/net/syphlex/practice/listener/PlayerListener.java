package net.syphlex.practice.listener;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.kit.impl.BoxingKit;
import net.syphlex.practice.manager.kit.impl.SumoKit;
import net.syphlex.practice.manager.match.Match;
import net.syphlex.practice.manager.match.MatchState;
import net.syphlex.practice.manager.menu.impl.LeaderboardsMenu;
import net.syphlex.practice.manager.menu.impl.PartyMatchMenu;
import net.syphlex.practice.manager.menu.impl.SettingsMenu;
import net.syphlex.practice.manager.party.Party;
import net.syphlex.practice.manager.profile.PlayerState;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
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
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PlayerListener implements Listener {

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
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent e){

        if (!(e.getEntity() instanceof Player)){
            return;
        }

        final Player p = (Player) e.getEntity();
        final Profile profile = Practice.get().getProfileManager().get(p);

        if (profile.getPlayerState() == PlayerState.IN_MATCH) {

            if (profile.isInMatch()) {
                if (profile.getMatch().getKit() instanceof SumoKit
                        || profile.getMatch().getKit() instanceof BoxingKit) {
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
    public void onEntityDamageEvent(EntityDamageEvent e){

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        final Player p = (Player) e.getEntity();
        final Profile profile = Practice.get().getProfileManager().get(p);

        if (profile.getPlayerState() == PlayerState.IN_SPAWN) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        if (!(e.getDamager() instanceof Player)) {
            return;
        }

        final Player p = (Player) e.getEntity();
        final Profile profile = Practice.get().getProfileManager().get(p);

        if (profile.getPlayerState() == PlayerState.IN_SPAWN) {
            e.setCancelled(true);
        }

        final Player damager = (Player) e.getDamager();
        final Profile damagerProfile = Practice.get().getProfileManager().get(damager);

        profile.setLastAttacker(damagerProfile);

        if (profile.isInMatch()) {

            if (profile.getMatch().getMatchState() != MatchState.ONGOING) {
                e.setCancelled(true);
            }

            if (profile.getMatch().getKit() instanceof SumoKit
                    || profile.getMatch().getKit() instanceof BoxingKit) {
                e.setDamage(0);
            }

            if (profile.getMatch().getKit() instanceof BoxingKit) {

                final Match match = profile.getMatch();

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
    public void onMobSpawnEvent(EntitySpawnEvent e){
        if (e.getEntity() instanceof Player){
            return;
        }
        if (e.getEntity() instanceof Item) {
            return;
        }
        e.setCancelled(true);
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

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        final Player p = (Player) e.getWhoClicked();
        final Profile profile = Practice.get().getProfileManager().get(p);

        if (!profile.isInMenu()) {

            if (profile.getPlayerState() == PlayerState.IN_SPAWN) {
                e.setCancelled(true);
            }

            return;
        }

        e.setCancelled(true);
        profile.getMenu().onClickEvent(new MenuClickEvent(profile, e.getRawSlot(), e.getClick()));
    }

    @EventHandler
    public void onPlayerItemDropEvent(PlayerDropItemEvent e){

        final Player p = e.getPlayer();
        final Profile profile = Practice.get().getProfileManager().get(p);

        switch (profile.getPlayerState()) {
            case IN_SPAWN:
            case IN_EVENT:
                e.setCancelled(true);
                break;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Practice.get(), () -> {
            e.getItemDrop().remove();
        }, 100L);
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {

        final Player p = e.getPlayer();
        final Profile profile = Practice.get().getProfileManager().get(p);
        final Action a = e.getAction();

        if (profile.getPlayerState() == PlayerState.IN_SPAWN) {

            switch (a) {
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:

                    e.setCancelled(true);

                    final ItemStack item = p.getItemInHand();

                    if (item.isSimilar(ItemUtil.getQueueMatchItem())) {

                        // open queue match menu

                        profile.openMenu(Practice.get().getMenuManager().getQueueMatchMenu());
                    } else if (item.isSimilar(ItemUtil.getCreatePartyItem())) {

                        // create party

                        Practice.get().getPartyManager().onCreate(profile);

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

                        profile.sendMessage("&cNot setup yet...");

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

                    final ItemStack itemStack = p.getItemInHand();

                    if (profile.isInMatch() && itemStack.isSimilar(ItemUtil.getBookKit())) {
                        profile.getMatch().getKit().giveKit(profile);
                        break;
                    }

                    break;
            }
        }
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

            p.setBedSpawnLocation(p.getLocation(), true);

            match.eliminate(profile);

            Bukkit.getScheduler().runTaskLater(Practice.get(), () -> {

                match.getProfileMap().forEach((profiles, alive) -> {

                    // hide player that just died from alive players
                    if (profiles != profile && alive) {
                        profiles.getPlayer().hidePlayer(profile.getPlayer());
                    }

                    // show player that just died to all dead players
                    if (profiles != profile && !alive) {
                        profiles.getPlayer().showPlayer(profile.getPlayer());
                    }

                });

                p.spigot().respawn();
            }, 10L);
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
                    oldSpot.setYaw(to.getYaw());
                    oldSpot.setPitch(to.getPitch());

                    e.setTo(oldSpot);

                }

                // Prevent movement in all directions (X, Y, Z), but allow yaw/pitch changes
                p.setVelocity(new Vector(0, 0, 0));
            }

            Match match = profile.getMatch();

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



}
