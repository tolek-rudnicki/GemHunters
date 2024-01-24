package pl.epsi.gemhunters.Gems;

import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
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
    private JadeGem jade = new JadeGem();
    private AmberGem amber = new AmberGem();
    private SapphireGem sapphire = new SapphireGem();

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
                                event.setCancelled(true);
                                return;
                            }
                        }
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
            } else if (jade.playerInAbility1(p)) {
                event.setDamage(event.getDamage() * 1.1);
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
                    for (Gem gem : gems) {
                        if (gem.getDisplayName().equalsIgnoreCase(itemName)) {
                            if (p.isSneaking()) {
                                gem.ability3(p);
                                event.setCancelled(true);
                            } else {
                                gem.ability1(p);
                                event.setCancelled(true);
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        UUID uuid = p.getUniqueId();
        Location npc1Loc = p.getLocation().clone().add(2, 0, 0);
        Location npc2Loc = p.getLocation().clone().subtract(2, 0, 0);

        if (SapphireGem.npcsForPlayer.get(uuid) != null) {
            List<ServerPlayer> npcs = SapphireGem.npcsForPlayer.get(uuid);
            ServerPlayer npc1 = npcs.get(0);
            ServerPlayer npc2 = npcs.get(1);


            for (Entity e : p.getNearbyEntities(25, 25, 25)) {
                if (e instanceof Player) {
                    Player pe = (Player) e;
                    ServerGamePacketListenerImpl pes = ((CraftPlayer) pe).getHandle().connection;

                    // pe
                    pes.send(new ClientboundMoveEntityPacket.Pos(npc1.getId(), (short) npc1Loc.getX(), (short) npc1Loc.getY(), (short)
                            npc1Loc.getZ(), false
                    ));
                    pes.send(new ClientboundRotateHeadPacket(npc1, (byte) npc1Loc.getYaw()));
                    pes.send(new ClientboundMoveEntityPacket.Rot(npc1.getId(), (byte) npc1Loc.getYaw(), (byte) npc1Loc.getPitch(), false));

                    pes.send(new ClientboundMoveEntityPacket.Pos(npc2.getId(), (short) npc2Loc.getX(), (short) npc2Loc.getY(), (short)
                            npc2Loc.getZ(), false
                    ));
                    pes.send(new ClientboundRotateHeadPacket(npc2, (byte) npc2Loc.getYaw()));
                    pes.send(new ClientboundMoveEntityPacket.Rot(npc2.getId(), (byte) npc2Loc.getYaw(), (byte) npc2Loc.getPitch(), false));
                }
            }
            //p.sendMessage("breh");
            ServerGamePacketListenerImpl ps = ((CraftPlayer) p).getHandle().connection;

            ps.send(new ClientboundMoveEntityPacket.Pos(npc1.getId(), (short) npc1Loc.getX(), (short) npc1Loc.getY(), (short)
                    npc1Loc.getZ(), false
            ));
            //npc1.teleportTo(npc1Loc.getX(), npc1Loc.getY(), npc1Loc.getZ());
            ps.send(new ClientboundRotateHeadPacket(npc1, (byte) npc1Loc.getYaw()));
            ps.send(new ClientboundMoveEntityPacket.Rot(npc1.getId(), (byte) npc1Loc.getYaw(), (byte) npc1Loc.getPitch(), false));

            ps.send(new ClientboundMoveEntityPacket.Pos(npc2.getId(), (short) npc2Loc.getX(), (short) npc2Loc.getY(), (short)
                    npc2Loc.getZ(), false
            ));
            //npc2.teleportTo(npc2Loc.getX(), npc2Loc.getY(), npc2Loc.getZ());
            ps.send(new ClientboundRotateHeadPacket(npc2, (byte) npc2Loc.getYaw()));
            ps.send(new ClientboundMoveEntityPacket.Rot(npc2.getId(), (byte) npc2Loc.getYaw(), (byte) npc2Loc.getPitch(), false));

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
        gems.add(new JadeGem());
        gems.add(new AmberGem());
        gems.add(new SapphireGem());

        for (Gem gem : gems) {
            gemstones.add(gem.generateItemStack());
        }

        startGemstoneTimerDisplay();
    }

}
