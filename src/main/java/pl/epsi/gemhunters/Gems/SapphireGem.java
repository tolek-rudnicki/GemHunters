package pl.epsi.gemhunters.Gems;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import pl.epsi.gemhunters.GemstoneRegistry;
import pl.epsi.gemhunters.Utils;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpResponse;
import java.util.*;

public class SapphireGem extends Gem {

    public static Map<UUID, List<ServerPlayer>> npcsForPlayer = new HashMap<>();

    public SapphireGem() {
        super();

        displayName = "Sapphire Gemstone";
        itemColor = ChatColor.BLUE;
        material = Material.LIGHT_BLUE_STAINED_GLASS;
        customModelID = 10240;

        lore.add(" ");
        lore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "RIGHT CLICK");
        lore.add(ChatColor.GRAY + "Boost you in to the air!");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 5s");
        lore.add(ChatColor.GOLD + "Ability: Spectral Scribe " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "LEFT CLICK");
        lore.add(ChatColor.GRAY + "Create 3 identical copies of yourself,");
        lore.add(ChatColor.GRAY + "that copy your exact moves confusing ");
        lore.add(ChatColor.GRAY + "the enemy");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 1min");
        lore.add(ChatColor.GOLD + "Ability: Light As A Feather " + ChatColor.RESET + ChatColor.BOLD + "" + ChatColor.YELLOW + "SHIFT + RIGHT CLICK");
        lore.add(ChatColor.GRAY + "Boost you in to the air!");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "Cooldown: 5min");
        lore.add(" ");
        lore.add(ChatColor.BLUE + "After 5 deaths, your gem shatters, ");
        lore.add(ChatColor.BLUE + "which gives you negative potion effects");
        lore.add(ChatColor.BLUE + "that you can get rid of, by crafting a new gem");
        lore.add(ChatColor.BLUE + "in the Gemstone Grinder!");
    }

    public void ability1(Player p) {

    }

    public void ability2(Player p) {
        GemstoneRegistry registry = GemstoneRegistry.getInstance();
        UUID uuid = p.getUniqueId();

        if (registry.canUseAbility(uuid, registry.getPlayerGemID(uuid), 1)) {
            // NMS HERE I COMEEEEEEEEEEEEE

            CraftPlayer cp = (CraftPlayer) p;
            MinecraftServer nmsServer = cp.getHandle().getServer();
            ServerLevel nmsWorld = cp.getHandle().getLevel();
            ServerPlayer npc = new ServerPlayer(nmsServer, nmsWorld, new GameProfile(UUID.randomUUID(), "bear_with_me_XD"), null);
            ServerPlayer npc2 = new ServerPlayer(nmsServer, nmsWorld, new GameProfile(UUID.randomUUID(), "bear_with_me_XD"), null);

            npcsForPlayer.put(uuid, new ArrayList<>());
            npcsForPlayer.get(uuid).add(npc);
            npcsForPlayer.get(uuid).add(npc2);

            try {
                String uuidString = uuid.toString().replace('-', ' ');
                uuidString = uuidString.replaceAll("\\s", "");

                URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" +
                        uuidString.strip() + "?unsigned");

                URLConnection con = url.openConnection();
                InputStream is = con.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(is)) ;
                String line = null;
                String value = null;
                String signature = null;

                while ((line = br.readLine()) != null) {
                    if (line.contains("value")) {
                        String temp = line.split(":")[1];
                        value = temp.strip().replaceAll("\"", "");
                        value = value.replaceAll(",", "");
                    } else if (line.contains("signature")) {
                        String temp = line.split(":")[1];
                        signature = temp.strip().replaceAll("\"", "");
                        signature = signature.replaceAll(",", "");
                    }
                }

                npc.getGameProfile().getProperties().put("textures", new Property("textures", value, signature));
                npc2.getGameProfile().getProperties().put("textures", new Property("textures", value, signature));


             } catch (Exception e) {
                System.err.println(e);
            }

            npc.setPos(p.getLocation().getBlockX() + 2, p.getLocation().getBlockY(), p.getLocation().getBlockZ());
            npc2.setPos(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ() + 2);

            List<Player> nearbyPlayers = new ArrayList<>();
            nearbyPlayers.add(p);

            for(Entity e : p.getNearbyEntities(25, 25, 25)) {
                if (e instanceof Player) {
                    nearbyPlayers.add((Player) e);
                }
            }

            for (Player n : nearbyPlayers) {
                CraftPlayer ncp = (CraftPlayer) n;

                ServerGamePacketListenerImpl ps = ncp.getHandle().connection;

                ps.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc));
                ps.send(new ClientboundAddPlayerPacket(npc));
                ps.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc2));
                ps.send(new ClientboundAddPlayerPacket(npc2));
            }



            scheduler.runTaskLater(plugin, () -> {
                for (Player n : nearbyPlayers) {
                    CraftPlayer ncp = (CraftPlayer) n;

                    ServerGamePacketListenerImpl ps = ncp.getHandle().connection;

                    ps.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npc));
                    ps.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npc2));

                    IntList il = new IntArrayList();
                    il.add(npc.getId());
                    il.add(npc2.getId());

                    ps.send(new ClientboundRemoveEntitiesPacket(il));

                    //npcsForPlayer.remove(uuid);
                }
            }, 30 * 20);
            //p.sendMessage(npcsForPlayer.get(uuid) + "");

            p.sendMessage(Utils.colorize("&7[&9✎&7] You used > &9Reflection Prism!"));
            registry.doUseAbility(uuid, registry.getPlayerGemID(uuid), 1);
        }
    }

    public void ability3(Player p) {

    }

    public List<ServerPlayer> getNpcsForUUID(UUID uuid) {
        //Bukkit.getPlayer(uuid).sendMessage(npcsForPlayer.get(uuid) + "");
        return npcsForPlayer.get(uuid);
        //return new ArrayList<>();
    }
}
