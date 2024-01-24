package pl.epsi.gemhunters.Gems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.epsi.gemhunters.GemstoneRegistry;
import pl.epsi.gemhunters.Main;
import pl.epsi.gemhunters.Utils;
import pl.epsi.gemhunters.particles.Effects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JadeGem extends Gem {

    private Map<UUID, Boolean> ability1Toggled = new HashMap<>();
    private Map<UUID, Integer> ability2Effects = new HashMap<>();
    private Map<UUID, Integer> ability2Scheduler = new HashMap<>();

    public JadeGem() {
        super();

        displayName = "Jade Gemstone";
        itemColor = ChatColor.GREEN;
        material = Material.LIME_STAINED_GLASS;
        customModelID = 10240;

        lore.add(" ");
        lore.add(ChatColor.GOLD + "Ability: Lucky Strike " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
        lore.add(ChatColor.GRAY + "Allows the player to do 10%");
        lore.add(ChatColor.GRAY + "more damage for 5s.");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
        lore.add(ChatColor.GOLD + "Ability: Prosperity Beacon " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
        lore.add(ChatColor.GRAY + "Creates a positive field around the player,");
        lore.add(ChatColor.GRAY + "which increases every player's luck within a ");
        lore.add(ChatColor.GRAY + "certain radius.");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
        lore.add(ChatColor.GOLD + "Ability: Serendipity Surge " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
        lore.add(ChatColor.GRAY + "Grants a surge of luck to");
        lore.add(ChatColor.GRAY + "the player, which allows you to get rarer loot.");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
        lore.add(" ");
        lore.add(ChatColor.GREEN + "After 5 deaths, your gem shatters, ");
        lore.add(ChatColor.GREEN + "which gives you negative potion effects");
        lore.add(ChatColor.GREEN + "that you can get rid of, by crafting a new gem");
        lore.add(ChatColor.GREEN + "in the Gemstone Grinder!");
    }

    public void ability1(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 0)) {
            ability1Toggled.put(uuid, true);

            scheduler.runTaskLater(plugin, () -> {
                ability1Toggled.remove(uuid);
            }, 5 * 20);

            p.sendMessage(Utils.colorize("&7[&a☘&7] You used > &aLucky Strike!"));
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 0);
        }
    }

    public void ability2(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        Effects effects = new Effects();

        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 1)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15 * 20, 1, true, false, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 15 * 20, 1, true, false, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 15 * 20, 1));

            ability2Effects.put(uuid, effects.startCircle(p, Particle.VILLAGER_HAPPY, 4));

            ability2Scheduler.put(uuid, scheduler.scheduleSyncRepeatingTask(plugin, () -> {
                List<Entity> nearby = p.getNearbyEntities(4, 4, 4);
                for (Entity e : nearby) {
                    if (e instanceof LivingEntity) {
                        ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 5*20, 0));
                    }
                }
            }, 0, 1));

            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
                effects.stopTask(ability2Effects.get(uuid));
                scheduler.cancelTask(ability2Scheduler.get(uuid));
                ability2Effects.remove(uuid);
                ability2Scheduler.remove(uuid);
            }, 15 * 20);

            p.sendMessage(Utils.colorize("&7[&a☘&7] You used > &aProsperity Beacon!"));
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 1);
        }
    }

    public void ability3(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 2)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 60 * 20, 1));

            p.sendMessage(Utils.colorize("&7[&a☘&7] You used > &aSerendipity Surge!"));
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 2);
        }
    }

    public boolean playerInAbility1(Player p) {
        return ability1Toggled.getOrDefault(p.getUniqueId(), false);
    }

}
