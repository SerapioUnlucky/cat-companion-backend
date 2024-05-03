package bolsasdete.catcompanionbackend.Controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import bolsasdete.catcompanionbackend.Services.IUserService;
import bolsasdete.catcompanionbackend.models.UserModel;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/user/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {

        UserModel user = null;
        Map<String, Object> response = new HashMap<>();
        
        try {

            user = userService.findById(id);

            if (user == null) {

                response.put("message", "Usuario no fue encontrado");
                return ResponseEntity.status(404).body(response);
    
            }

            response.put("message", "Usuario encontrado");
            response.put("user", user);
            return ResponseEntity.status(200).body(user);
            
        } catch (Exception e) {

            response.put("message", "Error interno del servidor al buscar el usuario");
            return ResponseEntity.status(500).body(response);
            
        }

    }

    @PutMapping("/user/update/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody UserModel user, BindingResult result, @PathVariable Long id){

        UserModel userCurrent = userService.findById(id);
        UserModel userUpdated = null;
        Map<String, Object> response = new HashMap<>();

        if (userCurrent == null) {

            response.put("message", "Usuario no fue encontrado");
            return ResponseEntity.status(404).body(response);

        }

        try {

            userCurrent.setFirstname(user.getFirstname() != null ? user.getFirstname() : userCurrent.getFirstname());
            userCurrent.setLastname(user.getLastname() != null ? user.getLastname() : userCurrent.getLastname());
            userCurrent.setCountry(user.getCountry() != null ? user.getCountry() : userCurrent.getCountry());
            userCurrent.setBiography(user.getBiography() != null ? user.getBiography() : userCurrent.getBiography());

            userUpdated = userService.save(userCurrent);

            response.put("message", "Usuario actualizado");
            response.put("user", userUpdated);
            return ResponseEntity.status(200).body(response);
            
        } catch (Exception e) {
            
            response.put("message", "Error interno del servidor al actualizar el usuario");
            return ResponseEntity.status(500).body(response);

        }

    }

    @PostMapping("/user/upload/{id}")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @PathVariable Long id) {

        UserModel user = userService.findById(id);
        Map<String, Object> response = new HashMap<>();

        if (user == null) {

            response.put("message", "Usuario no encontrado");
            return ResponseEntity.status(404).body(response);

        }

        if (file.isEmpty()) {

            response.put("message", "El archivo está vacío");
            return ResponseEntity.status(400).body(response);

        }

        if (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")
                && !file.getContentType().equals("image/jpg")) {

            response.put("message", "El archivo no es una imagen");
            return ResponseEntity.status(400).body(response);

        }

        String path = "users";
        java.io.File directory = new java.io.File(path);

        if (!directory.exists()) {

            directory.mkdir();

        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replace(" ", "");
        Path filePath = Paths.get(path).resolve(fileName).toAbsolutePath();

        try {

            Files.copy(file.getInputStream(), filePath);

            String imagePrevious = user.getPhoto();

            if (imagePrevious != null && imagePrevious.length() > 0) {

                Path photoPreviousPath = Paths.get("users").resolve(imagePrevious).toAbsolutePath();
                java.io.File photoPreviousFile = photoPreviousPath.toFile();

                if (photoPreviousFile.exists() && photoPreviousFile.canRead()) {

                    photoPreviousFile.delete();

                }

            }

            user.setPhoto(fileName);
            userService.save(user);

            response.put("publication", user);
            response.put("message", "La imagen ha sido subida con éxito");
            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {

            response.put("message", "Error interno en el servidor al subir la imagen");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);

        }

    }

    @GetMapping("/user/image/{imageName:.+}")
    public ResponseEntity<Resource> viewPhoto(@PathVariable String imageName) {

        String directory = "users";
        Path imagePath = Paths.get(directory, imageName);

        try {

            byte[] imageBytes = Files.readAllBytes(imagePath);

            Resource resource = new ByteArrayResource(imageBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(imageBytes.length);

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (IOException e) {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }

    }

}
