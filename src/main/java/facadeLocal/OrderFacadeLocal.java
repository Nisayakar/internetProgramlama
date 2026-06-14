package facadeLocal;

import entity.Order;
import entity.Product;
import entity.User;
import enums.OrderStatus;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface OrderFacadeLocal {
    boolean completeOrder(User user);
    void updateStatus(Long orderId, OrderStatus status);
    boolean cancelUserOrder(User user, Long orderId);
    void deleteOrder(Long orderId);
    Product findInsufficientStockProduct(User user);
    boolean canUserCancel(Order order);
    boolean canAdminUpdateStatus(Order order);
    List<Order> findAllOrders();
    List<Order> findByUserId(Long userId);
}



