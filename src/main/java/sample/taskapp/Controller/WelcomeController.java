package sample.taskapp.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {
    @GetMapping("/auth")
    public String welcomePage() {
        return "welcome";
    }

    @GetMapping("auth/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("auth/signin")
    public String signinPage() {
        return "signin";
    }
}
