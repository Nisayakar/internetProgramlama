package bean;

import entity.Address;
import facade.OperationResult;
import facadeLocal.AddressFacadeLocal;
import jakarta.ejb.EJB;
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
    private String message;

    public void save() {
        if (!sessionBean.isLoggedIn()) {
            message = "Adres eklemek için giriş yapmalısınız.";
            return;
        }

        OperationResult<Address> result = addressFacade.saveOrUpdate(sessionBean.getUser(), getAddress());
        message = result.getMessage();
        if (!result.isSuccess()) {
            return;
        }

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
        message = null;
    }

    public void delete(Address a) {
        OperationResult<Void> result = addressFacade.deleteForUser(sessionBean.getUser(), a);
        message = result.getMessage();
        if (result.isSuccess()) {
            myAddresses = null;
        }
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

    public void setAddress(Address address) {
        this.address = address;
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

    public void setMyAddresses(List<Address> myAddresses) {
        this.myAddresses = myAddresses;
    }

    public boolean isNoAddresses() {
        return getMyAddresses() == null || getMyAddresses().isEmpty();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


