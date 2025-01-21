package net.syphlex.practice.listener;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.profile.Profile;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class PotionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPotionSplashEvent(PotionSplashEvent e){

        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Player player = (Player) e.getEntity().getShooter();
        final Profile profile = Practice.get().getProfileManager().get(player);

        if (Practice.get().getConfigManager().isFastPotions()) {
            if (player.isSprinting() && e.getIntensity((LivingEntity) e.getEntity().getShooter()) > 0.5) {
                e.setIntensity((LivingEntity) e.getEntity().getShooter(), 1.0);
            }
        }

        if (profile.getUnmissedPotions() != 0) {
            profile.setUnmissedPotions(profile.getUnmissedPotions() - 1);
        }

        if (!e.getAffectedEntities().contains((LivingEntity) e.getEntity().getShooter())) {
            profile.setMissedPotions(profile.getMissedPotions() + 1);
        }
    }

    @EventHandler
    public void onProjectileLaunchEvent(ProjectileLaunchEvent e){

        if (!Practice.get().getConfigManager().isFastPotions()) {
            return;
        }

        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Player player = (Player) e.getEntity().getShooter();

        if (e.getEntityType() == EntityType.SPLASH_POTION && player.isSprinting()) {
            Vector velocity = e.getEntity().getVelocity();
            velocity.setY(velocity.getY() - Practice.get().getConfigManager().getPotionSpeed());
            e.getEntity().setVelocity(velocity);
        }
    }
}
