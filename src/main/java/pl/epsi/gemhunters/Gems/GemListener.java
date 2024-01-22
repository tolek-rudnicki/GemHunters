package pl.epsi.gemhunters.Gems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
import org.bukkit.scheduler.BukkitScheduler;
import pl.epsi.gemhunters.GemstoneProperty;
import pl.epsi.gemhunters.GemstoneRegistry;
import pl.epsi.gemhunters.Main;
import pl.epsi.gemhunters.Utils;

import java.util.*;

public class GemListener implements Listener {

    private List<ItemStack> gemstones = new ArrayList<>();
    private List<Gem> gems = new ArrayList<>();
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
                        for (Gem gem : gems) {
                            if (gem.getDisplayName().equalsIgnoreCase(i.getItemMeta().getDisplayName())) {
                                gem.ability2(p);
                                return;
                            }
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

                    for (Gem gem : gems) {
                        if (gem.getDisplayName().equalsIgnoreCase(itemName)) {
                            if (p.isSneaking()) {
                                gem.ability3(p);
                            } else {
                                gem.ability1(p);
                            }
                            return;
                        }
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
    public void onPickUp(PlayerPickupItemEvent event) { // MAKE IT SO U CAN ONLY PICK UP UR TYPE OF GEM
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

    public void startGemstoneTimerDisplay() {
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
        gems.add(new OpalGem());
        gems.add(new RubyGem());
        gems.add(new AmethystGem());
        gems.add(new JasperGem());
        gems.add(new TopazGem());

        for (Gem gem : gems) {
            gemstones.add(gem.generateItemStack());
        }

        startGemstoneTimerDisplay();

//        // JADE
//        List<String> jadeLore = new ArrayList<>();
//        jadeLore.add(" ");
//        jadeLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
//        jadeLore.add(ChatColor.GRAY + "Boost you in to the air!");
//        jadeLore.add(" ");
//        jadeLore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
//        jadeLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
//        jadeLore.add(ChatColor.GRAY + "Boost you in to the air!");
//        jadeLore.add(" ");
//        jadeLore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
//        jadeLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
//        jadeLore.add(ChatColor.GRAY + "Boost you in to the air!");
//        jadeLore.add(" ");
//        jadeLore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
//        jadeLore.add(" ");
//        jadeLore.add(ChatColor.GREEN + "After 5 deaths, your gem shatters, ");
//        jadeLore.add(ChatColor.GREEN + "which gives you negative potion effects");
//        jadeLore.add(ChatColor.GREEN + "that you can get rid of, by crafting a new gem");
//        jadeLore.add(ChatColor.GREEN + "in the Gemstone Grinder!");
//        // AMBER
//        List<String> amberLore = new ArrayList<>();
//        amberLore.add(" ");
//        amberLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
//        amberLore.add(ChatColor.GRAY + "Boost you in to the air!");
//        amberLore.add(" ");
//        amberLore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
//        amberLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
//        amberLore.add(ChatColor.GRAY + "Boost you in to the air!");
//        amberLore.add(" ");
//        amberLore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
//        amberLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
//        amberLore.add(ChatColor.GRAY + "Boost you in to the air!");
//        amberLore.add(" ");
//        amberLore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
//        amberLore.add(" ");
//        amberLore.add(ChatColor.GOLD + "After 5 deaths, your gem shatters, ");
//        amberLore.add(ChatColor.GOLD + "which gives you negative potion effects");
//        amberLore.add(ChatColor.GOLD + "that you can get rid of, by crafting a new gem");
//        amberLore.add(ChatColor.GOLD + "in the Gemstone Grinder!");
//        // SAPPHIRE
//        List<String> sapphireLore = new ArrayList<>();
//        sapphireLore.add(" ");
//        sapphireLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
//        sapphireLore.add(ChatColor.GRAY + "Boost you in to the air!");
//        sapphireLore.add(" ");
//        sapphireLore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
//        sapphireLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
//        sapphireLore.add(ChatColor.GRAY + "Boost you in to the air!");
//        sapphireLore.add(" ");
//        sapphireLore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
//        sapphireLore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
//        sapphireLore.add(ChatColor.GRAY + "Boost you in to the air!");
//        sapphireLore.add(" ");
//        sapphireLore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
//        sapphireLore.add(" ");
//        sapphireLore.add(ChatColor.BLUE + "After 5 deaths, your gem shatters, ");
//        sapphireLore.add(ChatColor.BLUE + "which gives you negative potion effects");
//        sapphireLore.add(ChatColor.BLUE + "that you can get rid of, by crafting a new gem");
//        sapphireLore.add(ChatColor.BLUE + "in the Gemstone Grinder!");

//        gemstones.add(generateGem("Jade Gemstone", jadeLore, ChatColor.GREEN, Material.LIME_STAINED_GLASS, 10240));
//        gemstones.add(generateGem("Amber Gemstone", amberLore, ChatColor.GOLD, Material.ORANGE_STAINED_GLASS, 10240));
//        gemstones.add(generateGem("Sapphire Gemstone", sapphireLore, ChatColor.BLUE, Material.BLUE_STAINED_GLASS, 10240));
    }

}
