package com.keyin;

import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;






public class ClientTests {

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(8081);
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());
    }

    @Test
    public void testCreateAircraft() {
        wireMockServer.resetAll();

        stubFor(post(urlEqualTo("/api/aircraft"))
                .withHeader("Content-Type", matching("application/json"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 27, \"type\": \"asd\", \"airlineName\": \"asd\", \"numberOfPassengers\": 2}")
                ));

        try {
            String response = ServerClient.performPostRequest("http://localhost:8081/api/aircraft", "{\"type\":\"asd\",\"airlineName\":\"asd\",\"numberOfPassengers\":2}");

            assertEquals("{\"id\": 27, \"type\": \"asd\", \"airlineName\": \"asd\", \"numberOfPassengers\": 2}", response);

            verify(postRequestedFor(urlEqualTo("/api/aircraft"))
                    .withHeader("Content-Type", matching("application/json"))
                    .withRequestBody(matching(".*\"type\":\"asd\".*"))
                    .withRequestBody(matching(".*\"airlineName\":\"asd\".*"))
                    .withRequestBody(matching(".*\"numberOfPassengers\":2.*")));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testReadAircraft() {
        wireMockServer.resetAll();

        stubFor(get(urlEqualTo("/api/aircraft/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"type\": \"xyz\", \"airlineName\": \"xyz\", \"numberOfPassengers\": 3}")
                ));


        try {
            String response = ServerClient.performGetRequest("http://localhost:8081/api/aircraft/1");
            assertEquals("{\"id\": 1, \"type\": \"xyz\", \"airlineName\": \"xyz\", \"numberOfPassengers\": 3}", response);
            verify(getRequestedFor(urlEqualTo("/api/aircraft/1")));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

@Test
    public void testUpdateAircraft() {
    wireMockServer.resetAll();

    stubFor(put(urlEqualTo("/api/aircraft/1"))
            .withHeader("Content-Type", matching("application/json"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\": 1, \"type\": \"newType\", \"airlineName\": \"newName\", \"numberOfPassengers\": 5}")
            ));


    try {
        String response = ServerClient.performPutRequest("http://localhost:8081/api/aircraft/1", "{\"type\":\"newType\",\"airlineName\":\"newName\",\"numberOfPassengers\":5}");
        assertEquals("{\"id\": 1, \"type\": \"newType\", \"airlineName\": \"newName\", \"numberOfPassengers\": 5}", response);
        verify(putRequestedFor(urlEqualTo("/api/aircraft/1"))
            .withHeader("Content-Type", matching("application/json"))
            .withRequestBody(matching(".*\"type\":\"newType\".*"))
            .withRequestBody(matching(".*\"airlineName\":\"newName\".*"))
            .withRequestBody(matching(".*\"numberOfPassengers\":5.*")));
    } catch (IOException | InterruptedException e) {
        e.printStackTrace();
    }

}
    @Test
    public void testDeleteAircraft() {
        try {
            String deleteResponse = ServerClient.performDeleteRequest("http://localhost:8080/api/aircraft/547");
            assertEquals("Aircraft with ID 547 not found.", deleteResponse);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }
}
