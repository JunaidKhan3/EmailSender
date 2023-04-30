package com.ultimate.email.controller;

import com.ultimate.email.model.RequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ultimate.email.service.EmailService;

/**
 * Takes the credential and delegate the responsibility to each specific Class
 *
 * @author Nikhil TK
 *
 * @see UserDetails
 * @see AuthenticationManager
 * @see UsernamePasswordAuthenticationToken
 */
@RestController
public class LoginController {

    @Autowired
    EmailService fileSytemStorage;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody RequestDTO req) throws Exception {
        fileSytemStorage.authenticateService(req);
        return new ResponseEntity<>(req, HttpStatus.OK);

    }

}
