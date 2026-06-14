package bean;

import entity.Address;
import facadeLocal.AddressFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("addressBean")
@ViewScoped
public class AddressBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private AddressFacadeLocal addressFacade;

    @Inject
    private SessionBean sessionBean;

    private Address address;
    private List<Address> myAddresses;

    public void save() {
        if (!sessionBean.isLoggedIn()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Adres eklemek için giriş yapmalısınız.");
            return;
        }

        boolean newRecord = getAddress().getId() == null;
        Address saved = addressFacade.saveOrUpdate(sessionBean.getUser(), getAddress());
        if (saved == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Adres bulunamadı.");
            return;
        }

        addMessage(FacesMessage.SEVERITY_INFO, newRecord ? "Adres eklendi." : "Adres güncellendi.");
        clear();
        myAddresses = null;
    }

    public void edit(Address a) {
        address = new Address();
        address.setId(a.getId());
        address.setTitle(a.getTitle());
        address.setCity(a.getCity());
        address.setDistrict(a.getDistrict());
        address.setFullAddress(a.getFullAddress());
        address.setPhone(a.getPhone());
        address.setUser(a.getUser());
    }

    public void delete(Address a) {
        if (!addressFacade.deleteForUser(sessionBean.getUser(), a)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Adres bulunamadı.");
            return;
        }
        addMessage(FacesMessage.SEVERITY_INFO, "Adres silindi.");
        myAddresses = null;
    }

    public void clear() {
        address = new Address();
    }

    public Address getAddress() {
        if (address == null) {
            address = new Address();
        }
        return address;
    }

    public List<Address> getMyAddresses() {
        if (!sessionBean.isLoggedIn()) {
            myAddresses = null;
            return myAddresses;
        }

        if (sessionBean.isLoggedIn() && myAddresses == null) {
            myAddresses = addressFacade.findByUserId(sessionBean.getUser().getId());
        }
        return myAddresses;
    }

    public boolean isNoAddresses() {
        return getMyAddresses() == null || getMyAddresses().isEmpty();
    }

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, text));
    }
}


