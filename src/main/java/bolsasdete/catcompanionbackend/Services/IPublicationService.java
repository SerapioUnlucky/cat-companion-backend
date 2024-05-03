package bolsasdete.catcompanionbackend.Services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import bolsasdete.catcompanionbackend.models.PublicationModel;

public interface IPublicationService {
    
    public PublicationModel save(PublicationModel publication);
    public void delete(Long id);
    public Page<PublicationModel> findAll(Pageable pageable);
    public PublicationModel findById(Long id);

}
