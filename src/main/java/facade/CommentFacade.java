package facade;

import entity.Comment;
import entity.Product;
import entity.User;
import facadeLocal.CommentFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class CommentFacade extends AbstractFacade implements CommentFacadeLocal {

    public OperationResult<Void> saveComment(User user, Long productId, Comment comment) {
        if (user == null) {
            return OperationResult.failure("Yorum yazmak için giriş yapmalısınız.");
        }

        Product product = findProduct(productId);
        if (product == null) {
            return OperationResult.failure("Ürün bulunamadı.");
        }

        if (comment.getRating() == null || comment.getRating() < 1 || comment.getRating() > 5) {
            return OperationResult.failure("Puan 1 ile 5 arasında olmalıdır.");
        }

        comment.setUser(user);
        comment.setProduct(product);
        comment.setCommentDate(LocalDateTime.now());
        save(comment);
        return OperationResult.success("Commentunuz eklendi.", null);
    }

    public OperationResult<Void> deleteComment(User user, boolean admin, Long commentId) {
        Comment comment = find(commentId);
        if (comment == null || user == null) {
            return OperationResult.failure("Yorum bulunamadı.");
        }

        boolean ownComment = comment.getUser() != null && comment.getUser().getId().equals(user.getId());
        if (!ownComment && !admin) {
            return OperationResult.failure("Bu comment silinemez.");
        }

        delete(comment);
        return OperationResult.success("Yorum silindi.", null);
    }

    public Comment save(Comment comment) {
        this.entityManager.persist(comment);
        this.entityManager.flush();
        return comment;
    }

    public void delete(Comment comment) {
        Comment merged = this.entityManager.merge(comment);
        this.entityManager.remove(merged);
    }

    public Comment find(Long id) {
        return this.entityManager.find(Comment.class, id);
    }

    public Product findProduct(Long productId) {
        return this.entityManager.find(Product.class, productId);
    }

    public List<Comment> findByProductId(Long productId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> cq = cb.createQuery(Comment.class);
        Root<Comment> root = cq.from(Comment.class);
        root.fetch("user", JoinType.LEFT);
        cq.where(cb.equal(root.get("product").get("id"), productId));
        CriteriaQuery<Comment> all = cq.select(root).orderBy(cb.desc(root.get("commentDate")));
        TypedQuery<Comment> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public List<Comment> findByUserId(Long userId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> cq = cb.createQuery(Comment.class);
        Root<Comment> root = cq.from(Comment.class);
        cq.where(cb.equal(root.get("user").get("id"), userId));
        CriteriaQuery<Comment> all = cq.select(root).orderBy(cb.desc(root.get("commentDate")));
        TypedQuery<Comment> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public boolean hasProductComment(Long productId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Comment> root = cq.from(Comment.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("product").get("id"), productId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long count = q.getSingleResult();
        return count > 0;
    }

    public boolean hasUserComment(Long userId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Comment> root = cq.from(Comment.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("user").get("id"), userId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long count = q.getSingleResult();
        return count > 0;
    }
}


