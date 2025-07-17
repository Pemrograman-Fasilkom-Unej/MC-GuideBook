# 📖 GuideBook Plugin

A Minecraft plugin that allows server owners to provide **in-game guide books** to players via the `/guide` command. Each guidebook is configurable via `config.yml`, and players can click broadcast messages to open them. It also features **random scheduled broadcasts** to inform players of available guides.

---

## ✨ Features

- 📘 Multiple in-game guide books
- 🕒 Configurable scheduled broadcast messages
- 💬 Clickable text to open guide books directly
- 🔄 `/guide reload` to reload config and books on the fly
- 🧠 Auto tab-complete for available books

---

## 🛠 Installation

1. Place the compiled JAR into your server’s `plugins/` folder.
2. Start the server to generate the `config.yml` and `books/` folder.
3. Add your books as separate `.yml` files in `plugins/GuideBook/books/`.

---

## 🔧 Configuration

### `config.yml`

```yaml
interval_minutes: 5
messages:
  - text: "&bIn memoriam &ePemro.id!"
    hover: "&7Opens in browser"
    click:
      action: "open_url"
      value: "https://web.archive.org/web/20230701195202/https://pemro.id/"
  - text: "&bUse &e/guide &bto read the guidebook"
    hover: "&7Click to open guide"
    click:
      action: "run_command"
      value: "/guide"
