package net.syphlex.practice.listener;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.ladder.impl.*;
import net.syphlex.practice.manager.match.MatchState;
import net.syphlex.practice.manager.menu.impl.bot.BotMatchMenu;
import net.syphlex.practice.manager.menu.impl.LeaderboardsMenu;
import net.syphlex.practice.manager.menu.impl.kiteditor.KitEditorSelectionMenu;
import net.syphlex.practice.manager.menu.impl.party.FightOtherPartyMenu;
import net.syphlex.practice.manager.menu.impl.party.PartyMatchMenu;
import net.syphlex.practice.manager.menu.impl.settings.ProfileSettingsMenu;
import net.syphlex.practice.manager.menu.impl.settings.SettingsMenu;
import net.syphlex.practice.manager.party.Party;
import net.syphlex.practice.manager.profile.objects.PlayerState;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.ItemUtil;
import net.syphlex.practice.util.Messages;
import net.syphlex.practice.util.Permissions;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;

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

        if (profile.getRenamingArena() != null) {
            e.setCancelled(true);

            if (e.getMessage().equalsIgnoreCase("cancel")) {
                profile.sendMessage("&cYou are no longer renaming this arena.");
                profile.setRenamingArena(null);
                return;
            }

            final String newName = e.getMessage().split(" ")[0];

            boolean invalid = false;

            for (String existingNames : Practice.get().getArenaManager().getArenaMap().keySet()) {
                if (existingNames.equals(newName)) {
                    invalid = true;
                    break;
                }
            }

            if (invalid) {
                profile.sendMessage("&cThat arena name already exists.");
                profile.setRenamingArena(null);
                return;
            }

            Practice.get().getArenaManager().getArenaMap().remove(profile.getRenamingArena().getName());

            profile.sendMessage("&aYou have changed the arena name to "
                    + newName + "&a.");

            Arena arenaData = profile.getRenamingArena();
            arenaData.setName(newName);

            Practice.get().getArenaManager().getArenaMap().put(newName, arenaData);

            profile.setRenamingArena(null);
            return;
        }

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

            if (profile.isInMatch()) {
                if (profile.getMatch().getMatchState() == MatchState.ENDED) {
                    e.setCancelled(true);
                }
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

            if (e.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE) {
                e.getItemDrop().remove();
                return;
            }

            if (profile.getMatch().getLadder() instanceof BridgeLadder
                    || profile.getMatch().getLadder() instanceof BowLadder
                    || profile.getMatch().getLadder() instanceof BoxingLadder) {
                e.setCancelled(true);
                return;
            }

            // blocks players from dropping their sword in soup match
            if (profile.getMatch().getLadder() instanceof SoupLadder) {
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

        if (profile.hasPermission(Permissions.ADMIN)) {
            return;
        }

        if (profile.isInMatch()) {

            if (e.getMessage().split(" ")[0].equalsIgnoreCase("/leave")
                    || e.getMessage().split(" ")[0].equalsIgnoreCase("/spawn")) {
                return;
            }

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
                if (profile.getMatch().getLadder() instanceof SumoLadder
                        || profile.getMatch().getLadder() instanceof BoxingLadder
                        || profile.getMatch().getLadder() instanceof BridgeLadder
                        || profile.getMatch().getLadder() instanceof SoupLadder) {
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

                    if (item.getType() == Material.POTION) {
                        e.setCancelled(false);
                        return;
                    }

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

                        profile.openMenu(Practice.get().getMenuManager().getEventMenu());
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

                        if (profile.isInParty()) {
                            if (profile.getParty().isLeader(profile)) {
                                profile.openMenu(new SettingsMenu());
                            }
                        } else {
                            profile.openMenu(new ProfileSettingsMenu(profile));
                        }

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

                    p.updateInventory();
                    break;
                case LEFT_CLICK_AIR:
                case LEFT_CLICK_BLOCK:
                    p.updateInventory();
                    break;
            }
        } else if (profile.getPlayerState() == PlayerState.SPECTATING_MATCH) {

            e.setCancelled(true);

            switch (a) {
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:

                    final ItemStack item = p.getItemInHand();

                    if (item.isSimilar(ItemUtil.getLeaveMatchSpectateItem())) {

                        // leave spectate

                        if (!profile.isSpectatingMatch()) {
                            profile.sendMessage("&cThere was an error while attempting this, please reconnect.");
                            return;
                        }

                        profile.stopSpectatingMatch();
                    }

                    break;
            }

            e.setCancelled(true);
            p.updateInventory();

        } else if (profile.getPlayerState() == PlayerState.IN_MATCH) {
            switch (a) {
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:

                    if (e.getItem() != null && e.getItem().getType() == Material.ENDER_PEARL) {

                        if (profile.getMatch().getMatchState() == MatchState.STARTING) {
                            e.setCancelled(true);
                            p.updateInventory();
                            return;
                        }

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
                        if (profile.getMatch().getLadder() instanceof SoupLadder
                                && e.getItem() != null
                                && e.getItem().getType() == Material.MUSHROOM_SOUP
                                && p.getHealth() < p.getMaxHealth()) {
                            p.setHealth(Math.min(p.getHealth() + 7, p.getMaxHealth()));
                            e.getItem().setType(Material.BOWL);
                        }
                    }

                    final ItemStack itemStack = p.getItemInHand();

                    if (profile.isInMatch()) {
                        if (itemStack.isSimilar(ItemUtil.getBookKit(profile.getMatch().getLadder()))) {
                            profile.getMatch().getLadder().giveKit(profile);
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
                    Practice.get().getQueueManager().queue(profile, profile.getLastMatchLadder());
                }
            }
        }
    }
}
