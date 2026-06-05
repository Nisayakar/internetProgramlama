package facadeLocal;

import entity.User;
import facade.OperationResult;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface UserFacadeLocal {
    OperationResult<Void> register(User user);
    OperationResult<User> login(String email, String password);
    OperationResult<Void> saveAdmin(User user, String adminPassword);
    OperationResult<Void> deleteUser(User user, User activeUser);
    User save(User user);
    User update(User user);
    void delete(User user);
    User find(Long id);
    List<User> findAllUsers();
    User findByEmail(String email);
    boolean isEmailInUse(String email, Long currentUserId);
    boolean hasRelatedRecord(Long userId);
}


