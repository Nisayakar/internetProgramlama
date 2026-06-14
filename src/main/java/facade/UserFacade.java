package facade;

import entity.User;
import enums.Role;
import facadeLocal.UserFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Stateless
public class UserFacade extends AbstractFacade implements UserFacadeLocal {

    
    public boolean register(User user) {
        if (isEmailInUse(user.getEmail(), null)) {
            return false;
        }

        user.setRole(Role.CUSTOMER);
        save(user);
        return true;
    }

    public User login(String email, String password) {
        User user = findByEmail(email);

        if (user != null && user.getPassword() != null && user.getPassword().equals(password)) {
            return user;
        }

        return null;
    }


    private boolean isEmailInUse(String email, Long currentUserId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<User> root = cq.from(User.class);
        cq.select(cb.count(root));

        if (currentUserId == null) {
            cq.where(cb.equal(root.get("email"), email));
        } else {
            cq.where(
                    cb.equal(root.get("email"), email),
                    cb.notEqual(root.get("id"), currentUserId)
            );
        }

        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long count = q.getSingleResult();
        return count > 0;
    }

  
    private User save(User user) {
        this.entityManager.persist(user);
        this.entityManager.flush();
        return user;
    }

    private User findByEmail(String email) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.where(cb.equal(root.get("email"), email));
        CriteriaQuery<User> all = cq.select(root);
        TypedQuery<User> q = this.entityManager.createQuery(all);
        List<User> found = q.getResultList();

        if (found.isEmpty()) {
            return null;
        }
        return found.get(0);
    }
}


