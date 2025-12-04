/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.core.party.view.widget;

import id.my.mdn.kupu.core.base.dao.AbstractFacade;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.widget.AbstractMutablePagedValueList;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import id.my.mdn.kupu.core.party.dao.PartyContactMechanismFacade;
import id.my.mdn.kupu.core.party.entity.ContactType;
import id.my.mdn.kupu.core.party.entity.Party;
import id.my.mdn.kupu.core.party.entity.PartyContactMechanism;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aphasan
 */
@Named(value = "partyContactMechanismList")
@Dependent
public class PartyContactMechanismList 
        extends AbstractMutablePagedValueList<PartyContactMechanism> {

    @Inject
    private PartyContactMechanismFacade dao;
     
    private Party party;    
     
    private ContactType contactType;

    public PartyContactMechanismList() {
        super(PartyContactMechanism.class);
    }

    @PostConstruct
    public void init() {
        filter.staticFilter = this::getStaticFilter;
    }

    @Override
    protected List<PartyContactMechanism> getPagedFetchedItemsInternal(int first, int pageSize, Map<String, Object> parameters, List<FilterData> filters, List<SorterData> sorters, DefaultList<PartyContactMechanism> defaultList, AbstractFacade.DefaultChecker defaultChecker) {
        
        return dao.findAll(first, pageSize, parameters, filters, sorters);
    }

    @Override
    protected long getItemsCountInternal(Map<String, Object> parameters, List<FilterData> filters, DefaultCount defaultCount, AbstractFacade.DefaultChecker defaultChecker) {
        return dao.countAll(parameters, filters);
    }

    @Override
    protected Result<String> createInternal(PartyContactMechanism entity) {
        return dao.create(entity);
    }

    @Override
    protected Result<String> editInternal(PartyContactMechanism entity) {
        return dao.edit(entity);
    }

    @Override
    protected Result<String> deleteInternal(PartyContactMechanism entity) {
        return dao.remove(entity);
    }

    private List<FilterData> getStaticFilter() {
        List<FilterData> filters = new ArrayList<>();
        filters.add(new FilterData("party", party));
        filters.add(new FilterData("contactType", contactType));
        return filters;
    }
    
    public void makeSelectionAsDefault() {
        dao.makeSelectionAsDefault(selector.getSelection(), contactType);
        invalidate();
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public ContactType getContactType() {
        return contactType;
    }

    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }

}
