package org.lzmcommon.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * RedisTemplate 序列化策略
     * <p>
     * key / hashKey：字符串（StringRedisSerializer）
     * <p>
     * value / hashValue：JSON（GenericJackson2JsonRedisSerializer，内部会开启 default typing，
     * 写出的 JSON 带 {@code @class}，因此能读回原始类型，而不是 LinkedHashMap）
     * <p>
     * 注意：StringRedisSerializer 只能序列化 String，传入其它对象会抛
     * {@code ClassCastException: xxx cannot be cast to java.lang.String}。
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer = jsonSerializer();

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);

        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 值序列化器：JSON + 类型信息
     */
    private GenericJackson2JsonRedisSerializer jsonSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        // java.time 支持（LocalDateTime / LocalDate 等）
        mapper.registerModule(new JavaTimeModule());
        // 时间按 ISO-8601 字符串写出，不写时间戳
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // default typing(@class) 由 GenericJackson2JsonRedisSerializer 构造器自动开启
        return new GenericJackson2JsonRedisSerializer(mapper);
    }
}
