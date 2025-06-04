package su.nezushin.clans.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import su.nezushin.clans.enums.ClanAction;
import su.nezushin.clans.enums.ClanGroup;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlStringSerializer {

    public static String serializeConfigurationSerializable(ConfigurationSerializable cs) {

        var conf = new YamlConfiguration();

        conf.set("value", cs);

        return conf.saveToString();
    }

    public static <T extends ConfigurationSerializable> T
    deserializeConfigurationSerializable(String str, Class<T> clazz) {
        if (str == null)
            return null;
        var conf = YamlConfiguration.loadConfiguration(new StringReader(str));
        var value = conf.get("value");

        return value != null ? clazz.cast(value) : null;
    }

    public static String serializeStringList(List<String> list) {

        var conf = new YamlConfiguration();

        conf.set("value", list);

        return conf.saveToString();
    }

    public static List<String> deserializStringList(String str) {
        if (str == null)
            return null;
        var conf = YamlConfiguration.loadConfiguration(new StringReader(str));

        return conf.getStringList("value");
    }

    public static String serializePermissionMap(Map<ClanAction, ClanGroup> permissions) {
        var conf = new YamlConfiguration();

        permissions.forEach((a, b) -> {
            conf.set(a.name().toLowerCase(), b.name());
        });

        return conf.saveToString();
    }

    public static Map<ClanAction, ClanGroup> deserializePermissionMap(String str) {

        var conf = YamlConfiguration.loadConfiguration(new StringReader(str));

        var map = new HashMap<ClanAction, ClanGroup>();

        for (var i : ClanAction.values()) {
            map.put(i, ClanGroup.valueOf(conf.getString(i.name().toLowerCase(), ClanGroup.OWNER.name())));
        }

        return map;
    }

}
