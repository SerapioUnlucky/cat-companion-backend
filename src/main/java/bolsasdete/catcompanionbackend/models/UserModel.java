package bolsasdete.catcompanionbackend.models;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="user", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class UserModel implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty(message = "El nombre de usuario no puede estar vacío")
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Pattern(regexp = "^[A-Za-záéíóúÁÉÍÓÚüÜñÑ]*$", message = "El nombre de usuario solo puede contener letras")
    @Size(min = 4, max = 20, message = "El nombre de usuario debe estar entre 4 y 20 caracteres")
    @Column(nullable = false)
    private String username;

    @NotEmpty(message = "El apellido no puede estar vacío")
    @NotBlank(message = "El apellido no puede estar vacío")
    @Pattern(regexp = "^[A-Za-záéíóúÁÉÍÓÚüÜñÑ]*$", message = "El apellido solo puede contener letras")
    @Size(min = 4, max = 20, message = "El apellido debe estar entre 4 y 20 caracteres")
    @Column(nullable = false)
    private String lastname;

    @NotEmpty(message = "El nombre no puede estar vacío")
    @NotBlank(message = "El nombre no puede estar vacío")
    @Pattern(regexp = "^[A-Za-záéíóúÁÉÍÓÚüÜñÑ]*$", message = "El nombre solo puede contener letras")
    @Size(min = 4, max = 20, message = "El nombre debe estar entre 4 y 20 caracteres")
    @Column(nullable = false)
    private String firstname;

    @NotEmpty(message = "El país no puede estar vacío")
    @NotBlank(message = "El país no puede estar vacío")
    @Pattern(regexp = "^[A-Za-záéíóúÁÉÍÓÚüÜñÑ]*$", message = "El país solo puede contener letras")
    @Size(min = 4, max = 20, message = "El país debe estar entre 4 y 12 caracteres")
    @Column(nullable = false)
    private String country;

    @Pattern(regexp = "^[A-Za-záéíóúÁÉÍÓÚüÜñÑ]*$", message = "La biografía solo puede contener letras")
    @Size(max = 200, message = "La biografía no debe tener mas 200 caracteres")
    private String biography;

    @NotEmpty(message = "La contraseña no puede estar vacío")
    @NotBlank(message = "La contraseña no puede estar vacío")
    @Column(nullable = false)
    private String password;

    private String photo;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

      return List.of(new SimpleGrantedAuthority(("USER")));

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
