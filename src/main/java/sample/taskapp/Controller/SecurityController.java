package sample.taskapp.Controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sample.taskapp.Dtos.SigninRequest;
import sample.taskapp.Dtos.SignupRequest;
import sample.taskapp.JWT.JwtCore;
import sample.taskapp.Model.User;
import sample.taskapp.Model.UserDetailsImpl;
import sample.taskapp.Repos.UserRepository;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class SecurityController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtCore jwtCore;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    @Autowired
    public void setJwtCore(JwtCore jwtCore) {
        this.jwtCore = jwtCore;
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute SignupRequest signupRequest, Model model) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            model.addAttribute("error", "Choose a different name");
            return "signup";
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            model.addAttribute("error", "Choose a different email");
            return "signup";
        }
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        if ("admin".equalsIgnoreCase(signupRequest.getUsername())) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }
        userRepository.save(user);
        return "redirect:/auth/signin";
    }
    @PostMapping("/signin")
    public String signin(@ModelAttribute SigninRequest signinRequest, Model model, HttpServletResponse response) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signinRequest.getUsername(),
                            signinRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtCore.generateToken(userDetails);

            Cookie jwtCookie = new Cookie("JWT", jwt);
//            jwtCookie.setHttpOnly(true);
//            jwtCookie.setSecure(true);
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);

            String role = ((UserDetailsImpl) userDetails).getRole();
            if ("ADMIN".equals(role)) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/tasks/";
            }
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Invalid username or password");
            return "signin";
        }
    }
}
