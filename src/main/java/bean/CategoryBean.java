package bean;

import entity.Category;
import facadeLocal.CategoryFacadeLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
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

    @PostConstruct
    public void init() {
        allCategories = categoryFacade.findAllCategories();
    }

    public void save() {
        boolean newRecord = getNewCategory().getId() == null;
        categoryFacade.saveCategory(getNewCategory());
        addMessage(FacesMessage.SEVERITY_INFO, newRecord ? "Kategori eklendi." : "Kategori güncellendi.");
        newCategory = new Category();
        allCategories = categoryFacade.findAllCategories();
    }

    public void edit(Category category) {
        this.newCategory = category;
    }

    public void delete(Category category) {
        if (!categoryFacade.deleteCategory(category)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Bu kategoriye ait ürün olduğu için silinemez.");
            return;
        }
        addMessage(FacesMessage.SEVERITY_INFO, "Kategori silindi.");
        newCategory = new Category();
        allCategories = categoryFacade.findAllCategories();
    }

    public void clear() {
        newCategory = new Category();
    }

    public List<Category> getAllCategories() {
        return allCategories;
    }

    public Category getNewCategory() {
        if (newCategory == null) {
            newCategory = new Category();
        }
        return newCategory;
    }

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, text));
    }
}



