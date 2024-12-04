package sample.taskapp.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String username;
    @Column
    private String email;
    @Column
    private String password;
    @Column(name = "role", nullable = false)
    String role;

    @Column(name = "profile_picture", nullable = false)
    private String profilePicture;
}
