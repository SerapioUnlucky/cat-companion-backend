package bolsasdete.catcompanionbackend.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bolsasdete.catcompanionbackend.Dao.IUserDao;
import bolsasdete.catcompanionbackend.models.UserModel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements IUserService{
    
    private final IUserDao userDao;

    @Override
    @Transactional
    public UserModel save(UserModel user) {

        return userDao.save(user);
        
    }

    @Override
    @Transactional(readOnly = true)
    public UserModel findByUsername(String username) {
        
        return userDao.findByUsername(username);

    }

    @Override
    @Transactional(readOnly = true)
    public UserModel findById(Long id) {
        
        return userDao.findById(id).orElse(null);

    }

    @Override
    @Transactional
    public void delete(Long id) {
        
        userDao.deleteById(id);

    }

}
