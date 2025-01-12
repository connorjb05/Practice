package net.syphlex.practice.manager.profile.objects;

import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.manager.profile.Profile;

import java.util.HashMap;
import java.util.Map;

public class DuelRequests {

    private final Map<Profile, Kit> requestMap = new HashMap<>();
    private final Map<Profile, Long> requestTimeMap = new HashMap<>();

    public boolean isRequestFrom(Profile requester){
        return requestMap.containsKey(requester);
    }

    public Kit getRequestKit(Profile requester){
        return requestMap.get(requester);
    }

    public void addRequest(Profile requester, Kit kit){
        requestMap.put(requester, kit);
        requestTimeMap.put(requester, System.currentTimeMillis());
    }

    public void delRequest(Profile profile){
        requestMap.remove(profile);
        requestTimeMap.remove(profile);
    }

    public void clearRequests(){
        requestMap.clear();
        requestTimeMap.clear();
    }

    public boolean isUnderCooldown(Profile requester){
        if (!requestTimeMap.containsKey(requester)) {
            return false;
        }
        return Math.abs(System.currentTimeMillis() - requestTimeMap.get(requester)) < 30000; // 30 second delay in sending duel requests
    }
}
