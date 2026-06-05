package facadeLocal;

import entity.Address;
import entity.User;
import facade.OperationResult;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface AddressFacadeLocal {
    OperationResult<Address> saveOrUpdate(User user, Address address);
    OperationResult<Void> deleteForUser(User user, Address address);
    Address save(Address address);
    Address update(Address address);
    void delete(Address address);
    Address find(Long id);
    List<Address> findByUserId(Long userId);
    boolean belongsToUser(Long addressId, Long userId);
    boolean hasUserAddress(Long userId);
}


