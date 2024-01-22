package pl.epsi.gemhunters.Gems;

import fr.skytasul.glowingentities.GlowingEntities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

public class OpalGem extends Gem {

    private GlowingEntities glowingEntities = new GlowingEntities(plugin);

    public OpalGem() {
        super();

        displayName = "Opal Gemstone";
        itemColor = ChatColor.WHITE;
        material = Material.WHITE_STAINED_GLASS;
        customModelID = 10240;

        lore.add(" ");
        lore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
        lore.add(ChatColor.GRAY + "Boost you in to the air, and gives you a little");
        lore.add(ChatColor.GRAY + "invulnerability time after landing.");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
        lore.add(ChatColor.GOLD + "Ability: Phantom Dash " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
        lore.add(ChatColor.GRAY + "Gives a burst of speed and jump boost,");
        lore.add(ChatColor.GRAY + "whilst also granting immunity to the player");
        lore.add(ChatColor.GRAY + "for 15 seconds.");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
        lore.add(ChatColor.GOLD + "Ability: Celestial Vanish " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
        lore.add(ChatColor.GRAY + "Grants the user invisibility, immunity");
        lore.add(ChatColor.GRAY + "and highlighting any players or mobs nearby for");
        lore.add(ChatColor.GRAY + "1 minute.");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
        lore.add(" ");
        lore.add(ChatColor.WHITE + "After 5 deaths, your gem shatters, ");
        lore.add(ChatColor.WHITE + "which gives you negative potion effects");
        lore.add(ChatColor.WHITE + "that you can get rid of, by crafting a new gem");
        lore.add(ChatColor.WHITE + "in the Gemstone Grinder!");
    }

    public void ability1(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
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

    public void ability2(Player p) {
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

    public void ability3(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
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
    }

}
