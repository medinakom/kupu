/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.core.reporting.service;

import id.my.mdn.kupu.core.reporting.model.ReportingJob;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseId;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Arief Prihasanto <aphasan57 at gmail.com>
 */
@Named(value = "reportingJobQueue")
@SessionScoped
public class ReportingJobQueue implements Serializable {

    private Queue<ReportingJob> jobs;

    private boolean busy = false;

    public void put(ReportingJob job) {
        if (jobs == null) {
            jobs = new ConcurrentLinkedQueue<>();
        }
        jobs.add(job);
    }

    public ReportingJob get() {
        if (jobs != null && !jobs.isEmpty()) {
            PhaseId phaseId = FacesContext.getCurrentInstance().getCurrentPhaseId();
            if (phaseId != null && phaseId.equals(PhaseId.RENDER_RESPONSE)) {
                return jobs.peek();
//                return null;
            } else {
                return jobs.poll();
            }
        } else {
            return null;
        }
    }

    public void clear() {
        if (jobs != null && !jobs.isEmpty()) {
            jobs.clear();
        }
    }

    public boolean isEmptyJob() {
        return jobs == null || jobs.isEmpty();
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}
