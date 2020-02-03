package example.letter.service;

import io.netty.buffer.Unpooled;
import io.rsocket.AbstractRSocket;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public class LetterService {
    private static final Logger LOG = LoggerFactory.getLogger(LetterService.class);

    public static void main(String... args) throws Exception {
        // Connect to the Number Service
        RSocket rSocket = RSocketFactory.connect()
                .transport(TcpClientTransport.create(7002))
                .start()
                .block();

        // Start the Fast Letter Service
        RSocketFactory.receive()
                .frameDecoder(PayloadDecoder.DEFAULT)
                .acceptor(new SocketAcceptor() {
                    @Override
                    public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {
                        return Mono.just(new AbstractRSocket() {
                            @Override
                            public Flux<Payload> requestStream(Payload payload) {
                                return Flux.range(1, Integer.MAX_VALUE)
                                        .doOnRequest(value -> {
                                            LOG.info("Received Request For: {}", value);
                                        })
                                        .map(integer -> RandomStringUtils.randomAlphabetic(1))
                                        .zipWith(rSocket.requestStream(DefaultPayload.create(Unpooled.EMPTY_BUFFER)))
                                        .map(objects -> {
                                            byte[] bytes = new byte[objects.getT2().data().readableBytes()];
                                            objects.getT2().data().readBytes(bytes);

                                            LOG.info("Sending: {}", objects.getT1() + new BigInteger(bytes).intValue());

                                            return DefaultPayload.create(objects.getT1() + new BigInteger(bytes).intValue());
                                        });
                            }
                        });
                    }
                })
                .transport(TcpServerTransport.create(7001))
                .start()
                .block();

        LOG.info("RSocket server started on port: 7001");

        Thread.currentThread().join();
    }
}
