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

    public OperationResult<Void> completeOrder(User user) {
        if (user == null) {
            return OperationResult.failure("/login.xhtml?faces-redirect=true");
        }

        Cart cart = cartFacade.findActiveCart(user, null);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return OperationResult.failure("Cartiniz boş.");
        }

        try {
            createFromCart(user, cart);
        } catch (RuntimeException e) {
            return OperationResult.failure(e.getMessage());
        }
        return OperationResult.success("Siparişiniz alındı.", null);
    }

    public OperationResult<Void> updateStatus(Long orderId, OrderStatus status, String successMessage) {
        Order order = find(orderId);
        if (order != null) {
            order.setStatus(status);
            update(order);
        }
        return OperationResult.success(successMessage, null);
    }

    public OperationResult<Void> cancelUserOrder(User user, Long orderId) {
        Order order = find(orderId);
        if (order == null || user == null
                || order.getUser() == null
                || !order.getUser().getId().equals(user.getId())) {
            return OperationResult.failure("Sipariş bulunamadı.");
        }

        if (!canUserCancel(order)) {
            return OperationResult.failure("Bu sipariş artık iptal edilemez.");
        }

        order.setStatus(OrderStatus.CANCELLED);
        update(order);
        return OperationResult.success("Sipariş iptal edildi.", null);
    }

    public OperationResult<Void> deleteOrder(Long orderId) {
        Order order = find(orderId);
        if (order != null) {
            delete(order);
        }
        return OperationResult.success("Sipariş silindi.", null);
    }

    public boolean canUserCancel(Order order) {
        return order != null && order.getStatus() == OrderStatus.PENDING_APPROVAL;
    }

    public boolean canAdminUpdateStatus(Order order) {
        return order != null && order.getStatus() != OrderStatus.CANCELLED;
    }

    public int countWaitingOrders(List<Order> orders) {
        int count = 0;
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.PENDING_APPROVAL || order.getStatus() == OrderStatus.PREPARING) {
                count++;
            }
        }
        return count;
    }

    public int countDeliveredOrders(List<Order> orders) {
        int count = 0;
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.DELIVERED) {
                count++;
            }
        }
        return count;
    }

    public double calculateOrderTotal(List<Order> orders) {
        double total = 0.0;
        for (Order order : orders) {
            if (order.getTotalAmount() != null) {
                total += order.getTotalAmount();
            }
        }
        return total;
    }

    public Order save(Order order) {
        this.entityManager.persist(order);
        this.entityManager.flush();
        return order;
    }

    public Order update(Order order) {
        this.entityManager.merge(order);
        this.entityManager.flush();
        return order;
    }

    public void delete(Order order) {
        Order merged = this.entityManager.merge(order);
        this.entityManager.remove(merged);
    }

    public Order find(Long id) {
        return this.entityManager.find(Order.class, id);
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

    public boolean hasUserOrder(Long userId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Order> root = cq.from(Order.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("user").get("id"), userId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long count = q.getSingleResult();
        return count > 0;
    }

    private Order createFromCart(User user, Cart cart) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalAmount());
        order.setStatus(OrderStatus.PENDING_APPROVAL);

        for (CartItem item : cart.getItems()) {
            Product product = this.entityManager.find(Product.class, item.getProduct().getId());
            if (product == null || product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Stok yetersiz: " + item.getProduct().getName());
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProductName(product.getName());
            detail.setPurchasePrice(product.getCurrentPrice());
            detail.setQuantity(item.getQuantity());
            order.getDetails().add(detail);
        }

        this.entityManager.persist(order);

        Cart managedCart = this.entityManager.merge(cart);
        managedCart.getItems().clear();
        managedCart.setTotalAmount(0.0);
        this.entityManager.flush();
        return order;
    }
}


