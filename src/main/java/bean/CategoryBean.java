package bean;

import entity.Category;
import facade.OperationResult;
import facadeLocal.CategoryFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("categoryBean")
@ViewScoped
public class CategoryBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private CategoryFacadeLocal categoryFacade;

    private Category newCategory;
    private List<Category> allCategories;
    private String message;

    public void save() {
        OperationResult<Void> result = categoryFacade.saveCategory(getNewCategory());
        message = result.getMessage();
        newCategory = new Category();
    }

    public void edit(Category category) {
        this.newCategory = category;
        message = null;
    }

    public void delete(Category category) {
        OperationResult<Void> result = categoryFacade.deleteCategory(category);
        message = result.getMessage();
        if (!result.isSuccess()) {
            return;
        }
        newCategory = new Category();
    }

    public void clear() {
        newCategory = new Category();
        message = null;
    }

    public List<Category> getAllCategories() {
        allCategories = categoryFacade.findAllCategories();
        return allCategories;
    }

    public int getCategoryCount() {
        return getAllCategories().size();
    }

    public void setAllCategories(List<Category> allCategories) {
        this.allCategories = allCategories;
    }

    public Category getNewCategory() {
        if (newCategory == null) {
            newCategory = new Category();
        }
        return newCategory;
    }

    public void setNewCategory(Category newCategory) {
        this.newCategory = newCategory;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}



