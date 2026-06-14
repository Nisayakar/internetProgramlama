package bean;

import entity.Order;
import entity.Product;
import enums.OrderStatus;
import facadeLocal.OrderFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
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

    public String completeOrder() {
        if (!sessionBean.isLoggedIn()) {
            return "/login.xhtml?faces-redirect=true";
        }

        Product stockProblem = orderFacade.findInsufficientStockProduct(sessionBean.getUser());
        if (stockProblem != null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Stok yetersiz: " + stockProblem.getName());
            return null;
        }

        if (!orderFacade.completeOrder(sessionBean.getUser())) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Cartiniz boş.");
            return null;
        }

        addMessage(FacesMessage.SEVERITY_INFO, "Siparişiniz alındı.");
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
        if (!orderFacade.cancelUserOrder(sessionBean.getUser(), orderId)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Sipariş bulunamadı veya artık iptal edilemez.");
            return null;
        }
        orderHistory = null;
        addMessage(FacesMessage.SEVERITY_INFO, "Sipariş iptal edildi.");
        return null;
    }

    public boolean isUserCancelable(Order order) {
        return orderFacade.canUserCancel(order);
    }

    public boolean isAdminStatusUpdatable(Order order) {
        return orderFacade.canAdminUpdateStatus(order);
    }

    private String updateStatus(Long orderId, OrderStatus status, String successMessage) {
        orderFacade.updateStatus(orderId, status);
        allOrders = null;
        orderHistory = null;
        addMessage(FacesMessage.SEVERITY_INFO, successMessage);
        return ADMIN_ORDERS_REDIRECT;
    }

    public String delete(Long orderId) {
        orderFacade.deleteOrder(orderId);
        allOrders = null;
        orderHistory = null;
        addMessage(FacesMessage.SEVERITY_INFO, "Sipariş silindi.");
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

    public boolean isNoOrders() {
        return getAllOrders().isEmpty();
    }

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, text));
    }
}



