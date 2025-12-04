/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.core.base.view.widget;

import id.my.mdn.kupu.core.base.dao.AbstractFacade.DefaultChecker;
import id.my.mdn.kupu.core.base.model.HierarchicalEntity;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.view.widget.AbstractPagedValueList.DefaultCount;
import static id.my.mdn.kupu.core.base.view.widget.Selector.CHECKBOX;
import static id.my.mdn.kupu.core.base.view.widget.Selector.SINGLE;
import id.my.mdn.kupu.core.base.view.widget.Selector.SelectionModeSelector;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.primefaces.model.TreeNode;

/**
 *
 * @author aphasan
 * @param <E>
 */
public abstract class AbstractValueTree<E extends HierarchicalEntity<E>>
        extends AbstractValueList<E> {

    @FunctionalInterface
    public static interface TreeNodeConstructor<E> {

        TreeNode<E> construct(E data, TreeNode<E> parent);
    }

    protected TreeNode<E> rootNode = null;

    protected DefaultCount defaultCount = () -> null;

    private TreeNodeConstructor<E> constructor;

    private TreeNode<E> nodeSelection;

    private List<TreeNode<E>> nodeSelections;

    public AbstractValueTree(Class<E> entityClass) {
        super(entityClass);
    }

    public List<TreeNode<E>> getNodeSelections() {
        return getNodeSelectionsInternal();
    }

    @Override
    public void setSelection(E selection) {
        selector.setSelection(selection);

        if (rootNode == null) {
            rootNode = createNode(getRoot(), null);
            constructRoot();
        }

        TreeNode<E> selectedNode = null;
        findSelection(rootNode, selectedNode);

        nodeSelection = selectedNode;
    }

    @Override
    public void setSelections(List<E> selections) {
        selector.setSelections(selections);

        if (rootNode == null) {
            rootNode = createNode(getRoot(), null);
            constructRoot();
        }

        List<TreeNode<E>> selectedNodes = new ArrayList<>();
        findSelections(rootNode, selectedNodes);

        nodeSelections = !selectedNodes.isEmpty() ? selectedNodes : null;
    }

    public void setNodeSelection(TreeNode<E> nodeSelection) {
        PhaseId phaseId = FacesContext.getCurrentInstance().getCurrentPhaseId();
        if (phaseId.equals(PhaseId.UPDATE_MODEL_VALUES)) {
            this.nodeSelection = nodeSelection;

            E selection = null;

            if (nodeSelection != null) {
                selection = nodeSelection.getData();
            }

            selector.setSelection(selection);
        }

    }

    public void setNodeSelections(List<TreeNode<E>> nodeSelections) {
        PhaseId phaseId = FacesContext.getCurrentInstance().getCurrentPhaseId();
        if (phaseId.equals(PhaseId.UPDATE_MODEL_VALUES)) {
            this.nodeSelections = nodeSelections;

            List<E> selections = null;

            if (nodeSelections != null) {
                selections = new ArrayList<>();
                for (TreeNode<E> node : nodeSelections) {
                    selections.add(node.getData());
                }
            }

            selector.setSelections(selections);
        }

    }

    public TreeNodeConstructor<E> getConstructor() {
        if (constructor == null) {
            constructor = (data, parent) -> new LazyDefaultTreeNode<E>(data, parent,
                    this::getChildren,
                    this::countChildren);
        }
        return constructor;
    }

    public void setConstructor(TreeNodeConstructor<E> constructor) {
        this.constructor = constructor;
    }

    public void setNodeSelectionInternal(TreeNode<E> nodeSelection) {
        this.nodeSelection = nodeSelection;

        E selection = null;

        if (nodeSelection != null) {
            selection = nodeSelection.getData();
        }

        selector.setSelectionInternal(selection);
        
    }

    public void setNodeSelectionsInternal(List<TreeNode<E>> nodeSelections) {
        this.nodeSelections = nodeSelections;

        List<E> selections = null;

        if (nodeSelections != null) {
            selections = new ArrayList<>();
            for (TreeNode<E> node : nodeSelections) {
                selections.add(node.getData());
            }
        }

        selector.setSelectionsInternal(selections);
    }

    public List<TreeNode<E>> getNodeSelectionsInternal() {
        return nodeSelections;
    }

    public TreeNode<E> getNodeSelection() {
        return nodeSelection;
    }

    public TreeNode<E> getNodeSelectionInternal() {
        return nodeSelection;
    }

    private void findSelection(TreeNode<E> firstNode, TreeNode<E> selectedNode) {
        TreeNode<E> node = firstNode;
        if (getSelection() == null) {
            return;
        }
        if (getSelection().equals(node.getData())) {
            selectedNode = node;
            node.setSelected(true);
            node.setExpanded(true);
            TreeNode<E> parent = node.getParent();
            while (parent != null) {
                parent.setExpanded(true);
                parent = parent.getParent();
            }
        }

        List<TreeNode<E>> childNodes = firstNode.getChildren();
        if (childNodes != null) {
            for (TreeNode<E> childNode : childNodes) {
                findSelection(childNode, selectedNode);
            }
        }
    }

    private void findSelections(TreeNode<E> firstNode, List<TreeNode<E>> selectedNodes) {
        TreeNode<E> node = firstNode;
        if (getSelections() == null) {
            return;
        }
        if (getSelections().contains(node.getData())) {
            if (!selectedNodes.contains(node)) {
                selectedNodes.add(node);
                node.setSelected(true);
                node.setExpanded(true);
                TreeNode<E> parent = node.getParent();
                while (parent != null) {
                    parent.setExpanded(true);
                    parent = parent.getParent();
                }
            }
        }

        List<TreeNode<E>> childNodes = firstNode.getChildren();
        if (childNodes != null) {
            for (TreeNode<E> childNode : childNodes) {
                findSelections(childNode, selectedNodes);
            }
        }
    }

    protected abstract long getItemsCountInternal(
            Map<String, Object> parameters,
            List<FilterData> filters,
            DefaultCount defaultCount,
            DefaultChecker defaultChecker);

    public TreeNode<E> getRootNode() {
        if (!isCached() || !isValid()) {
            rootNode = createNode(getRoot(), null);
            constructRoot();
        }
        return rootNode;
    }

    private void constructRoot() {
        if (!isCached()) {

            constructTree(rootNode);

            if (getSelectionMode().equals(SINGLE)) {
                TreeNode<E> selectedNode = null;
                findSelection(rootNode, selectedNode);
            } else {
                List<TreeNode<E>> selectedNodes = new ArrayList<>();
                findSelections(rootNode, selectedNodes);
            }

        } else {
            if (!valid) {

                constructTree(rootNode);

                if (getSelectionMode().equals(SINGLE)) {
                    TreeNode<E> selectedNode = null;
                    findSelection(rootNode, selectedNode);
                } else {
                    List<TreeNode<E>> selectedNodes = new ArrayList<>();
                    findSelections(rootNode, selectedNodes);
                }

                validate();

            }
        }
    }

    private void constructTree(TreeNode<E> topNode) {
        loadChildren(topNode);
    }

    private void loadChildren(TreeNode<E> parent) {

        List<E> childrenData = loadChildren(parent.getData());

        if (childrenData != null && !childrenData.isEmpty()) {

            for (E childData : childrenData) {

                TreeNode<E> childNode = createNode(childData, parent);

                parent.getChildren().add(childNode);

                loadChildren(childNode);

            }
        }
    }

    protected E getRoot() {
        return null;
    }

    protected TreeNode<E> createNode(E data, TreeNode<E> parent) {
        return getConstructor().construct(data, parent);
    }

    private List<E> loadChildren(E t) {
        List<FilterData> filters = new ArrayList<>();
        filters.addAll(filter.getValues());

        filters.add(FilterData.by("parent", t));

        return getFetchedItemsInternal(
                getParameters(),
                filters,
                getSorters(),
                defaultList,
                defaultChecker);
    }

    private List<TreeNode<E>> getChildren(TreeNode<E> parent) {
        List<FilterData> filters = filter.getValues();

        filters.add(new FilterData("parent", parent.getData()));

        List<E> children = getFetchedItemsInternal(
                getParameters(),
                filters,
                getSorters(),
                defaultList,
                defaultChecker);

        return children.stream()
                .map(e -> getConstructor().construct(parent.getData(), parent))
                .collect(Collectors.toList());

    }

    public Long countChildren(TreeNode<E> t) {
        List<FilterData> filters = filter.getValues();
        filters.add(new FilterData("parent", t.getData()));
        return getItemsCountInternal(getParameters(), filters, defaultCount, defaultChecker);
    }

    @Override
    protected void resetInternal() {
        if (getSelectionMode().equals(SINGLE)) {
            nodeSelection = null;
        } else {
            nodeSelections = null;
        }
        super.resetInternal();
        rootNode = null;
    }

    @Override
    public void setSelectionMode(SelectionModeSelector mode) {
        selector.setSelectionMode(mode);
        if (getSelectionMode().equals(CHECKBOX)) {
            setConstructor((data, parent) -> new LazyCheckboxTreeNode<>(data, parent,
                    this::getChildren,
                    this::countChildren));
        } else {
            setConstructor((data, parent) -> new LazyDefaultTreeNode<E>(data, parent,
                    this::getChildren,
                    this::countChildren));
        }
    }

    public void setSelectionsLabel(String selectionsLabel) {
        getSelector().setSelectionsLabel(selectionsLabel);
    }

    public void setNodeConstructor(TreeNodeConstructor<E> constructor) {
        setConstructor(constructor);
    }
}
