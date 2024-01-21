package pl.epsi.gemhunters.Gems;

import fr.skytasul.glowingentities.GlowingEntities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitScheduler;
import pl.epsi.gemhunters.GemstoneProperty;
import pl.epsi.gemhunters.GemstoneRegistry;
import pl.epsi.gemhunters.Main;
import pl.epsi.gemhunters.Utils;

import java.util.*;

public class GemListener implements Listener {

    private List<ItemStack> gemstones;
    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private Plugin plugin = Main.getPlugin(Main.class);
    private OpalGem opal = new OpalGem();
    private RubyGem ruby = new RubyGem();
    private AmethystGem amethyst = new AmethystGem();
    private JasperGem jasper = new JasperGem();
    private TopazGem topaz = new TopazGem();

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        Player p = event.getPlayer();

        if (isHoldingGem(p)) {
            ItemStack i = getHeldGem(p);
            if (i == null) return;
            if (!i.hasItemMeta()) return;
            String itemName = i.getItemMeta().getDisplayName();
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                for (ItemStack gemstone : gemstones) {
                    if(i.getItemMeta().getDisplayName().equalsIgnoreCase(gemstone.getItemMeta().getDisplayName())) {
                        if (itemName.contains("Opal")) {
                            opal.leftClick(p);
                        } else if (itemName.contains("Ruby")) {
                            ruby.leftClick(p);
                        } else if (itemName.contains("Amethyst")) {
                            amethyst.leftClick(p);
                        } else if (itemName.contains("Jasper")) {
                            jasper.leftClick(p);
                        } else if (itemName.contains("Topaz")) {
                            topaz.leftClick(p);
                        }
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof LivingEntity) {
            Player p = (Player) event.getEntity();
            LivingEntity livingEntity = (LivingEntity) event.getDamager();

            if (ruby.playerInList(p)) {
                livingEntity.damage(event.getDamage());
                p.sendMessage("&7[&c❤&7] Your &cReflective Shield&7 reflected the attack!");
                event.setCancelled(true);
            } else if (amethyst.playerInAbility1(p)) {
                event.setDamage(event.getDamage() * 0.75);
                p.sendMessage(Utils.colorize("&7[&5❈&7] Your &5amethyst gem&7 reduced your damage by 25%!"));
            } else if (topaz.playerInAbility2(p)) {
                topaz.ability2Boost(p);
                if (!topaz.boosted(p)) {
                    p.sendMessage(Utils.colorize("&7[&e✧&7] Your &etopaz gem&7 gave you +2 Hearts"));
                }
            }
        } else if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Player p = (Player) event.getEntity();
            Arrow arrow = (Arrow) event.getDamager();

            if (amethyst.playerInAbility2(p)) {
                if (Math.round(Math.random()) == 1) {
                    if (arrow.getShooter() instanceof LivingEntity) {
                        ((LivingEntity) arrow.getShooter()).damage(event.getDamage());
                        p.sendMessage(Utils.colorize("&7[&5❈&7] Your &5Reflection Prism &7ability reflected the damage to " +
                                "the attacker!"));
                        event.setCancelled(true);
                    }
                }
            }
        } else if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            Player p = (Player) event.getDamager();
            LivingEntity e = (LivingEntity) event.getEntity();

            if (jasper.playerInAbility2(p)) {
                if (jasper.getHitsInAbility2(p) > 1) {
                    event.setDamage(event.getDamage() * 1.25);
                    jasper.incrementHits(p);
                } else {
                    event.setDamage(event.getDamage() * 1.5);
                    jasper.incrementHits(p);
                }
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        ItemStack i = event.getItem();
        Player p = event.getPlayer();
        if (i == null) return;
        if (!i.hasItemMeta()) return;
        String itemName = i.getItemMeta().getDisplayName();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            for (ItemStack gemstone : gemstones) {
                if(i.getItemMeta().getDisplayName().equalsIgnoreCase(gemstone.getItemMeta().getDisplayName())) {
                    event.setCancelled(true);
                    if (itemName.contains("Opal")) {
                        opal.rightClick(p);
                    }
                    if (itemName.contains("Ruby")) {
                        ruby.rightClick(p);
                    } else if (itemName.contains("Amethyst")) {
                        amethyst.rightClick(p);
                    } else if (itemName.contains("Jasper")) {
                        jasper.rightClick(p);
                    } else if (itemName.contains("Topaz")) {
                        topaz.rightClick(p);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        UUID uuid = p.getUniqueId();
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        GemstoneProperty property = registry.get(uuid);

        if (property == null) {
            for (ItemStack gemstone : gemstones) {
                if (p.getInventory().contains(gemstone)) return;
            }
            int randomGemId = (int) Math.round(Math.random() * 7);
            p.getInventory().addItem(gemstones.get(randomGemId));
            registry.put(uuid, new GemstoneProperty(randomGemId));
            p.sendMessage(ChatColor.GREEN + "Congratulations! You got a " + gemstones.get(randomGemId).getItemMeta().getDisplayName());
        } else {
            for (ItemStack gemstone : gemstones) {
                if (p.getInventory().contains(gemstone)) return;
            }
            p.getInventory().addItem(gemstones.get(property.getGemId()));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryType type = event.getInventory().getType();
        if (type != InventoryType.CREATIVE && type != InventoryType.PLAYER && type != InventoryType.CRAFTING) {
            ItemStack i = event.getCurrentItem();
            if (i.getItemMeta() == null) return;
            for (ItemStack gemstone : gemstones) {
                if(i.getItemMeta().getDisplayName().equalsIgnoreCase(gemstone.getItemMeta().getDisplayName())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack i = event.getItemDrop().getItemStack();
        if (i.getItemMeta() == null) return;
        for(ItemStack gemstone : gemstones) {
            if(i.getItemMeta().getDisplayName().equalsIgnoreCase(gemstone.getItemMeta().getDisplayName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        for (ItemStack gemstone : gemstones) {
            for (ItemStack item : event.getPlayer().getInventory()) {
                if (item == null || !item.hasItemMeta()) continue;
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(gemstone.getItemMeta().getDisplayName())) return;
            }
        }
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
            applyDeathsToGem(event.getPlayer());
        }, 20);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        UUID uuid = p.getUniqueId();
        GemstoneRegistry.getInstance().die(uuid);
        applyDeathsToGem(p);
    }

    public void applyDeathsToGem(Player p) {
        int deathCount = GemstoneRegistry.getInstance().get(p.getUniqueId()).getDeathCount();

        ItemStack i = new ItemStack(Material.AIR);
        for (ItemStack item : p.getInventory()) {
            if (item == null) continue;
            if (item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Gemstone")) {
                i = item;
                break;
            }
        }

        if (deathCount == 5) {
            p.getInventory().remove(i);
            shatterGem(p);
            return;
        } else if (deathCount > 5) {
            p.getInventory().remove(i);
            return;
        }

        if (!i.hasItemMeta()) return;
        for (ItemStack gemstone : gemstones) {
            if(i.getItemMeta().getDisplayName().equalsIgnoreCase(gemstone.getItemMeta().getDisplayName())) {
                ItemStack clone = gemstone.clone();
                ItemMeta meta = clone.getItemMeta();
                List<String> lore = meta.getLore();
                lore.add("");
                lore.add("Death Count: " + deathCount);
                meta.setLore(lore);
                clone.setItemMeta(meta);
                p.getInventory().remove(i);
                p.getInventory().addItem(clone);
            }
        }
    }

    public void shatterGem(Player p) {
        p.sendMessage(ChatColor.RED + "Oh no! Your gemstone shattered!");
        p.sendMessage(ChatColor.RED + "You have to forge another one in the Gemstone Grinder to get rid of the negative effects!");
    }

    public boolean isHoldingGem(Player p) {
        ItemStack item1 = p.getInventory().getItemInMainHand();
        ItemStack item2 = p.getInventory().getItemInOffHand();

        if ((item1 == null || item1.hasItemMeta() != true) && (item2 == null || item2.hasItemMeta() != true)) return false;

        for (ItemStack gemstone : gemstones) {
            if ((item1.hasItemMeta() && item1.getItemMeta().getDisplayName().equalsIgnoreCase(gemstone.getItemMeta().getDisplayName())) ||
            (item2.hasItemMeta() && item2.getItemMeta().getDisplayName().equalsIgnoreCase(gemstone.getItemMeta().getDisplayName())))
                        return true;
        }
        return false;
    }

    public ItemStack getHeldGem(Player p) {
        if (isHoldingGem(p)) {
            ItemStack item1 = p.getInventory().getItemInMainHand();
            ItemStack item2 = p.getInventory().getItemInOffHand();

            if ((item1 == null || item1.hasItemMeta() != true) && (item2 == null || item2.hasItemMeta() != true)) return null;

            for (ItemStack gemstone : gemstones) {
                if ((item1.hasItemMeta() && item1.getItemMeta().getDisplayName().equalsIgnoreCase(gemstone.getItemMeta().getDisplayName()))) {
                    return item1;
                }
                if ((item2.hasItemMeta() && item2.getItemMeta().getDisplayName().equalsIgnoreCase(gemstone.getItemMeta().getDisplayName()))) {
                    return item2;
                }
            }
            return null;
        }
        return null;
    }

    public static String formatTime(long seconds1) {
        StringBuilder formattedTime = new StringBuilder();

        if (seconds1 == 0) {
            return Utils.colorize(" &aReady! ");
        }
        formattedTime.append(" &c" + formatSingleTime(seconds1)).append(" ");

        return formattedTime.toString();
    }

    private static String formatSingleTime(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;

        StringBuilder formattedString = new StringBuilder();

        if (minutes > 0) {
            formattedString.append(minutes).append("min ");
        }

        if (remainingSeconds > 0 || (minutes == 0 && seconds == 0)) {
            formattedString.append(remainingSeconds).append("s");
        }

        return formattedString.toString().trim();
    }

    public void gemstoneTimerDisplay() {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();

        scheduler.scheduleSyncRepeatingTask(plugin, () -> {
            Bukkit.getOnlinePlayers().forEach((Player p) -> {
                UUID uuid = p.getUniqueId();
                if (isHoldingGem(p)) {
                    Integer gemID = registry.getPlayerGemID(uuid);
                    if (gemID == null) return;

                    List<Long> time = new ArrayList<>();
                    String finalOutput = "";

                    for (int abilityID = 0; abilityID <= 2; abilityID++) {
                        Long remaining = registry.getRemainingCooldown(uuid, gemID, abilityID) / 20;
                        time.add(remaining);
                    }

                    for (Long l : time) {
                        finalOutput += formatTime(l);
                    }

                    Utils.sendActionBar(p, finalOutput);
                }
            });
        }, 0, 20);
    }

    public void init() {
        gemstoneTimerDisplay();
        gemstones = new ArrayList<>();

        // OPAL
        List<String> opalLore = new ArrayList<>();
        opalLore.add(" ");
        opalLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
        opalLore.add(ChatColor.GRAY + "Boost you in to the air, and gives you a little");
        opalLore.add(ChatColor.GRAY + "invulnerability time after landing.");
        opalLore.add(" ");
        opalLore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
        opalLore.add(ChatColor.GOLD + "Ability: Phantom Dash " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
        opalLore.add(ChatColor.GRAY + "Gives a burst of speed and jump boost,");
        opalLore.add(ChatColor.GRAY + "whilst also granting immunity to the player");
        opalLore.add(ChatColor.GRAY + "for 15 seconds.");
        opalLore.add(" ");
        opalLore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
        opalLore.add(ChatColor.GOLD + "Ability: Celestial Vanish " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
        opalLore.add(ChatColor.GRAY + "Grants the user invisibility, immunity");
        opalLore.add(ChatColor.GRAY + "and highlighting any players or mobs nearby for");
        opalLore.add(ChatColor.GRAY + "1 minute.");
        opalLore.add(" ");
        opalLore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
        opalLore.add(" ");
        opalLore.add(ChatColor.WHITE + "After 5 deaths, your gem shatters, ");
        opalLore.add(ChatColor.WHITE + "which gives you negative potion effects");
        opalLore.add(ChatColor.WHITE + "that you can get rid of, by crafting a new gem");
        opalLore.add(ChatColor.WHITE + "in the Gemstone Grinder!");
        // RUBY
        List<String> rubyLore = new ArrayList<>();
        rubyLore.add(" ");
        rubyLore.add(ChatColor.GOLD + "Ability: Pulse Of Vitality " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
        rubyLore.add(ChatColor.GRAY + "Gives you a almost instant healing");
        rubyLore.add(ChatColor.GRAY + "like effect.");
        rubyLore.add(" ");
        rubyLore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
        rubyLore.add(ChatColor.GOLD + "Ability: Reflective Shield " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
        rubyLore.add(ChatColor.GRAY + "Creates a shield around you, which");
        rubyLore.add(ChatColor.GRAY + "will reflect attacks back");
        rubyLore.add(ChatColor.GRAY + "to the attacker for 10 seconds.");
        rubyLore.add(" ");
        rubyLore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
        rubyLore.add(ChatColor.GOLD + "Ability: Evasive Flare " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
        rubyLore.add(ChatColor.GRAY + "Grants healing, strength, and speed");
        rubyLore.add(ChatColor.GRAY + "effects that allow you to escape battles.");
        rubyLore.add(" ");
        rubyLore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
        rubyLore.add(" ");
        rubyLore.add(ChatColor.RED + "After 5 deaths, your gem shatters, ");
        rubyLore.add(ChatColor.RED + "which gives you negative potion effects");
        rubyLore.add(ChatColor.RED + "that you can get rid of, by crafting a new gem");
        rubyLore.add(ChatColor.RED + "in the Gemstone Grinder!");
        // AMETHYST
        List<String> amethystLore = new ArrayList<>();
        amethystLore.add(" ");
        amethystLore.add(ChatColor.GOLD + "Ability: Guardian's Embrace " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
        amethystLore.add(ChatColor.GRAY + "Creates a particle effect around");
        amethystLore.add(ChatColor.GRAY + "the player, which reduces incoming damage");
        amethystLore.add(ChatColor.GRAY + "by 25% and disappears after 5s or when ");
        amethystLore.add(ChatColor.GRAY + "enough damage is dealt.");
        amethystLore.add(" ");
        amethystLore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
        amethystLore.add(ChatColor.GOLD + "Ability: Reflection Prism " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
        amethystLore.add(ChatColor.GRAY + "Creates a reflective prism that hovers");
        amethystLore.add(ChatColor.GRAY + "above the player and has a 50% chance");
        amethystLore.add(ChatColor.GRAY + "to reflect ranged attacks back to the ");
        amethystLore.add(ChatColor.GRAY + "attacker.");
        amethystLore.add(" ");
        amethystLore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
        amethystLore.add(ChatColor.GOLD + "Ability: Fortifying Aura " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
        amethystLore.add(ChatColor.GRAY + "Summons an aura that grants increased");
        amethystLore.add(ChatColor.GRAY + "damage resistance for all players within");
        amethystLore.add(ChatColor.GRAY + "the particle circle.");
        amethystLore.add(" ");
        amethystLore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
        amethystLore.add(" ");
        amethystLore.add(ChatColor.DARK_PURPLE + "After 5 deaths, your gem shatters, ");
        amethystLore.add(ChatColor.DARK_PURPLE + "which gives you negative potion effects");
        amethystLore.add(ChatColor.DARK_PURPLE + "that you can get rid of, by crafting a new gem");
        amethystLore.add(ChatColor.DARK_PURPLE + "in the Gemstone Grinder!");
        // JASPER // 3 adds the glowing effect to playeres within 8b + str
        List<String> jasperLore = new ArrayList<>();
        jasperLore.add(" ");
        jasperLore.add(ChatColor.GOLD + "Ability: Frenzied Assault " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
        jasperLore.add(ChatColor.GRAY + "Unleash a frenzied assault, giving the");
        jasperLore.add(ChatColor.GRAY + "player strength 1 and speed 1 for 5s");
        jasperLore.add(" ");
        jasperLore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
        jasperLore.add(ChatColor.GOLD + "Ability: Mighty Strike " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
        jasperLore.add(ChatColor.GRAY + "Unleash a powerfull attack for 3 hits");
        jasperLore.add(ChatColor.GRAY + "where the 1st hit deals +50% more");
        jasperLore.add(ChatColor.GRAY + "damage, and the 2nd and the 3rd deal");
        jasperLore.add(ChatColor.GRAY + "+25% damage.");
        jasperLore.add(" ");
        jasperLore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
        jasperLore.add(ChatColor.GOLD + "Ability: Brutal Aura " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
        jasperLore.add(ChatColor.GRAY + "Creates an aura around the player");
        jasperLore.add(ChatColor.GRAY + "where anyone within 8 block of the player");
        jasperLore.add(ChatColor.GRAY + "will get strength 1 and the player will");
        jasperLore.add(ChatColor.GRAY + "get strength 2, while also making everyone");
        jasperLore.add(ChatColor.GRAY + "in the aura glow for 30s!");
        jasperLore.add(" ");
        jasperLore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
        jasperLore.add(" ");
        jasperLore.add(ChatColor.LIGHT_PURPLE + "After 5 deaths, your gem shatters, ");
        jasperLore.add(ChatColor.LIGHT_PURPLE + "which gives you negative potion effects");
        jasperLore.add(ChatColor.LIGHT_PURPLE + "that you can get rid of, by crafting a new gem");
        jasperLore.add(ChatColor.LIGHT_PURPLE + "in the Gemstone Grinder!");
        // TOPAZ
        List<String> topazLore = new ArrayList<>();
        topazLore.add(" ");
        topazLore.add(ChatColor.GOLD + "Ability: Vitality Infusion " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
        topazLore.add(ChatColor.GRAY + "Infuses you with radiant vitality, which");
        topazLore.add(ChatColor.GRAY + "increases your max health, and provides");
        topazLore.add(ChatColor.GRAY + "a regeneration increase.");
        topazLore.add(" ");
        topazLore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
        topazLore.add(ChatColor.GOLD + "Ability: Joyful Surge " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
        topazLore.add(ChatColor.GRAY + "When enabled, get a surge of speed and");
        topazLore.add(ChatColor.GRAY + "a health boost when you get hit for ");
        topazLore.add(ChatColor.GRAY + "15s.");
        topazLore.add(" ");
        topazLore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
        topazLore.add(ChatColor.GOLD + "Ability: Joyful Resonance " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
        topazLore.add(ChatColor.GRAY + "Creates a burst of positive energy");
        topazLore.add(ChatColor.GRAY + "that grants every player within 5");
        topazLore.add(ChatColor.GRAY + "blocks a speed boost and increased");
        topazLore.add(ChatColor.GRAY + "health.");
        topazLore.add(ChatColor.RED + "This effect will cancel 15s after ");
        topazLore.add(ChatColor.RED + "using the ability if you don't get hit");
        topazLore.add(" ");
        topazLore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
        topazLore.add(" ");
        topazLore.add(ChatColor.YELLOW + "After 5 deaths, your gem shatters, ");
        topazLore.add(ChatColor.YELLOW + "which gives you negative potion effects");
        topazLore.add(ChatColor.YELLOW + "that you can get rid of, by crafting a new gem");
        topazLore.add(ChatColor.YELLOW + "in the Gemstone Grinder!");
        // JADE
        List<String> jadeLore = new ArrayList<>();
        jadeLore.add(" ");
        jadeLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
        jadeLore.add(ChatColor.GRAY + "Boost you in to the air!");
        jadeLore.add(" ");
        jadeLore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
        jadeLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
        jadeLore.add(ChatColor.GRAY + "Boost you in to the air!");
        jadeLore.add(" ");
        jadeLore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
        jadeLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
        jadeLore.add(ChatColor.GRAY + "Boost you in to the air!");
        jadeLore.add(" ");
        jadeLore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
        jadeLore.add(" ");
        jadeLore.add(ChatColor.GREEN + "After 5 deaths, your gem shatters, ");
        jadeLore.add(ChatColor.GREEN + "which gives you negative potion effects");
        jadeLore.add(ChatColor.GREEN + "that you can get rid of, by crafting a new gem");
        jadeLore.add(ChatColor.GREEN + "in the Gemstone Grinder!");
        // AMBER
        List<String> amberLore = new ArrayList<>();
        amberLore.add(" ");
        amberLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
        amberLore.add(ChatColor.GRAY + "Boost you in to the air!");
        amberLore.add(" ");
        amberLore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
        amberLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
        amberLore.add(ChatColor.GRAY + "Boost you in to the air!");
        amberLore.add(" ");
        amberLore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
        amberLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
        amberLore.add(ChatColor.GRAY + "Boost you in to the air!");
        amberLore.add(" ");
        amberLore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
        amberLore.add(" ");
        amberLore.add(ChatColor.GOLD + "After 5 deaths, your gem shatters, ");
        amberLore.add(ChatColor.GOLD + "which gives you negative potion effects");
        amberLore.add(ChatColor.GOLD + "that you can get rid of, by crafting a new gem");
        amberLore.add(ChatColor.GOLD + "in the Gemstone Grinder!");
        // SAPPHIRE
        List<String> sapphireLore = new ArrayList<>();
        sapphireLore.add(" ");
        sapphireLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
        sapphireLore.add(ChatColor.GRAY + "Boost you in to the air!");
        sapphireLore.add(" ");
        sapphireLore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
        sapphireLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
        sapphireLore.add(ChatColor.GRAY + "Boost you in to the air!");
        sapphireLore.add(" ");
        sapphireLore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
        sapphireLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
        sapphireLore.add(ChatColor.GRAY + "Boost you in to the air!");
        sapphireLore.add(" ");
        sapphireLore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
        sapphireLore.add(" ");
        sapphireLore.add(ChatColor.BLUE + "After 5 deaths, your gem shatters, ");
        sapphireLore.add(ChatColor.BLUE + "which gives you negative potion effects");
        sapphireLore.add(ChatColor.BLUE + "that you can get rid of, by crafting a new gem");
        sapphireLore.add(ChatColor.BLUE + "in the Gemstone Grinder!");

        gemstones.add(generateGem("Opal Gemstone", opalLore, ChatColor.WHITE, Material.WHITE_STAINED_GLASS, 10240));
        gemstones.add(generateGem("Ruby Gemstone", rubyLore, ChatColor.RED, Material.RED_STAINED_GLASS, 10240));
        gemstones.add(generateGem("Amethyst Gemstone", amethystLore, ChatColor.DARK_PURPLE, Material.PURPLE_STAINED_GLASS, 10240));
        gemstones.add(generateGem("Jasper Gemstone", jasperLore, ChatColor.LIGHT_PURPLE, Material.MAGENTA_STAINED_GLASS, 10240));
        gemstones.add(generateGem("Topaz Gemstone", topazLore, ChatColor.YELLOW, Material.YELLOW_STAINED_GLASS, 10240));
        gemstones.add(generateGem("Jade Gemstone", jadeLore, ChatColor.GREEN, Material.LIME_STAINED_GLASS, 10240));
        gemstones.add(generateGem("Amber Gemstone", amberLore, ChatColor.GOLD, Material.ORANGE_STAINED_GLASS, 10240));
        gemstones.add(generateGem("Sapphire Gemstone", sapphireLore, ChatColor.BLUE, Material.BLUE_STAINED_GLASS, 10240));
    }

    public ItemStack generateGem(String name, List<String> lore, ChatColor itemColor, Material mat, int customModelID) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(itemColor + name);
        meta.setLore(lore);
        meta.setCustomModelData(customModelID);

        item.setItemMeta(meta);

        return item;
    }

}
