package facadeLocal;

import entity.Category;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface CategoryFacadeLocal {
    void saveCategory(Category category);
    boolean deleteCategory(Category category);
    List<Category> findAllCategories();
}


