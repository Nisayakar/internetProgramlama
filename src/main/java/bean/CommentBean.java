package bean;

import entity.Comment;
import entity.Product;
import facadeLocal.CommentFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
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

    public void save() {
        if (!sessionBean.isLoggedIn()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Yorum yazmak için giriş yapmalısınız.");
            return;
        }

        Product product = commentFacade.findProduct(productId);
        if (product == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Ürün bulunamadı.");
            return;
        }

        if (getComment().getRating() == null || getComment().getRating() < 1 || getComment().getRating() > 5) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Puan 1 ile 5 arasında olmalıdır.");
            return;
        }

        commentFacade.saveComment(sessionBean.getUser(), productId, getComment());
        addMessage(FacesMessage.SEVERITY_INFO, "Commentunuz eklendi.");
        comment = new Comment();
        comments = null;
    }

    public void delete(Long commentId) {
        if (!commentFacade.deleteComment(sessionBean.getUser(), sessionBean.isAdmin(), commentId)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Yorum bulunamadı veya bu comment silinemez.");
            return;
        }
        comments = null;
        addMessage(FacesMessage.SEVERITY_INFO, "Yorum silindi.");
    }

    public Comment getComment() {
        if (comment == null) {
            comment = new Comment();
            comment.setRating(5);
        }
        return comment;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public List<Comment> getComments() {
        if (productId != null && comments == null) {
            comments = commentFacade.findByProductId(productId);
        }
        return comments;
    }

    public boolean isNoComments() {
        return getComments() == null || getComments().isEmpty();
    }

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, text));
    }
}


