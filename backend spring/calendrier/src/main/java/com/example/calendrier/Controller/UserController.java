package com.example.calendrier.Controller;

import com.example.calendrier.entity.User;
import com.example.calendrier.payload.request.ChangePasswordRequest;
import com.example.calendrier.payload.response.MessageResponse;
import com.example.calendrier.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping ("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
@GetMapping("/all")
    List<User> findAllUserList(){
return userService.findAllUser();
}
@GetMapping("/getbyrole/{role}")
public List<User> findUserByRole(@PathVariable User.ERole role){
    return userService.findUserByRole(role);
}

@GetMapping("/getuser/{userId}")
    public User findUserById(@PathVariable Long userId){
    return userService.findUserbyId(userId);
}

@PutMapping("/updateUser")
    public User updateUser(@RequestBody User u){
    return userService.updateUser(u);
}

@DeleteMapping("/delete/{userId}")
    public void removeUser(@PathVariable Long userId){
    userService.removeUser(userId);
}

@GetMapping("/getbydepartement/{departement}")
    public List<User> findUsersByDepartement(@PathVariable User.Departement departement){
    return userService.findUsersByDepartement(departement);
}

@GetMapping("/getbyusername/{username}")
public User findUserBYUsername(@PathVariable String username ){
    return userService.findByUsername(username);
}


@PutMapping("/blockUser/{userId}")
    public ResponseEntity<?> blockUser(@PathVariable Long userId) {
        try {
            User user = userService.findUserbyId(userId);
            if (user == null) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("ERROR: User does not exist!"));
            }
            user = userService.blockUser(userId);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("This account is blocked successfully!"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Failed to block this account!"));
        }
    }
    @PutMapping("/unblock/{userId}")
    public ResponseEntity< ? > unblockUser(@PathVariable Long userId) {
        try {
            if(userService.findUserbyId(userId)==null){
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("ERROR: user does not exist!"));
            }
            userService.unBlockUser(userId);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse(" un blocked successfully!"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(" Failed to unblock employee!"));
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePasswordByUser(@RequestBody ChangePasswordRequest changePasswordRequest){
        if (!userService.changePasswordByUser(changePasswordRequest.getId(), changePasswordRequest.getPassword(), changePasswordRequest.getNewPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: you entered a wrong password!"));
        }
        return ResponseEntity
                .ok()
                .body(new MessageResponse("Password changed successfully !"));
    }

}
