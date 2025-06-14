# NClans

Simple cross-server clans with economy and placeholders support

### Features 

- Clan taxes (May be disabled)
- Seamless cross-server work (With bungeecord or without it)
- Cross server clan home teleportation (Only in one proxy instance; May be disabled)
- Simple player online tracking system
- Clan leave/join cooldown for players

### Dependencies 

- [AnvilORM](https://github.com/NezuShin/AnvilORM/releases/tag/V1.0.0) 
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- [Vault](https://www.spigotmc.org/resources/vault.34315/) (Optional; If you need economy support)
- MYSQL (Optional; If you need cross-server work)
- Papermc server or its fork

### Placeholders

- `%nclans_clan_displayname%` - Player's clan displayname with additional space symbol before displayname. Empty if player is not in clan
- `%nclans_clan_displayname_minimessage%` - Same as previous, but in Paper's minimessage format 

### Permissions

- `nclans.admin` - access for command `/clans admin`
- `nclans.displayname.change` - change clan display name (change colors only)
- Another clan commands can be accessed without any premissions

### Admin commands
- `/clans admin reload` - reload config
- `/clans admin refresh` - refresh cache for this server

### How does it work?

Data from database cached in every server. When players change database state (create/join/leave clan, etc), servers reload their cache. Every command interaction runs async and always communicates with database, cache is synchronous and read-only. Plugin communication between servers is done via BungeeCord API (faster; supported by Velocity; preferred by the plugin) or MySQL database (slower; does not requires a proxy or online players on every server) for sending chat messages and refreshing cache. 
