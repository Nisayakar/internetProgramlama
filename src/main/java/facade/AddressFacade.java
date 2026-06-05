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

    public OperationResult<Address> saveOrUpdate(User user, Address address) {
        if (user == null) {
            return OperationResult.failure("Adres eklemek için giriş yapmalısınız.");
        }
        if (address == null) {
            return OperationResult.failure("Adres bulunamadı.");
        }

        address.setUser(user);
        if (address.getId() == null) {
            save(address);
            return OperationResult.success("Adres eklendi.", address);
        }

        if (!belongsToUser(address.getId(), user.getId())) {
            return OperationResult.failure("Adres bulunamadı.");
        }

        update(address);
        return OperationResult.success("Adres güncellendi.", address);
    }

    public OperationResult<Void> deleteForUser(User user, Address address) {
        if (user == null || address == null || address.getId() == null || !belongsToUser(address.getId(), user.getId())) {
            return OperationResult.failure("Adres bulunamadı.");
        }

        delete(address);
        return OperationResult.success("Adres silindi.", null);
    }

    public Address save(Address address) {
        this.entityManager.persist(address);
        this.entityManager.flush();
        return address;
    }

    public Address update(Address address) {
        this.entityManager.merge(address);
        this.entityManager.flush();
        return address;
    }

    public void delete(Address address) {
        Address merged = this.entityManager.merge(address);
        this.entityManager.remove(merged);
    }

    public Address find(Long id) {
        return this.entityManager.find(Address.class, id);
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

    public boolean belongsToUser(Long addressId, Long userId) {
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

    public boolean hasUserAddress(Long userId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Address> root = cq.from(Address.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("user").get("id"), userId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long count = q.getSingleResult();
        return count > 0;
    }
}


