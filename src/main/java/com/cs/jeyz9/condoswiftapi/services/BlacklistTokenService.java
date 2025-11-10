package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.models.BlackListToken;
import com.cs.jeyz9.condoswiftapi.repository.BlacklistTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BlacklistTokenService {
    private final BlacklistTokenRepository repository;
    
    @Autowired
    public BlacklistTokenService(BlacklistTokenRepository repository) {
        this.repository = repository;
    }

    public void blacklist(String token, LocalDateTime expiry) {
        BlackListToken bt = new BlackListToken();
        bt.setToken(token);
        bt.setExpiresAt(expiry);
        repository.save(bt);
    }

    public boolean isBlacklisted(String token) {
        return repository.existsByToken(token);
    }
}
