package bolsasdete.catcompanionbackend.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import bolsasdete.catcompanionbackend.models.UserModel;

public interface IUserDao extends JpaRepository<UserModel, Long>{

    UserModel findByUsername(String username);
    
}
