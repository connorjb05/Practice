package net.syphlex.practice.event;

import lombok.Getter;
import lombok.Setter;
import net.syphlex.practice.manager.profile.Profile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

@Getter
@Setter
public class ProfileDamageEvent extends Event {

    public static HandlerList handlers = new HandlerList();

    private final Profile victim;
    private final Profile attacker;
    private final EntityDamageEvent.DamageCause damageCause;

    private boolean pvp, cancelled = false;

    public ProfileDamageEvent(Profile victim, Profile attacker, EntityDamageEvent.DamageCause damageCause){

        this.victim = victim;
        this.attacker = attacker;
        this.damageCause = damageCause;

        this.pvp = attacker != null;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
