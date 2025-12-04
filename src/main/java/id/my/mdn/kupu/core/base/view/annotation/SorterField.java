/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/AnnotationType.java to edit this template
 */
package id.my.mdn.kupu.core.base.view.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author aphasan
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SorterField {
    static enum Sort {
        AUTO(true),
        MANUAL(false);
        
        private final boolean autosort;

        private Sort(boolean autosort) {
            this.autosort = autosort;
        }

        public boolean isAutosort() {
            return autosort;
        }
    }
    
    static enum Order {
        ASC("ASC"),
        DESC("DESC");
        
        private final String sql;

        private Order(String sql) {
            this.sql = sql;
        }

        public String getSql() {
            return sql;
        }
    }
    
    String value() default "";
    String label() default "";
    Order order() default Order.ASC;
    Sort sort() default Sort.AUTO;
}
