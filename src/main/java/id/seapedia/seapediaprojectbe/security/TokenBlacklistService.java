package id.seapedia.seapediaprojectbe.security;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklistService {

    private final Set<String> blacklistedJtis = ConcurrentHashMap.newKeySet();

    public void blacklist(String jti) {
        blacklistedJtis.add(jti);
    }

    public boolean isBlacklisted(String jti) {
        return blacklistedJtis.contains(jti);
    }
}