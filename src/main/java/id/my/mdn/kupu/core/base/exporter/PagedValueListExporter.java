/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package id.my.mdn.kupu.core.base.exporter;

import id.my.mdn.kupu.core.base.view.widget.AbstractPagedValueList;
import jakarta.faces.context.FacesContext;
import java.util.List;
import org.primefaces.component.datatable.DataTable;

/**
 * Different implementation of ExcelXExporter using the POI streaming API:
 *
 * SXSSF (package: org.apache.poi.xssf.streaming) is an API-compatible streaming
 * extension of XSSF to be used when very large spreadsheets have to be
 * produced, and heap space is limited. SXSSF achieves its low memory footprint
 * by limiting access to the rows that are within a sliding window.
 *
 * @param <E>
 * @param <C>
 */
public class PagedValueListExporter<E, C extends AbstractPagedValueList<E>> extends ValueListExporter<E, C> {

    public PagedValueListExporter(C valueList) {
        super(valueList);
    }

    @Override
    protected void exportAll(FacesContext context, DataTable table) {

        int pageSize = getValueList().getPager().getPageSize().intValue();
        int itemsCount = getValueList().getPager().getItemsCount().intValue();

        int offset = 0;
        List<E> items;

        do {
            items = getValueList().getFetchedItems(offset, pageSize,
                    getValueList().getParameters(),
                    getValueList().getFilter().getValues(),
                    getValueList().getSorters(),
                    getValueList().getDefaultList(),
                    getValueList().getDefaultChecker()
            );
            table.setValue(items);
            for (int rowIndex = 0; rowIndex < items.size(); rowIndex++) {
                exportRow(context, table, rowIndex);
            }
            offset += pageSize;
        } while ((!items.isEmpty()) || (offset < itemsCount));

    }
}
