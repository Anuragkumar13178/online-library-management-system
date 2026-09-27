package com.librarymanagement.controller;
import com.librarymanagement.dto.ApiDtos.PasswordInput;import com.librarymanagement.entity.User;import com.librarymanagement.repository.UserRepository;import com.librarymanagement.service.LibraryService;import jakarta.validation.Valid;import org.springframework.security.core.Authentication;import org.springframework.security.crypto.password.PasswordEncoder;import org.springframework.web.bind.annotation.*;import java.util.Map;
@RestController @RequestMapping("/api/profile") public class ProfileController{
 private final LibraryService service;private final UserRepository users;private final PasswordEncoder encoder;public ProfileController(LibraryService s,UserRepository u,PasswordEncoder e){service=s;users=u;encoder=e;}
 @PutMapping("/password") public Map<String,Object> password(Authentication a,@Valid @RequestBody PasswordInput in){User u=service.user(a.getName());if(!encoder.matches(in.currentPassword(),u.getPasswordHash()))throw new IllegalArgumentException("Current password is incorrect.");u.setPasswordHash(encoder.encode(in.newPassword()));users.save(u);return Map.of("success",true,"message","Password updated successfully.");}
}
