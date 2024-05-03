package bolsasdete.catcompanionbackend.Controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import bolsasdete.catcompanionbackend.Services.IPublicationService;
import bolsasdete.catcompanionbackend.models.PublicationModel;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicationController {

    private final IPublicationService publicationService;

    @GetMapping("/publications/{page}")
    public Page<PublicationModel> index(@PathVariable Integer page) {

        if (page == null || page < 0)
            page = 0;

        return publicationService.findAll(PageRequest.of(page, 10));

    }

    @PostMapping("/publication/create")
    public ResponseEntity<?> create(@Valid @RequestBody PublicationModel publication, BindingResult result) {

        PublicationModel newPublication = null;
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {

            String firstError = result.getFieldErrors().stream()
                    .findFirst()
                    .map(err -> err.getDefaultMessage())
                    .orElse("Error de validación desconocido");

            response.put("message", firstError);
            return ResponseEntity.status(400).body(response);

        }

        try {

            newPublication = publicationService.save(publication);

        } catch (Exception e) {

            response.put("message", "Error interno del servidor al guardar la publicación");
            return ResponseEntity.status(500).body(response);

        }

        newPublication.setUser(null);

        response.put("message", "Publicación creada con éxito");
        response.put("publication", newPublication);
        return ResponseEntity.status(201).body(response);

    }

    @PutMapping("/publication/update/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody PublicationModel publication, BindingResult result, @PathVariable Long id) {

        PublicationModel publicationToUpdate = publicationService.findById(id);
        Map<String, Object> response = new HashMap<>();

        if (publicationToUpdate == null) {

            response.put("message", "Publicación no encontrada");
            return ResponseEntity.status(404).body(response);

        }

        if (result.hasErrors()) {

            String firstError = result.getFieldErrors().stream()
                    .findFirst()
                    .map(err -> err.getDefaultMessage())
                    .orElse("Error de validación desconocido");

            response.put("message", firstError);
            return ResponseEntity.status(400).body(response);

        }

        try {

            publicationToUpdate.setText(publication.getText());
            publicationService.save(publicationToUpdate);

        } catch (Exception e) {

            response.put("message", "Error interno del servidor al actualizar la publicación");
            return ResponseEntity.status(500).body(response);

        }

        response.put("message", "Publicación actualizada con éxito");
        response.put("publication", publicationToUpdate);
        return ResponseEntity.status(200).body(response);

    }

    @DeleteMapping("/publication/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        PublicationModel publication = publicationService.findById(id);
        Map<String, Object> response = new HashMap<>();

        if (publication == null) {

            response.put("message", "Publicación no encontrada");
            return ResponseEntity.status(404).body(response);

        }

        try {

            String imagePrevious = publication.getImage();

            if (imagePrevious != null && imagePrevious.length() > 0) {

                Path photoPreviousPath = Paths.get("publications").resolve(imagePrevious).toAbsolutePath();
                java.io.File photoPreviousFile = photoPreviousPath.toFile();

                if (photoPreviousFile.exists() && photoPreviousFile.canRead()) {

                    photoPreviousFile.delete();

                }

            }

            publicationService.delete(id);

        } catch (Exception e) {

            response.put("message", "Error interno del servidor al eliminar la publicación");
            return ResponseEntity.status(500).body(response);

        }

        response.put("message", "Publicación eliminada con éxito");
        return ResponseEntity.status(200).body(response);

    }

    @PostMapping("/publication/upload/{id}")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @PathVariable Long id) {

        PublicationModel publication = publicationService.findById(id);
        Map<String, Object> response = new HashMap<>();

        if (publication == null) {

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

        String path = "publications";
        java.io.File directory = new java.io.File(path);

        if (!directory.exists()) {

            directory.mkdir();

        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replace(" ", "");
        Path filePath = Paths.get(path).resolve(fileName).toAbsolutePath();

        try {

            Files.copy(file.getInputStream(), filePath);

            String imagePrevious = publication.getImage();

            if (imagePrevious != null && imagePrevious.length() > 0) {

                Path photoPreviousPath = Paths.get("publications").resolve(imagePrevious).toAbsolutePath();
                java.io.File photoPreviousFile = photoPreviousPath.toFile();

                if (photoPreviousFile.exists() && photoPreviousFile.canRead()) {

                    photoPreviousFile.delete();

                }

            }

            publication.setImage(fileName);
            publicationService.save(publication);

            response.put("publication", publication);
            response.put("message", "La imagen ha sido subida con éxito");
            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {

            response.put("message", "Error interno en el servidor al subir la imagen");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);

        }

    }

    @GetMapping("/publication/image/{imageName:.+}")
    public ResponseEntity<Resource> viewPhoto(@PathVariable String imageName) {

        String directory = "publications";
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

    @PutMapping("/publication/like/{id}/{userId}")
    public ResponseEntity<?> like(@PathVariable Long id, @PathVariable Long userId) {

        PublicationModel publication = publicationService.findById(id);
        Map<String, Object> response = new HashMap<>();

        if (publication == null) {

            response.put("message", "Publicación no encontrada");
            return ResponseEntity.status(404).body(response);

        }

        try {

            if (publication.getLikes().contains(userId)) {

                publication.getLikes().remove(userId);
                publicationService.save(publication);

                response.put("message", "Like quitado con éxito");
                response.put("publication", publication);
                return ResponseEntity.status(200).body(response);

            }

            publication.getLikes().add(userId);
            publicationService.save(publication);

            response.put("message", "Like dado con éxito");
            response.put("publication", publication);
            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {

            response.put("message", "Error interno del servidor al dar o quitar like a la publicación");
            return ResponseEntity.status(500).body(response);

        }

    }

}
