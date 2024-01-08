package pl.epsi.gemhunters;

import fr.skytasul.glowingentities.GlowingEntities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.epsi.gemhunters.Commands.ClearList;
import pl.epsi.gemhunters.Gems.GemListener;

public class Main extends JavaPlugin {

    private final ConsoleCommandSender sender = Bukkit.getConsoleSender();
    private final PluginManager manager = Bukkit.getPluginManager();


    @Override
    public void onEnable() {
        sender.sendMessage(ChatColor.GREEN + "Started Gem Hunters Plugin!!");

        GemstoneRegistry.getInstance().load();

        GemListener listener = new GemListener();
        listener.init();
        manager.registerEvents(listener, this);

        this.getCommand("clearlist").setExecutor(new ClearList());
    }

    @Override
    public void onDisable() {
        sender.sendMessage(ChatColor.RED + "Closing Gem Hunters Plugin!");
        GemstoneRegistry.getInstance().save();
    }
}
