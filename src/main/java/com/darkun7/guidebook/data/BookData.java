package com.darkun7.guidebook.data;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class BookData {
    public final String title;
    public final String author;
    public final List<String> pages;

    public BookData(String title, String author, List<String> pages) {
        this.title = title;
        this.author = author;
        this.pages = pages;
    }

    public static BookData loadFrom(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        String title = config.getString("title", "Untitled");
        String author = config.getString("author", "Unknown");
        List<String> pages = config.getStringList("pages");

        if (pages == null || pages.isEmpty()) return null;
        return new BookData(title, author, pages);
    }
}
