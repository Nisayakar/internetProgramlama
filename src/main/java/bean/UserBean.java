package bean;

import entity.User;
import enums.Role;
import facadeLocal.UserFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("userBean")
@ViewScoped
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UserFacadeLocal userFacade;

    @Inject
    private SessionBean sessionBean;

    private User newUser;
    private String loginEmail;
    private String loginPassword;

    public String register() {
        if (!userFacade.register(getNewUser())) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Bu e-posta adresi zaten kullanılıyor.");
            return null;
        }

        newUser = new User();
        return "/login.xhtml?faces-redirect=true";
    }

    public String login() {
        User user = userFacade.login(loginEmail, loginPassword);

        if (user != null) {
            sessionBean.setUser(user);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", user);

            if (user.getRole() == Role.ADMIN) {
                return "/panel/products.xhtml?faces-redirect=true";
            }
            return "/index.xhtml?faces-redirect=true";
        }

        addMessage(FacesMessage.SEVERITY_ERROR, "E-posta veya şifre hatalı.");
        return null;
    }

    public String logout() {
        return sessionBean.logout();
    }

    public User getNewUser() {
        if (newUser == null) {
            newUser = new User();
        }
        return newUser;
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

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, text));
    }
}



