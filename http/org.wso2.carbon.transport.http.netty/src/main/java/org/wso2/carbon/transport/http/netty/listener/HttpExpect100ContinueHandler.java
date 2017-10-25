package org.wso2.carbon.transport.http.netty.listener;

/**
 * Created by anjana on 9/15/17.
 */
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.wso2.carbon.transport.http.netty.contract.ServerConnectorFuture;
import org.wso2.carbon.transport.http.netty.message.HTTPCarbonMessage;


/**
 * Created by anjana on 9/8/17.
 */
public class HttpExpect100ContinueHandler extends SourceHandler {

    HTTPCarbonMessage cMsg = null;
    boolean is100Continue = false;
    public HttpExpect100ContinueHandler(ServerConnectorFuture serverConnectorFuture, String interfaceId)
            throws Exception {
        super(serverConnectorFuture, interfaceId);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            HttpHeaders headers = request.headers();
            if (headers.contains("Expect")) {
                String s = "";
                DefaultFullHttpResponse rsp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                                                          HttpResponseStatus.CONTINUE, Unpooled
                                                                                  .wrappedBuffer(s.getBytes("UTF-8")));
                ctx.writeAndFlush(rsp);
                cMsg = setupCarbonMessage(request);
                is100Continue = true;
            } else {
                super.channelRead(ctx, msg);
            }
        } else if (msg instanceof HttpContent && is100Continue) {
            ByteBuf content = ((HttpContent) msg).content();
            cMsg.addHttpContent(new DefaultLastHttpContent(content));
            cMsg.setEndOfMsgAdded(true);
            notifyRequestListener(cMsg, ctx);
        } else {
            super.channelRead(ctx, msg);
        }
    }
}
