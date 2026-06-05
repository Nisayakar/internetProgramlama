package facadeLocal;

import entity.Comment;
import entity.Product;
import entity.User;
import facade.OperationResult;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface CommentFacadeLocal {
    OperationResult<Void> saveComment(User user, Long productId, Comment comment);
    OperationResult<Void> deleteComment(User user, boolean admin, Long commentId);
    Comment save(Comment comment);
    void delete(Comment comment);
    Comment find(Long id);
    Product findProduct(Long productId);
    List<Comment> findByProductId(Long productId);
    List<Comment> findByUserId(Long userId);
    boolean hasProductComment(Long productId);
    boolean hasUserComment(Long userId);
}


