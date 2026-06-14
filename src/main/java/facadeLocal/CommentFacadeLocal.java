package facadeLocal;

import entity.Comment;
import entity.Product;
import entity.User;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface CommentFacadeLocal {
    boolean saveComment(User user, Long productId, Comment comment);
    boolean deleteComment(User user, boolean admin, Long commentId);
    Product findProduct(Long productId);
    List<Comment> findByProductId(Long productId);
}


