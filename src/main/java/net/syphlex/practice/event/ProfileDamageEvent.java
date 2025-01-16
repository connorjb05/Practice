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

    private static final HandlerList handlers = new HandlerList();

    private final Profile attacker;
    private final Profile victim;
    private final EntityDamageEvent.DamageCause cause;
    private double damage, finalDamage;
    private boolean cancelled = false;

    public ProfileDamageEvent(Profile attacker, Profile victim, EntityDamageEvent.DamageCause cause, double damage, double finalDamage){
        this.attacker = attacker;
        this.victim = victim;
        this.cause = cause;
        this.damage = damage;
        this.finalDamage = finalDamage;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
