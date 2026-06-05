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

    public OperationResult<Void> toggleFavorite(User user, Long productId) {
        if (user == null) {
            return OperationResult.failure("Favoritelere eklemek için giriş yapmalısınız.");
        }

        Favorite existing = findByUserAndProduct(user.getId(), productId);
        if (existing != null) {
            delete(existing);
            return OperationResult.success("Ürün favorilerden çıkarıldı.", null);
        }

        Product product = findProduct(productId);
        if (product == null) {
            return OperationResult.failure("Ürün bulunamadı.");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite.setCreatedAt(LocalDateTime.now());
        save(favorite);
        return OperationResult.success("Ürün favorilere eklendi.", null);
    }

    public OperationResult<Void> removeFavorite(User user, Long favoriteId) {
        Favorite favorite = find(favoriteId);
        if (favorite == null || user == null
                || favorite.getUser() == null
                || !favorite.getUser().getId().equals(user.getId())) {
            return OperationResult.failure("Favori kaydı bulunamadı.");
        }

        delete(favorite);
        return OperationResult.success("Ürün favorilerden çıkarıldı.", null);
    }

    public Favorite save(Favorite favorite) {
        this.entityManager.persist(favorite);
        this.entityManager.flush();
        return favorite;
    }

    public void delete(Favorite favorite) {
        Favorite merged = this.entityManager.merge(favorite);
        this.entityManager.remove(merged);
    }

    public Favorite find(Long id) {
        return this.entityManager.find(Favorite.class, id);
    }

    public Product findProduct(Long productId) {
        return this.entityManager.find(Product.class, productId);
    }

    public Favorite findByUserAndProduct(Long userId, Long productId) {
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
        } else {
            return found.get(0);
        }
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

    public boolean isFavorite(Long userId, Long productId) {
        return findByUserAndProduct(userId, productId) != null;
    }

    public boolean isFavorite(User user, Long productId) {
        return user != null && isFavorite(user.getId(), productId);
    }

    public boolean hasFavoriteRecord(Long productId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Favorite> root = cq.from(Favorite.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("product").get("id"), productId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long count = q.getSingleResult();
        return count > 0;
    }

    public boolean hasUserFavorite(Long userId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Favorite> root = cq.from(Favorite.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("user").get("id"), userId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long count = q.getSingleResult();
        return count > 0;
    }
}


