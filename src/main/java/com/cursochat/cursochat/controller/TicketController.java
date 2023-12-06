package com.cursochat.cursochat.controller;

import com.cursochat.cursochat.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpHeaders;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("v1/ticket")
@CrossOrigin
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping
    public Map<String, String> buildTicket(@RequestHeader(AUTHORIZATION) String authorization) {

        String token = Optional.ofNullable(authorization)
                .map(it -> it.replace("Bearer ", ""))
                .orElse(authorization);
        String ticket = ticketService.buildAndSaveTicket(token);
return Map.of("ticket",ticket);
    }
}
