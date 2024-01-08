package pl.epsi.gemhunters.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.epsi.gemhunters.GemstoneRegistry;

public class ClearList implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        GemstoneRegistry.getInstance().clear();
        sender.sendMessage("CLEARED!");
        return false;
    }
}
