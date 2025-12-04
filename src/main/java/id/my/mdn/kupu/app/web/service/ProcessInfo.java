/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.service;

import java.io.Serializable;

/**
 *
 * @author aphasan
 */
public final class ProcessInfo implements Serializable {
    
    public final ProcessStatus status;
    public final String payload;

    public ProcessInfo(ProcessStatus status, String payload) {
        this.status = status;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return status.name();
    }
    
}
