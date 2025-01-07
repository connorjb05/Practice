package net.syphlex.practice.manager.profile;

import com.ngxdev.knockback.KnockbackModule;
import com.ngxdev.knockback.KnockbackProfile;
import fr.mrmicky.fastboard.FastBoard;
import lombok.Getter;
import lombok.Setter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.manager.match.Match;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.party.Party;
import net.syphlex.practice.util.StringUtil;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Setter
@Getter
public class Profile {
    private final Player player;
    private final EntityPlayer entityPlayer;

    private final Map<Party, Long> partyInvitations = new HashMap<>();

    private final Map<UUID, Match> duelRequests = new HashMap<>();

    private final boolean[] settings = new boolean[PlayerSettings.values().length];

    private FastBoard scoreboard;

    private PlayerState playerState = PlayerState.IN_SPAWN;

    private Menu menu = null;

    private Kit kitQueued = null;

    private Match match = null;

    private Party party = null;

    private int hits;

    private boolean partyChat = false;

    private Profile lastAttacker = null;

    public Profile(final Player player){
        this.player = player;
        this.entityPlayer = ((CraftPlayer)player).getHandle();

        scoreboard = new FastBoard(player);
        scoreboard.updateTitle(StringUtil.CC(Practice.PRIMARY_COLOR + "&lSyphlex &7❘ &fPractice"));
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

    public void sendMessage(String message){
        if (player == null) {
            return;
        }
        player.sendMessage(StringUtil.CC(message));
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
        menu = null;
    }

    public boolean isInMenu(){
        return menu != null;
    }

    public boolean isInMatch(){
        return match != null;
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
}
