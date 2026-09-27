package com.librarymanagement.controller;
import com.librarymanagement.dto.ApiDtos.*; import com.librarymanagement.entity.User; import com.librarymanagement.security.JwtService; import com.librarymanagement.service.LibraryService; import com.librarymanagement.repository.UserRepository; import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/auth") public class AuthController {
 private final LibraryService service;private final UserRepository users;private final PasswordEncoder encoder;private final JwtService jwt;
 public AuthController(LibraryService s,UserRepository u,PasswordEncoder e,JwtService j){service=s;users=u;encoder=e;jwt=j;}
 @PostMapping("/register") public ResponseEntity<?> register(@Valid @RequestBody Register in){User u=service.register(in);return ResponseEntity.status(201).body(Map.of("success",true,"user",service.publicUser(u)));}
 @PostMapping("/login") public Map<String,Object> login(@Valid @RequestBody Login in){User u=users.findByEmail(in.email().toLowerCase().trim()).filter(x->encoder.matches(in.password(),x.getPasswordHash())&&x.getAccountStatus().name().equals("ACTIVE")).orElseThrow(()->new IllegalArgumentException("Invalid email or password."));return Map.of("success",true,"token",jwt.create(u.getEmail(),u.getRole().name()),"user",service.publicUser(u));}
}
