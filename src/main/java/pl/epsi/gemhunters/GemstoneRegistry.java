package pl.epsi.gemhunters;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class GemstoneRegistry {

    private static GemstoneRegistry instance;

    Map<UUID, GemstoneProperty> gemstones = new HashMap<>();

    private GemstoneRegistry() {};

    public static GemstoneRegistry getInstance() {
        if (GemstoneRegistry.instance == null) GemstoneRegistry.instance = new GemstoneRegistry();
        return GemstoneRegistry.instance;
    }

    public GemstoneProperty get(final UUID uuid) {
        return gemstones.get(uuid);
    }

    public void put(UUID uuid, GemstoneProperty property) {
        gemstones.put(uuid, property);
    }

    public void clear() {
        gemstones.clear();
    }

    public void die(UUID uuid) {
        GemstoneProperty property = gemstones.get(uuid);
        if (property != null) property.die();
    }

    public boolean canUseAbility(UUID uuid, int gemId, int abilityId) {
        GemstoneProperty property = gemstones.get(uuid);
        return property == null || property.getAbilityCooldown(gemId, abilityId) < Bukkit.getPlayer(uuid).getWorld().getFullTime();
    }

    public void doUseAbility(UUID uuid, int gemId, int abilityId) {
        GemstoneProperty property = gemstones.get(uuid);
        if (property == null) property = new GemstoneProperty(gemId);
        property.markAbilityCooldown(gemId, abilityId, Bukkit.getPlayer(uuid).getWorld().getFullTime());
    }

    public Integer getPlayerGemID(UUID uuid) {
        GemstoneProperty property = gemstones.get(uuid);
        return property == null ? null : property.getGemId();
    }

    public Long getRemainingCooldown(UUID uuid, int gemID, int abilityID) {
        GemstoneProperty property = gemstones.get(uuid);
        Long expiresAt = property.getAbilityCooldown(gemID, abilityID);
        Long currentTime = Bukkit.getPlayer(uuid).getWorld().getFullTime();
        long remaining = expiresAt - currentTime;
        return remaining < 0 ? 0 : remaining;
    }

    public void load() {
        File folder = new File("./plugins/GemHunters");
        folder.mkdir();

        try {
            Scanner reader = new Scanner(new File(folder + "/gems.txt"));
            while (reader.hasNextLine()) {
                try {
                    final String[] items = reader.nextLine().split("\\|");
                    UUID uuid = UUID.fromString(items[0]);
                    gemstones.put(uuid, GemstoneProperty.fromLine(items[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found, not loading");
        }
    }

    public void save() {
        File folder = new File("./plugins/GemHunters");
        folder.mkdir();
        String data = "";

        for (UUID uuid : gemstones.keySet()) {
            data += uuid + "|" + gemstones.get(uuid).toLine() + "\n";
        }

        try {
            FileOutputStream writer = new FileOutputStream(folder + "/gems.txt", false);
            writer.write(data.getBytes());
            writer.close();
        } catch (IOException e) {
            System.err.println(("Could not write file"));
        }
    }

}
