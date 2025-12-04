/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.core.base.view.widget;

import id.my.mdn.kupu.core.local.view.util.DatabaseResourceBundle;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.FacesMessage.Severity;
import jakarta.faces.context.FacesContext;
import java.util.ResourceBundle;

/**
 *
 * @author aphasan
 */
@ApplicationScoped
public class Toast {
    
    public static final boolean KEEP_ALIVE = true;

    public static void error(String msg, String... params) {
        show(FacesMessage.SEVERITY_ERROR, msg, false, params);
    }

    public static void error(String msg, boolean keepAlive, String... params) {
        show(FacesMessage.SEVERITY_ERROR, msg, keepAlive, params);
    }

    public static void info(String msg, String... params) {
        show(FacesMessage.SEVERITY_INFO, msg, false, params);
    }

    public static void info(String msg, boolean keepAlive, String... params) {
        show(FacesMessage.SEVERITY_INFO, msg, keepAlive, params);
    }

    public static void warning(String msg, String... params) {
        show(FacesMessage.SEVERITY_WARN, msg, false, params);
    }

    public static void warning(String msg, boolean keepAlive, String... params) {
        show(FacesMessage.SEVERITY_WARN, msg, keepAlive, params);
    }

    public static void fatal(String msg, String... params) {
        show(FacesMessage.SEVERITY_FATAL, msg, false, params);
    }

    public static void fatal(String msg, boolean keepAlive, String... params) {
        show(FacesMessage.SEVERITY_FATAL, msg, keepAlive, params);
    }

    private static void show(Severity severity, String value, boolean keepAlive, String... params) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = ResourceBundle.getBundle(DatabaseResourceBundle.class.getCanonicalName());
        String message = bundle.containsKey(value) ? bundle.getString(value) : value;

        if (message == null || message.isEmpty()) {
            message = value;
        }

        int i = 0;
        for (String param : params) {
            message = message.replaceAll("\\{" + i + "\\}", param);
            i++;
        }

        context.addMessage(null, new FacesMessage(severity, message, message));
        
        if(keepAlive) {
            context.getExternalContext().getFlash().setKeepMessages(true);
        }
    }
}
