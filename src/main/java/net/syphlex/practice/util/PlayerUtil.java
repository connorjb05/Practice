package net.syphlex.practice.util;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

@UtilityClass
public class PlayerUtil {

    public void sendClickableText(Player p, String msg, String cmd){
        TextComponent textComponent = new TextComponent(StringUtil.CC(msg));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + cmd));
        p.spigot().sendMessage(textComponent);
    }


    public void showPlayer(Player player){
        for (Player players : Bukkit.getOnlinePlayers()) {
            players.showPlayer(player);
        }
    }

    public void resetPlayer(Player player){

        if (player == null) {
            return;
        }

        player.setGameMode(GameMode.SURVIVAL);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.setFallDistance(0);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20f);
        player.setFireTicks(0);

        for (PotionEffect e : player.getActivePotionEffects()) {
            player.removePotionEffect(e.getType());
        }
    }

    public void hidePlayer(Player player, Player target){
        if (player.canSee(target)) {
            player.hidePlayer(target);
        }
    }

    public void showPlayer(Player player, Player target){
        if (!player.canSee(target)) {
            player.showPlayer(target);
        }
    }

}
