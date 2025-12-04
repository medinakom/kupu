/*
 * Copyright 2015 mbahbejo.
 * All rights reserved

 * A lot of time, effort and money is spent designing and implementing the software.
 * All system design, text, graphics, the selection and arrangement thereof, and
 * all software compilations, underlying source code, software and all other material
 * on this software are copyright mbahbejo and any affiliates.
 * 
 * In simple terms, every element of this software is protected by copyright.
 * Unless you have our express written permission, you are not allowed
 * to copy partially and or completely, modify partially and or completely,
 * use partially and or completely and or reproduce any part of this  software
 * in any way, shape and or form.
 * 
 * Taking material from other source code and or document mbahbejo and affiliates has designed is
 * also prohibited. You can be prosecuted by the licensee as well as by us as licensor.
 * 
 * Any other use of materials of this software, including reproduction for purposes other
 * than that noted in the business agreement, modification, distribution, or republication,
 * without the prior written permission of mbahbejo is strictly prohibited.
 * 
 * The source code, partially and or completely, shall not be presented and or shown
 * and or performed to public and or other parties without the prior written permission
 * of mbahbejo

 */
package id.my.mdn.kupu.core.security.view.widget;

import id.my.mdn.kupu.core.base.dao.AbstractFacade;
import id.my.mdn.kupu.core.base.util.Do;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.widget.AbstractMutablePagedValueList;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import id.my.mdn.kupu.core.security.dao.ApplicationSecurityGroupFacade;
import id.my.mdn.kupu.core.security.model.ApplicationSecurityGroup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mbahbejo
 */
@Named(value = "securityGroupList")
@Dependent
public class ApplicationSecurityGroupList
        extends AbstractMutablePagedValueList<ApplicationSecurityGroup>
        implements Serializable {

    @Inject
    private ApplicationSecurityGroupFacade dao;

    @Inject
    private ApplicationSecurityGroupFilter filterContent;

    public ApplicationSecurityGroupList() {
        super(ApplicationSecurityGroup.class);
    }

    @PostConstruct
    public void init() {
        filter.setContent(filterContent);
    }

    @Override
    protected List<ApplicationSecurityGroup> getPagedFetchedItemsInternal(int first, int pageSize, Map<String, Object> parameters, List<FilterData> filters, List<SorterData> sorters, DefaultList<ApplicationSecurityGroup> defaultList, AbstractFacade.DefaultChecker defaultChecker) {

        return dao.findAll(first, pageSize, parameters, filters, sorters);
    }

    @Override
    protected long getItemsCountInternal(Map<String, Object> parameters, List<FilterData> filters, DefaultCount defaultCount, AbstractFacade.DefaultChecker defaultChecker) {

        return dao.countAll(parameters, filters);
    }

    @Override
    protected boolean updateInternal(String field, ApplicationSecurityGroup entity, Object newValue, Map<String, Object> context) {
        switch (field) {
            case "landingPage":
                entity.setLandingPage((String) newValue);
                break;
            default:
                Do.nothing();
        }
        return super.updateInternal(field, entity, newValue, context);
    }

    @Override
    protected Result<String> createInternal(ApplicationSecurityGroup entity) {
        return dao.create(entity);
    }

    @Override
    protected Result<String> editInternal(ApplicationSecurityGroup entity) {
        return dao.edit(entity);
    }

    @Override
    protected Result<String> deleteInternal(ApplicationSecurityGroup entity) {
        return dao.remove(entity);
    }

}
