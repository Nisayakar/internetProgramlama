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

    // CRUD Operations
    public Product saveProduct(Product product, Long categoryId, Part uploadedImage) {
        if (categoryId != null) {
            Category selectedCategory = findCategory(categoryId);
            product.setCategory(selectedCategory);
        }

        if (uploadedImage != null && uploadedImage.getSize() > 0) {
            try {
                saveImage(product, uploadedImage);
            } catch (IOException e) {
                return null;
            }
        }

        if (product.getId() == null) {
            return save(product);
        }

        return update(product);
    }

    public boolean deleteProduct(Product product) {
        if (hasRelatedRecord(product.getId())) {
            return false;
        }
        delete(product);
        return true;
    }

    // Query Operations
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

    // Private Helpers
    private Product save(Product product) {
        this.entityManager.persist(product);
        this.entityManager.flush();
        return product;
    }

    private Product update(Product product) {
        this.entityManager.merge(product);
        this.entityManager.flush();
        return product;
    }

    private void delete(Product product) {
        Product merged = this.entityManager.merge(product);
        this.entityManager.remove(merged);
    }

    private Category findCategory(Long categoryId) {
        return this.entityManager.find(Category.class, categoryId);
    }

    private boolean hasRelatedRecord(Long productId) {
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


