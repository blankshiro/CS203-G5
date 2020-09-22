package com.cs203t5.ryverbank.token;

import org.springframework.stereotype.Service;

@Service
public class ConfirmationTokenService {
    private ConfirmationTokenRepository tokenRepo;

    public void saveToken(ConfirmationToken confirmationToken) {
        tokenRepo.save(confirmationToken);
    }

    public void deleteToken(String id) {
        tokenRepo.deleteById(id);
    }
}
