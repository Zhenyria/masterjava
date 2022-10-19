package ru.javaops.masterjava.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * gkislin
 * 01.11.2016
 */
@UtilityClass
public class Configs {
    private static final Map<String, Config> inMemoryConfigs = new ConcurrentHashMap<>();

    public static Config getConfig(String resource) {
        Config config = inMemoryConfigs.get(resource);
        if (Objects.isNull(config)) {
            config = ConfigFactory.parseResources(resource).resolve();
            inMemoryConfigs.put(resource, config);
        }
        return config;
    }

    public static Config getConfig(String resource, String domain) {
        return getConfig(resource).getConfig(domain);
    }
}
