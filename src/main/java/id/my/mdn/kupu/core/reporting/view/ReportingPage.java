/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.core.reporting.view;

import id.my.mdn.kupu.core.base.view.Page;
import id.my.mdn.kupu.core.base.view.annotation.Bookmarked;
import id.my.mdn.kupu.core.base.view.widget.Filter;
import id.my.mdn.kupu.core.reporting.model.ReportingJob;
import id.my.mdn.kupu.core.reporting.service.ReportingJobQueue;
import jakarta.faces.event.ActionEvent;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Arief Prihasanto <aphasan57 at gmail.com>
 */
public abstract class ReportingPage extends Page {

    @Inject
    private ReportingJobQueue jobQueue;

    protected final Filter filter;
    
    @Bookmarked
    protected Boolean autoload = false;

    public Boolean getAutoload() {
        return autoload;
    }

    public void setAutoload(Boolean autoload) {
        this.autoload = autoload;
    }

    @Override
    public Map<String, List<String>> getStates() {
        Map<String, List<String>> states = super.getStates();
        states.putAll(filter.getStates());
        return states;
    }

    public ReportingPage() {
        filter = new Filter(this::onFilter);
    }

    public void onFilter() {
        prepareReport();
    }

    public Filter getFilter() {
        return filter;
    }

    protected boolean isReady() {
        return true;
    }

    protected abstract List<ReportingJob> prepareReportingJob();

    public void prepareReport(ActionEvent event) {
        prepareReport();
    }

    public void prepareReport() {

        jobQueue.setBusy(true);
        PrimeFaces.current().executeScript("PF('poll').start()");
        PrimeFaces.current().executeScript("PF('blocker').show()");

        if (!isReady()) {
            jobQueue.setBusy(false);
            return;
        }

        List<ReportingJob> preparedJobs = prepareReportingJob();

        for (ReportingJob preparedJob : preparedJobs) {
            jobQueue.put(preparedJob);
        }

    }

    public void pollForCompleteness() {
        if (!jobQueue.isBusy()) {
            PrimeFaces.current().executeScript("PF('poll').stop()");
            PrimeFaces.current().executeScript("PF('blocker').hide()");
        }
    }
}
