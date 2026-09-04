package de.hitohitonika.myjavabot.discord.detectors;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Random;

@ApplicationScoped
@Startup
public class TwitterLinkDetector extends ListenerAdapter {

    private final List<String> twitterLinks;

    public TwitterLinkDetector(
            JDA jda,
            @ConfigProperty(name = "discord.twitter.betterEmbeds") List<String> twitterLinks
    ) {
        this.twitterLinks = twitterLinks;
        jda.addEventListener(this);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent messageReceivedEvent) {
        String content = messageReceivedEvent.getMessage().getContentRaw();

        if (messageReceivedEvent.getAuthor().isBot() || !(content.toLowerCase().contains("https://twitter.com/") || content.toLowerCase().contains("https://x.com/")))
            return;

        Log.info("Received a twitter link: " + content.toLowerCase());

        messageReceivedEvent.getChannel().sendTyping().queue();

        String result = "<@%s>".formatted(messageReceivedEvent.getAuthor().getId()) + ": ";

        var index = new Random().nextInt(twitterLinks.size());
        String replacement = twitterLinks.get(index);

        if (content.contains("https://twitter.com/")) {
            content = content.replace("https://twitter.com/", replacement);
        } else if (content.contains("https://x.com/")) {
            content = content.replace("https://x.com/", replacement);
        }

        result += content;

        if (messageReceivedEvent.getChannelType() != ChannelType.PRIVATE) {
            messageReceivedEvent.getMessage().delete().queue();
        }
        messageReceivedEvent.getChannel().sendMessage(result).queue();

    }
}
