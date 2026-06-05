package facadeLocal;

import entity.Category;
import entity.Product;
import facade.OperationResult;
import jakarta.ejb.Local;
import jakarta.servlet.http.Part;
import java.util.List;

@Local
public interface ProductFacadeLocal {
    OperationResult<Product> saveProduct(Product product, Long categoryId, Part uploadedImage);
    OperationResult<Void> deleteProduct(Product product);
    int countLowStockProducts();
    double calculateInventoryValue();
    Product save(Product product);
    Product update(Product product);
    void delete(Product product);
    Product find(Long id);
    List<Product> findAllProducts();
    List<Category> findAllCategories();
    List<Product> findByCategory(Long categoryId);
    Category findCategory(Long categoryId);
    boolean hasProductInCategory(Long categoryId);
    boolean hasRelatedRecord(Long productId);
}


