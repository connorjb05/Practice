package net.syphlex.practice.util;

public enum Messages {
    NO_PERMISSION("&cNo permission."),
    STORE_ADVERTISEMENT("&6You can purchase a rank from https://store.syphlex.net/ to access this feature."),
    NOT_ONLINE("&cThat player is not online."),
    EVENT_CHAT_PREFIX("&7(&bEvent&7) ");

    private final String message;

    Messages(String message){
        this.message = message;
    }

    public String get(){
        return StringUtil.CC(message);
    }
}
