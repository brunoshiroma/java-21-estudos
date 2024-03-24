package com.brunoshiroma.java.vinte.um.estudos;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Teste do novo StructuredTaskScope , utilizando Virtual Threads.
 * Analisando os logs de execução do teste, é possivel notar a concorrencia e o uso das Virtual Threads e threads
 * @see <a href="https://openjdk.org/jeps/453">JEPS453</a>
 * @see <a href="https://www.inf.puc-rio.br/~noemi/pcp-16/aula1/aula1.pdf">Programação Concorrente e Paralela. Noemi Rodriguez 2016</a>
 */
class StructuredConcurrencyTest {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private final Logger logger = Logger.getLogger(StructuredConcurrencyTest.class.getName());

    //repetindo só para ajudar a ver as diferentes Virtual Threads rodando
    @RepeatedTest(2)
    void test() throws InterruptedException, ExecutionException {
        logaQuantidadeThreadsAtivas();
        //cria um scope com green threads
        try (final var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            Supplier<HttpResponse<String>> temperatura = scope.fork(this::buscaTemperatura);
            Supplier<HttpResponse<String>> cotacaoUSD = scope.fork(this::buscaCotacaoDolar);

            logger.info("Fazendo join nas tasks em " + Instant.now());
            logaQuantidadeThreadsAtivas();
            scope
                    .join()
                    .throwIfFailed();
            logaQuantidadeThreadsAtivas();

            logger.info("Validando resultados em " + Instant.now());
            Assertions.assertNotNull(temperatura.get().body());
            Assertions.assertNotNull(cotacaoUSD.get().body());
        }
        logaQuantidadeThreadsAtivas();
    }

    HttpResponse<String> buscaTemperatura() throws IOException, InterruptedException {
        logger.info(Thread.currentThread().toString());
        final var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.open-meteo.com/v1/forecast?latitude=-23.5475&longitude=-46.6361&hourly=temperature_2m&forecast_days=1"))
                .build();
        logger.info("Fazendo request api de temperatura em " + Instant.now());
        final var resultado = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        logger.info("Feito a request api de temperatura em " + Instant.now());
        return resultado;
    }

    HttpResponse<String> buscaCotacaoDolar() throws IOException, InterruptedException {
        logger.info(Thread.currentThread().toString());
        final var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://economia.awesomeapi.com.br/json/last/USD-BRL"))
                .build();
        logger.info("Fazendo request api de cotação USD em " + Instant.now());
        final var resultado = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        logger.info("Feito a request api de cotação USD em " + Instant.now());
        return resultado;
    }

    void logaQuantidadeThreadsAtivas () {
        logger.info(String.valueOf(Thread.activeCount()));
    }

}
