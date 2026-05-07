package com.market.bean;

import com.market.entity.Kullanici;
import com.market.enums.Rol;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("sessionBean")
@SessionScoped
public class SessionBean implements Serializable {

    private Kullanici kullanici;

    public boolean isGirisYapmis() {
        return kullanici != null;
    }

    public boolean isAdmin() {
        return isGirisYapmis() && kullanici.getRol() == Rol.ADMIN;
    }

    public String cikisYap() {
        kullanici = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    public Kullanici getKullanici() {
        return kullanici;
    }

    public void setKullanici(Kullanici kullanici) {
        this.kullanici = kullanici;
    }
}