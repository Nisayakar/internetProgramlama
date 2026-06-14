package bean;

import entity.Favorite;
import facadeLocal.FavoriteFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("favoriteBean")
@ViewScoped
public class FavoriteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private FavoriteFacadeLocal favoriteFacade;

    @Inject
    private SessionBean sessionBean;

    private List<Favorite> myFavorites;

    public void toggleFavorite(Long productId) {
        if (!sessionBean.isLoggedIn()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Favoritelere eklemek için giriş yapmalısınız.");
            return;
        }

        boolean favorite = favoriteFacade.isFavorite(sessionBean.getUser(), productId);
        if (!favoriteFacade.toggleFavorite(sessionBean.getUser(), productId)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Ürün bulunamadı.");
            return;
        }
        addMessage(FacesMessage.SEVERITY_INFO, favorite ? "Ürün favorilerden çıkarıldı." : "Ürün favorilere eklendi.");
        myFavorites = null;
    }

    public void removeFavorite(Long favoriteId) {
        if (!favoriteFacade.removeFavorite(sessionBean.getUser(), favoriteId)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Favori kaydı bulunamadı.");
            return;
        }
        myFavorites = null;
        addMessage(FacesMessage.SEVERITY_INFO, "Ürün favorilerden çıkarıldı.");
    }

    public boolean isFavorite(Long productId) {
        return favoriteFacade.isFavorite(sessionBean.getUser(), productId);
    }

    public List<Favorite> getMyFavorites() {
        if (!sessionBean.isLoggedIn()) {
            myFavorites = null;
            return myFavorites;
        }

        if (myFavorites == null) {
            myFavorites = favoriteFacade.findByUserId(sessionBean.getUser().getId());
        }
        return myFavorites;
    }

    public boolean isNoFavorites() {
        return getMyFavorites() == null || getMyFavorites().isEmpty();
    }

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, text));
    }
}


