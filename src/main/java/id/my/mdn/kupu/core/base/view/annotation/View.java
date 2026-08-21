package id.my.mdn.kupu.core.base.view.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Explicitly defines the XHTML view path for a Page class.
 * If present, ModuleUtil will use this path instead of the convention-based
 * path.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface View {
    String value();
}
