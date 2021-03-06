package com.beth.infy.controller;

import com.beth.infy.auth.AuthorizationUtil;
import com.beth.infy.domain.AuthRequest;
import com.beth.infy.domain.AuthResponse;
import com.beth.infy.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class AuthenticationController extends AbstractController {

    @Autowired
    private AuthorizationUtil jwtUtil;

    @Autowired
    private AuthorizationService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping(value = "/api/v1/login", produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUserName(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUserName());

        final String token = jwtUtil.generateToken(userDetails);
        AuthResponse response = new AuthResponse(token);
        return ResponseEntity.ok(gson.toJson(response));
    }

    private void authenticate(String userName, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));

        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
