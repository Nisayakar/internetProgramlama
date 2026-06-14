package facade;

import entity.Address;
import entity.User;
import facadeLocal.AddressFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Stateless
public class AddressFacade extends AbstractFacade implements AddressFacadeLocal {

    public Address saveOrUpdate(User user, Address address) {
        if (user == null) {
            return null;
        }
        if (address == null) {
            return null;
        }

        address.setUser(user);
        if (address.getId() == null) {
            return save(address);
        }

        if (!belongsToUser(address.getId(), user.getId())) {
            return null;
        }

        return update(address);
    }

    public boolean deleteForUser(User user, Address address) {
        if (user == null || address == null || address.getId() == null || !belongsToUser(address.getId(), user.getId())) {
            return false;
        }

        delete(address);
        return true;
    }

    private Address save(Address address) {
        this.entityManager.persist(address);
        this.entityManager.flush();
        return address;
    }

    private Address update(Address address) {
        this.entityManager.merge(address);
        this.entityManager.flush();
        return address;
    }

    private void delete(Address address) {
        Address merged = this.entityManager.merge(address);
        this.entityManager.remove(merged);
    }

    public List<Address> findByUserId(Long userId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Address> cq = cb.createQuery(Address.class);
        Root<Address> root = cq.from(Address.class);
        cq.where(cb.equal(root.get("user").get("id"), userId));
        CriteriaQuery<Address> all = cq.select(root).orderBy(cb.asc(root.get("id")));
        TypedQuery<Address> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    private boolean belongsToUser(Long addressId, Long userId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Address> root = cq.from(Address.class);
        cq.select(cb.count(root));
        cq.where(
                cb.equal(root.get("id"), addressId),
                cb.equal(root.get("user").get("id"), userId)
        );
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long count = q.getSingleResult();
        return count > 0;
    }
}


