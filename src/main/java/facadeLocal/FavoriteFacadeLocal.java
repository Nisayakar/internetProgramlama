package facadeLocal;

import entity.Favorite;
import entity.Product;
import entity.User;
import facade.OperationResult;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface FavoriteFacadeLocal {
    OperationResult<Void> toggleFavorite(User user, Long productId);
    OperationResult<Void> removeFavorite(User user, Long favoriteId);
    Favorite save(Favorite favorite);
    void delete(Favorite favorite);
    Favorite find(Long id);
    Product findProduct(Long productId);
    Favorite findByUserAndProduct(Long userId, Long productId);
    List<Favorite> findByUserId(Long userId);
    boolean isFavorite(Long userId, Long productId);
    boolean isFavorite(User user, Long productId);
    boolean hasFavoriteRecord(Long productId);
    boolean hasUserFavorite(Long userId);
}


