package pl.epsi.gemhunters.Gems;

import fr.skytasul.glowingentities.GlowingEntities;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.EulerAngle;
import pl.epsi.gemhunters.GemstoneRegistry;
import pl.epsi.gemhunters.Main;
import pl.epsi.gemhunters.Utils;
import pl.epsi.gemhunters.particles.Effects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AmethystGem {

    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private Plugin plugin = Main.getPlugin(Main.class);
    private GlowingEntities glowingEntities = new GlowingEntities(plugin);
    private Map<UUID, Boolean> ability1 = new HashMap<>();
    private Map<UUID, Boolean> ability2 = new HashMap<>();
    private Map<UUID, Integer> ability1Effects = new HashMap<>();
    private Map<UUID, Integer> ability3Effects = new HashMap<>();
    private Map<UUID, Integer> ability2Scheduler = new HashMap<>();
    private Map<UUID, Integer> ability3Scheduler = new HashMap<>();

    public void leftClick(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 1)) {
            World w = p.getWorld();
            Location playerLoc = p.getLocation();
            ability2.put(uuid, true);

            ArmorStand stand = (ArmorStand) w.spawnEntity(playerLoc.clone().add(0, 2, 0), EntityType.ARMOR_STAND);

            stand.addScoreboardTag(uuid + "");
            stand.setInvulnerable(true);
            stand.setInvisible(true);
            stand.setGravity(false);
            stand.setCollidable(false);

            stand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
            stand.getEquipment().setHelmet(new ItemStack(Material.PURPLE_CONCRETE));

            ability2Scheduler.put(uuid, scheduler.scheduleSyncRepeatingTask(plugin, () -> {
                double angle = 0.25 * stand.getTicksLived();

                Location newLoc = p.getLocation().clone().add(0, 2 + (0.25 * Math.sin(0.1 * stand.getTicksLived())), 0);
                stand.setHeadPose(new EulerAngle(0, Math.toRadians(angle * 10), 0));
                stand.teleport(newLoc);
            },0, 1));



            scheduler.runTaskLater(plugin, () -> {
                scheduler.cancelTask(ability2Scheduler.get(uuid));
                stand.remove();
                ability2.remove(uuid);
            }, 30 * 20);

            p.sendMessage(Utils.colorize("&7[&5❈&7] You used > &5Reflection Prism!"));
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 1);
        }
    }

    public void rightClick(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (p.isSneaking()) {
            if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 2)) {
                Effects effects = new Effects();
                ability3Effects.put(uuid, effects.amethystAbility3(p, Particle.DRAGON_BREATH));

                ability3Scheduler.put(uuid, scheduler.scheduleSyncRepeatingTask(plugin, () -> {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5*20, 1));
                    List<Entity> nearby = p.getNearbyEntities(2, 2, 2);
                    for (Entity e : nearby) {
                        if (e instanceof LivingEntity) {
                            ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5*20, 1));
                        }
                    }
                },0, 1));

                scheduler.runTaskLater(plugin, () -> {
                    effects.stopTask(ability3Effects.get(uuid));
                    scheduler.cancelTask(ability3Scheduler.get(uuid));

                }, 60 * 20);
                p.sendMessage(Utils.colorize("&7[&5❈&7] You used > &5Fortifying Aura!"));
                registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 2);
            }
        } else {
            if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 0)) {
                Effects effects = new Effects();
                ability1Effects.put(uuid, effects.amethystAbility1(p, Particle.DRAGON_BREATH));
                ability1.put(p.getUniqueId(), true);

                scheduler.runTaskLater(plugin, () -> {
                    effects.stopTask(ability1Effects.get(uuid));
                    ability1.remove(uuid);
                }, 5 * 20);

                p.sendMessage(Utils.colorize("&7[&5❈&7] You used > &5Guardian's Embrace!"));
                registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 0);
            }
        }
    }

    public boolean playerInAbility1(Player p) {
        return ability1.getOrDefault(p.getUniqueId(), false);
    }

    public boolean playerInAbility2(Player p) {
        return ability2.getOrDefault(p.getUniqueId(), false);
    }

}
