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
- MYSQL
- Papermc server or its fork

### Placeholders

- `%nclans_clan_displayname%` - Player's clan displayname with additional space symbol before displayname. Empty if player is not in clan
- `%nclans_clan_displayname_minimessage%` - Same as previous, but in Paper's minimessage format 

### Permissions

- `nclans.admin` - access for command `/clans admin`
- `nclans.displayname.change` - change clan display name (change colors only)

### Admin commands
- `/clans admin reload` - reload config
- `/clans admin refresh` - refresh cache for this server

### How it works?

Data from database cached in every server. When players changes database state (create/join/leave clan, etc), servers reloading its cache. Every command interaction running async and always communicates with database, cache is read-only. Plugin messaging between server via bungeecord api (faster; if available; Velocity also supports it) or mysql database (slower; not requires proxy or online players on every server) for sending chat messages and refreshing cache. 
