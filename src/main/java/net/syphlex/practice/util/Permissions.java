package net.syphlex.practice.util;

import net.syphlex.practice.manager.profile.Profile;

public enum Permissions {
    SET_SPAWN("syphlex.setspawn"),
    ARENA("syphlex.arena"),
    OPEN_PARTY("syphlex.open.party"),
    EVENT_HOST("syphlex.event.host");

    private final String permission;

    Permissions(String permission){
        this.permission = permission;
    }

    public String get(){
        return permission;
    }
}
