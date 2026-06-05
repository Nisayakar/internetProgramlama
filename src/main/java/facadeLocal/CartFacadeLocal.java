package facadeLocal;

import entity.Cart;
import entity.CartItem;
import entity.Product;
import entity.User;
import facade.OperationResult;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface CartFacadeLocal {
    Cart createTemporaryCart();
    Cart findActiveCart(User user, Cart currentCart);
    OperationResult<Cart> addProduct(User user, Cart currentCart, Long productId);
    OperationResult<Cart> decreaseQuantity(User user, Cart currentCart, CartItem item);
    OperationResult<Cart> removeItem(User user, Cart currentCart, CartItem item);
    OperationResult<Cart> clearCart(User user, Cart currentCart);
    int countItems(Cart cart);
    Cart save(Cart cart);
    Cart update(Cart cart);
    void delete(Cart cart);
    Cart find(Long id);
    Product findProduct(Long productId);
    User findUser(Long userId);
    List<Cart> findAllCarts();
    Cart findByUserId(Long userId);
    boolean hasProductItem(Long productId);
    boolean hasUserCart(Long userId);
}


