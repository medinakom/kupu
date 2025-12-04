/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.core.base.view.widget;

import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.AjaxBehaviorEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.primefaces.PrimeFaces;

/**
 *
 * @author aphasan
 * @param <E>
 */
public abstract class InlineEditor<E> {

    @FunctionalInterface
    public static interface Context {

        Map<String, Object> get();
    }

    @FunctionalInterface
    public static interface DataContext {

        Object get(String rowKey);
    }

    @FunctionalInterface
    public static interface InlineEditorListener<E> {

        void doAction(E entity, Map<String, Object> context);
    }

    protected E selected;

    protected Context context;

    protected DataContext dataContext;

    protected List<String> updates;

    protected InlineEditorListener<E> saveListener;

    protected Map<String, Object> inputContext;

    protected abstract void reload(Context ctx);

    public void initContext(ActionEvent evt) {
        String upd = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("updates");
        this.updates = List.of(upd.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());

        inputContext = new HashMap<>();

        String strCtx = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("context");

        if (strCtx != null && !strCtx.isBlank()) {

            String[] tokens = strCtx.split(";");

            for (String token : tokens) {
                String[] vMap = token.split("=");
                inputContext.put(vMap[0], convertInternal(vMap[0], vMap[1]));
            }

        }

        if (context != null) {
            inputContext.putAll(context.get());
        }

        reload(() -> inputContext);
    }

    private Object convertInternal(String key, String value) {
        if (key.equals("data")) {
            if (dataContext != null) {
                return dataContext.get(value);
            } else {
                return null;
            }
        }
        return convert(key, value);
    }

    protected Object convert(String key, String value) {
        return value;
    }

    public String getUpdates() {
        return updates.stream().collect(Collectors.joining(" "));
    }

    public void onSave(ActionEvent evt) {
        onSaveInternal(selected);
    }

    public void onSave(AjaxBehaviorEvent evt) {
        onSaveInternal(selected);
    }

    protected void onSaveInternal(E selected) {
        if (saveListener != null) {
            saveListener.doAction(selected, inputContext);
            PrimeFaces.current().ajax().update(updates);
        }
    }

    public void setSaveListener(InlineEditorListener<E> saveListener) {
        this.saveListener = saveListener;
    }

    public Map<String, Object> getInputContext() {
        return inputContext;
    }

    public E getSelected() {
        return selected;
    }

    public void setSelected(E selected) {
        this.selected = selected;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setDataContext(DataContext dataContext) {
        this.dataContext = dataContext;
    }
}
