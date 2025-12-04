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
package id.my.mdn.kupu.core.security.view;

import id.my.mdn.kupu.core.base.view.ChildPage;
import id.my.mdn.kupu.core.base.view.annotation.Bookmarked;
import id.my.mdn.kupu.core.base.view.annotation.Creator;
import id.my.mdn.kupu.core.base.view.annotation.Deleter;
import id.my.mdn.kupu.core.base.view.annotation.Editor;
import id.my.mdn.kupu.core.security.view.widget.ApplicationSecurityGroupList;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author mbahbejo
 */
@Named(value = "applicationSecurityGroupPage")
@ViewScoped
public class ApplicationSecurityGroupPage extends ChildPage implements Serializable {

    @Inject
    @Bookmarked
    private ApplicationSecurityGroupList dataView;

    @PostConstruct
    @Override
    public void init() {
        super.init();
    }

    @Creator(of = "dataView")
    public void create() {
        gotoChild(ApplicationSecurityGroupEditorPage.class)
                .open();
    }

    @Editor(of = "dataView")
    public void openDataEditor() {
        gotoChild(GroupAccessControlPage.class)
                .addParam("sg")
                .withValues(dataView.getSelected())
                .open();
    }

    @Deleter(of = "dataView")
    public void delete() {
        dataView.deleteSelected();
    }

    public ApplicationSecurityGroupList getDataView() {
        return dataView;
    }

}
