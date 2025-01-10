package net.syphlex.practice.manager.bot.traits;

import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.util.Vector;

import java.util.Random;

public class NoDebuffBotTrait extends Trait {
    private Player target;
    private Random random = new Random();

    public NoDebuffBotTrait() {
        super("NoDebuffBotTrait");
    }

    @Override
    public void run() {
        if (!getNPC().isSpawned()) return;

        Player bot = (Player) getNPC().getEntity();
        if (target == null || !target.isOnline() || target.isDead()) {
            target = findNearestPlayer(bot);
        }

        if (target != null) {
            double distance = bot.getLocation().distance(target.getLocation());

            // Move towards the target
            if (distance > 1.5) {
                getNPC().getNavigator().setTarget(target, true);
            }

            // Attack the target
            if (distance <= 3) {
                //bot.swingMainHand();
                target.damage(1.0, bot);
            }

            // Use health potions when health is low
            if (bot.getHealth() <= 10.0) {
                throwPotion(bot);
            }

            // Use Ender Pearls randomly for repositioning
            if (random.nextInt(100) < 5 && distance > 5) {
                //useEnderPearl(bot, target);
            }
        }
    }

    private Player findNearestPlayer(Player bot) {
        Player nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.equals(bot)) {
                double distance = player.getLocation().distance(bot.getLocation());
                if (distance < minDistance) {
                    nearest = player;
                    minDistance = distance;
                }
            }
        }
        return nearest;
    }

    private void throwPotion(Player bot) {
        ItemStack potion = new ItemStack(Material.POTION, 1, (short) 16421);
        if (bot.getInventory().contains(potion)) {

            ThrownPotion thrownPotion = bot.getWorld().spawn(bot.getLocation(), ThrownPotion.class);
            thrownPotion.setItem(new ItemStack(Material.POTION, 1, (short) 16421));

            Vector direction = bot.getLocation().getDirection().normalize();
            thrownPotion.setVelocity(direction);  // Adjust the multiplier to control speed

            //bot.getWorld().dropItem(bot.getEyeLocation(), potion); // Simulate potion throw
            bot.getInventory().removeItem(potion);
            //bot.setHealth(Math.min(bot.getHealth() + 8.0, bot.getMaxHealth())); // Heal 4 hearts
        }
    }

    private void useEnderPearl(Player bot, Player target) {
        ItemStack pearl = new ItemStack(Material.ENDER_PEARL);
        if (bot.getInventory().contains(pearl)) {
            Location pearlTarget = target.getLocation().add(0, 1, 0);
            bot.teleport(pearlTarget); // Simulate pearl teleport
            bot.getInventory().removeItem(pearl);
        }
    }
}
