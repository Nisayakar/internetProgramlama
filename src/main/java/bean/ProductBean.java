package bean;

import entity.Category;
import entity.Product;
import facade.OperationResult;
import facadeLocal.ProductFacadeLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import jakarta.servlet.http.Part;

@Named("productBean")
@ViewScoped
public class ProductBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ProductFacadeLocal productFacade;

    private Product product;
    private List<Product> allProducts;
    private List<Category> categories;
    private Long selectedCategoryId;
    private Long selectedShowcaseCategoryId;
    private Long detailProductId;
    private Product detailProduct;
    private Part uploadedImage;
    private String message;

    @PostConstruct
    public void init() {
        product = new Product();
        allProducts = productFacade.findAllProducts();
        categories = productFacade.findAllCategories();
        selectedCategoryId = null;
    }

    public void save() {
        OperationResult<Product> result = productFacade.saveProduct(product, selectedCategoryId, uploadedImage);
        message = result.getMessage();
        if (!result.isSuccess()) {
            return;
        }
        init();
    }

    public void delete(Product u) {
        OperationResult<Void> result = productFacade.deleteProduct(u);
        message = result.getMessage();
        if (result.isSuccess()) {
            init();
        }
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
        message = null;
    }

    public void selectCategory(Long categoryId) {
        selectedShowcaseCategoryId = categoryId;
    }

    public void showAllCategories() {
        selectedShowcaseCategoryId = null;
    }

    public void loadProductDetail() {
        if (detailProductId == null) {
            detailProduct = null;
            return;
        }

        detailProduct = productFacade.find(detailProductId);
    }

    public List<Product> getShowcaseProducts() {
        if (selectedShowcaseCategoryId == null) {
            return getAllProducts();
        }
        return productFacade.findByCategory(selectedShowcaseCategoryId);
    }

    public int getLowStockWarningCount() {
        return productFacade.countLowStockProducts();
    }

    public double getTotalInventoryValue() {
        return productFacade.calculateInventoryValue();
    }

    public int getProductCount() {
        return getAllProducts().size();
    }

    public int getCategoryCount() {
        return getCategories().size();
    }

    public boolean isNoCategories() {
        return categories == null || categories.isEmpty();
    }

    public boolean isNoProducts() {
        return getAllProducts().isEmpty();
    }


    public Product getProduct() {
        if (product == null) {
            product = new Product();
        }
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<Product> getAllProducts() {
        allProducts = productFacade.findAllProducts();
        return allProducts;
    }

    public void setAllProducts(List<Product> allProducts) {
        this.allProducts = allProducts;
    }

    public List<Category> getCategories() {
        categories = productFacade.findAllCategories();
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Long getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public void setSelectedCategoryId(Long selectedCategoryId) {
        this.selectedCategoryId = selectedCategoryId;
    }

    public Long getSelectedShowcaseCategoryId() {
        return selectedShowcaseCategoryId;
    }

    public void setSelectedShowcaseCategoryId(Long selectedShowcaseCategoryId) {
        this.selectedShowcaseCategoryId = selectedShowcaseCategoryId;
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

    public void setDetailProduct(Product detailProduct) {
        this.detailProduct = detailProduct;
    }

    public Part getUploadedImage() {
        return uploadedImage;
    }

    public void setUploadedImage(Part uploadedImage) {
        this.uploadedImage = uploadedImage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}



