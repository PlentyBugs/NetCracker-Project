package org.netcracker.project.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.netcracker.project.model.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name="usr")
@Data
@NoArgsConstructor
public class User implements UserDetails {
    @NotBlank(message = "Name can't be empty!")
    private String name;
    @NotBlank(message = "Surname can't be empty!")
    private String surname;
    @NotBlank(message = "Second name can't be empty!")
    private String secName;
    @Email(message = "E-mail's format is not correct!")
    private String email;
    @NotBlank(message = "Password can't be empty!")
    @Size(min=8)
    private String password;
    @NotBlank(message = "Username can't be empty!")

    private String username;   //username=login
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ElementCollection(targetClass = Role.class, fetch=FetchType.EAGER)
    //to add @CollectionTable
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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
        return true;
    }
}
