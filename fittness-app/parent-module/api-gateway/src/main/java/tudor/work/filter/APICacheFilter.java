//package tudor.work.filter;
////
////import com.google.common.net.HttpHeaders;
////import lombok.Getter;
////import lombok.Setter;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.cloud.gateway.filter.GatewayFilter;
////import org.springframework.cloud.gateway.filter.GatewayFilterChain;
////import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
////import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
////import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
////import org.springframework.core.io.buffer.DataBuffer;
////import org.springframework.core.io.buffer.DataBufferFactory;
////import org.springframework.core.io.buffer.DataBufferUtils;
////import org.springframework.data.redis.core.ReactiveRedisOperations;
////import org.springframework.http.MediaType;
////import org.springframework.http.server.reactive.ServerHttpResponse;
////import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
////import org.springframework.stereotype.Component;
////import org.springframework.web.server.ServerWebExchange;
////import reactor.core.publisher.Flux;
////import reactor.core.publisher.Mono;
////
////import java.nio.charset.StandardCharsets;
////import java.time.Duration;
////
////@Component
////public class RedisCacheFilter extends AbstractGatewayFilterFactory<RedisCacheFilter.Config> {
////
////    private final ModifyResponseBodyGatewayFilterFactory modifyResponseBodyFilterFactory;
////    private final ReactiveRedisOperations<String, String> reactiveRedisOperations;
////
////    @Autowired
////    public RedisCacheFilter(ModifyResponseBodyGatewayFilterFactory modifyResponseBodyFilterFactory,
////                            ReactiveRedisOperations<String, String> reactiveRedisOperations) {
////        super(Config.class);
////        this.modifyResponseBodyFilterFactory = modifyResponseBodyFilterFactory;
////        this.reactiveRedisOperations = reactiveRedisOperations;
////    }
////
////    @Getter
////    @Setter
////    public static class Config {
////        private Duration ttl = Duration.ofMinutes(5); // Set default TTL to 5 minutes
////    }
////
////    @Override
////    public GatewayFilter apply(Config config) {
////        return (exchange, chain) -> {
////            String cacheKey = exchange.getRequest().getURI().toString();
////            return reactiveRedisOperations.opsForValue().get(cacheKey)
////                    .flatMap(cachedResponse -> writeCachedResponse(exchange, cachedResponse))
////                    .switchIfEmpty(Mono.defer(() -> processWithCache(exchange, chain, cacheKey, config)));
////        };
////    }
////
////    //Cache hit. The content is found in the cache and is returned to the client.
//////    private Mono<Void> writeCachedResponse(ServerWebExchange exchange, String cachedResponse) {
//////        System.out.println("Cache Hit");
//////        return exchange.getResponse().writeWith(
//////                Mono.just(exchange.getResponse().bufferFactory().wrap(cachedResponse.getBytes(StandardCharsets.UTF_8)))
//////        );
//////    }
////
////    private Mono<Void> writeCachedResponse(ServerWebExchange exchange, String cachedResponse) {
////        System.out.println("Cache Hit");
////        if (cachedResponse != null) { // Check that the cached response is not null
////            exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
////            return exchange.getResponse().writeWith(
////                    Mono.just(exchange.getResponse().bufferFactory().wrap(cachedResponse.getBytes(StandardCharsets.UTF_8)))
////            );
////        } else {
////            return Mono.empty(); // Cached response was null, so do nothing (this should not happen if the cache is working correctly)
////        }
////    }
////
////
////    //Cache miss. The content is not found in
////    //the cache and the request goes on to the chain.
////    //After the response is generated by the backend services the
////    //response will be stored in the redis cache
//////    private Mono<Void> processWithCache(ServerWebExchange exchange, GatewayFilterChain chain, String cacheKey, Config config) {
//////        System.out.println("Cache Miss");
//////
//////        ModifyResponseBodyGatewayFilterFactory.Config modifyConfig = new ModifyResponseBodyGatewayFilterFactory.Config();
//////        modifyConfig.setRewriteFunction(String.class, String.class, (exchange1, originalResponseBody) -> {
//////            // Cache the original response body
//////            reactiveRedisOperations.opsForValue().set(cacheKey, originalResponseBody, config.getTtl()).subscribe();
//////            return Mono.just(originalResponseBody);
//////        });
//////
//////        GatewayFilter filter = modifyResponseBodyFilterFactory.apply(modifyConfig);
//////        return filter.filter(exchange, chain);
//////    }
////
////        private Mono<Void> processWithCache(ServerWebExchange exchange, GatewayFilterChain chain, String cacheKey, Config config) {
////        return reactiveRedisOperations.opsForValue().set(cacheKey, exchange.getResponse().bufferFactory().toString(), config.getTtl()).then(Mono.just(exchange.getRequest().)).then(Mono.just(exchange.getResponse().bufferFactory().toString())).then();
////
////    }
////
////}
//
//import lombok.Getter;
//import lombok.Setter;
//import org.reactivestreams.Publisher;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferUtils;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.http.MediaType;
//import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.time.Duration;
//
//@Component
//public class APICacheFilter extends AbstractGatewayFilterFactory<APICacheFilter.Config> {
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    @Autowired
//    public APICacheFilter(RedisTemplate<String, Object> redisTemplate) {
//        super(Config.class);
//        this.redisTemplate = redisTemplate;
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            String cacheKey = config.getKey().isEmpty() ? generateCacheKey(exchange) : config.getKey();
//            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
//
//            if (redisTemplate.hasKey(cacheKey)) {
//                // Fetch from cache and assume it's a byte array
//                byte[] cachedResponseBody = (byte[]) ops.get(cacheKey);
//                if (cachedResponseBody != null) {
//                    // Write the cached response to the client
//                    DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(cachedResponseBody);
//                    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
//                    return exchange.getResponse().writeWith(Flux.just(dataBuffer));
//                }
//            }
//            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(exchange.getResponse()) {
//                @Override
//                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
//                    return DataBufferUtils.join(Flux.from(body))
//                            .flatMap(dataBuffer -> {
//                                byte[] out = new byte[dataBuffer.readableByteCount()];
//                                dataBuffer.read(out);
//                                DataBufferUtils.release(dataBuffer);
//
//                                // Save the response body in cache
//                                ops.set(cacheKey, out, Duration.ofSeconds(config.getValue()));
//
//                                // Release memory and write response to the client
//                                return super.writeWith(Flux.just(exchange.getResponse().bufferFactory().wrap(out)));
//                            });
//                }
//            };
//
//            return chain.filter(exchange.mutate().response(decoratedResponse).build());
//        };
//
//    }
//    private String generateCacheKey(ServerWebExchange exchange) {
//        String path = exchange.getRequest().getURI().getPath();
//        String query = exchange.getRequest().getQueryParams().toString();
//        return path + "?" + query;
//    }
//
//    @Getter
//    @Setter
//    public static class Config {
//        private int value = 60;
//        private String key = "";
//    }
//}