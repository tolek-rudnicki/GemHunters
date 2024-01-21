package pl.epsi.gemhunters.Gems;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import pl.epsi.gemhunters.GemstoneRegistry;
import pl.epsi.gemhunters.Main;
import pl.epsi.gemhunters.Utils;
import pl.epsi.gemhunters.particles.Effects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RubyGem {

    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private Plugin plugin = Main.getPlugin(Main.class);
    private Map<UUID, Boolean> leftClickAbility = new HashMap<>();
    private Map<UUID, Integer> effectsID = new HashMap<>();

    public void leftClick(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 1)) {
            Effects effects = new Effects();
            effectsID.put(uuid, effects.rubyShield(p, Particle.FLAME, 2, 25));
            leftClickAbility.put(p.getUniqueId(), true);

            scheduler.runTaskLater(plugin, () -> {
                effects.stopTask(effectsID.get(uuid));
                leftClickAbility.remove(uuid);
            }, 10 * 20);

            p.sendMessage(Utils.colorize("&7[&c❤&7] You used > &cReflective Shield!"));
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 1);
        }
    }

    public boolean playerInList(Player p) {
        return leftClickAbility.getOrDefault(p.getUniqueId(), false);
    }

    public void rightClick(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (p.isSneaking()) {
            if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 2)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30 * 20, 1, true, true, true));
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30 * 20, 1, true, true, true));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30 * 20, 1, true, true, true));

                p.sendMessage(Utils.colorize("&7[&c❤&7] You used > &cEvasive Flare!"));
                registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 2);
            }
        } else {
            if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 0)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 1, true, true, true));

                p.sendMessage(Utils.colorize("&7[&c❤&7] You used > &cPulse Of Vitality!"));
                registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 0);
            }
        }
    }

}
