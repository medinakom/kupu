/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.core.base.view.widget;

/**
 *
 * @author aphasan
 */
public final class LazyChooser<E> {
    
    @FunctionalInterface
    public interface LazyChooserListener<E> {
        void onSelect(E selection);
    }
}
