package bean;

import entity.Favorite;
import facade.OperationResult;
import facadeLocal.FavoriteFacadeLocal;
import jakarta.ejb.EJB;
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
    private String message;

    public void toggleFavorite(Long productId) {
        if (!sessionBean.isLoggedIn()) {
            message = "Favoritelere eklemek için giriş yapmalısınız.";
            return;
        }

        OperationResult<Void> result = favoriteFacade.toggleFavorite(sessionBean.getUser(), productId);
        message = result.getMessage();
        myFavorites = null;
    }

    public void removeFavorite(Long favoriteId) {
        OperationResult<Void> result = favoriteFacade.removeFavorite(sessionBean.getUser(), favoriteId);
        myFavorites = null;
        message = result.getMessage();
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

    public void setMyFavorites(List<Favorite> myFavorites) {
        this.myFavorites = myFavorites;
    }

    public boolean isNoFavorites() {
        return getMyFavorites() == null || getMyFavorites().isEmpty();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


