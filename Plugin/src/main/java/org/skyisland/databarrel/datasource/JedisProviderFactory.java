package org.skyisland.databarrel.datasource;

import org.skyisland.databarrel.config.RedisConfiguration;
import redis.clients.jedis.*;

public class JedisProviderFactory {

    /**
     * Не возвращает {@link JedisPooled} или {@link JedisCluster}, чтобы
     * создать унифицированный интерфейс, который позволяет работать
     * как с кластером узлов, так и с standalone узлом.
     * **/
    public UnifiedJedis create(RedisConfiguration configuration) {
        if (configuration.isCluster()) {
            throw new UnsupportedOperationException("Redis cluster not supported");
        } else {
            return new JedisPooled(
                    configuration.host(),
                    configuration.port(),
                    configuration.user(),
                    configuration.password()
            );
        }
    }

}
