/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.core.security;

import id.my.mdn.kupu.core.base.AbstractModule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.LoginToContinue;

/**
 *
 * @author aphasan
 */
@Named(value = "security")
@ApplicationScoped
@CustomFormAuthenticationMechanismDefinition(
        loginToContinue = @LoginToContinue(
                loginPage = "/login.xhtml",
                errorPage = ""
        )
)
public class SecurityModule extends AbstractModule {

    @Override
    protected String getLabel() {
        return "security.title";
    }

    @Override
    protected int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected boolean visible() {
        return false;
    }
    
}
