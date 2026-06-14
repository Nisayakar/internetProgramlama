package facadeLocal;

import entity.Address;
import entity.User;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface AddressFacadeLocal {
    Address saveOrUpdate(User user, Address address);
    boolean deleteForUser(User user, Address address);
    List<Address> findByUserId(Long userId);
}


