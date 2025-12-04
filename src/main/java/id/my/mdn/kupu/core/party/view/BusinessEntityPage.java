/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.core.party.view;

import id.my.mdn.kupu.core.base.util.RequestedView;
import id.my.mdn.kupu.core.base.view.Page;
import id.my.mdn.kupu.core.base.view.annotation.Bookmarked;
import id.my.mdn.kupu.core.base.view.annotation.Creator;
import id.my.mdn.kupu.core.base.view.annotation.Deleter;
import id.my.mdn.kupu.core.base.view.annotation.Editor;
import static id.my.mdn.kupu.core.base.view.widget.Selector.SINGLE;
import id.my.mdn.kupu.core.party.entity.BusinessEntity;
import id.my.mdn.kupu.core.party.view.widget.BusinessEntityTree;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author Arief Prihasanto <aphasan57 at gmail.com>
 */
@Named(value = "entitasPage")
@ViewScoped
public class BusinessEntityPage extends Page implements Serializable {

    @Bookmarked
    @Inject
    private BusinessEntityTree dataView;

    @Override
    @PostConstruct
    public void init() {
        super.init();
        dataView.setSelectionMode(() -> SINGLE);
    }

    @Creator(of = "dataView")
    public void openDataCreator() {
        RequestedView creatorPage = gotoChild(BusinessEntityEditorPage.class);

        BusinessEntity parent = dataView.getSelected();

        if (parent != null) {
            creatorPage.addParam("parent").withValues(parent);
            creatorPage.addParam("currentDate")
                    .withValues(LocalDate.now());
        }

        creatorPage.open();
    }

    @Editor(of = "dataView")
    public void openDataEditor() {
        gotoChild(BusinessEntityDetailPage.class)
                .addParam("bp")
                .withValues(dataView.getSelected())
                .open();
    }

    @Deleter(of = "dataView")
    public void openDataDeleter() {
        dataView.deleteSelected();
    }

    public BusinessEntityTree getDataView() {
        return dataView;
    }

}
