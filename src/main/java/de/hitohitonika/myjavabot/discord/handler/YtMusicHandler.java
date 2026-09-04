package de.hitohitonika.myjavabot.discord.handler;

import de.hitohitonika.myjavabot.services.DebtServiceClient;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.util.Arrays;

@ApplicationScoped
@Startup
public class YtMusicHandler extends ListenerAdapter {
    private final static String DEBT_URL_MESSAGE = """
            Neuer Monat, neue Rechnung :)
            <@&1049289887146319952>
            https://ytmusic.hitohitonika.de/debts
            """;

    private final static String SPECIFIC_MESSAGE = "Dein Link: https://ytmusic.hitohitonika.de/debts?name=%s";

    private final String wunkusId;
    private final String hamhamId;
    private final String maxId;

    private final DebtServiceClient debtServiceClient;

    private TextChannel guildChannel;

    public YtMusicHandler(
            JDA jda,
            @ConfigProperty(name = "discord.id.humunkulus") String wunkus,
            @ConfigProperty(name = "discord.id.hamham") String hamham,
            @ConfigProperty(name = "discord.id.max") String max,
            @ConfigProperty(name = "discord.id.lobby1") String lobbyEinsId,
            @ConfigProperty(name = "discord.id.lobby1.channel.ytmusic") String ytmusicChannelId,
            @RestClient DebtServiceClient debtServiceClient
    ) {
        jda.addEventListener(this);

        this.wunkusId = wunkus;
        this.hamhamId = hamham;
        this.maxId = max;
        this.debtServiceClient = debtServiceClient;

        var guild = jda.getGuildById(lobbyEinsId);
        if (guild != null) {
            this.guildChannel = jda.getTextChannelById(ytmusicChannelId);
        }
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("ytmusic")) {
            event.deferReply().queue();
            if (event.getUser().getId().equals(wunkusId)) {
                event.getHook().sendMessage(SPECIFIC_MESSAGE.formatted("Lucas")).queue();
            } else if (event.getUser().getId().equals(hamhamId)) {
                event.getHook().sendMessage(SPECIFIC_MESSAGE.formatted("Hamed")).queue();
            }
            event.getHook().sendMessage(DEBT_URL_MESSAGE).queue();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().getId().equals(maxId) && event.getMessage().getContentRaw().startsWith("###pay:")) {
            var messageParts = event.getMessage().getContentRaw().split(":", 3);

            if (messageParts.length != 3) {
                return;
            }

            Log.info(Arrays.toString(messageParts));

            String name = messageParts[1].trim();
            String amount = messageParts[2].trim();

            debtServiceClient.payDebt(name, new BigDecimal(amount))
                    .subscribe().with(
                            response -> event.getChannel().sendMessage(response.toString()).queue(),
                            throwable -> event.getChannel().sendMessage("Wallah hier hats gekracht " + throwable.getMessage()).queue()
                    );
        }
    }

    @Scheduled(cron = "0 0 5 1 * ?")
    public void sendReminder() {
        guildChannel.sendMessage(DEBT_URL_MESSAGE).queue();
    }
}
