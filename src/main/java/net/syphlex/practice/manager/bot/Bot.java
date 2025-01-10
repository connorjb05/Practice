package net.syphlex.practice.manager.bot;

import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Random;
import java.util.UUID;

@Getter
public class Bot {

    private final UUID uuid;

    private final NPC npc;
    private final Player botPlayer;

    private final BotDifficulty difficulty;

    public Bot(String displayName, BotDifficulty difficulty, Trait trait){

        this.uuid = UUID.randomUUID();
        this.difficulty = difficulty;

        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER,
                uuid,
                new Random().nextInt(Integer.MAX_VALUE),
                StringUtil.CC(displayName));
        this.botPlayer = (Player) npc.getEntity();
        this.npc.addTrait(trait);
    }

    public void spawn(Location location){
        npc.spawn(location);
    }

    public void teleport(Location location){

        if (!npc.isSpawned()) {
            npc.spawn(location);
            return;
        }

        npc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public void destroy(){
        Practice.get().getProfileManager().getProfileMap().remove(uuid);
        npc.destroy();
        CitizensAPI.getNPCRegistry().deregister(npc);
    }
}
