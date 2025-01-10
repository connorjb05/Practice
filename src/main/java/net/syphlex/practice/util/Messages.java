package net.syphlex.practice.util;

public enum Messages {
    NO_PERMISSION("&cNo permission."),
    STORE_ADVERTISEMENT("&6You can purchase a rank from https://store.syphlex.net/ to access this feature.");

    private final String message;

    Messages(String message){
        this.message = message;
    }

    public String get(){
        return StringUtil.CC(message);
    }
}
