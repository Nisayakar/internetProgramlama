package facadeLocal;

import entity.Favorite;
import entity.User;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface FavoriteFacadeLocal {
    boolean toggleFavorite(User user, Long productId);
    boolean removeFavorite(User user, Long favoriteId);
    List<Favorite> findByUserId(Long userId);
    boolean isFavorite(User user, Long productId);
}


