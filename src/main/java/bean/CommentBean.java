package bean;

import entity.Comment;
import facade.OperationResult;
import facadeLocal.CommentFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("commentBean")
@ViewScoped
public class CommentBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private CommentFacadeLocal commentFacade;

    @Inject
    private SessionBean sessionBean;

    private Comment comment;
    private Long productId;
    private List<Comment> comments;
    private String message;

    public void save() {
        OperationResult<Void> result = commentFacade.saveComment(sessionBean.getUser(), productId, getComment());
        message = result.getMessage();
        if (!result.isSuccess()) {
            return;
        }
        comment = new Comment();
        comments = null;
    }

    public void delete(Long commentId) {
        OperationResult<Void> result = commentFacade.deleteComment(sessionBean.getUser(), sessionBean.isAdmin(), commentId);
        if (result.isSuccess()) {
            comments = null;
        }
        message = result.getMessage();
    }

    public Comment getComment() {
        if (comment == null) {
            comment = new Comment();
            comment.setRating(5);
        }
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public List<Comment> getComments() {
        if (productId != null) {
            comments = commentFacade.findByProductId(productId);
        }
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public boolean isNoComments() {
        return getComments() == null || getComments().isEmpty();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


