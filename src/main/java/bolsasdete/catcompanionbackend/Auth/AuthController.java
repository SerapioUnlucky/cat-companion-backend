package bolsasdete.catcompanionbackend.Auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bolsasdete.catcompanionbackend.Jwt.JwtService;
import bolsasdete.catcompanionbackend.Services.IUserService;
import bolsasdete.catcompanionbackend.models.UserModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService authService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserModel user, BindingResult result) {

        UserModel newUser = null;
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {

            String firstError = result.getFieldErrors().stream()
                    .findFirst()
                    .map(err -> err.getDefaultMessage())
                    .orElse("Error de validación desconocido");

            response.put("message", firstError);
            return ResponseEntity.status(400).body(response);

        }

        if ( authService.findByUsername(user.getUsername()) != null) {

            response.put("message", "El nombre de usuario ya se encuentra registrado");
            return ResponseEntity.badRequest().body(response);

        }

        try {

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            newUser = authService.save(user);

        } catch (Exception e) {

            response.put("message", "Error interno en el servidor al registrar usuario");
            return ResponseEntity.status(500).body(response);

        }

        response.put("message", "Usuario registrado correctamente");
        response.put("user", newUser);  
        return ResponseEntity.ok(response);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserModel user, BindingResult result) {

        UserModel userFound = null;
        Map<String, Object> response = new HashMap<>();

        userFound = authService.findByUsername(user.getUsername());

        if (userFound == null) {

            response.put("message", "Usuario no encontrado");
            return ResponseEntity.badRequest().body(response);

        }

        if (!passwordEncoder.matches(user.getPassword(), userFound.getPassword())) {

            response.put("message", "Contraseña incorrecta");
            return ResponseEntity.badRequest().body(response);

        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        String token=jwtService.getToken(user);
        
        response.put("message", "Usuario logueado correctamente");
        response.put("token", token);
        response.put("user", userFound);
        return ResponseEntity.ok(response);

    }

}
