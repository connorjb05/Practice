package net.syphlex.practice.listener;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.ProfileDamageEvent;
import net.syphlex.practice.manager.kit.impl.BoxingKit;
import net.syphlex.practice.manager.kit.impl.BridgeKit;
import net.syphlex.practice.manager.kit.impl.SumoKit;
import net.syphlex.practice.manager.match.Match;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.manager.profile.objects.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class MatchListener implements Listener {

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
    public void onEntityDamageEvent(EntityDamageEvent e) {

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        final Player p = (Player) e.getEntity();
        final Profile profile = Practice.get().getProfileManager().get(p);

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
    public void onProfileDamageEvent(ProfileDamageEvent e){

        if (e.isPvp()) {



        } else {

            if (e.getVictim().getPlayerState() == PlayerState.IN_SPAWN) {
                e.setCancelled(true);
            }

            if (e.getVictim().isInMatch()) {

                final Match match = e.getVictim().getMatch();

                if (match.getKit() instanceof SumoKit
                        || match.getKit() instanceof BridgeKit
                        || match.getKit() instanceof BoxingKit) {

                    if (e.getDamageCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                            || e.getDamageCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                        return;
                    }

                    e.setCancelled(true);
                }
            }
        }

    }
}
