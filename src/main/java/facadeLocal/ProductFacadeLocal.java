package facadeLocal;

import entity.Product;
import jakarta.ejb.Local;
import jakarta.servlet.http.Part;
import java.util.List;

@Local
public interface ProductFacadeLocal {
    Product saveProduct(Product product, Long categoryId, Part uploadedImage);
    boolean deleteProduct(Product product);
    Product find(Long id);
    List<Product> findAllProducts();
}


