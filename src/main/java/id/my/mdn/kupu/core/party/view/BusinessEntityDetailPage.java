/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.core.party.view;

import id.my.mdn.kupu.core.base.view.ChildPage;
import id.my.mdn.kupu.core.base.view.annotation.Bookmarked;
import id.my.mdn.kupu.core.party.dao.BusinessEntityFacade;
import id.my.mdn.kupu.core.party.entity.BusinessEntity;
import id.my.mdn.kupu.core.party.entity.Organization;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Arief Prihasanto <aphasan57 at gmail.com>
 */
@Named(value = "businessEntityDetailPage")
@ViewScoped
public class BusinessEntityDetailPage extends ChildPage implements Serializable {

    @Bookmarked(name = "kp")
    private BusinessEntity businessEntity;

    @Inject
    private OrganizationDetailPage partyDetailPage;

    @Inject
    private BusinessEntityFacade businessEntityFacade;

    @PostConstruct
    @Override
    public void init() {
        super.init();
        partyDetailPage.init();        
    }
    
    @Override
    public void load() {
        partyDetailPage.setParty((Organization) businessEntity.getParty());
        partyDetailPage.setContextSupplier(() -> this);
        partyDetailPage.setUpdateListener(party -> businessEntityFacade.edit(businessEntity));
        partyDetailPage.load();
    }

    public OrganizationDetailPage getPartyDetailPage() {
        return partyDetailPage;
    }

    public void setBusinessEntity(BusinessEntity businessEntity) {
        this.businessEntity = businessEntity;
    }

    public BusinessEntity getBusinessEntity() {
        return businessEntity;
    }
}
