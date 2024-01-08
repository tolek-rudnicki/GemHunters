package pl.epsi.gemhunters.libs;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cooldown {

    private Map<UUID, ArrayList<Long>> cooldowns = new HashMap<>();

    private int indexOf(int gemstoneID, int abilityID) {
        return gemstoneID * 3 + abilityID;
    }

    public void mark(UUID uuid, int gemstoneID, int abilityID) {
        ArrayList<Long> cooldown = cooldowns.getOrDefault(uuid, new ArrayList<>());
        cooldown.set(indexOf(gemstoneID, abilityID), Bukkit.getPlayer(uuid).getWorld().getFullTime());
    }

    public Long get(UUID uuid, int gemstoneID, int abilityID) {
        return cooldowns.getOrDefault(uuid, new ArrayList<>()).get(indexOf(gemstoneID, abilityID));
    }

}
