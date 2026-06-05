package facade;

import entity.CartItem;
import entity.Category;
import entity.Comment;
import entity.Favorite;
import entity.Product;
import facadeLocal.ProductFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.faces.context.FacesContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Stateless
public class ProductFacade extends AbstractFacade implements ProductFacadeLocal {

    public OperationResult<Product> saveProduct(Product product, Long categoryId, Part uploadedImage) {
        if (categoryId != null) {
            Category selectedCategory = findCategory(categoryId);
            product.setCategory(selectedCategory);
        }

        if (uploadedImage != null && uploadedImage.getSize() > 0) {
            try {
                saveImage(product, uploadedImage);
            } catch (IOException e) {
                return OperationResult.failure("Görsel yüklenirken bir hata oluştu.");
            }
        }

        if (product.getId() == null) {
            return OperationResult.success("Ürün eklendi.", save(product));
        }

        return OperationResult.success("Ürün güncellendi.", update(product));
    }

    public OperationResult<Void> deleteProduct(Product product) {
        if (hasRelatedRecord(product.getId())) {
            return OperationResult.failure("Sepet, favori veya yorum kaydı olan ürün silinemez.");
        }
        delete(product);
        return OperationResult.success("Ürün silindi.", null);
    }

    public int countLowStockProducts() {
        int count = 0;
        for (Product product : findAllProducts()) {
            Integer stock = product.getStockQuantity();
            if (stock != null && stock < 10) {
                count++;
            }
        }
        return count;
    }

    public double calculateInventoryValue() {
        double total = 0.0;
        for (Product product : findAllProducts()) {
            if (product.getCurrentPrice() != null && product.getStockQuantity() != null) {
                total += product.getCurrentPrice() * product.getStockQuantity();
            }
        }
        return total;
    }

    public Product save(Product product) {
        this.entityManager.persist(product);
        this.entityManager.flush();
        return product;
    }

    public Product update(Product product) {
        this.entityManager.merge(product);
        this.entityManager.flush();
        return product;
    }

    public void delete(Product product) {
        Product merged = this.entityManager.merge(product);
        this.entityManager.remove(merged);
    }

    public Product find(Long id) {
        return this.entityManager.find(Product.class, id);
    }

    public List<Product> findAllProducts() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        CriteriaQuery<Product> all = cq.select(root).orderBy(cb.asc(root.get("name")));
        TypedQuery<Product> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public List<Product> findByCategory(Long categoryId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.where(cb.equal(root.get("category").get("id"), categoryId));
        CriteriaQuery<Product> all = cq.select(root).orderBy(cb.asc(root.get("name")));
        TypedQuery<Product> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public List<Category> findAllCategories() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Category> cq = cb.createQuery(Category.class);
        Root<Category> root = cq.from(Category.class);
        CriteriaQuery<Category> all = cq.select(root).orderBy(cb.asc(root.get("name")));
        TypedQuery<Category> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public Category findCategory(Long categoryId) {
        return this.entityManager.find(Category.class, categoryId);
    }

    public boolean hasProductInCategory(Long categoryId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("category").get("id"), categoryId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long count = q.getSingleResult();
        return count > 0;
    }

    public boolean hasRelatedRecord(Long productId) {
        return hasCartItem(productId) || hasFavorite(productId) || hasComment(productId);
    }

    private boolean hasCartItem(Long productId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<CartItem> root = cq.from(CartItem.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("product").get("id"), productId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        return q.getSingleResult() > 0;
    }

    private boolean hasFavorite(Long productId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Favorite> root = cq.from(Favorite.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("product").get("id"), productId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        return q.getSingleResult() > 0;
    }

    private boolean hasComment(Long productId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Comment> root = cq.from(Comment.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("product").get("id"), productId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        return q.getSingleResult() > 0;
    }

    private void saveImage(Product product, Part uploadedImage) throws IOException {
        String fileName = uploadedImage.getSubmittedFileName();
        String extension = fileExtension(fileName);
        String uniqueFileName = UUID.randomUUID() + extension;
        Path uploadsDirectory = findUploadsDirectory();

        Files.createDirectories(uploadsDirectory);
        Path targetFile = uploadsDirectory.resolve(uniqueFileName);

        try (InputStream input = uploadedImage.getInputStream()) {
            Files.copy(input, targetFile);
        }

        product.setImageUrl("/resources/uploads/" + uniqueFileName);
    }

    private Path findUploadsDirectory() {
        String realPath = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRealPath("/resources/uploads");

        if (realPath != null) {
            return Paths.get(realPath);
        }

        return Paths.get(System.getProperty("user.dir"), "src", "main", "webapp", "resources", "uploads");
    }

    private String fileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".jpg";
        }

        String extension = fileName.substring(fileName.lastIndexOf('.')).toLowerCase(Locale.ROOT);
        if (extension.matches("\\.(jpg|jpeg|png|webp|gif)")) {
            return extension;
        }

        return ".jpg";
    }
}


