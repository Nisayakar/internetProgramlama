package facade;

import entity.Category;
import entity.Product;
import facadeLocal.CategoryFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Stateless
public class CategoryFacade extends AbstractFacade implements CategoryFacadeLocal {

    public OperationResult<Void> saveCategory(Category category) {
        if (category.getId() == null) {
            save(category);
            return OperationResult.success("Kategori eklendi.", null);
        }

        update(category);
        return OperationResult.success("Kategori güncellendi.", null);
    }

    public OperationResult<Void> deleteCategory(Category category) {
        if (hasProduct(category.getId())) {
            return OperationResult.failure("Bu kategoriye ait ürün olduğu için silinemez.");
        }

        delete(category);
        return OperationResult.success("Kategori silindi.", null);
    }

    public Category save(Category category) {
        this.entityManager.persist(category);
        this.entityManager.flush();
        return category;
    }

    public Category update(Category category) {
        this.entityManager.merge(category);
        this.entityManager.flush();
        return category;
    }

    public void delete(Category category) {
        Category merged = this.entityManager.merge(category);
        this.entityManager.remove(merged);
    }

    public Category find(Long id) {
        return this.entityManager.find(Category.class, id);
    }

    public List<Category> findAllCategories() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Category> cq = cb.createQuery(Category.class);
        Root<Category> root = cq.from(Category.class);
        CriteriaQuery<Category> all = cq.select(root).orderBy(cb.asc(root.get("name")));
        TypedQuery<Category> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public boolean hasProduct(Long categoryId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("category").get("id"), categoryId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long count = q.getSingleResult();
        return count > 0;
    }
}


