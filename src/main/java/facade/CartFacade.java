package facade;

import entity.Cart;
import entity.CartItem;
import entity.Product;
import entity.User;
import facadeLocal.CartFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class CartFacade extends AbstractFacade implements CartFacadeLocal {

    
    public Cart createTemporaryCart() {
        Cart temporaryCart = new Cart();
        temporaryCart.setItems(new ArrayList<>());
        temporaryCart.setTotalAmount(0.0);
        return temporaryCart;
    }

    public Cart findActiveCart(User user, Cart currentCart) {
        if (user == null) {
            return currentCart == null ? createTemporaryCart() : currentCart;
        }

        Cart cart = findByUserId(user.getId());
        if (cart == null) {
            cart = createUserCart(user.getId());
        }
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }
        return cart;
    }

    public Cart addProduct(User user, Cart currentCart, Long productId) {
        if (user == null) {
            return null;
        }

        Cart cart = findActiveCart(user, currentCart);
        Product product = findProduct(productId);
        if (!isAvailable(product)) {
            return null;
        }

        CartItem existingItem = findItem(cart, product);
        if (existingItem != null) {
            return increaseQuantity(cart, existingItem, product);
        }

        cart.getItems().add(createItem(cart, product));
        calculate(cart);
        return update(cart);
    }

    public Cart decreaseQuantity(User user, Cart currentCart, CartItem item) {
        if (item == null) {
            return findActiveCart(user, currentCart);
        }
        Cart cart = findActiveCart(user, currentCart);
        if (item.getQuantity() <= 1) {
            return removeItem(user, cart, item);
        }
        item.setQuantity(item.getQuantity() - 1);
        updateSubtotal(item);
        calculate(cart);
        return update(cart);
    }

    public Cart removeItem(User user, Cart currentCart, CartItem item) {
        Cart cart = findActiveCart(user, currentCart);
        cart.getItems().remove(item);
        calculate(cart);
        return update(cart);
    }

    
    public int countItems(Cart cart) {
        if (cart == null || cart.getItems() == null) {
            return 0;
        }

        int count = 0;
        for (CartItem item : cart.getItems()) {
            count += item.getQuantity();
        }
        return count;
    }

    public Product findProduct(Long productId) {
        return this.entityManager.find(Product.class, productId);
    }

   
    private Cart save(Cart cart) {
        this.entityManager.persist(cart);
        this.entityManager.flush();
        return cart;
    }

    private Cart update(Cart cart) {
        Cart merged = this.entityManager.merge(cart);
        this.entityManager.flush();
        return merged;
    }

    private User findUser(Long userId) {
        return this.entityManager.find(User.class, userId);
    }

    private boolean isAvailable(Product product) {
        return product != null
                && product.getStockQuantity() != null
                && product.getStockQuantity() > 0;
    }

    private CartItem findItem(Cart cart, Product product) {
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(product.getId())) {
                return item;
            }
        }

        return null;
    }

    private Cart increaseQuantity(Cart cart, CartItem item, Product product) {
        if (item.getQuantity() >= product.getStockQuantity()) {
            return cart;
        }

        item.setQuantity(item.getQuantity() + 1);
        updateSubtotal(item);
        calculate(cart);
        return update(cart);
    }

    private CartItem createItem(Cart cart, Product product) {
        CartItem item = new CartItem();
        item.setProduct(product);
        item.setCart(cart);
        item.setQuantity(1);
        updateSubtotal(item);
        return item;
    }

    private void updateSubtotal(CartItem item) {
        item.setSubtotal(item.getQuantity() * item.getProduct().getCurrentPrice());
    }

    private Cart findByUserId(Long userId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Cart> cq = cb.createQuery(Cart.class);
        Root<Cart> root = cq.from(Cart.class);
        Fetch<Object, Object> items = root.fetch("items", JoinType.LEFT);
        items.fetch("product", JoinType.LEFT);
        cq.where(cb.equal(root.get("user").get("id"), userId));
        cq.distinct(true);
        CriteriaQuery<Cart> all = cq.select(root);
        TypedQuery<Cart> q = this.entityManager.createQuery(all);
        List<Cart> found = q.getResultList();

        if (found.isEmpty()) {
            return null;
        }
        return found.get(0);
    }

    private void calculate(Cart cart) {
        if (cart == null || cart.getItems() == null) {
            return;
        }

        double total = 0.0;
        for (CartItem item : cart.getItems()) {
            total += item.getSubtotal();
        }
        cart.setTotalAmount(total);
    }

    private Cart createUserCart(Long userId) {
        Cart cart = new Cart();
        cart.setUser(findUser(userId));
        cart.setItems(new ArrayList<>());
        cart.setTotalAmount(0.0);
        return save(cart);
    }
}


