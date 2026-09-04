package de.hitohitonika.myjavabot.discord.detectors;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
@Startup
public class SoitzuDetector extends ListenerAdapter {

    private final AtomicInteger soitzuCounter = new AtomicInteger(0);

    private final String soitzuId;


    public SoitzuDetector(
            JDA jda,
            @ConfigProperty(name = "discord.id.soitzu") String soitzuId
    ) {
        this.soitzuId = soitzuId;
        jda.addEventListener(this);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent messageReceivedEvent) {
        if (messageReceivedEvent.getAuthor().getId().equals(soitzuId)) {
            var currentCounter = soitzuCounter.incrementAndGet();
            if (currentCounter % 10 == 0) {
                soitzuCounter.set(0);
                messageReceivedEvent
                        .getChannel()
                        .sendMessage("Nikita halt doch bitte mal dein Maul")
                        .queue();
            }
        }
    }
}

