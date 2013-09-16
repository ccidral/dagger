package dagger.server.netty;

import io.netty.util.AttributeKey;

public class ChannelAttributes {

    public static final AttributeKey<Long> CHANNEL_ID = new AttributeKey<>("dagger-channel-id");

}
