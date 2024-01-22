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

public class JasperGem extends Gem {

    private Map<UUID, Boolean> ability2 = new HashMap<>();
    private Map<UUID, Integer> ability2Scheduler = new HashMap<>();
    private Map<UUID, Integer> ability2Hits = new HashMap<>();
    private Map<UUID, Integer> ability3Scheduler = new HashMap<>();

    public JasperGem() {
        super();

        displayName = "Jasper Gemstone";
        itemColor = ChatColor.LIGHT_PURPLE;
        material = Material.MAGENTA_STAINED_GLASS;
        customModelID = 10240;

        lore.add(" ");
        lore.add(ChatColor.GOLD + "Ability: Frenzied Assault " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
        lore.add(ChatColor.GRAY + "Unleash a frenzied assault, giving the");
        lore.add(ChatColor.GRAY + "player strength 1 and speed 1 for 5s");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
        lore.add(ChatColor.GOLD + "Ability: Mighty Strike " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
        lore.add(ChatColor.GRAY + "Unleash a powerfull attack for 3 hits");
        lore.add(ChatColor.GRAY + "where the 1st hit deals +50% more");
        lore.add(ChatColor.GRAY + "damage, and the 2nd and the 3rd deal");
        lore.add(ChatColor.GRAY + "+25% damage.");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
        lore.add(ChatColor.GOLD + "Ability: Brutal Aura " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
        lore.add(ChatColor.GRAY + "Creates an aura around the player");
        lore.add(ChatColor.GRAY + "where anyone within 8 block of the player");
        lore.add(ChatColor.GRAY + "will get strength 1 and the player will");
        lore.add(ChatColor.GRAY + "get strength 2, while also making everyone");
        lore.add(ChatColor.GRAY + "in the aura glow for 30s!");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
        lore.add(" ");
        lore.add(ChatColor.LIGHT_PURPLE + "After 5 deaths, your gem shatters, ");
        lore.add(ChatColor.LIGHT_PURPLE + "which gives you negative potion effects");
        lore.add(ChatColor.LIGHT_PURPLE + "that you can get rid of, by crafting a new gem");
        lore.add(ChatColor.LIGHT_PURPLE + "in the Gemstone Grinder!");
    }

    public void ability1(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 0)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5 * 20, 0));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 0));

            p.sendMessage(Utils.colorize("&7[&d❁&7] You used > &dFrenzied Assault!"));
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 0);
        }
    }
    public void ability2(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 1)) {
            ability2.put(uuid, true);
            ability2Hits.put(uuid, 1);

            ability2Scheduler.put(uuid, scheduler.scheduleSyncRepeatingTask(plugin, () -> {
                if (ability2Hits.getOrDefault(uuid, 1) == 4) {
                    ability2.remove(uuid);
                    ability2Hits.remove(uuid);
                    scheduler.cancelTask(ability2Scheduler.get(uuid));
                }
            }, 0, 1));



            p.sendMessage(Utils.colorize("&7[&d❁&7] You used > &dMighty Strike!"));
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 1);
        }
    }

    public void ability3(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 2)) {
            ability3Scheduler.put(uuid, scheduler.scheduleSyncRepeatingTask(plugin, () -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5*20, 1));
                List<Entity> nearby = p.getNearbyEntities(2, 2, 2);
                for (Entity e : nearby) {
                    if (e instanceof Player) {
                        ((Player) e).addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5*20, 0));
                        ((Player) e).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5*20, 1));
                    }
                }
            }, 0, 1));

            scheduler.runTaskLater(plugin, () -> {
                scheduler.cancelTask(ability3Scheduler.get(uuid));
            }, 30 * 20);
            p.sendMessage(Utils.colorize("&7[&d❁&7] You used > &dBrutal Aura!"));
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 2);
        }
    }

    public boolean playerInAbility2(Player p) {
        return ability2.getOrDefault(p.getUniqueId(), false);
    }
    public int getHitsInAbility2(Player p) { return ability2Hits.getOrDefault(p.getUniqueId(), 1); }
    public void incrementHits(Player p) {
        UUID uuid = p.getUniqueId();
        ability2Hits.put(uuid, ability2Hits.getOrDefault(uuid, 1) + 1);
    }

}
