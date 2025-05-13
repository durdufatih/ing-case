package com.fatihdurdu.loancase.security;

import com.fatihdurdu.loancase.model.dto.AuthRequest;
import com.fatihdurdu.loancase.model.dto.AuthResponse;
import com.fatihdurdu.loancase.model.entity.User;
import com.fatihdurdu.loancase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        String jwtToken = jwtService.generateToken(
                new UserDetailsImpl(user)
        );
        
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}
