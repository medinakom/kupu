/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.admin;

import id.my.mdn.kupu.core.base.view.widget.Toast;
import jakarta.inject.Named;
import java.io.Serializable;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.extensions.event.ClipboardErrorEvent;
import org.primefaces.extensions.event.ClipboardSuccessEvent;

/**
 *
 * @author Arief Prihasanto <aphasan at mdnx.dev>
 */
@Named
@ViewScoped
public class ClipboardController implements Serializable {

    private static final long serialVersionUID = 20120224L;

    public void successListener(final ClipboardSuccessEvent successEvent) {
        Toast.info("Sukses");
    }

    public void errorListener(final ClipboardErrorEvent errorEvent) {
        Toast.info("Gagal");
    }
}
