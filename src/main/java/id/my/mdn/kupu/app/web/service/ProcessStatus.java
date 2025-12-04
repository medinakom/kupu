/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.service;

/**
 *
 * @author aphasan
 */
public enum ProcessStatus {    
    
    BEGIN_VIDEO_PROC,
    SAVE_VIDEO_PROC,
    SLICE_VIDEO_PROC,
    STORE_VIDEO_PROC,
    END_VIDEO_PROC,
    
    BEGIN_AUDIO_PROC,
    SAVE_AUDIO_PROC,
    SLICE_AUDIO_PROC,
    STORE_AUDIO_PROC,
    END_AUDIO_PROC,
    AUDIO_FAILED,
    VIDEO_FAILED,
    FAILED,
    END_PROC,
    END
}
