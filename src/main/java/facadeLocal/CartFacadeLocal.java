package facadeLocal;

import entity.Cart;
import entity.CartItem;
import entity.Product;
import entity.User;
import jakarta.ejb.Local;

@Local
public interface CartFacadeLocal {
    Cart createTemporaryCart();
    Cart findActiveCart(User user, Cart currentCart);
    Cart addProduct(User user, Cart currentCart, Long productId);
    Cart decreaseQuantity(User user, Cart currentCart, CartItem item);
    Cart removeItem(User user, Cart currentCart, CartItem item);
    int countItems(Cart cart);
    Product findProduct(Long productId);
}


