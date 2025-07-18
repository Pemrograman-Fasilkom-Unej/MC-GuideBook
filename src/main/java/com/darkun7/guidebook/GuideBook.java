package com.darkun7.guidebook;

import org.bukkit.plugin.java.JavaPlugin;

import com.darkun7.guidebook.data.BookData;
import com.darkun7.guidebook.command.GuideCommand;
import com.darkun7.guidebook.scheduler.Broadcast;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;
import java.util.Map;

public class GuideBook extends JavaPlugin {

    private final Map<String, BookData> books = new HashMap<>();
    private Broadcast broadcaster;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        loadBooks();
        broadcaster = new Broadcast(this);
        broadcaster.loadConfig(getConfig());
        broadcaster.start(getConfig().getInt("interval_minutes", 5) * 60L * 20);

        getCommand("guide").setExecutor(new GuideCommand(this));
        getCommand("guide").setTabCompleter(new GuideCommand(this));
        
        getLogger().info("GuideBook enabled.");
    }

    public Map<String, BookData> getBooks() {
        return books;
    }

    public void loadBooks() {
        books.clear();

        File folder = new File(getDataFolder(), "books");
        if (!folder.exists()) {
            folder.mkdirs();
            saveResource("books/rules.yml", false);
            saveResource("books/system.yml", false);
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null || files.length == 0) {
            getLogger().warning("No book files found in 'books/' folder!");
            return;
        }

        for (File file : files) {
            try {
                BookData book = BookData.loadFrom(file);
                if (book != null) {
                    books.put(file.getName().replace(".yml", ""), book);
                } else {
                    getLogger().warning("Failed to load book: " + file.getName());
                }
            } catch (Exception e) {
                getLogger().warning("Error loading book '" + file.getName() + "': " + e.getMessage());
            }
        }
    }


    @Override
    public void onDisable() {
        if (broadcaster != null) broadcaster.stop();
    }

    public Broadcast getBroadcaster() {
        return broadcaster;
    }

    public void reloadAll() {
        reloadConfig();
        loadBooks();
        broadcaster.reload(getConfig());
    }
}
