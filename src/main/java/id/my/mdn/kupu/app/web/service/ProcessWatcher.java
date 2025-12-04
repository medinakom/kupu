/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package id.my.mdn.kupu.app.web.service;

import id.my.mdn.kupu.app.web.model.VideoKajian;

/**
 *
 * @author aphasan
 */
@FunctionalInterface
public interface ProcessWatcher {
    void notify(VideoKajian vk, int status, String payload);
}
