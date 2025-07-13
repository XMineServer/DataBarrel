package org.skyisland.databarrel.datasource;

import org.skyisland.databarrel.config.RedisConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class JedisProviderFactory {

    /**
     * Не возвращает {@link Jedis} или {@link JedisCluster}, чтобы
     * создать унифицированный интерфейс, который позволяет работать
     * как с кластером узлов, так и с standalone узлом.
     * **/
    public JedisCommandsProvider<?> create(RedisConfiguration configuration) {
        if (configuration.isCluster()) {
            throw new UnsupportedOperationException("Redis cluster not supported");
        } else {
            var provider = new JedisPoolCommandsProvider(configuration);
            provider.initialize();
            return provider;
        }
    }

    private static class JedisPoolCommandsProvider implements JedisCommandsProvider<Jedis> {

        private final RedisConfiguration configuration;
        private JedisPool pool;

        public JedisPoolCommandsProvider(RedisConfiguration configuration) {
            this.configuration = configuration;
        }

        private void initialize() {
            pool = new JedisPool(configuration.host(), configuration.port());
        }

        @Override
        public void close() {
            if (pool != null) {
                pool.close();
            }
        }

        @Override
        public Jedis get() {
            return pool.getResource();
        }
    }

}
