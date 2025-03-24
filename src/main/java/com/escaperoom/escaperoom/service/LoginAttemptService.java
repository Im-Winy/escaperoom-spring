package com.escaperoom.escaperoom.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.cache.LoadingCache;

import jakarta.servlet.http.HttpServletRequest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class LoginAttemptService {
    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS =5;
    private static final int ATTEMPT_INCREMENT =1;
    private LoadingCache<String,Integer> loginAttemptCache;
    
    @Autowired
    private HttpServletRequest request;
    

    public LoginAttemptService() {
        super();
        loginAttemptCache = 
        		CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS)
                .build(
                        new CacheLoader<String, Integer>() {
                            @Override
                            public Integer load(final String key) {
                                return 0;
                            }
                        }
                );
    }

    public void addUserToLoginAttemptCache(String username) {
        int attempts =0;

        try {
            attempts = ATTEMPT_INCREMENT +loginAttemptCache.get(username);

        } catch (ExecutionException e) {
           e.printStackTrace();
        }
        loginAttemptCache.put(username,attempts);

    }
    public boolean hasExceededMaxAttempts(String username){
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;  //Echec de connexion
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;   //Connexion OK
    }

    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }
    

	public void loginFailed(final String key) {
		
		int attempts;
		try {
			
			attempts = loginAttemptCache.get(key);
			
		} catch (final ExecutionException e) {
			
			attempts = 0;
		}
		
		attempts ++;
		loginAttemptCache.put(key, attempts);
		
	}
	
	public boolean isBlocked() {
		
		try {
			return loginAttemptCache.get(getClientIP()) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
			
		} catch (final ExecutionException e) {

			return false;
		}
		
	}
	
	private String getClientIP() {
		
		final String xfHeader = request.getHeader("X-Forwarded-For");
		
		if(xfHeader != null) {
			
			return xfHeader.split(",")[0];
			
		}
		return request.getRemoteAddr();
	}
}