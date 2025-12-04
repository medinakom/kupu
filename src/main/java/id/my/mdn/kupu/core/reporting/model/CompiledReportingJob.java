/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.core.reporting.model;

import id.my.mdn.kupu.core.reporting.ds.ReportDataSource;
import java.util.Map;

/**
 *
 * @author Arief Prihasanto <aphasan57 at gmail.com>
 */
public class CompiledReportingJob {

    private final byte[] compiledTemplate;

    private final Map<String, Object> parameters;

    private final ReportDataSource dataSource;

    public CompiledReportingJob(ReportDataSource dataSource,
            Map<String, Object> parameters,
            byte[] templateName) {
        this.compiledTemplate = templateName;
        this.parameters = parameters;
        this.dataSource = dataSource;
    }

    public byte[] getCompiledTemplate() {
        return compiledTemplate;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public ReportDataSource getDataSource() {
        return dataSource;
    }
}
