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

    public boolean saveComment(User user, Long productId, Comment comment) {
        if (user == null) {
            return false;
        }

        Product product = findProduct(productId);
        if (product == null) {
            return false;
        }

        if (comment.getRating() == null || comment.getRating() < 1 || comment.getRating() > 5) {
            return false;
        }

        comment.setUser(user);
        comment.setProduct(product);
        comment.setCommentDate(LocalDateTime.now());
        save(comment);
        return true;
    }

    public boolean deleteComment(User user, boolean admin, Long commentId) {
        Comment comment = find(commentId);
        if (comment == null || user == null) {
            return false;
        }

        boolean ownComment = comment.getUser() != null && comment.getUser().getId().equals(user.getId());
        if (!ownComment && !admin) {
            return false;
        }

        delete(comment);
        return true;
    }

    private Comment save(Comment comment) {
        this.entityManager.persist(comment);
        this.entityManager.flush();
        return comment;
    }

    private void delete(Comment comment) {
        Comment merged = this.entityManager.merge(comment);
        this.entityManager.remove(merged);
    }

    private Comment find(Long id) {
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

}


