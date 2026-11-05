package com.AgricultureFMSystem.AgricultureFMSystem.entity;

import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

/**
 * User Entity for Authentication, Role-based Access, and Login.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 60)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "password", nullable = false, length = 120)
    private String password;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    @Builder.Default
    private UserRole role = UserRole.FARMER;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
