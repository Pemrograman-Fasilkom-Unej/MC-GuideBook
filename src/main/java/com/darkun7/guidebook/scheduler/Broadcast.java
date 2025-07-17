package com.darkun7.guidebook.scheduler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Broadcast {

    private final JavaPlugin plugin;
    private final List<Component> messages = new ArrayList<>();
    private BukkitTask task;

    public Broadcast(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public void loadConfig(FileConfiguration config) {
        messages.clear();

        List<?> list = config.getList("messages");
        if (list == null || list.isEmpty()) {
            plugin.getLogger().warning("No broadcast messages found in config!");
            return;
        }

        for (Object obj : list) {
            if (obj instanceof String str) {
                messages.add(LegacyComponentSerializer.legacyAmpersand().deserialize(str));
            } else if (obj instanceof Map<?, ?> map) {
                String text = (String) map.get("text");
                String hover = (String) map.get("hover");

                Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(text != null ? text : "");

                if (hover != null) {
                    component = component.hoverEvent(HoverEvent.showText(
                            LegacyComponentSerializer.legacyAmpersand().deserialize(hover)
                    ));
                }

                Object clickRaw = map.get("click");
                if (clickRaw instanceof Map<?, ?> clickMap) {
                    String actionStr = (String) clickMap.get("action");
                    String value = (String) clickMap.get("value");

                    if (actionStr != null && value != null) {
                        ClickEvent.Action action = switch (actionStr.toLowerCase()) {
                            case "run_command" -> ClickEvent.Action.RUN_COMMAND;
                            case "suggest_command" -> ClickEvent.Action.SUGGEST_COMMAND;
                            case "open_url" -> ClickEvent.Action.OPEN_URL;
                            default -> null;
                        };
                        if (action != null) {
                            component = component.clickEvent(ClickEvent.clickEvent(action, value));
                        }
                    }
                }

                messages.add(component);
            }
        }
    }

    public void start(long intervalTicks) {
        if (messages.isEmpty()) {
            plugin.getLogger().warning("No broadcast messages found in config!");
            return;
        }

        if (task != null) task.cancel();

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Component message = messages.get(ThreadLocalRandom.current().nextInt(messages.size()));
            Bukkit.getServer().sendMessage(message);
        }, intervalTicks, intervalTicks);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void reload(FileConfiguration config) {
        stop();
        loadConfig(config);
        long intervalTicks = config.getInt("interval_minutes", 5) * 60L * 20;
        start(intervalTicks);
    }
}
