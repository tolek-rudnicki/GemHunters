package pl.epsi.gemhunters.Gems;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import pl.epsi.gemhunters.GemstoneRegistry;
import pl.epsi.gemhunters.Main;
import pl.epsi.gemhunters.Utils;

import java.util.*;

public class TopazGem {

    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private Plugin plugin = Main.getPlugin(Main.class);
    private Map<UUID, Boolean> ability2 = new HashMap<>();
    private Map<UUID, Boolean> ability2Boosted = new HashMap<>();

    public void leftClick(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 1)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15 * 20, 0));

            ability2.put(uuid, true);

            scheduler.runTaskLater(plugin, () -> {
                ability2.remove(uuid);
            }, 15 * 20);

            p.sendMessage(Utils.colorize("&7[&e✧&7] You used > &eJoyful Surge!"));
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 1);
        }
    }

    public void rightClick(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (p.isSneaking()) {
            if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 2)) {
                List<Player> applied = new ArrayList<>();
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60 * 20, 0));

                p.getNearbyEntities(5, 5, 5).forEach((Entity e) -> {
                    if (e instanceof Player) {
                        Player pl = (Player) e;
                        pl.setMaxHealth(22);
                        pl.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60 * 20, 0));
                        applied.add(pl);
                    }
                });

                p.setMaxHealth(24);
                p.setHealth(24);

                scheduler.runTaskLater(plugin, () -> {
                    applied.forEach((Player pl) -> {
                        pl.setMaxHealth(20);
                    });
                }, 60 * 20);

                p.sendMessage(Utils.colorize("&7[&e✧&7] You used > &eJoyful Resonance!"));
                registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 2);
            }
        } else {
            if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 0)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 0));
                p.setMaxHealth(24);

                scheduler.runTaskLater(plugin, () -> {
                    p.setMaxHealth(20);
                }, 5 * 20);

                p.sendMessage(Utils.colorize("&7[&e✧&7] You used > &eVitality Infusion!"));
                registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 0);
            }
        }
    }
    public boolean playerInAbility2(Player p) {
        return ability2.getOrDefault(p.getUniqueId(), false);
    }

    public void ability2Boost(Player p) {
        UUID uuid = p.getUniqueId();

        if (!ability2Boosted.containsKey(uuid)) {
            p.setMaxHealth(24);

            scheduler.runTaskLater(plugin, () -> {
                p.setMaxHealth(20);
                ability2Boosted.remove(uuid);
            }, 15 * 20);

            ability2Boosted.put(uuid, true);
        }
    }

    public boolean boosted(Player p) {
        return ability2Boosted.get(p.getUniqueId());
    }

}
