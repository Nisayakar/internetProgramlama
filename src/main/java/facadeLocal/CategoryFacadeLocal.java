package facadeLocal;

import entity.Category;
import facade.OperationResult;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface CategoryFacadeLocal {
    OperationResult<Void> saveCategory(Category category);
    OperationResult<Void> deleteCategory(Category category);
    Category save(Category category);
    Category update(Category category);
    void delete(Category category);
    Category find(Long id);
    List<Category> findAllCategories();
    boolean hasProduct(Long categoryId);
}


