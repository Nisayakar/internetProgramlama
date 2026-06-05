package facade;

import entity.Address;
import entity.Cart;
import entity.Comment;
import entity.Favorite;
import entity.Order;
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

    public OperationResult<Void> register(User user) {
        if (isEmailInUse(user.getEmail(), null)) {
            return OperationResult.failure("Bu e-posta adresi zaten kullanılıyor.");
        }

        user.setRole(Role.CUSTOMER);
        save(user);
        return OperationResult.success(null, null);
    }

    public OperationResult<User> login(String email, String password) {
        User user = findByEmail(email);

        if (user != null && user.getPassword() != null && user.getPassword().equals(password)) {
            return OperationResult.success(null, user);
        }

        return OperationResult.failure("E-posta veya şifre hatalı.");
    }

    public OperationResult<Void> saveAdmin(User user, String adminPassword) {
        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }

        if (isEmailInUse(user.getEmail(), user.getId())) {
            return OperationResult.failure("Bu e-posta adresi başka bir kullanıcı tarafından kullanılıyor.");
        }

        if (user.getId() == null) {
            return saveNewAdminUser(user, adminPassword);
        }

        return updateAdminUser(user, adminPassword);
    }

    public OperationResult<Void> deleteUser(User user, User activeUser) {
        if (activeUser != null && activeUser.getId().equals(user.getId())) {
            return OperationResult.failure("Aktif oturumdaki kullanıcı silinemez.");
        }

        if (hasRelatedRecord(user.getId())) {
            return OperationResult.failure("Sipariş veya cart kaydı olan kullanıcı silinemez.");
        }

        delete(user);
        return OperationResult.success("Kullanıcı silindi.", null);
    }

    public User save(User user) {
        this.entityManager.persist(user);
        this.entityManager.flush();
        return user;
    }

    public User update(User user) {
        this.entityManager.merge(user);
        this.entityManager.flush();
        return user;
    }

    public void delete(User user) {
        User merged = this.entityManager.merge(user);
        this.entityManager.remove(merged);
    }

    public User find(Long id) {
        return this.entityManager.find(User.class, id);
    }

    public List<User> findAllUsers() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        CriteriaQuery<User> all = cq.select(root).orderBy(cb.asc(root.get("id")));
        TypedQuery<User> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public User findByEmail(String email) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.where(cb.equal(root.get("email"), email));
        CriteriaQuery<User> all = cq.select(root);
        TypedQuery<User> q = this.entityManager.createQuery(all);
        List<User> found = q.getResultList();

        if (found.isEmpty()) {
            return null;
        } else {
            return found.get(0);
        }
    }

    public boolean isEmailInUse(String email, Long currentUserId) {
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

    public boolean hasRelatedRecord(Long userId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        Long orderCount = countByUser(cb, Order.class, userId);
        Long cartCount = countByUser(cb, Cart.class, userId);
        Long addressCount = countByUser(cb, Address.class, userId);
        Long favoriteCount = countByUser(cb, Favorite.class, userId);
        Long commentCount = countByUser(cb, Comment.class, userId);
        return orderCount + cartCount + addressCount + favoriteCount + commentCount > 0;
    }

    private <T> Long countByUser(CriteriaBuilder cb, Class<T> entityClass, Long userId) {
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(entityClass);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("user").get("id"), userId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        return q.getSingleResult();
    }

    private OperationResult<Void> saveNewAdminUser(User user, String adminPassword) {
        if (adminPassword == null || adminPassword.isBlank()) {
            return OperationResult.failure("Yeni kullanıcı için şifre zorunludur.");
        }

        user.setPassword(adminPassword);
        save(user);
        return OperationResult.success("Kullanıcı eklendi.", null);
    }

    private OperationResult<Void> updateAdminUser(User user, String adminPassword) {
        User existing = find(user.getId());
        if (existing == null) {
            return OperationResult.failure("Kullanıcı bulunamadı.");
        }

        existing.setFullName(user.getFullName());
        existing.setEmail(user.getEmail());
        existing.setRole(user.getRole());
        if (adminPassword != null && !adminPassword.isBlank()) {
            existing.setPassword(adminPassword);
        }
        update(existing);
        return OperationResult.success("Kullanıcı güncellendi.", null);
    }
}


