/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.core.base.view.widget;

import id.my.mdn.kupu.core.base.util.Result;
import static id.my.mdn.kupu.core.base.view.widget.Selector.SINGLE;
import jakarta.faces.event.AjaxBehaviorEvent;
import java.util.List;
import java.util.Map;
import org.primefaces.event.CellEditEvent;

/**
 *
 * @author aphasan
 * @param <E>
 */
public abstract class AbstractMutablePagedValueList<E>
        extends AbstractPagedValueList<E> implements ICreate<E>, IEdit<E>, IDelete<E> {

    private PageCaller creator;

    private PageCaller editor;

    private PageCaller deleter;

    public AbstractMutablePagedValueList(Class<E> entityClass) {
        super(entityClass);
    }

    public E getSelected() {
        if (getSelectionMode().equals(Selector.SINGLE)) {
            return getSelection();
        } else {
            return getSelections() != null ? getSelections().getLast() : null;
        }
    }

    public void onCellEdit(CellEditEvent event) {
//        E entity = getSelected();

        E entity = (E) event.getRowData();

        if (entity != null) {

            String field = (String) event.getColumn().getExportTag();
            Object value = event.getNewValue();
            if (field != null) {
                update(entity, field, value, null);
            }
        }
    }

    public void onValueChange(AjaxBehaviorEvent evt) {
        String field = (String) evt.getComponent().getAttributes().get("field");
        E data = (E) evt.getComponent().getAttributes().get("data");
        Object value = evt.getComponent().getAttributes().get("value");
        List<Object> context = (List<Object>) evt.getComponent().getAttributes().get("context");
        updateInternal(field, data, value, Map.of("context", context));
    }

    public void update(E entity, String field, Object newValue, Map<String, Object> context) {
        if (updateInternal(field, entity, newValue, context)) {
            edit(entity);
        }
        postUpdate(entity);
    }

    protected boolean updateInternal(String field, E entity, Object newValue, Map<String, Object> context) {
        return true;
    }

    protected void postUpdate(E entity) {

    }

    protected E getSelected(String rowkey) {
        if (getSelected() != null && getSelected().toString().equals(rowkey)) {
            return getSelected();
        } else {
            E selection = findEntity(rowkey);
            if (isCached()) {
                selection = getFetchedItems().get(getFetchedItems().indexOf(selection));
            }
            setSelected(selection);

            return selection;
        }
    }

    protected E findEntity(String id) {
        return null;
    }

    public String[] getCreatePermission() {
        return new String[]{};
    }

    public String[] getUpdatePermission() {
        return new String[]{};
    }

    public String[] getDeletePermission() {
        return new String[]{};
    }

    //<editor-fold defaultstate="collapsed" desc="ICreate implementation">
    @Override
    public PageCaller getCreator() {
        return creator;
    }

    @Override
    public void setCreator(PageCaller creator) {
        this.creator = creator;
    }

    @Override
    public void create(E entity) {
        Result<String> result = createInternal(entity);
        if (selector.getSelectionMode().equals(SINGLE)) {
            selector.setSelection(entity);
        } else {
            selector.setSelections(List.of(entity));
        }
        if (cached) {
            invalidate();
        }
        if (result.isSuccess()) {
            Toast.info(result.payload);
        } else {
            Toast.error(result.payload);
        }
    }

    protected abstract Result<String> createInternal(E entity);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="IEdit implementation">
    @Override
    public PageCaller getEditor() {
        return editor;
    }

    @Override
    public void setEditor(PageCaller editor) {
        this.editor = editor;
    }

    @Override
    public void edit(E entity) {
        Result<String> result = editInternal(entity);
        if (result.isSuccess()) {
            Toast.info(result.payload);
        } else {
            Toast.error(result.payload);
        }
    }

    protected abstract Result<String> editInternal(E entity);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="IDelete implementation">
    @Override
    public PageCaller getDeleter() {
        return deleter;
    }

    @Override
    public void setDeleter(PageCaller deleter) {
        this.deleter = deleter;
    }

    @Override
    public void delete(E entity) {
        Result<String> result = deleteInternal(entity);
        if (cached) {
            invalidate();
        }
        if (result.isSuccess()) {
            Toast.info(result.payload);
        } else {
            Toast.error(result.payload);
        }
    }

    public void deleteSelected() {
        if (getSelectionMode().equals(Selector.SINGLE)) {
            deleteSelection();
        } else {
            deleteSelections();
        }
    }

    public void deleteSelection() {
        delete(selector.getSelection());
        selector.setSelection(null);

    }

    public void deleteSelections() {
        selector.getSelections().stream().forEach(e -> delete(e));
        selector.setSelections(null);

    }

    protected abstract Result<String> deleteInternal(E entity);
//</editor-fold>
}
