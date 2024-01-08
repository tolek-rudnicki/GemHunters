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
import pl.epsi.gemhunters.particles.Effects;

import java.util.UUID;

public class RubyGem {

    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private Plugin plugin = Main.getPlugin(Main.class);

    public void leftClick(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 4)) {
            Effects effects = new Effects();
            int rubyID = effects.rubyShield(p, Particle.FLAME, 2, 25);
            scheduler.runTaskLater(plugin, () -> {
                effects.stopTask(rubyID);
            }, 10 * 20);
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 4);
        }
    }

    public void rightClick(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (p.isSneaking()) {
            if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 5)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30 * 20, 1, true, true, true));
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30 * 20, 1, true, true, true));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30 * 20, 1, true, true, true));
                registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 5);
            }
        } else {
            if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 3)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 1, true, true, true));
                registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 3);
            }
        }
    }

}
