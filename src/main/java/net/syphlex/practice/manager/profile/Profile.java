package net.syphlex.practice.manager.profile;

import com.ngxdev.knockback.KnockbackModule;
import com.ngxdev.knockback.KnockbackProfile;
import fr.mrmicky.fastboard.FastBoard;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.*;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.match.MatchInventories;
import net.syphlex.practice.manager.match.team.MatchTeam;
import org.bukkit.inventory.ItemStack;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.ladder.Ladder;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
public class Profile {
    private final Player player;
    private final EntityPlayer entityPlayer;

    private final Object fileLock = new Object();

    private final Map<Party, Long> partyInvitations = new HashMap<>();
    private final Map<Ladder, ItemStack[]> kitPresets = new HashMap<>();

    private final TtlHashMap<UUID, MatchInventories> postMatchInventories = new TtlHashMap<>(TimeUnit.MINUTES, 2);

    private final DuelRequests duelRequests = new DuelRequests();

    private final boolean[] settings = new boolean[PlayerSettings.values().length];

    private FastBoard scoreboard;

    private PlayerState playerState = PlayerState.IN_SPAWN;

    private Menu menu = null;

    private Ladder ladderQueued = null, lastMatchLadder = null;

    private Match match = null;

    private Match spectatingMatch = null;

    private Party party = null;

    private int hits, combo, longestCombo, missedPotions, UnmissedPotions, swings, respawnTimer = 0;

    private boolean partyChat = false, build = false;

    private Arena renamingArena = null;

    private Profile lastAttacker = null;

    private long enderpearlCooldown = -1, lastPearlUseTime = -1, lastMatchTimeEnded;

    public Profile(final Player player) {
        this.player = player;
        this.entityPlayer = ((CraftPlayer) player).getHandle();

        scoreboard = new FastBoard(player);
        scoreboard.updateTitle(StringUtil.CC(Practice.PRIMARY_COLOR + "&lSyphlex &7❘ &fPractice"));
    }

    public MatchTeam getMatchTeam(){

        if (!isInMatch()) {
            return null;
        }

        return match.getTeam(this);
    }

    public void setSpectateMatchInventory(){

        if (player == null || !player.isOnline()) {
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItem(8, ItemUtil.getLeaveMatchSpectateItem());
        player.updateInventory();
    }

    public void setLobbyInventory(){

        if (player == null || !player.isOnline()) {
            return;
        }

        if (isInQueue()) {
            setQueueInventory();
            return;
        }

        if (isInParty()) {
            setPartyInventory();
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.getInventory().setItem(0, ItemUtil.getQueueMatchItem());
        player.getInventory().setItem(3, ItemUtil.getEventHostItem());
        player.getInventory().setItem(5, ItemUtil.getCreatePartyItem());
        player.getInventory().setItem(6, ItemUtil.getLayoutEditorItem());
        player.getInventory().setItem(7, ItemUtil.getSettingsItem());
        player.getInventory().setItem(8, ItemUtil.getLeaderboardsItem());
        player.updateInventory();
    }

    public void setPartyInventory(){

        if (player == null || !player.isOnline()) {
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        if (party.isLeader(this)) {
            player.getInventory().setItem(0, ItemUtil.getPartyMatchItem());
            player.getInventory().setItem(1, ItemUtil.getPartyFightOtherPartyItem());
        }
        player.getInventory().setItem(4, ItemUtil.getPartyMemberListItem());
        player.getInventory().setItem(7, ItemUtil.getSettingsItem());
        player.getInventory().setItem(8, ItemUtil.getLeavePartyItem());

        player.updateInventory();
    }

    public void setQueueInventory(){

        if (player == null || !player.isOnline()) {
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItem(8, ItemUtil.getLeaveQueueItem());
        player.updateInventory();
    }

    public void reset(){

        if (player == null || !player.isOnline()) {
            return;
        }

        player.setGameMode(GameMode.SURVIVAL);

        if (player.isFlying()) {
            player.setFlying(false);
        }

        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.setExp(0);
        player.setTotalExperience(0);

        player.setFallDistance(0);
        player.setFireTicks(0);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20f);

        for (PotionEffect e : player.getActivePotionEffects()) {
            player.removePotionEffect(e.getType());
        }

        player.updateInventory();
    }

    public void sendClickableMessage(String message, String command){

        if (player == null || !player.isOnline()) {
            return;
        }

        TextComponent component = new TextComponent(StringUtil.CC(message));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));
        player.spigot().sendMessage(component);
    }

    public void hide(Profile target){
        if (player.canSee(target.getPlayer())){
            player.hidePlayer(target.getPlayer());
        }
    }

    public void show(Profile target){
        if (!player.canSee(target.getPlayer())) {
            player.showPlayer(target.getPlayer());
        }
    }

    public void saveKitPreset(Ladder ladder, ItemStack[] inventory){
        kitPresets.putIfAbsent(ladder, inventory);
    }

    public ItemStack[] getKitPreset(Ladder ladder){
        return kitPresets.getOrDefault(ladder, null);
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
        match.addSpectator(this);
    }

    public void stopSpectatingMatch(){

        sendMessage("&cYou are no longer spectating a match...");

        spectatingMatch.removeSpectator(this);
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
        return lastMatchLadder != null;
    }

    public boolean isSpectatingMatch(){
        return spectatingMatch != null;
    }

    public boolean isInQueue(){
        return ladderQueued != null;
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

    public void sendTitle(final String title, final String subTitle, int fadeIn, int stay, int fadeOut){
        IChatBaseComponent titleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + StringUtil.CC(title) + "\"}");
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleComponent, fadeIn, stay, fadeOut);

        // Subtitle component
        IChatBaseComponent subtitleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + StringUtil.CC(subTitle) + "\"}");
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleComponent, fadeIn, stay, fadeOut);

        // Send both packets to the player
        entityPlayer.playerConnection.sendPacket(titlePacket);
        entityPlayer.playerConnection.sendPacket(subtitlePacket);
    }
}
