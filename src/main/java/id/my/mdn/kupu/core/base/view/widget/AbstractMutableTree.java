/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.core.base.view.widget;

import id.my.mdn.kupu.core.base.model.HierarchicalEntity;
import id.my.mdn.kupu.core.base.util.Result;
import static id.my.mdn.kupu.core.base.view.widget.Selector.SINGLE;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aphasan
 * @param <E>
 */
public abstract class AbstractMutableTree<E extends HierarchicalEntity<E>>
        extends AbstractValueTree<E>
        implements ICreate<E>, IEdit<E>, IDelete<E> {

    private PageCaller creator;

    private PageCaller editor;

    private PageCaller deleter;

    public AbstractMutableTree(Class<E> entityClass) {
        super(entityClass);
    }

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
        invalidate();
        if (result.isSuccess()) {
            Toast.info(result.payload);
        } else {
            Toast.error(result.payload);
        }
    }

    protected abstract Result<String> createInternal(E entity);

    public E getSelected() {
        if (getSelectionMode().equals(Selector.SINGLE)) {
            return getSelection();
        } else {
            return (getSelections() != null &&  !getSelections().isEmpty()) ? getSelections().getLast() : null;
        }
    }

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
        if(result.isSuccess()) {
            Toast.info(result.payload);
        } else {
            Toast.error(result.payload);
        }
    }

    public void deleteSelections() {
        final List<E> parents = new ArrayList<>();
        selector.getSelections().stream().forEach(e -> {
            E parent = e.getParent();
            delete(e);
            if (parent != null) {
                parents.add(parent);
            }
        });
        selector.setSelections(parents);
    }

    public void deleteSelected() {
        if (getSelectionMode().equals(Selector.SINGLE)) {
            deleteSelection();
        } else {
            deleteSelections();
        }
    }

    public void deleteSelection() {
        E parent = selector.getSelection().getParent();
        delete(selector.getSelection());
        selector.setSelection(parent);
    }

    protected abstract Result<String> deleteInternal(E entity);

    public String[] getCreatePermission() {
        return new String[]{};
    }

    public String[] getUpdatePermission() {
        return new String[]{};
    }

    public String[] getDeletePermission() {
        return new String[]{};
    }
}
