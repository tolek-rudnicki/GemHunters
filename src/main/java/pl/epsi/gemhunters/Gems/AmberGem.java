package pl.epsi.gemhunters.Gems;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.epsi.gemhunters.GemstoneRegistry;
import pl.epsi.gemhunters.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AmberGem extends Gem {

    private Map<UUID, Integer> ability3Scheduler = new HashMap<>();
    private Map<UUID, Player> ability3Spectating = new HashMap<>();

    public AmberGem() {
        super();

        displayName = "Amber Gemstone";
        itemColor = ChatColor.GOLD;
        material = Material.ORANGE_STAINED_GLASS;
        customModelID = 10240;

        lore.add(" ");
        lore.add(ChatColor.GOLD + "Ability: Swift Sprint " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
        lore.add(ChatColor.GRAY + "When activated, your speed significantly ");
        lore.add(ChatColor.GRAY + "increases.");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
        lore.add(ChatColor.GOLD + "Ability: Zephyr Dash " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
        lore.add(ChatColor.GRAY + "Makes the player dash in the direction");
        lore.add(ChatColor.GRAY + "they are currently looking in, while also ");
        lore.add(ChatColor.GRAY + "giving the player speed and jump boost.");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
        lore.add(ChatColor.GOLD + "Ability: Ephemeral Flicker " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
        lore.add(ChatColor.GRAY + "Allows you to spectate the person nearest you");
        lore.add(ChatColor.GRAY + "for 30s but the person you are spectating will");
        lore.add(ChatColor.GRAY + "know that you are spectating them.");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
        lore.add(" ");
        lore.add(ChatColor.GOLD + "After 5 deaths, your gem shatters, ");
        lore.add(ChatColor.GOLD + "which gives you negative potion effects");
        lore.add(ChatColor.GOLD + "that you can get rid of, by crafting a new gem");
        lore.add(ChatColor.GOLD + "in the Gemstone Grinder!");
    }

    public void ability1(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 0)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2));

            p.sendMessage(Utils.colorize("&7[&6⸎&7] You used > &6Swift Sprint!"));
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 0);
        }
    }

    public void ability2(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 1)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15 * 20, 1));
            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 15 * 20, 1));

            p.setVelocity(p.getLocation().getDirection().multiply(2).setY(1));

            p.sendMessage(Utils.colorize("&7[&6⸎&7] You used > &6Zephyr Dash!"));
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 1);
        }
    }

    public void ability3(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();
        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 2)) {
            Player s = null;
            for (Entity e : p.getNearbyEntities(10, 10, 10)) {
                if (e instanceof Player) s = (Player) e;
            }

            if (s == null) {
                p.sendMessage(Utils.colorize("&7[&6⸎&7] Could not find any player within 10 blocks!"));
            } else {
                p.sendMessage(Utils.colorize("&7[&6⸎&7] You are now spectating &6" + s.getName()));

                ability3Spectating.put(uuid, s);

                Player finalS = s;
                ability3Scheduler.put(uuid, scheduler.scheduleSyncRepeatingTask(plugin, () -> {
                    p.setSpectatorTarget(ability3Spectating.get(uuid));
                }, 0, 1));

                scheduler.runTaskLater(plugin, () -> {
                    scheduler.cancelTask(ability3Scheduler.get(uuid));
                    p.setGameMode(GameMode.SURVIVAL);
                }, 30 * 20);

                p.setGameMode(GameMode.SPECTATOR);
                p.setSpectatorTarget(s);
            }



            p.sendMessage(Utils.colorize("&7[&6⸎&7] You used > &6Ephemeral Flicker!"));
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 2);
        }
    }
}
