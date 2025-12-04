/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.core.security.service;

import id.my.mdn.kupu.core.security.model.ApplicationSecurityMap;
import id.my.mdn.kupu.core.security.model.ApplicationUser;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author aphasan
 */
@Named(value = "loginPage")
@RequestScoped
public class LoginPage implements Serializable {

    private static final Logger Log = Logger.getLogger(LoginPage.class.getCanonicalName());

    @NotNull
    private String username;

    @NotNull
    private String password;

    @Inject
    private ApplicationSecurityService securityService;

    @Inject
    private SecurityContext securityContext;

    @Inject
    private FacesContext facesContext;

    public void login() throws IOException {
        switch (continueAuthentication()) {
            case SEND_CONTINUE:
                facesContext.responseComplete();
                break;
            case SEND_FAILURE:
                facesContext.addMessage(
                        "messages",
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Login failed",
                                null
                        )
                );
                break;
            case SUCCESS:
                facesContext.responseComplete();
                break;
            case NOT_DONE:
                break;
        }
    }

    public void logout(String landingPage) throws ServletException, IOException {
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        request.logout();
        request.getSession().invalidate();
        externalContext.redirect(landingPage);
    }

    public void logout() throws ServletException, IOException {
        logout("/");
    }

    private AuthenticationStatus continueAuthentication() {
        ExternalContext externalContext = facesContext.getExternalContext();
        AuthenticationStatus authenticationStatus = securityContext.authenticate(
                (HttpServletRequest) externalContext.getRequest(),
                (HttpServletResponse) externalContext.getResponse(),
                AuthenticationParameters.withParams().credential(
                        new UsernamePasswordCredential(username, password)
                )
        );

        return authenticationStatus;
    }

    public void landUser() {
        Principal principal = securityContext.getCallerPrincipal();
        if (principal == null) {
            return;
        }
        ApplicationUser appUser = securityService.findUserByUsername(principal.getName());
        List<ApplicationSecurityMap> securityMaps = securityService.getSecurityMap(appUser);
        String landingPage = null;
        for (ApplicationSecurityMap securityMap : securityMaps) {
            if (securityMap.isMainGroup()) {
                landingPage = securityMap.getGroup().getLandingPage();
                break;
            }
        }

        if (landingPage != null) {
            try {
                facesContext.getExternalContext().redirect(landingPage);
            } catch (IOException ex) {
                System.getLogger(LoginPage.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
