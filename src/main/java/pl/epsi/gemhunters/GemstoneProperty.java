package pl.epsi.gemhunters;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.UUID;

public class GemstoneProperty {

    static int[] abilityCooldownTimeout = { 5 * 20, 60 * 20, 300 * 20 }; // in seconds; *20 means converting from ticks to seconds

    private int gemId;

    private int deathCount = 0;

    private Long[] cooldown = new Long[] {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public GemstoneProperty(int gemId) {
        this.gemId = gemId;
    }

    public GemstoneProperty(int gemId, int deathCount) {
        this.gemId = gemId;
        this.deathCount = deathCount;
    }

    public int getGemId() {
        return gemId;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void die() {
        ++deathCount;
    }

    public void markAbilityCooldown(int gemId, int abilityId, Long timestamp) {
        cooldown[abilityIndex(gemId, abilityId)] = timestamp;
    }

    public Long getAbilityCooldown(int gemId, int abilityId) {
        return cooldown[abilityIndex(gemId, abilityId)] + GemstoneProperty.abilityCooldownTimeout[abilityId];
    }

    private int abilityIndex(int gemId, int abilityId) {
        return gemId * 3 + abilityId;
    }

    public static GemstoneProperty fromLine(final String line) {
        String[] items = line.split(",");
        return new GemstoneProperty(Integer.valueOf(items[0]), Integer.valueOf(items[1]));
    }

    public String toLine() {
        return gemId + "," + deathCount;
    }

}
