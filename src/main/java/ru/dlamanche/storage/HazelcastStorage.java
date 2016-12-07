package ru.dlamanche.storage;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Date: 05.12.2016
 * Time: 19:16
 *
 * @author Dmitry Shuplyakov
 */
public class HazelcastStorage {

    private static final Logger log = LoggerFactory.getLogger(HazelcastStorage.class);

    private final static String DEFAULT_MAP = "DEFAULT";

    HazelcastInstance instance;

    public HazelcastStorage() {
        Config cfg = new Config();
        instance = Hazelcast.newHazelcastInstance(cfg);
        shutdownHook();
    }

    public void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("Closing hazelcast");
                instance.shutdown();
            }
        });
    }

    public HazelcastInstance getInstance() {
        return instance;
    }

    public <E> IMap<String, List<E>> getMap() {
        return instance.getMap(DEFAULT_MAP);
    }

    public void addToMap(Long key, Long value) {
        instance.getMap(DEFAULT_MAP).set(key, value);
    }
}
