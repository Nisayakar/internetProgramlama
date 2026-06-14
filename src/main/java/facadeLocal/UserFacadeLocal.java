package facadeLocal;

import entity.User;
import jakarta.ejb.Local;

@Local
public interface UserFacadeLocal {
    boolean register(User user);
    User login(String email, String password);
}


