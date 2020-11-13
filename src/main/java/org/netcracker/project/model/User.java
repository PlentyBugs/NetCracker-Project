package org.netcracker.project.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.netcracker.project.model.enums.Result;
import org.netcracker.project.model.enums.Role;
import org.netcracker.project.model.enums.TeamRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name="usr")
@Data
@NoArgsConstructor
public class User implements UserDetails {
    private static final long serialVersionUID = 3856464070955127754L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name can't be empty!")
    private String name;
    @NotBlank(message = "Surname can't be empty!")
    private String surname;
    @NotBlank(message = "Second name can't be empty!")
    private String secName;
    @Email(message = "E-mail's format is not correct!")
    @NotBlank
    private String email;
    @NotNull
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    @NotBlank(message = "Username can't be empty!")

    private String username;   //username=login

    @Column(name="avatar_filename", nullable = false)
    private String avatarFilename = "default.png";

    private boolean active;
    private String activationCode;

    @ElementCollection(targetClass = Role.class, fetch=FetchType.EAGER)
    @CollectionTable(name = "usr_role", joinColumns = @JoinColumn(name = "usr_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @ElementCollection(targetClass = TeamRole.class, fetch=FetchType.EAGER)
    @CollectionTable(name = "usr_team_role", joinColumns = @JoinColumn(name = "usr_id"))
    @Enumerated(EnumType.STRING)
    private Set<TeamRole> teamRoles;

    // todo: Надо как-то попробовать сделать ленивую подгрузку статистики и команд, но это проблема завтрашнего дня

    // Надо решить проблему с ленивой подгрузкой
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usr_team",
            joinColumns = { @JoinColumn(name = "usr_id") },
            inverseJoinColumns = { @JoinColumn(name = "team_id") }
    )
    private Set<Team> teams = new HashSet<>();


    @ElementCollection(targetClass = Result.class, fetch = FetchType.EAGER)
    @CollectionTable(name="result_type", joinColumns = @JoinColumn(name="usr_id"))
    @Enumerated(EnumType.STRING)
    private Set<Result> result=new HashSet<>();

    @ManyToMany
    private Map<Result,Competition> statistics=new HashMap<>();
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
