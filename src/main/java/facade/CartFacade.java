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

    public OperationResult<Cart> addProduct(User user, Cart currentCart, Long productId) {
        if (user == null) {
            return OperationResult.failure("Carte eklemek için giriş yapmalısınız.");
        }

        Cart cart = findActiveCart(user, currentCart);
        Product product = findProduct(productId);
        if (product == null || product.getStockQuantity() == null || product.getStockQuantity() <= 0) {
            return OperationResult.failure("Bu ürün stokta yok.");
        }

        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(product.getId())) {
                if (item.getQuantity() >= product.getStockQuantity()) {
                    return OperationResult.success("Stok sınırına ulaşıldı.", cart);
                }
                item.setQuantity(item.getQuantity() + 1);
                item.setSubtotal(item.getQuantity() * product.getCurrentPrice());
                calculate(cart);
                return OperationResult.success("Sepet güncellendi.", update(cart));
            }
        }

        CartItem newItem = new CartItem();
        newItem.setProduct(product);
        newItem.setCart(cart);
        newItem.setQuantity(1);
        newItem.setSubtotal(product.getCurrentPrice());

        cart.getItems().add(newItem);
        calculate(cart);
        return OperationResult.success("Ürün sepete eklendi.", update(cart));
    }

    public OperationResult<Cart> decreaseQuantity(User user, Cart currentCart, CartItem item) {
        if (item == null) {
            return OperationResult.success(null, findActiveCart(user, currentCart));
        }
        Cart cart = findActiveCart(user, currentCart);
        if (item.getQuantity() <= 1) {
            return removeItem(user, cart, item);
        }
        item.setQuantity(item.getQuantity() - 1);
        item.setSubtotal(item.getQuantity() * item.getProduct().getCurrentPrice());
        calculate(cart);
        return OperationResult.success("Sepet güncellendi.", update(cart));
    }

    public OperationResult<Cart> removeItem(User user, Cart currentCart, CartItem item) {
        Cart cart = findActiveCart(user, currentCart);
        cart.getItems().remove(item);
        calculate(cart);
        return OperationResult.success("Ürün sepetten çıkarıldı.", update(cart));
    }

    public OperationResult<Cart> clearCart(User user, Cart currentCart) {
        Cart cart = findActiveCart(user, currentCart);
        cart.getItems().clear();
        cart.setTotalAmount(0.0);
        if (cart.getId() != null) {
            cart = update(cart);
        }
        return OperationResult.success(null, cart);
    }

    public int countItems(Cart cart) {
        int count = 0;
        if (cart != null && cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                count += item.getQuantity();
            }
        }
        return count;
    }

    public Cart save(Cart cart) {
        this.entityManager.persist(cart);
        this.entityManager.flush();
        return cart;
    }

    public Cart update(Cart cart) {
        Cart merged = this.entityManager.merge(cart);
        this.entityManager.flush();
        return merged;
    }

    public void delete(Cart cart) {
        Cart merged = this.entityManager.merge(cart);
        this.entityManager.remove(merged);
    }

    public Cart find(Long id) {
        return this.entityManager.find(Cart.class, id);
    }

    public Product findProduct(Long productId) {
        return this.entityManager.find(Product.class, productId);
    }

    public User findUser(Long userId) {
        return this.entityManager.find(User.class, userId);
    }

    public List<Cart> findAllCarts() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Cart> cq = cb.createQuery(Cart.class);
        Root<Cart> root = cq.from(Cart.class);
        root.fetch("items", JoinType.LEFT);
        cq.distinct(true);
        CriteriaQuery<Cart> all = cq.select(root).orderBy(cb.asc(root.get("id")));
        TypedQuery<Cart> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public Cart findByUserId(Long userId) {
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
        } else {
            return found.get(0);
        }
    }

    public boolean hasProductItem(Long productId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<CartItem> root = cq.from(CartItem.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("product").get("id"), productId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long count = q.getSingleResult();
        return count > 0;
    }

    public boolean hasUserCart(Long userId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Cart> root = cq.from(Cart.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("user").get("id"), userId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long count = q.getSingleResult();
        return count > 0;
    }

    private void calculate(Cart cart) {
        double total = 0.0;
        if (cart != null && cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                total += item.getSubtotal();
            }
            cart.setTotalAmount(total);
        }
    }

    private Cart createUserCart(Long userId) {
        Cart cart = new Cart();
        cart.setUser(findUser(userId));
        cart.setItems(new ArrayList<>());
        cart.setTotalAmount(0.0);
        return save(cart);
    }
}


