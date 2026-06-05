package bean;

import entity.Order;
import enums.OrderStatus;
import facade.OperationResult;
import facadeLocal.OrderFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("orderBean")
@ViewScoped
public class OrderBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String ADMIN_ORDERS_REDIRECT = "/panel/orders.xhtml?faces-redirect=true";

    @Inject
    private SessionBean sessionBean;

    @EJB
    private OrderFacadeLocal orderFacade;

    private List<Order> orderHistory;
    private List<Order> allOrders;
    private String message;

    public String completeOrder() {
        if (!sessionBean.isLoggedIn()) {
            return "/login.xhtml?faces-redirect=true";
        }

        OperationResult<Void> result = orderFacade.completeOrder(sessionBean.getUser());
        message = result.getMessage();
        if (!result.isSuccess()) {
            return null;
        }

        orderHistory = null;
        allOrders = null;
        return null;
    }

    public String approve(Long orderId) {
        return updateStatus(orderId, OrderStatus.APPROVED, "Sipariş onaylandı.");
    }

    public String markPreparing(Long orderId) {
        return updateStatus(orderId, OrderStatus.PREPARING, "Sipariş hazırlanıyor olarak işaretlendi.");
    }

    public String deliver(Long orderId) {
        return updateStatus(orderId, OrderStatus.DELIVERED, "Sipariş teslim edildi.");
    }

    public String cancel(Long orderId) {
        return updateStatus(orderId, OrderStatus.CANCELLED, "Sipariş iptal edildi.");
    }

    public String cancelUserOrder(Long orderId) {
        OperationResult<Void> result = orderFacade.cancelUserOrder(sessionBean.getUser(), orderId);
        if (result.isSuccess()) {
            orderHistory = null;
        }
        message = result.getMessage();
        return null;
    }

    public boolean isUserCancelable(Order order) {
        return orderFacade.canUserCancel(order);
    }

    public boolean isAdminStatusUpdatable(Order order) {
        return orderFacade.canAdminUpdateStatus(order);
    }

    public String updateStatus(Long orderId, OrderStatus status) {
        return updateStatus(orderId, status, "Sipariş durumu güncellendi.");
    }

    private String updateStatus(Long orderId, OrderStatus status, String successMessage) {
        OperationResult<Void> result = orderFacade.updateStatus(orderId, status, successMessage);
        allOrders = null;
        orderHistory = null;
        message = result.getMessage();
        return ADMIN_ORDERS_REDIRECT;
    }

    public String delete(Long orderId) {
        OperationResult<Void> result = orderFacade.deleteOrder(orderId);
        allOrders = null;
        orderHistory = null;
        message = result.getMessage();
        return ADMIN_ORDERS_REDIRECT;
    }

    public List<Order> getOrderHistory() {
        if (sessionBean.isLoggedIn() && orderHistory == null) {
            orderHistory = orderFacade.findByUserId(sessionBean.getUser().getId());
        }
        return orderHistory;
    }

    public List<Order> getAllOrders() {
        if (allOrders == null) {
            allOrders = orderFacade.findAllOrders();
        }
        return allOrders;
    }

    public int getWaitingOrderCount() {
        return orderFacade.countWaitingOrders(getAllOrders());
    }

    public int getOrderCount() {
        return getAllOrders().size();
    }

    public int getDeliveredOrderCount() {
        return orderFacade.countDeliveredOrders(getAllOrders());
    }

    public double getTotalOrderAmount() {
        return orderFacade.calculateOrderTotal(getAllOrders());
    }

    public boolean isNoOrders() {
        return getAllOrders().isEmpty();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}



