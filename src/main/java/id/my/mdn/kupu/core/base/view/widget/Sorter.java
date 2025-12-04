/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.core.base.view.widget;

import id.my.mdn.kupu.core.base.view.annotation.SorterField;
import id.my.mdn.kupu.core.base.view.annotation.SorterFields;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author aphasan
 * @param <E>
 */
public class Sorter<E> implements IBookmarkable, Serializable {

    @FunctionalInterface
    public static interface SorterListener {

        void onSort(Object obj);
    }

    @FunctionalInterface
    public static interface SorterDataProcessor {

        List<SorterData> process(List<SorterData> source);
    }

    private String name = "sorter";

    private Map<String, SorterData> listSorterData = new HashMap<>();

    private final SorterListener context;

    private final Class<E> entityClass;

    public Sorter(Class<E> entityClass, SorterListener context) {
        this.entityClass = entityClass;
        this.context = context;
        reload();
    }

    private void reload() {
        listSorterData = extractSorterData(entityClass);
    }

    public static Map<String, SorterData> extractSorterData(Class<?> entityClass) {
        Map<String, SorterData> listSorterData = new HashMap<>();

        for (SorterFields annotations : entityClass.getAnnotationsByType(SorterFields.class)) {
            for (SorterField annotation : annotations.value()) {
                String value = annotation.value();
                String label = annotation.label().isEmpty() ? value : annotation.label();
                listSorterData.put(value, new SorterData(value, annotation.order(), annotation.sort(), label));
            }
        }

        for (SorterField annotation : entityClass.getAnnotationsByType(SorterField.class)) {
            String value = annotation.value();
            String label = annotation.label().isEmpty() ? value : annotation.label();
            listSorterData.put(value, new SorterData(value, annotation.order(), annotation.sort(), label));
        }

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(SorterField.class)) {
                SorterField annotation = field.getAnnotation(SorterField.class);
                String value = annotation.value().isEmpty() ? field.getName() : annotation.value();
                String label = annotation.label().isEmpty() ? value : annotation.label();
                listSorterData.put(value, new SorterData(value, annotation.order(), annotation.sort(), label));
            }
        }

        return listSorterData;
    }

    public List<SorterData> getDeclaredSorterData() {
        return (List<SorterData>) listSorterData.values();
    }
    
    protected SorterDataProcessor postProcessor = x -> x;

    public SorterDataProcessor getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(SorterDataProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }

    public List<SorterData> getListSorterData() {
        List<SorterData> activeSorterData = new ArrayList<>();
        for (SorterData sorter : listSorterData.values()) {
            if (sorter.isSort()) {
                activeSorterData.add(sorter);
            }
        }
        return postProcessor.process(activeSorterData);
    }

    public void setListSorterData(List<SorterData> listSorterData) {
        for (SorterData sorterData : listSorterData) {
//            int sorterDataIdx = this.listSorterData.indexOf(sorterData);
            SorterData existingSorterData = this.listSorterData.get(sorterData.getField());
            existingSorterData.setOrder(sorterData.getOrder());
            existingSorterData.setSort(true);
        }
    }

    public void doUnsort() {
        listSorterData.clear();
        reload();
        context.onSort(null);
    }

    public void doSort() {
        context.onSort(null);
    }

    @Override
    public Map<String, List<String>> getStates() {

        Map<String, List<String>> states = new HashMap<>();

        List<String> value = getListSorterData().stream()
                .filter(data -> data.isSort())
                .map(data -> data.toString())
                .collect(Collectors.toList());

        states.put(name, value);

        return states;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
