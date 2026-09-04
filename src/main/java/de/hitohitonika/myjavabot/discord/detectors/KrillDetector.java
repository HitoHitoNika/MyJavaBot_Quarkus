package de.hitohitonika.myjavabot.discord.detectors;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@ApplicationScoped
@Startup
public class KrillDetector extends ListenerAdapter {

    public KrillDetector(JDA jda) {
        jda.addEventListener(this);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent messageReceivedEvent) {
        if (messageReceivedEvent.getAuthor().isBot() || !messageReceivedEvent.getMessage().getContentRaw().toLowerCase().contains("krill"))
            return;

        messageReceivedEvent.getChannel()
                .sendMessage("https://tenor.com/view/you-should-low-tier-god-ltg-krill-one-in-a-krillion-gif-7939027577238167153")
                .queue();
    }


}

