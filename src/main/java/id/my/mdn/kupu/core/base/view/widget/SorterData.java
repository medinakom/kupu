/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.core.base.view.widget;

import id.my.mdn.kupu.core.base.util.EntityUtil;
import id.my.mdn.kupu.core.base.view.annotation.SorterField.Order;
import id.my.mdn.kupu.core.base.view.annotation.SorterField.Sort;
import java.util.Objects;

/**
 *
 * @author aphasan
 */
public final class SorterData {
    
    private final String field;
    private String order;
    private boolean sort;
    private final String label;

    public SorterData(String field, Order order, Sort sort, String label) {
        this.field = field;
        this.order = order.getSql();
        this.sort = sort.isAutosort();
        this.label = label;
    }

    public static SorterData by(String field, Order order, Sort sort) {
        return new SorterData(field, order, sort, field);
    }

    public static SorterData by(String field, Order order) {
        return new SorterData(field, order.getSql());
    }

    public static SorterData by(String field) {
        return new SorterData(field);
    }

    public SorterData(String field, Order order, Sort sort) {
        this.field = field;
        this.order = order.getSql();
        this.sort = sort.isAutosort();
        this.label = field;
    }

    public SorterData(String field, String order) {
        this.field = field;
        this.order = order;
        this.sort = Sort.MANUAL.isAutosort();
        this.label = field;
    }
    
    public SorterData(String... attrs) {
        this(attrs[0], attrs[1]);
    }

    @Override
    public String toString() {
        return EntityUtil.createStringId(field, order);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.field);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SorterData other = (SorterData) obj;
        return Objects.equals(this.field, other.field);
    }

    public SorterData(String field) {
        this(field, Order.ASC, Sort.MANUAL, field);
    }

    public String getField() {
        return field;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Order getSortOrder() {
        return Order.valueOf(this.order);
    }

    public void setSortOrder(Order order) {
        this.order = order.getSql();
    }

    public boolean isSort() {
        return sort;
    }

    public void setSort(boolean sort) {
        this.sort = sort;
    }

    public String getLabel() {
        return label;
    }
    
}
