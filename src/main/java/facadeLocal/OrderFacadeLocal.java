package facadeLocal;

import entity.Order;
import entity.User;
import enums.OrderStatus;
import facade.OperationResult;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface OrderFacadeLocal {
    OperationResult<Void> completeOrder(User user);
    OperationResult<Void> updateStatus(Long orderId, OrderStatus status, String successMessage);
    OperationResult<Void> cancelUserOrder(User user, Long orderId);
    OperationResult<Void> deleteOrder(Long orderId);
    boolean canUserCancel(Order order);
    boolean canAdminUpdateStatus(Order order);
    int countWaitingOrders(List<Order> orders);
    int countDeliveredOrders(List<Order> orders);
    double calculateOrderTotal(List<Order> orders);
    Order save(Order order);
    Order update(Order order);
    void delete(Order order);
    Order find(Long id);
    List<Order> findAllOrders();
    List<Order> findByUserId(Long userId);
    boolean hasUserOrder(Long userId);
}



