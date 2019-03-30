package me.rotemfo.grpc.cache.client;

import com.google.protobuf.ByteString;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import me.rotemfo.grpc.cache.service.CacheServiceGrpc;
import me.rotemfo.grpc.cache.service.CacheServiceProtos;

import java.util.Iterator;

public class CacheServiceClient {

    private final Channel channel;
    private final CacheServiceGrpc.CacheServiceBlockingStub stub;

    public CacheServiceClient(final int port) {
        channel = ManagedChannelBuilder.forAddress("0.0.0.0", port).usePlaintext().build();
        stub = CacheServiceGrpc.newBlockingStub(channel);
    }

    public CacheServiceProtos.GetResponse get(final String key) {
        return stub.get(CacheServiceProtos.GetRequest.newBuilder().setKey(key).build());
    }

    public CacheServiceProtos.SetResponse set(final String key, final String value) {
        return this.set(key, value.getBytes());
    }

    public CacheServiceProtos.SetResponse set(final String key, final byte[] value) {
        return this.set(key, ByteString.copyFrom(value));
    }

    public CacheServiceProtos.SetResponse set(final String key, final ByteString value) {
        return stub.set(CacheServiceProtos.SetRequest.newBuilder().setKey(key).setValue(value).build());
    }

    public Iterator<CacheServiceProtos.GetByKeyPatternResponse> getByKeyPattern(final String pattern) {
        return stub.getByKeyPattern(CacheServiceProtos.GetByKeyPatternRequest.newBuilder().setPattern(pattern).build());
    }

    public CacheServiceProtos.ResetResponse reset() {
        return stub.reset(CacheServiceProtos.ResetRequest.newBuilder().build());
    }
}
