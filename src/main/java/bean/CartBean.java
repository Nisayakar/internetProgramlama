package bean;

import entity.Cart;
import entity.CartItem;
import facade.OperationResult;
import facadeLocal.CartFacadeLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("cartBean")
@SessionScoped
public class CartBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private CartFacadeLocal cartFacade;

    @Inject
    private SessionBean sessionBean;

    private Cart cart;
    private String message;

    @PostConstruct
    public void init() {
        cart = cartFacade.createTemporaryCart();
    }

    public void addToCart(Long productId) {
        OperationResult<Cart> result = cartFacade.addProduct(sessionBean.getUser(), cart, productId);
        if (result.getData() != null) {
            cart = result.getData();
        }
        message = result.getMessage();
    }

    public void decreaseQuantity(CartItem item) {
        OperationResult<Cart> result = cartFacade.decreaseQuantity(sessionBean.getUser(), cart, item);
        cart = result.getData();
        message = result.getMessage();
    }

    public void removeFromCart(CartItem item) {
        OperationResult<Cart> result = cartFacade.removeItem(sessionBean.getUser(), cart, item);
        cart = result.getData();
        message = result.getMessage();
    }

    public void clearCart() {
        OperationResult<Cart> result = cartFacade.clearCart(sessionBean.getUser(), cart);
        cart = result.getData();
        message = null;
    }

    public boolean isCartEmpty() {
        return getCart().getItems().isEmpty();
    }

    public int getCartItemCount() {
        return cartFacade.countItems(getCart());
    }

    public Cart getCart() {
        return getActiveCart();
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private Cart getActiveCart() {
        cart = cartFacade.findActiveCart(sessionBean.getUser(), cart);
        return cart;
    }
}



