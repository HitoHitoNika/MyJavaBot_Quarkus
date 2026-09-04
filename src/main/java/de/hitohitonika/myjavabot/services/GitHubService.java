package de.hitohitonika.myjavabot.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;

@ApplicationScoped
public class GitHubService {
    private final GitHub gitHub;
    private final String username;

    public GitHubService(
            @ConfigProperty(name = "github.username") String username,
            @ConfigProperty(name = "github.token") String token) throws IOException {
        this.username = username;
        this.gitHub = new GitHubBuilder().withOAuthToken(token).build();
        this.gitHub.checkApiUrlValidity();
    }

    public GHIssue createIssue(String title, String body, String repository) throws IOException {
        GHRepository repo = gitHub.getRepository(username + "/" + repository);

        return repo.createIssue(title).body(body).create();
    }
}
