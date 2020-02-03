package example.client;

import io.netty.buffer.Unpooled;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class Client {
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    public static void main(String... args) throws Exception {
        RSocket rSocket = RSocketFactory.connect()
                .transport(TcpClientTransport.create(7001))
                .start()
                .block();

        CountDownLatch latch = new CountDownLatch(1);

        rSocket.requestStream(DefaultPayload.create(Unpooled.EMPTY_BUFFER))
                .limitRate(10)  // limit the count service to emitting 10 values at a time
                .doOnComplete(() -> {
                    LOG.info("Done");
                    latch.countDown();
                })
                .subscribe(payload -> {
                    LOG.info("Received: {}", payload.getDataUtf8());
                });

        latch.await();
    }
}
