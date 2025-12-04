/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.core.reporting.ds;

import net.sf.jasperreports.engine.JREmptyDataSource;

/**
 *
 * @author aphasan
 */
public class EmptyDataSource implements ReportDataSource {

    @Override
    public Object get() {
        return new JREmptyDataSource();
    }
    
}
