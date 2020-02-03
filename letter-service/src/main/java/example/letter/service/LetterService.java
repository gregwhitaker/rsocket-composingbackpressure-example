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
                                return rSocket.requestStream(DefaultPayload.create(Unpooled.EMPTY_BUFFER))
                                        .doOnRequest(value -> {
                                            LOG.info("Received Request For: {}", value);
                                        })
                                        .map(numPayload -> {
                                            byte[] bytes = new byte[numPayload.data().readableBytes()];
                                            numPayload.data().readBytes(bytes);

                                            return new BigInteger(bytes).intValue();
                                        })
                                        .map(integer -> RandomStringUtils.randomAlphabetic(1) + integer)
                                        .map(s -> {
                                            LOG.info("Sending: {}", s);
                                            return DefaultPayload.create(s);
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
