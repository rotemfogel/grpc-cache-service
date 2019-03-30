package me.rotemfo.grpc.cache.client;

import com.google.common.collect.Sets;
import com.google.protobuf.ByteString;
import me.rotemfo.grpc.cache.service.CacheServiceProtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final String a = "a";
    private static final String aa = "aa";
    private static final String b = "b";
    private static final String A = "A";
    private static final String AA = "AA";
    private static final String B = "B";
    private static final String p = "a*";

    public static void main(String[] args) {
        final CacheServiceClient client = new CacheServiceClient(8080);
        logger.info("sending ResetRequest");
        CacheServiceProtos.ResetResponse reset = client.reset();
        assert (reset != null);
        logger.info("sending GetRequest with {}", a);
        CacheServiceProtos.GetResponse get = client.get(a);
        assert (get.getValue().equals(ByteString.EMPTY));
        logger.info("get empty response for {}", a);
        logger.info("sending SetRequest with {}:{}", a, A);
        CacheServiceProtos.SetResponse set = client.set(a, A);
        assert (set != null);
        logger.info("sending GetRequest with {}", a);
        get = client.get(a);
        String value = get.getValue().toStringUtf8();
        assert (value.equals(A));
        logger.info("get {} response for {}", value, a);
        logger.info("sending SetRequest with {}:{}", aa, AA);
        client.set(aa, AA);
        logger.info("sending SetRequest with {}:{}", b, B);
        client.set(b, B);
        logger.info("sending GetRequest with {}", b);
        get = client.get(b);
        value = get.getValue().toStringUtf8();
        assert (value.equals(B));
        logger.info("get {} response for {}", value, b);
        logger.info("sending GetByKeyPatternResponse with {}", p);
        Set<CacheServiceProtos.GetByKeyPatternResponse> getby = Sets.newHashSet(client.getByKeyPattern(p));
        assert (getby.size() == 2);
        StringBuffer sb = new StringBuffer();
        getby.stream().forEach(e -> {
            final String v = e.getValue().toStringUtf8();
            sb.append(v).append(",");
            assert (v.equals(A) || v.equals(AA));
        });
        logger.info("got {} response for {}", sb.toString(), p);
    }
}