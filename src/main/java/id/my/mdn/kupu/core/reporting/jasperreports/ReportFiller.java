/*
 * Copyright 2014 Arief Prihasanto <ariefp5758 at gmail.com>.
 * All rights reserved

 * A lot of time, effort and money is spent designing and implementing the software.
 * All system design, text, graphics, the selection and arrangement thereof, and
 * all software compilations, underlying source code, software and all other material
 * on this software are copyright Arief Prihasanto <ariefp5758 at gmail.com> and any affiliates.
 * 
 * In simple terms, every element of this software is protected by copyright.
 * Unless you have our express written permission, you are not allowed
 * to copy partially and or completely, modify partially and or completely,
 * use partially and or completely and or reproduce any part of this  software
 * in any way, shape and or form.
 * 
 * Taking material from other source code and or document Arief Prihasanto <ariefp5758 at gmail.com> and affiliates has designed is
 * also prohibited. You can be prosecuted by the licensee as well as by us as licensor.
 * 
 * Any other use of materials of this software, including reproduction for purposes other
 * than that noted in the business agreement, modification, distribution, or republication,
 * without the prior written permission of Arief Prihasanto <ariefp5758 at gmail.com> is strictly prohibited.
 * 
 * The source code, partially and or completely, shall not be presented and or shown
 * and or performed to public and or other parties without the prior written permission
 * of Arief Prihasanto <ariefp5758 at gmail.com>

 */
package id.my.mdn.kupu.core.reporting.jasperreports;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import id.my.mdn.kupu.core.reporting.ds.ReportDataSource;
import id.my.mdn.kupu.core.reporting.exception.ReportDeserializationException;
import id.my.mdn.kupu.core.reporting.exception.ReportFillingException;
import id.my.mdn.kupu.core.reporting.model.CompiledReportingJob;
import id.my.mdn.kupu.core.reporting.service.ReportingJobQueue;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import static net.sf.jasperreports.engine.query.EjbqlConstants.PARAMETER_JPA_ENTITY_MANAGER;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Named
@RequestScoped
public class ReportFiller {

    @PersistenceUnit(unitName = "KupuPersistenceUnit")
    private EntityManagerFactory emf;

    @Inject
    private ReportingJobQueue jobQueue;

    public void fillAsPdf(InputStream templateInputStream, Map<String, Object> parameters,
            ReportDataSource dataSource, OutputStream outputStream) throws ReportFillingException {

        try {
            parameters.put(JRParameter.REPORT_LOCALE, Locale.of("in", "ID"));
            JasperRunManager.runReportToPdfStream(templateInputStream, outputStream,
                    parameters, (JRDataSource) dataSource.get());
        } catch (JRException ex) {
            throw new ReportFillingException(ex);
        } finally {
            jobQueue.clear();
            jobQueue.setBusy(false);
        }
    }

    private InputStream deserializeTemplate(byte[] compiledTemplate)
            throws ReportDeserializationException {
        ByteArrayInputStream bis = new ByteArrayInputStream(compiledTemplate);
        return bis;
    }

    public void fillAsCompositePdf(OutputStream outputStream, List<CompiledReportingJob> jobs) throws ReportDeserializationException {
        try {
            List<JasperPrint> jasperPrints = new ArrayList<>();
            for (CompiledReportingJob job : jobs) {
                Map<String, Object> parameters = job.getParameters();
                parameters.put(JRParameter.REPORT_LOCALE, Locale.of("in", "ID"));
                jasperPrints.add(
                        JasperFillManager.fillReport(
                                deserializeTemplate(job.getCompiledTemplate()),
                                parameters,
                                (JRDataSource) job.getDataSource().get()
                        )
                );
            }
            exportCombine(outputStream, jasperPrints);
        } catch (JRException ex) {
            Logger.getLogger(ReportFiller.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            jobQueue.clear();
            jobQueue.setBusy(false);
        }
    }

    public void exportCombine(OutputStream output, List<JasperPrint> jasperPrints) {
        try {
            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, output);
            document.open();
            for (JasperPrint filledReport : jasperPrints) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                export(baos, filledReport);
                PdfReader reader = new PdfReader(baos.toByteArray());
                int pages = reader.getNumberOfPages();
                for (int i = 1; i <= pages; i++) {
                    PdfImportedPage page = copy.getImportedPage(reader, i);
                    copy.addPage(page);
                }
            }

            document.close();
        } catch (DocumentException | IOException ex) {
            Logger.getLogger(ReportExporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void export(OutputStream outputStream, JasperPrint jasperPrint) {
        try {
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
        } catch (JRException ex) {
            Logger.getLogger(ReportExporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fillAsPdf(InputStream templateInputStream, Map<String, Object> parameters,
            OutputStream outputStream) throws ReportFillingException {

        parameters.put(PARAMETER_JPA_ENTITY_MANAGER, emf.createEntityManager());
        try {
            JasperRunManager.runReportToPdfStream(templateInputStream, outputStream, parameters);
        } catch (JRException ex) {
            throw new ReportFillingException(ex);
        } finally {
            jobQueue.clear();
            jobQueue.setBusy(false);
        }
    }

    public void fillAsXlsx(InputStream templateInputStream, Map<String, Object> parameters,
            ReportDataSource dataSource, OutputStream os)
            throws ReportFillingException {

        try {
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(
                    new SimpleExporterInput(
                            JasperFillManager.fillReport(templateInputStream, parameters, (JRDataSource) dataSource.get())
                    )
            );
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
            exporter.exportReport();
        } catch (JRException ex) {
            Logger.getLogger(ReportExporter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            jobQueue.clear();
            jobQueue.setBusy(false);
        }
    }

    public void fillAsXlsx(InputStream templateInputStream, Map<String, Object> parameters,
            OutputStream os) throws ReportFillingException {

        parameters.put(PARAMETER_JPA_ENTITY_MANAGER, emf.createEntityManager());

        try {
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(JasperFillManager.fillReport(templateInputStream, parameters)));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
            exporter.exportReport();
        } catch (JRException ex) {
            Logger.getLogger(ReportExporter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            jobQueue.clear();
            jobQueue.setBusy(false);
        }

    }
}
