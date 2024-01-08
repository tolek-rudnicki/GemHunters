package pl.epsi.gemhunters;

import net.minecraft.network.chat.ChatMessageContent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Utils {

    public static String colorize(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static void sendActionBar(Player player, String message) {
        message= message.replaceAll("%player%", player.getDisplayName());
        message = Utils.colorize(message);
        CraftPlayer p = (CraftPlayer) player;
        ChatMessageContent cmc = new ChatMessageContent(message);
        ClientboundSetActionBarTextPacket ppoc = new ClientboundSetActionBarTextPacket(cmc.decorated());
        p.getHandle().connection.connection.send(ppoc);
    }

}
