package facade;

import entity.Cart;
import entity.CartItem;
import entity.Order;
import entity.OrderDetail;
import entity.Product;
import entity.User;
import enums.OrderStatus;
import facadeLocal.CartFacadeLocal;
import facadeLocal.OrderFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class OrderFacade extends AbstractFacade implements OrderFacadeLocal {

    @EJB
    private CartFacadeLocal cartFacade;

   
    public boolean completeOrder(User user) {
        if (user == null) {
            return false;
        }

        Cart cart = cartFacade.findActiveCart(user, null);
        if (isEmpty(cart)) {
            return false;
        }

        if (findInsufficientStockProduct(cart) != null) {
            return false;
        }

        createFromCart(user, cart);
        return true;
    }

    public void updateStatus(Long orderId, OrderStatus status) {
        Order order = find(orderId);
        if (order == null) {
            return;
        }

        order.setStatus(status);
        update(order);
    }

    public boolean cancelUserOrder(User user, Long orderId) {
        Order order = find(orderId);
        if (!belongsToUser(order, user)) {
            return false;
        }

        if (!canUserCancel(order)) {
            return false;
        }

        order.setStatus(OrderStatus.CANCELLED);
        update(order);
        return true;
    }

    public void deleteOrder(Long orderId) {
        Order order = find(orderId);
        if (order == null) {
            return;
        }

        delete(order);
    }

   
    public Product findInsufficientStockProduct(User user) {
        if (user == null) {
            return null;
        }

        Cart cart = cartFacade.findActiveCart(user, null);
        return findInsufficientStockProduct(cart);
    }

    public List<Order> findAllOrders() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> root = cq.from(Order.class);
        root.fetch("user", JoinType.INNER);
        root.fetch("details", JoinType.LEFT);
        cq.distinct(true);
        CriteriaQuery<Order> all = cq.select(root).orderBy(cb.desc(root.get("orderDate")));
        TypedQuery<Order> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public List<Order> findByUserId(Long userId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> root = cq.from(Order.class);
        root.fetch("details", JoinType.LEFT);
        cq.where(cb.equal(root.get("user").get("id"), userId));
        cq.distinct(true);
        CriteriaQuery<Order> all = cq.select(root).orderBy(cb.desc(root.get("orderDate")));
        TypedQuery<Order> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

  
    public boolean canUserCancel(Order order) {
        return order != null && order.getStatus() == OrderStatus.PENDING_APPROVAL;
    }

    public boolean canAdminUpdateStatus(Order order) {
        return order != null && order.getStatus() != OrderStatus.CANCELLED;
    }

    
    private Order update(Order order) {
        this.entityManager.merge(order);
        this.entityManager.flush();
        return order;
    }

    private void delete(Order order) {
        Order merged = this.entityManager.merge(order);
        this.entityManager.remove(merged);
    }

    private Order find(Long id) {
        return this.entityManager.find(Order.class, id);
    }

    private boolean isEmpty(Cart cart) {
        return cart == null || cart.getItems() == null || cart.getItems().isEmpty();
    }

    private boolean belongsToUser(Order order, User user) {
        return order != null
                && user != null
                && order.getUser() != null
                && order.getUser().getId().equals(user.getId());
    }

    private Product findInsufficientStockProduct(Cart cart) {
        if (isEmpty(cart)) {
            return null;
        }

        for (CartItem item : cart.getItems()) {
            Product product = this.entityManager.find(Product.class, item.getProduct().getId());
            if (hasInsufficientStock(product, item)) {
                return item.getProduct();
            }
        }

        return null;
    }

    private boolean hasInsufficientStock(Product product, CartItem item) {
        return product == null || product.getStockQuantity() < item.getQuantity();
    }

    private Order createFromCart(User user, Cart cart) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalAmount());
        order.setStatus(OrderStatus.PENDING_APPROVAL);

        for (CartItem item : cart.getItems()) {
            order.getDetails().add(createDetail(order, item));
        }

        this.entityManager.persist(order);
        clearCart(cart);
        this.entityManager.flush();
        return order;
    }

    private OrderDetail createDetail(Order order, CartItem item) {
        Product product = this.entityManager.find(Product.class, item.getProduct().getId());
        product.setStockQuantity(product.getStockQuantity() - item.getQuantity());

        OrderDetail detail = new OrderDetail();
        detail.setOrder(order);
        detail.setProductName(product.getName());
        detail.setPurchasePrice(product.getCurrentPrice());
        detail.setQuantity(item.getQuantity());
        return detail;
    }

    private void clearCart(Cart cart) {
        Cart managedCart = this.entityManager.merge(cart);
        managedCart.getItems().clear();
        managedCart.setTotalAmount(0.0);
    }
}


