package net.syphlex.practice.util;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
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

    public void sendTitle(final Player player, final String title, int fadeIn, int stay, int fadeOut){

        CraftPlayer craftPlayer = (CraftPlayer) player;

        IChatBaseComponent titleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + StringUtil.CC(title) + "\"}");
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleComponent, fadeIn, stay, fadeOut);

        craftPlayer.getHandle().playerConnection.sendPacket(titlePacket);
    }


    public void resetPlayer(Player player){

        if (player == null) {
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20f);
        player.setFireTicks(0);

        for (PotionEffect e : player.getActivePotionEffects()) {
            player.removePotionEffect(e.getType());
        }
    }

}
