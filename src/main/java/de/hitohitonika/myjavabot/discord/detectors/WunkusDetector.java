package de.hitohitonika.myjavabot.discord.detectors;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
@Startup
public class WunkusDetector extends ListenerAdapter {
    public final static List<String> CAT_GIF_URLS = List.of(
            "https://tenor.com/view/aa-uh-uh-uh-gif-11541220613235213729",
            "https://tenor.com/view/uuh-cat-funny-cats-uhh-gif-5575392758585270122",
            "https://tenor.com/view/cat-meme-pee-cat-pee-funny-lmfao-gif-14727908981812019274",
            "https://tenor.com/view/buh-gif-14465458220549268962",
            "https://tenor.com/view/cat-gif-496001393010401237",
            "https://tenor.com/view/cat-flash-camera-flashbang-gif-14820120581461492980",
            "https://tenor.com/view/larry-larry-cat-chat-larry-meme-chat-meme-cat-gif-10061556685042597078",
            "https://tenor.com/view/cat-laughing-meme-gif-8665530932329511839",
            "https://tenor.com/view/orange-cat-stare-funny-gif-26367587",
            "https://tenor.com/view/kiss-gif-11816971814746635421",
            "https://tenor.com/view/4evil-jabroni-mike-jmike-gif-3285609652059000679",
            "https://tenor.com/view/catsena-gif-13142271985268802464",
            "https://tenor.com/view/cuh-cuh-cat-cat-meme-cat-tiktok-gif-3023015301402154829",
            "https://tenor.com/view/kataman-cat-gif-10580123519733406458",
            "https://tenor.com/view/cat-meng-cat-meme-meme-giggling-meng-gif-1852422441948846398",
            "https://tenor.com/view/glorp-outer-space-gif-988837663270596515",
            "https://tenor.com/view/choy-cat-meme-tik-tok-choi-gif-13813246108286362253"
    );
    private static String lastCatGifUrl = null;
    private final AtomicInteger counter = new AtomicInteger(0);

    public WunkusDetector(
            JDA jda
    ) {
        jda.addEventListener(this);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent messageReceivedEvent) {
        if (messageReceivedEvent.getAuthor().getId().equals("306792892373139456")) {
            var currentCounter = counter.incrementAndGet();
            if (currentCounter >= 5) {
                counter.set(0);
                messageReceivedEvent.getChannel()
                        .sendMessage(selectRandomCatGif())
                        .queue();
            }
        }
    }

    private String selectRandomCatGif() {
        List<String> possibleGifs = new ArrayList<>(CAT_GIF_URLS);
        if (lastCatGifUrl != null) {
            possibleGifs.remove(lastCatGifUrl);
        }

        Random rand = new Random();
        String newGifUrl = possibleGifs.get(rand.nextInt(possibleGifs.size()));

        lastCatGifUrl = newGifUrl;
        return newGifUrl;
    }
}
