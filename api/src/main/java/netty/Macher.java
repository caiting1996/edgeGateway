package netty;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelMatcher;

public class Macher implements ChannelMatcher {
    Channel id;
    public Macher(Channel id) {
        this.id = id;
    }

   // @Override
    public boolean matches(Channel channel) {
        return channel.equals(this.id);
    }

}
