package pl.epsi.gemhunters.Gems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

public class TopazGem extends Gem {

    private Map<UUID, Boolean> ability2 = new HashMap<>();
    private Map<UUID, Boolean> ability2Boosted = new HashMap<>();

    public TopazGem() {
        super();

        displayName = "Topaz Gemstone";
        itemColor = ChatColor.YELLOW;
        material = Material.YELLOW_STAINED_GLASS;
        customModelID = 10240;

        lore.add(" ");
        lore.add(ChatColor.GOLD + "Ability: Vitality Infusion " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
        lore.add(ChatColor.GRAY + "Infuses you with radiant vitality, which");
        lore.add(ChatColor.GRAY + "increases your max health, and provides");
        lore.add(ChatColor.GRAY + "a regeneration increase.");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
        lore.add(ChatColor.GOLD + "Ability: Joyful Surge " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
        lore.add(ChatColor.GRAY + "When enabled, get a surge of speed and");
        lore.add(ChatColor.GRAY + "a health boost when you get hit for ");
        lore.add(ChatColor.GRAY + "15s.");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
        lore.add(ChatColor.GOLD + "Ability: Joyful Resonance " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
        lore.add(ChatColor.GRAY + "Creates a burst of positive energy");
        lore.add(ChatColor.GRAY + "that grants every player within 5");
        lore.add(ChatColor.GRAY + "blocks a speed boost and increased");
        lore.add(ChatColor.GRAY + "health.");
        lore.add(ChatColor.RED + "This effect will cancel 15s after ");
        lore.add(ChatColor.RED + "using the ability if you don't get hit");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
        lore.add(" ");
        lore.add(ChatColor.YELLOW + "After 5 deaths, your gem shatters, ");
        lore.add(ChatColor.YELLOW + "which gives you negative potion effects");
        lore.add(ChatColor.YELLOW + "that you can get rid of, by crafting a new gem");
        lore.add(ChatColor.YELLOW + "in the Gemstone Grinder!");
    }


    public void ability1(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();

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

    public void ability2(Player p) {
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

    public void ability3(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();

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
