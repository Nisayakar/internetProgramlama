package facade;

import entity.Favorite;
import entity.Product;
import entity.User;
import facadeLocal.FavoriteFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class FavoriteFacade extends AbstractFacade implements FavoriteFacadeLocal {

    public boolean toggleFavorite(User user, Long productId) {
        if (user == null) {
            return false;
        }

        Favorite existing = findByUserAndProduct(user.getId(), productId);
        if (existing != null) {
            delete(existing);
            return true;
        }

        Product product = findProduct(productId);
        if (product == null) {
            return false;
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite.setCreatedAt(LocalDateTime.now());
        save(favorite);
        return true;
    }

    public boolean removeFavorite(User user, Long favoriteId) {
        Favorite favorite = find(favoriteId);
        if (favorite == null || user == null
                || favorite.getUser() == null
                || !favorite.getUser().getId().equals(user.getId())) {
            return false;
        }

        delete(favorite);
        return true;
    }

    private Favorite save(Favorite favorite) {
        this.entityManager.persist(favorite);
        this.entityManager.flush();
        return favorite;
    }

    private void delete(Favorite favorite) {
        Favorite merged = this.entityManager.merge(favorite);
        this.entityManager.remove(merged);
    }

    private Favorite find(Long id) {
        return this.entityManager.find(Favorite.class, id);
    }

    private Product findProduct(Long productId) {
        return this.entityManager.find(Product.class, productId);
    }

    private Favorite findByUserAndProduct(Long userId, Long productId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Favorite> cq = cb.createQuery(Favorite.class);
        Root<Favorite> root = cq.from(Favorite.class);
        root.fetch("product", JoinType.LEFT);
        cq.where(
                cb.equal(root.get("user").get("id"), userId),
                cb.equal(root.get("product").get("id"), productId)
        );
        CriteriaQuery<Favorite> all = cq.select(root);
        TypedQuery<Favorite> q = this.entityManager.createQuery(all);
        List<Favorite> found = q.getResultList();

        if (found.isEmpty()) {
            return null;
        }
        return found.get(0);
    }

    public List<Favorite> findByUserId(Long userId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Favorite> cq = cb.createQuery(Favorite.class);
        Root<Favorite> root = cq.from(Favorite.class);
        root.fetch("product", JoinType.LEFT);
        cq.where(cb.equal(root.get("user").get("id"), userId));
        CriteriaQuery<Favorite> all = cq.select(root).orderBy(cb.desc(root.get("createdAt")));
        TypedQuery<Favorite> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    private boolean isFavorite(Long userId, Long productId) {
        return findByUserAndProduct(userId, productId) != null;
    }

    public boolean isFavorite(User user, Long productId) {
        return user != null && isFavorite(user.getId(), productId);
    }
}


