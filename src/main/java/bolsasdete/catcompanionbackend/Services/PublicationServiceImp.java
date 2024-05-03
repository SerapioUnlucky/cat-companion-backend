package bolsasdete.catcompanionbackend.Services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bolsasdete.catcompanionbackend.Dao.IPublicationDao;
import bolsasdete.catcompanionbackend.models.PublicationModel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicationServiceImp implements IPublicationService{
    
    private final IPublicationDao publicationDao;

    @Override
    @Transactional
    public PublicationModel save(PublicationModel publication) {

        return publicationDao.save(publication);
        
    }

    @Override
    @Transactional
    public void delete(Long id) {
        
        publicationDao.deleteById(id);

    }

    @Override
    @Transactional(readOnly = true)
    public Page<PublicationModel> findAll(Pageable pageable) {
        
        return publicationDao.findAll(pageable);

    }

    @Override
    @Transactional(readOnly = true)
    public PublicationModel findById(Long id) {
        
        return publicationDao.findById(id).orElse(null);

    }

}
