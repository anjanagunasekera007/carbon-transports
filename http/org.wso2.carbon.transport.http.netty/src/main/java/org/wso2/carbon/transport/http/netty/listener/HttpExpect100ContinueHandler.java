package org.wso2.carbon.transport.http.netty.listener;

/**
 * Created by anjana on 9/15/17.
 */
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

//**
// * Created by anjana on 9/8/17.
// */
public class HttpExpect100ContinueHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("100 Continue Handler");
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            HttpHeaders headers = request.headers();
            if (headers.contains("Expect")) {
                System.out.println("=========================== Expect ON TOP Found ===========================");
                String s = "";
                FullHttpResponse rsp;
                rsp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE, Unpooled
                        .wrappedBuffer(s.getBytes("UTF-8")));
                ctx.writeAndFlush(rsp);
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }
}
