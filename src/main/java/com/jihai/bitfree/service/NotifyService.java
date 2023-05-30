package com.jihai.bitfree.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotifyService {
    public void sendNotice(String email, String password) {
        log.info("send password {} to email {}", password, email);
    }
}
