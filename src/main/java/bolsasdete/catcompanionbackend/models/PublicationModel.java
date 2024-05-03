package bolsasdete.catcompanionbackend.models;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="publication")
public class PublicationModel implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Size(max = 200, message = "El texto no debe tener mas 200 caracteres")
    private String text;

    @Column(nullable = false)
    private Date created_at;

    @ElementCollection
    @Builder.Default
    private Set<Long> likes = new HashSet<>();

    private String image;

    @NotNull(message = "El usuario no puede estar vacío")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UserModel user;

    @PrePersist
    public void prePersist() {

        created_at = new Date();

    }

}
