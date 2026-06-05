package bean;

import entity.User;
import enums.Role;
import facade.OperationResult;
import facadeLocal.UserFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("userBean")
@ViewScoped
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UserFacadeLocal userFacade;

    @Inject
    private SessionBean sessionBean;

    private User newUser;
    private User adminUser;
    private List<User> allUsers;
    private String adminPassword;
    private String loginEmail;
    private String loginPassword;
    private String message;

    public String register() {
        OperationResult<Void> result = userFacade.register(getNewUser());
        if (!result.isSuccess()) {
            message = result.getMessage();
            return null;
        }

        newUser = new User();
        message = null;
        return "/login.xhtml?faces-redirect=true";
    }

    public String login() {
        OperationResult<User> result = userFacade.login(loginEmail, loginPassword);

        if (result.isSuccess()) {
            User user = result.getData();
            sessionBean.setUser(user);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", user);
            message = null;

            if (user.getRole() == Role.ADMIN) {
                return "/panel/products.xhtml?faces-redirect=true";
            }
            return "/index.xhtml?faces-redirect=true";
        }

        message = result.getMessage();
        return null;
    }

    public void saveAdmin() {
        OperationResult<Void> result = userFacade.saveAdmin(getAdminUser(), adminPassword);
        message = result.getMessage();
        if (!result.isSuccess()) {
            if ("Kullanıcı bulunamadı.".equals(message)) {
                clearAdminForm();
            }
            return;
        }

        clearAdminForm();
    }

    public void edit(User user) {
        adminUser = new User();
        adminUser.setId(user.getId());
        adminUser.setFullName(user.getFullName());
        adminUser.setEmail(user.getEmail());
        adminUser.setRole(user.getRole());
        adminPassword = null;
        message = null;
    }

    public void delete(User user) {
        OperationResult<Void> result = userFacade.deleteUser(user, sessionBean.getUser());
        message = result.getMessage();
        if (!result.isSuccess()) {
            return;
        }

        clearAdminForm();
    }

    public void clearAdminForm() {
        adminUser = new User();
        adminUser.setRole(Role.CUSTOMER);
        adminPassword = null;
    }

    public String logout() {
        return sessionBean.logout();
    }

    public List<User> getAllUsers() {
        allUsers = userFacade.findAllUsers();
        return allUsers;
    }

    public void setAllUsers(List<User> allUsers) {
        this.allUsers = allUsers;
    }

    public int getUserCount() {
        return getAllUsers().size();
    }

    public Role[] getRoles() {
        return Role.values();
    }

    public User getNewUser() {
        if (newUser == null) {
            newUser = new User();
        }
        return newUser;
    }

    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }

    public User getAdminUser() {
        if (adminUser == null) {
            adminUser = new User();
        }
        return adminUser;
    }

    public void setAdminUser(User adminUser) {
        this.adminUser = adminUser;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getLoginEmail() {
        return loginEmail;
    }

    public void setLoginEmail(String loginEmail) {
        this.loginEmail = loginEmail;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}



