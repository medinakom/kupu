/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.service;

import id.my.mdn.kupu.app.web.model.VideoKajian;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author aphasan
 */
@Named(value = "videoProcessInfo")
@ApplicationScoped
public class VideoProcessInfo implements Serializable {

    private final Map<String, Queue<ProcessInfo>> progressMap = new ConcurrentHashMap<>();

    public ProcessInfo get(VideoKajian vk) {
        synchronized (vk) {
            Queue<ProcessInfo> queue = progressMap.get(vk.getId());
            ProcessInfo info = queue.poll();
            return info;
        }
    }

    public void set(VideoKajian vk, ProcessInfo processInfo) {
        synchronized (vk) {
            progressMap.get(vk.getId()).add(processInfo);
        }
    }
    
    public void createQueue(VideoKajian vk) {
        progressMap.put(vk.getId(), new ConcurrentLinkedQueue<>());
    }
    
    public void removeQueue(VideoKajian vk) {
        progressMap.remove(vk.getId());
    }

}
