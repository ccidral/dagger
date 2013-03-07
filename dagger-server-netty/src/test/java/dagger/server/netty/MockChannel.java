package dagger.server.netty;

import dagger.lang.NotImplementedYet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.MessageBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.SocketAddress;

public class MockChannel implements Channel {

    public final ByteBuf outputBuffer = Unpooled.buffer();

    @Override
    public Integer id() {
        throw new NotImplementedYet();
    }

    @Override
    public EventLoop eventLoop() {
        throw new NotImplementedYet();
    }

    @Override
    public Channel parent() {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelConfig config() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isOpen() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isRegistered() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isActive() {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelMetadata metadata() {
        throw new NotImplementedYet();
    }

    @Override
    public ByteBuf outboundByteBuffer() {
        return outputBuffer;
    }

    @Override
    public <T> MessageBuf<T> outboundMessageBuffer() {
        throw new NotImplementedYet();
    }

    @Override
    public SocketAddress localAddress() {
        throw new NotImplementedYet();
    }

    @Override
    public SocketAddress remoteAddress() {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture closeFuture() {
        throw new NotImplementedYet();
    }

    @Override
    public Unsafe unsafe() {
        throw new NotImplementedYet();
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> tAttributeKey) {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture bind(SocketAddress socketAddress) {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress) {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress1) {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture disconnect() {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture close() {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture deregister() {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture flush() {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture write(Object o) {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture sendFile(FileRegion fileRegion) {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture bind(SocketAddress socketAddress, ChannelPromise channelPromise) {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, ChannelPromise channelPromise) {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress1, ChannelPromise channelPromise) {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise channelPromise) {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture close(ChannelPromise channelPromise) {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture deregister(ChannelPromise channelPromise) {
        throw new NotImplementedYet();
    }

    @Override
    public void read() {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture flush(ChannelPromise channelPromise) {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture write(Object o, ChannelPromise channelPromise) {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture sendFile(FileRegion fileRegion, ChannelPromise channelPromise) {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelPipeline pipeline() {
        throw new NotImplementedYet();
    }

    @Override
    public ByteBufAllocator alloc() {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelPromise newPromise() {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        throw new NotImplementedYet();
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable throwable) {
        throw new NotImplementedYet();
    }

    @Override
    public int compareTo(Channel o) {
        throw new NotImplementedYet();
    }
}
