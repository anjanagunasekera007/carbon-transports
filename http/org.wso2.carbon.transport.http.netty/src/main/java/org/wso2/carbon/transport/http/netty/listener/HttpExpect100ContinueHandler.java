package org.wso2.carbon.transport.http.netty.listener;

/**
 * Created by anjana on 9/15/17.
 */
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.wso2.carbon.transport.http.netty.contract.ServerConnectorFuture;
import org.wso2.carbon.transport.http.netty.message.HTTPCarbonMessage;


//**
// * Created by anjana on 9/8/17.
// */
public class HttpExpect100ContinueHandler extends SourceHandler {

    HTTPCarbonMessage cMsg = null;
    boolean is100Continue = false;
    public HttpExpect100ContinueHandler(ServerConnectorFuture serverConnectorFuture, String interfaceId)
            throws Exception {
        super(serverConnectorFuture, interfaceId);
    }

//
//    public static void main(String[] args) {
//        HttpWsConnectorFactory factory = new HttpWsConnectorFactoryImpl();
//        ListenerConfiguration listenerConfig = ListenerConfiguration.getDefault();
//        listenerConfig.setPort(9000);
//        ServerConnector connector = factory.createServerConnector(
//                ServerBootstrapConfiguration.getInstance(), listenerConfig);
//        ServerConnectorFuture future = connector.start();
//        future.setHttpConnectorListener(new HttpConnectorListener() {
//            @Override
//            public void onMessage(HTTPCarbonMessage httpMessage) {
//                System.out.println("Received: " + httpMessage);
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//
//            }
//        });
//
//    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {
            System.out.println("Http request");
//            HttpRequest request = (HttpRequest) msg;
        }

        if (msg instanceof FullHttpMessage) {
            System.out.println("Http Full mesage");
//            FullHttpRequest httpRequest = (FullHttpRequest) msg;
//            FullHttpMessage httpMessage = (FullHttpMessage) msg;
//            FullHttpRequest httpRequest1 = new Fu
        }
        if (msg instanceof HttpContent) {
            System.out.println("Http content");
            HttpContent content = (HttpContent) msg;


//            HttpContent content = (HttpContent) msg;
        }
        if (msg instanceof FullHttpRequest) {
            System.out.println(" HTTP REQUEST");
        }



//        if (msg instanceof HttpRequest) {
//            HttpRequest request = (HttpRequest) msg;
//            HttpHeaders headers = request.headers();
//            if (headers.contains("Expect")) {
//                System.out.println("=========================== Expect ON TOP Found ===========================");
//                String s = "";
//                FullHttpResponse rsp;
//                rsp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE, Unpooled
//                        .wrappedBuffer(s.getBytes("UTF-8")));
//                System.out.println("WROTE TO CTX-  - - - -     -2");
//                ctx.writeAndFlush(rsp);
//                System.out.println(" LOL ");
//                return;
//            } else {
//                super.channelRead(ctx, msg);
//            }
//        } else {
//            super.channelRead(ctx, msg);
//        }


        //==================================================================
        if (msg instanceof HttpRequest) {
            System.out.println(" : : : : : " + "instance of http request " + " : : : : : : : : ");
            HttpRequest request = (HttpRequest) msg;
            HttpHeaders headers = request.headers();
            boolean bb = headers.contains("Expect");
            System.out.println(bb);
            if (headers.contains("Expect")) {
                System.out.println(" ================ found Expect 100 - continue --------------------------");
                String s = "";
                DefaultFullHttpResponse rsp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                                                          HttpResponseStatus.CONTINUE, Unpooled
                                                                                  .wrappedBuffer(s.getBytes("UTF-8")));
                ctx.writeAndFlush(rsp);
                cMsg = setupCarbonMessage(request);
                is100Continue = true;

                System.out.println(cMsg);
            } else {
                System.out.println(" : : : : : " + " NO 100 CONTINUE ALTHOUGH A http request " + " : : : : : : : : ");

                super.channelRead(ctx, msg);
            }
        } else if (msg instanceof HttpContent && is100Continue) {
            System.out.println(" : : : : : " + " IS HTTP CONTENT : : ");

            System.out.println("=============== Content recieved =====================");
            ByteBuf c = ((HttpContent) msg).content();
            System.out.println(c.toString());
            ByteBuf content = ((HttpContent) msg).content();

            cMsg.addHttpContent(new DefaultLastHttpContent(content));
            cMsg.setEndOfMsgAdded(true);
            notifyRequestListener(cMsg, ctx);
        } else {
            super.channelRead(ctx, msg);
        }



//
//        if (msg instanceof FullHttpMessage) {
//            FullHttpMessage fullHttpMessage = (FullHttpMessage) msg;
//            cMsg = setupCarbonMessage(fullHttpMessage);
//            publishToMessageProcessor(cMsg);
//            ByteBuf content = ((FullHttpMessage) msg).content();
//            cMsg.addHttpContent(new DefaultLastHttpContent(content));
//            cMsg.setEndOfMsgAdded(true);
//            if (HTTPTransportContextHolder.getInstance().getHandlerExecutor() != null) {
//                HTTPTransportContextHolder.getInstance().getHandlerExecutor().executeAtSourceRequestSending(cMsg);
//            }

//        } else if (msg instanceof HttpRequest) {
//            HttpRequest httpRequest = (HttpRequest) msg;
//            cMsg = (HTTPCarbonMessage) setupCarbonMessage(httpRequest);
//            publishToMessageProcessor(cMsg);
//
//        } else {
//            if (cMsg != null) {
//                if (msg instanceof HttpContent) {
//                    HttpContent httpContent = (HttpContent) msg;
//                    cMsg.addHttpContent(httpContent);
//                    if (msg instanceof LastHttpContent) {
//                        cMsg.setEndOfMsgAdded(true);
////                        if (HTTPTransportContextHolder.getInstance().getHandlerExecutor() != null) {
////                            HTTPTransportContextHolder.getInstance().getHandlerExecutor().
////                                    executeAtSourceRequestSending(cMsg);
////                        }
//                    }
//                }
//            }
//        }
//    }
    }
}
