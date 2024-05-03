package bolsasdete.catcompanionbackend.Services;

import bolsasdete.catcompanionbackend.models.UserModel;

public interface IUserService {
    
    public UserModel save(UserModel user);
    public UserModel findByUsername(String username);
    public UserModel findById(Long id);
    public void delete(Long id);

}
