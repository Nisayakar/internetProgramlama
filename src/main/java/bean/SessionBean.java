package bean;

import entity.User;
import enums.Role;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("sessionBean")
@SessionScoped
public class SessionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private User user;

    public boolean isLoggedIn() {
        return user != null;
    }

    public boolean isAdmin() {
        return isLoggedIn() && user.getRole() == Role.ADMIN;
    }

    public String logout() {
        user = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}



