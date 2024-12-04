package sample.taskapp.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sample.taskapp.Model.User;
import sample.taskapp.Model.UserDetailsImpl;
import sample.taskapp.Repos.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    public UserService(UserRepository userRepository) {
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User '%s' not found", username)
        ));
        return UserDetailsImpl.build(user);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found"));
    }

    public void updateUsername(Long userId, String username) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(username);
        userRepository.save(user);
    }

    public void updateEmail(Long id, String email) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(email);
        userRepository.save(user);
    }

    public void updatePassword(Long id, String password) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(new BCryptPasswordEncoder().encode(password));
        userRepository.save(user);

        sendPasswordChangeEmail(user);
    }

    public void sendPasswordChangeEmail(User user) {
        String loginLink = "http://localhost:8080/auth/signin";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Successfully Changed");
        message.setText("Your password has been successfully changed.\n" +
                "You can now log in using your new password by visiting the following link: \n" + loginLink);

        mailSender.send(message);
    }

    public void updateProfilePicture(Long id, MultipartFile file) {
        try {
            User user = userRepository.findById(id).
                    orElseThrow(() -> new RuntimeException("User not found"));
            String filename = saveProfilePicture(file);
            user.setProfilePicture(filename);
            userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String saveProfilePicture(MultipartFile file) throws IOException {
        String uploadDir = "user-images";
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("File saved at: " + path.toAbsolutePath());
        return fileName;
    }

}
