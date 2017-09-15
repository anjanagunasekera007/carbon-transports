package org.wso2.carbon.transport.http.netty.Expect100Handler;

import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.transport.http.netty.common.Constants;
import org.wso2.carbon.transport.http.netty.config.TransportsConfiguration;
import org.wso2.carbon.transport.http.netty.config.YAMLTransportConfigurationBuilder;
import org.wso2.carbon.transport.http.netty.contract.HttpClientConnector;
import org.wso2.carbon.transport.http.netty.contract.HttpResponseFuture;
import org.wso2.carbon.transport.http.netty.contract.ServerConnector;
import org.wso2.carbon.transport.http.netty.https.HTTPSClientTestCase;
import org.wso2.carbon.transport.http.netty.https.HTTPSConnectorListener;
import org.wso2.carbon.transport.http.netty.message.HTTPCarbonMessage;
import org.wso2.carbon.transport.http.netty.passthrough.PassthroughMessageProcessorListener;
import org.wso2.carbon.transport.http.netty.util.TestUtil;
import org.wso2.carbon.transport.http.netty.util.server.HttpServer;
import org.wso2.carbon.transport.http.netty.util.server.HttpsServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * Created by anjana on 9/12/17.
 */
public class Expect100TestCase {
    private final Logger log = LoggerFactory.getLogger(HTTPSClientTestCase.class);

    private HttpsServer httpsServer;
    private HttpClientConnector httpClientConnector;
    //    private int port = 9092;
    //private String testValue = "Test Message";
    private URI baseURI = URI.create(String.format("http://%s:%d", "localhost", 9000));
    private List<ServerConnector> serverConnectors;
    private static final String testValue = "Test Message";
    private HttpServer httpServer;

    @BeforeClass
    public void setup() {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

//        TransportsConfiguration transportsConfiguration =
//                TestUtil.getConfiguration("/simple-test-config" + File.separator + "netty-transports.yml");
//        httpsServer = TestUtil.startHttpsServer(TestUtil.TEST_HTTPS_SERVER_PORT, testValue, "text/plain");
//        HttpWsConnectorFactory connectorFactory = new HttpWsConnectorFactoryImpl();
//        httpClientConnector = connectorFactory.createHttpClientConnector(
//                HTTPConnectorUtil.getTransportProperties(transportsConfiguration),
//                HTTPConnectorUtil.getSenderConfiguration(transportsConfiguration, Constants.HTTPS_SCHEME));
        TransportsConfiguration configuration = YAMLTransportConfigurationBuilder
                .build("src/test/resources/simple-test-config/netty-transports.yml");
        serverConnectors = TestUtil.startConnectors(
                configuration, new PassthroughMessageProcessorListener(configuration));
        httpServer = TestUtil.startHTTPServer(TestUtil.TEST_HTTP_SERVER_PORT, testValue, Constants.TEXT_PLAIN);

    }

    @Test
    public void test100Expect() throws IOException{
//        try {
//            HttpURLConnection urlConn = request(baseURI, HttpMethod.POST.name(), true);
//            urlConn.setRequestProperty("Expect","100");
//
//
//            String t = urlConn.getResponseMessage();
//            String y = urlConn.getHeaderFieldKey(0);
//            String h = urlConn.getHeaderField("OK");
//            TestUtil.writeContent(urlConn,"contentxxx");
//            String content = TestUtil.getContent(urlConn);
//            urlConn.disconnect();
//        } catch (IOException e) {
//            TestUtil.handleException("IOException occurred while running test100Expect", e);
//        }

//        URL url = baseURI.resolve("/websocket").toURL();
//        baseURI = URI.create("http://localhost:9090/echo/add");
        URL url = baseURI.toURL();
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setRequestMethod("POST");
        urlConn.setRequestProperty("Expect","100-continue");//        urlConn.setRequestProperty("connection", "upgrade");
        urlConn.setRequestProperty("Content-Type","apication/json");
        urlConn.setRequestProperty("JSON","{\"name\":\"aaa\",\"username\":\"xyz\",\"password\":\"xyz\"}");
//        urlConn.setRequestProperty("upgrade", "websocket");
//        urlConn.setRequestProperty("Sec-WebSocket-Key", "dGhlIHNhbXBsZSBub25jZQ==");
//        urlConn.setRequestProperty("Sec-WebSocket-Version", "13");
        int t = urlConn.getResponseCode();
        String r = urlConn.getResponseMessage();
//        Assert.assertEquals(urlConn.getResponseCode(), 101);
//        Assert.assertEquals(urlConn.getResponseMessage(), "Switching Protocols");
//        Assert.assertEquals(urlConn.getHeaderField("upgrade"), "websocket");
//        Assert.assertEquals(urlConn.getHeaderField("connection"), "upgrade");
//        Assert.assertTrue(urlConn.getHeaderField("sec-websocket-accept") != null);
    }

    public static HttpURLConnection request(URI baseURI, String method, boolean keepAlive)
            throws IOException {
        URL url = baseURI.toURL();
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        if (method.equals(HttpMethod.POST.name()) || method.equals(HttpMethod.PUT.name())) {
            urlConn.setDoOutput(true);
        }
        urlConn.setRequestMethod(method);
        if (!keepAlive) {
            urlConn.setRequestProperty("Connection", "Keep-Alive");
        }

        return urlConn;
    }

    @Test
    public void testHttpsGet() {
        try {
            HTTPCarbonMessage msg = new HTTPCarbonMessage();
            msg.setProperty("PORT", TestUtil.TEST_HTTPS_SERVER_PORT);
            msg.setProperty("PROTOCOL", "http");
            msg.setProperty("HOST", "localhost");
            msg.setProperty("HTTP_METHOD", "POST");
            msg.setHeader("Expect","100");
            msg.setEndOfMsgAdded(true);

            CountDownLatch latch = new CountDownLatch(1);
            HTTPSConnectorListener listener = new HTTPSConnectorListener(latch);
            HttpResponseFuture responseFuture = httpClientConnector.send(msg);
            responseFuture.setHttpConnectorListener(listener);

            latch.await(5, TimeUnit.SECONDS);

            HTTPCarbonMessage response = listener.getHttpResponseMessage();
            assertNotNull(response);
            String res = response.getHeader("continue");
            assertEquals("100",res);

            String result = new BufferedReader(new InputStreamReader(response.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));

            assertEquals(testValue, result);
        } catch (Exception e) {
            TestUtil.handleException("Exception occurred while running httpsGetTest", e);
        }
    }
}
