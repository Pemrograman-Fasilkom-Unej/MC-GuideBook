package com.darkun7.guidebook.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.darkun7.guidebook.GuideBook;
import com.darkun7.guidebook.data.BookData;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.ArrayList;
import java.util.List;

public class GuideCommand implements CommandExecutor, TabCompleter {

    private final GuideBook plugin;

    public GuideCommand(GuideBook plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /guide reload
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("guide.reload")) {
                sender.sendMessage("§cYou don't have permission.");
                return true;
            }
            plugin.reloadAll();
            sender.sendMessage("§aGuide books and broadcast reloaded.");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this.");
            return true;
        }

        // /guide
        if (args.length != 1) {
            sender.sendMessage("§cUsage: §e/guide §f<bookName>");
            sender.sendMessage("§7Available:");

            for (String name : plugin.getBooks().keySet()) {
                TextComponent msg = new TextComponent(" §a• §f" + name);
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guide " + name));
                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Click to open book '" + name + "'").create()));
                player.spigot().sendMessage(msg);
            }
            return true;
        }

        // /guide <bookName>
        BookData book = plugin.getBooks().get(args[0].toLowerCase());
        if (book == null) {
            sender.sendMessage("§cGuide not found.");
            return true;
        }

        openBook(player, book);
        return true;
    }

    private void openBook(Player player, BookData book) {
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) item.getItemMeta();
        meta.setTitle(book.title);
        meta.setAuthor(book.author);
        for (String page : book.pages) {
            meta.addPage(page.replace("\\n", "\n"));
        }
        item.setItemMeta(meta);

        int slot = player.getInventory().getHeldItemSlot();
        ItemStack old = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, item);
        player.openBook(item);

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.getInventory().setItem(slot, old), 1L);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(plugin.getBooks().keySet());
            completions.add("reload");
            return completions;
        }
        return null;
    }
}
