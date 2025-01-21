package net.syphlex.practice.util;

import lombok.experimental.UtilityClass;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.ladder.Ladder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

@UtilityClass
public class StringUtil {

    public String CC(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public List<String> CC(List<String> list) {
        List<String> translated = new ArrayList<>();
        for (String str : list) {
            translated.add(CC(str));
        }
        return translated;
    }

    public List<Ladder> deserializeKits(List<String> stringKits){

        List<Ladder> ladders = new ArrayList<>();

        for (String kitName : stringKits) {

            if (Practice.get().getLadderManager().getLadderMap().get(kitName) == null) {
                continue;
            }

            ladders.add(Practice.get().getLadderManager().getLadderMap().get(kitName));
        }

        return ladders;
    }

    public List<String> serializeKits(Set<Ladder> ladderList){
        List<String> kitNames = new ArrayList<>();
        for (Ladder ladder : ladderList) {
            kitNames.add(ladder.getName());
        }
        return kitNames;
    }

    public Location deserializeLocation(String location){

        if (location == null || location.equalsIgnoreCase("null")) {
            return null;
        }

        if (!location.matches("^[a-zA-Z_0-9]+;[-+]?\\d+(\\.\\d+)?;[-+]?\\d+(\\.\\d+)?;[-+]?\\d+(\\.\\d+)?;[-+]?\\d+(\\.\\d+)?;[-+]?\\d+(\\.\\d+)?$")) {
            throw new IllegalArgumentException("Invalid location format. Expected: world;x;y;z;yaw;pitch");
        }


        String[] split = location.split(";");

        String worldName = split[0];
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        float yaw = Float.parseFloat(split[4]);
        float pitch = Float.parseFloat(split[5]);

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("World '" + worldName + "' does not exist.");
        }

        return new Location(world, x, y, z, yaw, pitch);
    }

    public String serializeLocation(Location location) {

        if (location == null) {
            return "null";
        }

        return location.getWorld().getName() + ";"
                + location.getX() + ";"
                + location.getY() + ";"
                + location.getZ() + ";"
                + location.getYaw() + ";"
                + location.getPitch();
    }

    public String romanNumerals(int Int) {
        LinkedHashMap<String, Integer> roman_numerals = new LinkedHashMap<String, Integer>();
        roman_numerals.put("M", 1000);
        roman_numerals.put("CM", 900);
        roman_numerals.put("D", 500);
        roman_numerals.put("CD", 400);
        roman_numerals.put("C", 100);
        roman_numerals.put("XC", 90);
        roman_numerals.put("L", 50);
        roman_numerals.put("XL", 40);
        roman_numerals.put("X", 10);
        roman_numerals.put("IX", 9);
        roman_numerals.put("V", 5);
        roman_numerals.put("IV", 4);
        roman_numerals.put("I", 1);
        StringBuilder res = new StringBuilder();
        for (Map.Entry<String, Integer> entry : roman_numerals.entrySet()) {
            int matches = Int / entry.getValue();
            res.append(StringUtil.repeat(entry.getKey(), matches));
            Int %= entry.getValue();
        }
        return res.toString();
    }

    private String repeat(String s, int n) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < n) {
            sb.append(s);
            ++i;
        }
        return sb.toString();
    }
}
