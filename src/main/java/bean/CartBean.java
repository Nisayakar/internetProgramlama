package bean;

import entity.Cart;
import entity.CartItem;
import entity.Product;
import facadeLocal.CartFacadeLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
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

    private static final String CART_LOADED_REQUEST_KEY = "cartBean.cartLoaded";

    private Cart cart;

    @PostConstruct
    public void init() {
        cart = cartFacade.createTemporaryCart();
    }

    public void addToCart(Long productId) {
        if (!sessionBean.isLoggedIn()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Sepete eklemek için giriş yapmalısınız.");
            return;
        }

        Product product = cartFacade.findProduct(productId);
        if (isUnavailable(product)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Bu ürün stokta yok.");
            return;
        }

        Cart activeCart = cartFacade.findActiveCart(sessionBean.getUser(), cart);
        if (hasReachedStockLimit(activeCart, product)) {
            cart = activeCart;
            addMessage(FacesMessage.SEVERITY_WARN, "Stok sınırına ulaşıldı.");
            return;
        }

        cart = cartFacade.addProduct(sessionBean.getUser(), cart, productId);
        addMessage(FacesMessage.SEVERITY_INFO, "Ürün sepete eklendi.");
    }

    public void decreaseQuantity(CartItem item) {
        cart = cartFacade.decreaseQuantity(sessionBean.getUser(), cart, item);
        addMessage(FacesMessage.SEVERITY_INFO, "Sepet güncellendi.");
    }

    public void removeFromCart(CartItem item) {
        cart = cartFacade.removeItem(sessionBean.getUser(), cart, item);
        addMessage(FacesMessage.SEVERITY_INFO, "Ürün sepetten çıkarıldı.");
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

    private Cart getActiveCart() {
        if (!FacesContext.getCurrentInstance().getExternalContext().getRequestMap().containsKey(CART_LOADED_REQUEST_KEY)) {
            cart = cartFacade.findActiveCart(sessionBean.getUser(), cart);
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(CART_LOADED_REQUEST_KEY, true);
        }
        return cart;
    }

    private boolean isUnavailable(Product product) {
        return product == null
                || product.getStockQuantity() == null
                || product.getStockQuantity() <= 0;
    }

    private boolean hasReachedStockLimit(Cart activeCart, Product product) {
        for (CartItem item : activeCart.getItems()) {
            if (isSameProduct(item, product) && item.getQuantity() >= product.getStockQuantity()) {
                return true;
            }
        }

        return false;
    }

    private boolean isSameProduct(CartItem item, Product product) {
        return item.getProduct().getId().equals(product.getId());
    }

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, text));
    }
}



