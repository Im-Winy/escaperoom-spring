package com.escaperoom.escaperoom.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;

    // Cache username -> nombre de tentatives échouées
    private final LoadingCache<String, Integer> attemptsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS) // expire au bout d'un jour
            .build(new CacheLoader<>() {
                @Override
                public Integer load(String key) {
                    return 0;
                }
            });

    // Incrémente le nombre de tentatives échouées
    public void loginFailed(String username) {
        int attempts = attemptsCache.getUnchecked(username);
        attemptsCache.put(username, attempts + 1);
    }

    // Réinitialise le compteur (après succès)
    public void loginSucceeded(String username) {
        attemptsCache.invalidate(username);
    }

    // Indique si un utilisateur a dépassé le nombre max de tentatives
    public boolean isBlocked(String username) {
        return attemptsCache.getUnchecked(username) >= MAX_ATTEMPTS;
    }
}
