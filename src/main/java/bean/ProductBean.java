package bean;

import entity.Product;
import facadeLocal.ProductFacadeLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import java.io.Serializable;
import java.util.List;

@Named("productBean")
@ViewScoped
public class ProductBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ProductFacadeLocal productFacade;

    private Product product;
    private List<Product> allProducts;
    private Long selectedCategoryId;
    private Long detailProductId;
    private Product detailProduct;
    private Part uploadedImage;

    @PostConstruct
    public void init() {
        product = new Product();
        allProducts = productFacade.findAllProducts();
        selectedCategoryId = null;
    }

    public void save() {
        boolean newRecord = product.getId() == null;
        Product saved = productFacade.saveProduct(product, selectedCategoryId, uploadedImage);
        if (saved == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Görsel yüklenirken bir hata oluştu.");
            return;
        }
        addMessage(FacesMessage.SEVERITY_INFO, newRecord ? "Ürün eklendi." : "Ürün güncellendi.");
        init();
    }

    public void delete(Product u) {
        if (!productFacade.deleteProduct(u)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Sepet, favori veya yorum kaydı olan ürün silinemez.");
            return;
        }
        addMessage(FacesMessage.SEVERITY_INFO, "Ürün silindi.");
        init();
    }

    public void edit(Product u) {
        this.product = u;
        if (product.getCategory() != null) {
            this.selectedCategoryId = product.getCategory().getId();
        }
    }

    public void clear() {
        product = new Product();
        selectedCategoryId = null;
        uploadedImage = null;
    }

    public void loadProductDetail() {
        if (detailProductId == null) {
            detailProduct = null;
            return;
        }

        detailProduct = productFacade.find(detailProductId);
    }

    public boolean isNoProducts() {
        return allProducts.isEmpty();
    }


    public Product getProduct() {
        if (product == null) {
            product = new Product();
        }
        return product;
    }

    public List<Product> getAllProducts() {
        return allProducts;
    }

    public Long getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public void setSelectedCategoryId(Long selectedCategoryId) {
        this.selectedCategoryId = selectedCategoryId;
    }

    public Long getDetailProductId() {
        return detailProductId;
    }

    public void setDetailProductId(Long detailProductId) {
        this.detailProductId = detailProductId;
    }

    public Product getDetailProduct() {
        return detailProduct;
    }

    public Part getUploadedImage() {
        return uploadedImage;
    }

    public void setUploadedImage(Part uploadedImage) {
        this.uploadedImage = uploadedImage;
    }

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, text));
    }
}



