package me.rotemfo.grpc.cache.server;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import me.rotemfo.grpc.cache.service.CacheServiceGrpc;
import me.rotemfo.grpc.cache.service.CacheServiceProtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class CacheServiceImpl extends CacheServiceGrpc.CacheServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(CacheServiceImpl.class);

    private static final ConcurrentHashMap<String, ByteString> cache = new ConcurrentHashMap<>();
    private static final CacheServiceProtos.SetResponse setRespone = CacheServiceProtos.SetResponse.newBuilder().build();
    private static final CacheServiceProtos.ResetResponse resetRespone = CacheServiceProtos.ResetResponse.newBuilder().setReset(true).build();

    @Override
    public void get(CacheServiceProtos.GetRequest request, StreamObserver<CacheServiceProtos.GetResponse> responseObserver) {
        final String key = request.getKey();
        logger.info("got GetRequest with key {}", key);
        ByteString value = cache.get(key);
        if (value == null) value = ByteString.EMPTY;
        responseObserver.onNext(CacheServiceProtos.GetResponse.newBuilder().setValue(value).build());
        responseObserver.onCompleted();
    }

    @Override
    public void set(CacheServiceProtos.SetRequest request, StreamObserver<CacheServiceProtos.SetResponse> responseObserver) {
        final String key = request.getKey();
        final ByteString value = request.getValue();
        logger.info("got SetRequest with {}:{}", key, value.toStringUtf8());
        cache.put(key, value);
        responseObserver.onNext(setRespone);
        responseObserver.onCompleted();
    }

    @Override
    public void getByKeyPattern(CacheServiceProtos.GetByKeyPatternRequest request, StreamObserver<CacheServiceProtos.GetByKeyPatternResponse> responseObserver) {
        final String keyPattern = request.getPattern();
        logger.info("got GetByKeyPatternRequest with {}", keyPattern);
        final Pattern pattern = Pattern.compile(keyPattern);
        Set<String> keys = cache.keySet().stream().filter(k -> pattern.matcher(k).matches()).collect(Collectors.toSet());
        keys.forEach(k -> {
            responseObserver.onNext(CacheServiceProtos.GetByKeyPatternResponse.newBuilder().setKey(k).setValue(cache.get(k)).build());
        });
        responseObserver.onCompleted();
    }

    @Override
    public void reset(CacheServiceProtos.ResetRequest request, StreamObserver<CacheServiceProtos.ResetResponse> responseObserver) {
        logger.info("got ResetRequest");
        cache.clear();
        responseObserver.onNext(resetRespone);
        responseObserver.onCompleted();
    }
}

