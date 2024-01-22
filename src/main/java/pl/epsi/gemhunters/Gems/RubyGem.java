package pl.epsi.gemhunters.Gems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.epsi.gemhunters.GemstoneRegistry;
import pl.epsi.gemhunters.Utils;
import pl.epsi.gemhunters.particles.Effects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RubyGem extends Gem {

    private Map<UUID, Boolean> leftClickAbility = new HashMap<>();
    private Map<UUID, Integer> effectsID = new HashMap<>();

    public RubyGem() {
        super();

        displayName = "Ruby Gemstone";
        itemColor = ChatColor.RED;
        material = Material.RED_STAINED_GLASS;
        customModelID = 10240;

        lore.add(" ");
        lore.add(ChatColor.GOLD + "Ability: Pulse Of Vitality " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
        lore.add(ChatColor.GRAY + "Gives you a almost instant healing");
        lore.add(ChatColor.GRAY + "like effect.");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
        lore.add(ChatColor.GOLD + "Ability: Reflective Shield " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
        lore.add(ChatColor.GRAY + "Creates a shield around you, which");
        lore.add(ChatColor.GRAY + "will reflect attacks back");
        lore.add(ChatColor.GRAY + "to the attacker for 10 seconds.");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
        lore.add(ChatColor.GOLD + "Ability: Evasive Flare " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
        lore.add(ChatColor.GRAY + "Grants healing, strength, and speed");
        lore.add(ChatColor.GRAY + "effects that allow you to escape battles.");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
        lore.add(" ");
        lore.add(ChatColor.RED + "After 5 deaths, your gem shatters, ");
        lore.add(ChatColor.RED + "which gives you negative potion effects");
        lore.add(ChatColor.RED + "that you can get rid of, by crafting a new gem");
        lore.add(ChatColor.RED + "in the Gemstone Grinder!");
    }

    public void ability1(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 0)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 1, true, true, true));

            p.sendMessage(Utils.colorize("&7[&c❤&7] You used > &cPulse Of Vitality!"));
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 0);
        }
    }

    public void ability2(Player p) {
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

    public void ability3(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 2)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30 * 20, 1, true, true, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30 * 20, 1, true, true, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30 * 20, 1, true, true, true));

            p.sendMessage(Utils.colorize("&7[&c❤&7] You used > &cEvasive Flare!"));
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 2);
        }
    }
    public boolean playerInList(Player p) {
        return leftClickAbility.getOrDefault(p.getUniqueId(), false);
    }



}
