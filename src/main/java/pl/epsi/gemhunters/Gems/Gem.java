package pl.epsi.gemhunters.Gems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import pl.epsi.gemhunters.Main;
import pl.epsi.gemhunters.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class Gem {

    protected String displayName = "GEM";
    protected List<String> lore = new ArrayList<>();
    protected ChatColor itemColor = ChatColor.WHITE;
    protected Material material = Material.WHITE_STAINED_GLASS;
    protected int customModelID = 10240;

    protected BukkitScheduler scheduler = Bukkit.getScheduler();
    protected Plugin plugin = Main.getPlugin(Main.class);

    public String getDisplayName() {
        return itemColor + displayName;
    }

    public abstract void ability1(Player p);

    public abstract void ability2(Player p);

    public abstract void ability3(Player p);

    public ItemStack generateItemStack() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(itemColor + displayName);
        meta.setLore(lore);
        meta.setCustomModelData(customModelID);

        item.setItemMeta(meta);

        return item;
    }
}
