package com.dxvalley.project.controllers;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dxvalley.project.models.Account;
import com.dxvalley.project.models.Users;
import com.dxvalley.project.repositories.RoleRepository;
import com.dxvalley.project.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
  private final UserRepository userRepository;
  private final RoleRepository roleRepo;
  private final PasswordEncoder passwordEncoder;

  private boolean isSysAdmin() {
    AtomicBoolean hasSysAdmin = new AtomicBoolean(false);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    auth.getAuthorities().forEach(grantedAuthority -> {
      if (grantedAuthority.getAuthority().equals("sysAdmin")) {
        hasSysAdmin.set(true);
      }
    });
    return hasSysAdmin.get();
  }

  private boolean isOwnAccount(String userName) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return userRepository.findByUsername((String) auth.getPrincipal()).getUsername().equals(userName);
  }

  // @GetMapping("/getUsers")
  // List<Users> getUsers() {
  //   if (isSysAdmin()) {
  //     return this.userRepository.findAll(Sort.by("username"));
  //   }
  //   var users = this.userRepository.findAll(Sort.by("username"));
  //   users.removeIf(user -> {
  //     var containsAdmin = false;
  //     for (var role : user.getRoles()) {
  //       containsAdmin = containsAdmin || role.getRoleName().equals("admin");
  //     }
  //     return containsAdmin;
  //   });
  //   return users;
  // }
  @GetMapping("/getUsers")
  List<Users> getAllUsers() {
   List<Users> users=userRepository.findAll();
    return users;
  }

  @GetMapping("/getUser/{userId}")
  public ResponseEntity<?> getByUserId(@PathVariable Long userId) {
    var user = userRepository.findByUserId(userId);
    if (user == null) {
      createUserResponse response = new createUserResponse("error", "Cannot find this user!");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  // @GetMapping("/getUserProfile/{phoneNumber}")
  // public GetUserProfile getUserProfile(@PathVariable String phoneNumber) {
  //   GetUserProfile getUserProfile = new GetUserProfile();
  //   Users user = userRepository.findByUsername(phoneNumber);
  //   getUserProfile.setFullName(user.getFullName());
  //   getUserProfile.setPhoneNumber(user.getUsername());
  //   getUserProfile.setEmail(user.getEmail());
  //   List<Account> accounts = user.getAccounts();
  //   String mainAccount = "";
  //   for (int i = 0; i < accounts.size(); i++) {
  //     if (accounts.get(i).getIsMainAccount() == true) {
  //       mainAccount = accounts.get(i).getAccountNumber();
  //     }
  //   }
  //   getUserProfile.setAccountNumber(mainAccount);
  //   getUserProfile.setBirthDate(user.getBirthDate());
  //   getUserProfile.setProfileImage(user.getImageUrl());
  //   return getUserProfile;
  // }

  @GetMapping("/getUserByPhone/{username}")
  public ResponseEntity<?> getByUsername(@PathVariable String username) {
    var user = userRepository.findByUsername(username);

    if (user == null) {
      createUserResponse response = new createUserResponse("error", "Cannot find user with this phone number!");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @PostMapping("/createUser")
  public ResponseEntity<createUserResponse> accept(@RequestBody Users tempUser) {
    var user = userRepository.findByUsername(tempUser.getUsername());
    if (user != null) {
      createUserResponse response = new createUserResponse("error", "user already exists");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    tempUser.setRoles(
        tempUser.getRoles().stream().map(x -> this.roleRepo.findByRoleName(x.getRoleName()))
            .collect(Collectors.toList()));
    tempUser.setPassword(passwordEncoder.encode(tempUser.getPassword()));
    userRepository.save(tempUser);
    createUserResponse response = new createUserResponse("success", "user created successfully");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PutMapping("/changePin/{phoneNumber}")
  public ResponseEntity<pinchangeResponse> pinChange(@RequestBody Users tempUser,
      @PathVariable String phoneNumber) {
    Users user = userRepository.findByUsername(phoneNumber);
    // edit(tempUser,user);
    // user.setUsername(tempUser.getUsername());

    user.setPassword(passwordEncoder.encode(tempUser.getPassword()));
    userRepository.save(user);
    // .setPassword(null);
    pinchangeResponse response = new pinchangeResponse("success");
    return new ResponseEntity<>(response, HttpStatus.OK);

  }

  @PutMapping("/manageAccount/{userName}/{usernameChange}")
  public Users manageAccount(@RequestBody UsernamePassword temp,
      @PathVariable String userName,
      @PathVariable Boolean usernameChange) throws AccessDeniedException {
    if (isOwnAccount(userName)) {
      Users user = userRepository.findByUsername(userName);
      if (passwordEncoder.matches(temp.getOldPassword(), user.getPassword())) {
        user.setPassword(passwordEncoder.encode(temp.getNewPassword()));
      }
      if (usernameChange) {
        user.setUsername(temp.getNewUsername());
      }
      Users response = userRepository.save(user);
      response.setPassword(null);
      return response;
    } else
      throw new AccessDeniedException("403 Forbidden");
  }

  @DeleteMapping("/delete/user/{userId}")
  void deleteUser(@PathVariable Long userId) {
    this.userRepository.deleteById(userId);
  }

}

@Getter
@Setter
class UsernamePassword {
  private String newUsername;
  private String newPassword;
  private String oldPassword;
}

@Getter
@Setter
@AllArgsConstructor
class createUserResponse {
  String status;
  String description;
}

@Getter
@Setter
@AllArgsConstructor
class pinchangeResponse {
  String status;
}

@Data
class GetUserProfile {
  String fullName;
  String phoneNumber;
  String email;
  String accountNumber;
  String birthDate;
  String profileImage;
}