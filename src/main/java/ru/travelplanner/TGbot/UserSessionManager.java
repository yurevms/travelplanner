package ru.travelplanner.TGbot;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
public class UserSessionManager {

    private Map<Long, String> userSessions = new HashMap<>();

    public void loginUser(Long chatId, String username) {
        userSessions.put(chatId, username);
    }

    public void logoutUser(Long chatId) {
        userSessions.remove(chatId);
    }

    public String getUsername(Long chatId) {
        return userSessions.get(chatId);
    }

    public boolean isUserLoggedIn(Long chatId) {
        return userSessions.containsKey(chatId);
    }
}