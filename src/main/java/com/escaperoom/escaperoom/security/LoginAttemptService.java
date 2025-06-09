package com.escaperoom.escaperoom.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

	private static final int MAX_ATTEMPT = 5;
	private final LoadingCache<String, Integer> attemptsCache;

	public LoginAttemptService() {
		this.attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES)
				.build(new CacheLoader<>() {
					public Integer load(String key) {
						return 0;
					}
				});
	}

	public void loginFailed(String key) {
		int attempts = 0;
		try {
			attempts = attemptsCache.get(key);
		} catch (ExecutionException e) {
			
		}
		attempts++;
		attemptsCache.put(key, attempts);
	}

	public void loginSucceeded(String key) {
		attemptsCache.invalidate(key);
	}

	public boolean isBlocked(String key) {
		try {
			return attemptsCache.get(key) >= MAX_ATTEMPT;
		} catch (ExecutionException e) {
			return false;
		}
	}
}
