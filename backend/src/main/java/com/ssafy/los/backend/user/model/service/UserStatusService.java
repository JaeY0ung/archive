package com.ssafy.los.backend.user.model.service;

import java.util.Set;

public interface UserStatusService {

    void setUserOnline(Long id);

    void setUserOffline(Long id);

    public boolean isUserOnline(Long id);

    public Set<String> getOnlineUsers();
}
