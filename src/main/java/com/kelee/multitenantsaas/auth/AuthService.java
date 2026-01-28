package com.kelee.multitenantsaas.auth;

import com.kelee.multitenantsaas.config.EmailAlreadyExistsException;
import com.kelee.multitenantsaas.config.TenantNotFoundException;
import com.kelee.multitenantsaas.config.UserNotFoundException;
import com.kelee.multitenantsaas.tenant.Tenant;
import com.kelee.multitenantsaas.tenant.TenantRepository;
import com.kelee.multitenantsaas.user.Role;
import com.kelee.multitenantsaas.user.User;
import com.kelee.multitenantsaas.user.UserRepository;
import com.kelee.multitenantsaas.user.api.AuthResponse;
import com.kelee.multitenantsaas.user.api.LoginRequest;
import com.kelee.multitenantsaas.user.api.SignupRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
            TenantRepository tenantRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new TenantNotFoundException(request.tenantId()));

        String hashedPassword = passwordEncoder.encode(request.password());

        User user = new User(tenant, request.email(), hashedPassword, Role.MEMBER);
        userRepository.save(user);

        String token = jwtService.generateToken(
                user.getId(),
                tenant.getId(),
                user.getEmail(),
                user.getRole().name());

        return new AuthResponse(token, user.getId(), tenant.getId(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UserNotFoundException(request.email());
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getTenant().getId(),
                user.getEmail(),
                user.getRole().name());

        return new AuthResponse(token, user.getId(), user.getTenant().getId(), user.getEmail(), user.getRole().name());
    }
}