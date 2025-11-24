package com.xtrade.order.book.api;


import com.xtrade.order.book.api.payload.UserCredentials;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginApi {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextHolderStrategy securityContextHolderStrategy;

    @RequestMapping(method = RequestMethod.POST, consumes = {
        MediaType.APPLICATION_FORM_URLENCODED_VALUE,
    })
    public ResponseEntity<Void> login(
        @ModelAttribute UserCredentials userCredentials,
        HttpSession session
    ) {
        if(!session.isNew()) session.invalidate();
        Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(userCredentials.username(), userCredentials.password()));
        final var sctx = securityContextHolderStrategy.createEmptyContext();
        sctx.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(sctx);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sctx);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

}
