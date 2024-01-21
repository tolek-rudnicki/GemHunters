package pl.epsi.gemhunters.Gems;

import fr.skytasul.glowingentities.GlowingEntities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.checkerframework.checker.units.qual.C;
import pl.epsi.gemhunters.GemstoneRegistry;
import pl.epsi.gemhunters.Main;
import pl.epsi.gemhunters.Utils;

import java.util.List;
import java.util.UUID;

public class OpalGem {

    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private Plugin plugin = Main.getPlugin(Main.class);
    private GlowingEntities glowingEntities = new GlowingEntities(plugin);

    public void leftClick(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 1)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15 * 20, 1, true, false, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 15 * 20, 1, true, false, true));
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
                p.setInvulnerable(false);
            }, 15 * 20);
            p.setInvulnerable(true);

            p.sendMessage(Utils.colorize("&7[&f❂&7] You used > &fPhantom Dash!"));
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 1);
        }
    }

    public void rightClick(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (p.isSneaking()) {
            if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 2)) {
                List<Entity> entities = p.getNearbyEntities(15, 15, 15);

                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60 * 20, 1, true, false, true));
                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 55 * 20, 1, true, false, true));
                p.setInvulnerable(true);

                entities.forEach((Entity e) -> {
                    try {
                        glowingEntities.setGlowing(e, p, ChatColor.WHITE);
                    } catch (ReflectiveOperationException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
                    entities.forEach((Entity e) -> {
                        try {
                            glowingEntities.unsetGlowing(e, p);
                        } catch (ReflectiveOperationException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    p.setInvulnerable(false);
                }, 60 * 20);

                p.sendMessage(Utils.colorize("&7[&f❂&7] You used > &fCelestial Vanish!"));
                registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 2);
            }
        } else {
            if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 0)) {
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
                    p.setInvulnerable(false);
                }, 8 * 20);
                p.setInvulnerable(true);
                p.setVelocity(p.getLocation().getDirection().multiply(2).setY(1.5));

                p.sendMessage(Utils.colorize("&7[&f❂&7] You used > &fLight As A Feather!"));
                registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 0);
            }
        }
    }

}
