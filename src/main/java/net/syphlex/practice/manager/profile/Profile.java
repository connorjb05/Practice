package net.syphlex.practice.manager.profile;

import com.ngxdev.knockback.KnockbackModule;
import com.ngxdev.knockback.KnockbackProfile;
import fr.mrmicky.fastboard.FastBoard;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.inventory.ItemStack;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.manager.match.Match;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.party.Party;
import net.syphlex.practice.manager.profile.objects.DuelRequests;
import net.syphlex.practice.manager.profile.objects.PlayerSettings;
import net.syphlex.practice.manager.profile.objects.PlayerState;
import net.syphlex.practice.util.*;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Setter
@Getter
public class Profile {
    private final Player player;
    private final EntityPlayer entityPlayer;

    private final Object fileLock = new Object();

    private final Map<Party, Long> partyInvitations = new HashMap<>();
    private final Map<Kit, ItemStack[]> kitPresets = new HashMap<>();

    private final DuelRequests duelRequests = new DuelRequests();

    private final boolean[] settings = new boolean[PlayerSettings.values().length];

    private FastBoard scoreboard;

    private PlayerState playerState = PlayerState.IN_SPAWN;

    private Menu menu = null;

    private Kit kitQueued = null, lastMatchKit = null;

    private Match match = null;

    private Match spectatingMatch = null;

    private Party party = null;

    private int hits;

    private boolean partyChat = false, build = false;

    private Profile lastAttacker = null;

    private long enderpearlCooldown = -1, lastPearlUseTime = -1;

    public Profile(final Player player) {
        this.player = player;
        this.entityPlayer = ((CraftPlayer) player).getHandle();

        scoreboard = new FastBoard(player);
        scoreboard.updateTitle(StringUtil.CC(Practice.PRIMARY_COLOR + "&lSyphlex &7❘ &fPractice"));
    }

    public void saveKitPreset(Kit kit, ItemStack[] inventory){
        kitPresets.putIfAbsent(kit, inventory);
    }

    public ItemStack[] getKitPreset(Kit kit){
        return kitPresets.getOrDefault(kit, null);
    }

    public void startEnderpearlCooldown(){
        // 14 is actually 15 seconds
        enderpearlCooldown = System.currentTimeMillis() + (long) (14 * 1000);
        player.setExp(0.99f);

        new BukkitRunnable(){
            @Override
            public void run(){

                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }

                if (isOnEnderpearlCooldown()) {
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        player.setExp(player.getExp() - 0.99f / ((float)(14.0 * 20.0) / 2.0f));
                    } else {
                        player.setLevel(0);
                        player.setExp(0);
                        enderpearlCooldown = -1;
                        this.cancel();
                    }
                } else {
                    player.setLevel(0);
                    player.setExp(0);
                    enderpearlCooldown = -1;
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(Practice.get(), 0L, 2L);
    }

    public boolean isOnEnderpearlCooldown(){
        return enderpearlCooldown > System.currentTimeMillis();
    }

    public boolean getSetting(PlayerSettings setting){
        return settings[setting.ordinal()];
    }

    public void setSetting(PlayerSettings setting, boolean enabled){
        settings[setting.ordinal()] = enabled;
    }

    public void setKnockback(String profile){
        KnockbackProfile knockbackProfile = KnockbackModule.INSTANCE.profiles
                .getOrDefault(profile, KnockbackModule.getDefault());
        entityPlayer.setKnockback(knockbackProfile);
    }

    public void startSpectatingMatch(Match match){

        sendMessage("&aYou are now spectating a match...");

        spectatingMatch = match;

        PlayerUtil.resetPlayer(player);

        player.setAllowFlight(true);
        player.setFlying(true);
        player.setGameMode(GameMode.CREATIVE);

        match.addSpectator(this);

        // hide spectators from the alive players in game
        match.getAlivePlayers(match.getProfileMap()).forEach(profile -> {
            profile.getPlayer().hidePlayer(player);
        });
    }

    public void stopSpectatingMatch(){

        sendMessage("&cYou are no longer spectating a match...");

        match.removeSpectator(this);

        teleport(Practice.get().getConfigManager().getMainSpawn());
        PlayerUtil.resetPlayer(player);
        InventoryUtil.setSpawnInventory(player);

        // show player back to the players in match once the player is no longer spectating
        match.getAlivePlayers(match.getProfileMap()).forEach(profile -> {
            profile.getPlayer().showPlayer(player);
        });

        spectatingMatch = null;
    }

    public void sendMessage(String message){

        if (player == null) {
            return;
        }

        player.sendMessage(StringUtil.CC(message));
    }

    public void sendMessage(Messages message){
        sendMessage(message.get());
    }

    public void teleport(Location location){

        if (location == null) {
            return;
        }

        player.teleport(location);
    }

    public void openMenu(Menu menu){
        player.closeInventory();
        this.menu = menu;
        player.openInventory(menu.inventory);
    }

    public void closeMenu(){
        menu.onCloseEvent(this);
        menu = null;
    }

    public boolean hasPermission(String permission){
        if (player == null) {
            return false;
        }
        return player.hasPermission(permission);
    }

    public boolean hasPermission(Permissions permission){
        return hasPermission(permission.get());
    }

    public boolean isInMenu(){
        return menu != null;
    }

    public boolean isInMatch(){
        return match != null;
    }

    public boolean hasLastMatchKit(){
        return lastMatchKit != null;
    }

    public boolean isSpectatingMatch(){
        return spectatingMatch != null;
    }

    public Profile getMatchOpponent(){

        if (isInMatch()){
            return match.getOpponents(this).get(0);
        }
        return null;
    }

    public boolean isInQueue(){
        return kitQueued != null;
    }

    public boolean isInParty(){
        return party != null;
    }

    public int getPing(){
        return entityPlayer.ping;
    }

    public void sendPacket(Packet<?> packet){
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

    public void sendTitle(final String title, int fadeIn, int stay, int fadeOut){
        CraftPlayer craftPlayer = (CraftPlayer) player;

        IChatBaseComponent titleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + StringUtil.CC(title) + "\"}");
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleComponent, fadeIn, stay, fadeOut);

        craftPlayer.getHandle().playerConnection.sendPacket(titlePacket);
    }
}
