package de.hitohitonika.myjavabot.discord;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@ApplicationScoped
public class JDAConfig {

    @Inject
    @ConfigProperty(name = "bot.token")
    String token;

    @Inject
    @ConfigProperty(name = "github.repos")
    List<String> repos;

    @Produces
    @ApplicationScoped
    public JDA jda() throws InterruptedException {
        var jda = JDABuilder.createLight(token, List.of(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .disableCache(CacheFlag.VOICE_STATE)
                .setAudioModuleConfig(null)
                .setActivity(Activity.competing("Soitzu wo bist du? :("))
                .build().awaitReady();

        jda.updateCommands().addCommands(
                Commands.slash("ytmusic", "Spuckt den Spotify Übersicht Link aus."),
                Commands.slash("spam", "Spammed die Person deiner Wahl voll.").addOptions(
                        List.of(
                                new OptionData(OptionType.USER, "target", "Ziel"),
                                new OptionData(OptionType.STRING, "message", "Die Nachricht für den Spam"),
                                new OptionData(OptionType.INTEGER, "amount", "Die Anzahl der Nachrichten")
                        )
                ),
                Commands.slash("report-issue", "Hier kannst du Probleme mit einer meiner Anwendungen melden :)").addOptions(
                        List.of(
                                new OptionData(OptionType.STRING, "anwendung", "Mit welcher Anwendung tritt das Problem auf? (Im Zweifel nimm einfach den MyJavaBot)", true).addChoices(
                                        repos.stream().map(val -> new Command.Choice(val, val)).toList()
                                ),
                                new OptionData(OptionType.STRING, "title", "Gib dem Problem ein kurzen, prägnanten und erklärenden Namen", true),
                                new OptionData(OptionType.STRING, "erwartung", "Wie sollte es eigentlich funktionieren?", true),
                                new OptionData(OptionType.STRING, "tatsächliches-verhalten", "Wie äußert sich das Fehlverhalten?", true)
                        )
                ),
                Commands.slash("request-feature", "Hier kannst du dir ein neues Feature wünschen").addOptions(
                        List.of(
                                new OptionData(OptionType.STRING, "anwendung", "Für welche Anwendung ist das Feature?", true).addChoices(
                                        repos.stream().map(val -> new Command.Choice(val, val)).toList()
                                ),
                                new OptionData(OptionType.STRING, "title", "Gib dem Feature ein kurzen, prägnanten und erklärenden Namen", true),
                                new OptionData(OptionType.STRING, "beschreibung", "Erkläre genau wie du dir vorstellst das dein Feature funktionieren soll", true)
                        )
                )
        ).queue(commands -> Log.infof("REGISTRIERUNG ABGESCHLOSSEN: %s", commands));
        return jda;

    }
}
