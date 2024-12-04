package sample.taskapp.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sample.taskapp.JWT.JwtCore;
import sample.taskapp.Model.UserDetailsImpl;
import sample.taskapp.Service.UserService;
import sample.taskapp.Model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtCore jwtCore;

    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/profile")
    public String userProfile(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userService.findUserById(userDetails.getId());
        model.addAttribute("user", user);
        return "user-profile";
    }

    @PostMapping("/update-username")
    public String updateUsername(@RequestParam String username,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails,
                                 HttpServletResponse response) {
        userService.updateUsername(userDetails.getId(), username);

        UserDetails updatedUserDetails = userDetailsService.loadUserByUsername(username);
        String newJwtToken = jwtCore.generateToken(updatedUserDetails);

        Cookie jwtCookie = new Cookie("JWT", newJwtToken);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);

        Authentication authentication = new UsernamePasswordAuthenticationToken(updatedUserDetails, null, updatedUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "redirect:/user/profile?usernameUpdated";
    }

    @PostMapping("/update-email")
    public String updateEmail(@AuthenticationPrincipal UserDetailsImpl userDetails,
                              @RequestParam String email) {
        userService.updateEmail(userDetails.getId(), email);
        return "redirect:/user/profile?emailUpdated";
    }

    @PostMapping("/update-password")
    public String updatePassword(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                 @RequestParam String password,
                                 HttpServletResponse response) {
        userService.updatePassword(userDetails.getId(), password);

        UserDetails updatedUserDetails = userDetailsService.loadUserByUsername(userDetails.getUsername());
        String newJwtToken = jwtCore.generateToken(updatedUserDetails);

        Cookie jwtCookie = new Cookie("JWT", newJwtToken);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);

        Authentication authentication = new UsernamePasswordAuthenticationToken(updatedUserDetails, null, updatedUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/user/profile?passwordUpdated";
    }

    @PostMapping("/upload-picture")
    public String uploadProfilePicture(@RequestParam("file") MultipartFile file,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails,
                                       HttpServletResponse response) {
        System.out.println("Method accessed");
        userService.updateProfilePicture(userDetails.getId(), file);

        UserDetails updatedUserDetails = userService.loadUserByUsername(userDetails.getUsername());
        String newJwt = jwtCore.generateToken(updatedUserDetails);

        Cookie jwtCookie = new Cookie("JWT", newJwt);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);

        Authentication authentication = new UsernamePasswordAuthenticationToken(updatedUserDetails, null, updatedUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/user/profile?pictureUploaded";
    }


}
