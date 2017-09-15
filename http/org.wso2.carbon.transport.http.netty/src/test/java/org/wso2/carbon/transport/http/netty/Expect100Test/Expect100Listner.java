package org.wso2.carbon.transport.http.netty.Expect100Handler;

import org.wso2.carbon.transport.http.netty.contract.HttpConnectorListener;
import org.wso2.carbon.transport.http.netty.message.HTTPCarbonMessage;

import java.util.concurrent.CountDownLatch;

/**
 * Created by anjana on 9/12/17.
 */
public class Expect100Listner implements HttpConnectorListener{

    private HTTPCarbonMessage httpMessage;
    private CountDownLatch latch;

    public Expect100Listner(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onMessage(HTTPCarbonMessage httpMessage) {
        this.httpMessage = httpMessage;
        latch.countDown();
    }

    @Override
    public void onError(Throwable throwable) {

    }

    public HTTPCarbonMessage getHttpResponseMessage() {
        return httpMessage;
    }
}


