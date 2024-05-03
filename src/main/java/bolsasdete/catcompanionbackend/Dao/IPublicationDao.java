package bolsasdete.catcompanionbackend.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import bolsasdete.catcompanionbackend.models.PublicationModel;

public interface IPublicationDao extends JpaRepository<PublicationModel, Long>{
    
}
