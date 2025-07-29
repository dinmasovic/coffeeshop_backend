package com.example.coffeeshop.config;

import com.example.coffeeshop.model.domain.Worker;
import com.example.coffeeshop.service.CoffeeShopService;
import com.example.coffeeshop.service.WorkerService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final WorkerService workerService;
    private final CoffeeShopService coffeeShopService;

    public CustomAuthenticationProvider(PasswordEncoder passwordEncoder, WorkerService workerService, CoffeeShopService coffeeShopService) {
        this.passwordEncoder = passwordEncoder;
        this.workerService = workerService;
        this.coffeeShopService = coffeeShopService;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (username.isEmpty() || password.isEmpty()) {
            throw new BadCredentialsException("Empty credentials!");
        }

        UserDetails userDetails = null;

        try {
            userDetails = this.workerService.loadUserByUsername(username);
        } catch (Exception e) {
        }
        if (userDetails == null) {
            try {
                userDetails = this.coffeeShopService.loadUserByUsername(username);
            } catch (Exception e) {
                throw new BadCredentialsException("User not found!");
            }
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Password is incorrect!");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
