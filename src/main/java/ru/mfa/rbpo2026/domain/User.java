package ru.mfa.rbpo2026.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Пользователь системы: студент или сотрудник студенческого городка. */
@Entity @Table(name="users", uniqueConstraints=@UniqueConstraint(name="uk_users_email", columnNames="email"))
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotBlank @Column(name="full_name", nullable=false, length=160) private String fullName;
    @Email @NotBlank @Column(nullable=false, length=254) private String email;
    @Column(name="password_hash", length=100) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable=true, length=20) private UserRole role = UserRole.STUDENT;
    @Column(nullable=true) private boolean enabled = true;
    @Column(name="account_non_locked", nullable=true) private boolean accountNonLocked = true;
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getFullName(){return fullName;} public void setFullName(String v){this.fullName=v;}
    public String getEmail(){return email;} public void setEmail(String v){this.email=v;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){this.passwordHash=v;}
    public UserRole getRole(){return role;} public void setRole(UserRole v){this.role=v;}
    public boolean isEnabled(){return enabled;} public void setEnabled(boolean v){this.enabled=v;}
    public boolean isAccountNonLocked(){return accountNonLocked;} public void setAccountNonLocked(boolean v){this.accountNonLocked=v;}
}
