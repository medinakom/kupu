package id.my.mdn.kupu.core.base.util;

import id.my.mdn.kupu.core.base.view.Page;
import id.my.mdn.kupu.core.base.view.util.ConverterUtil;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.convert.Converter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;

/**
 *
 * @author aphasan
 */
public class RequestedView {

    private final String view;

    private final Map<String, List<String>> params;

    private String currentKey;

    public RequestedView(String view) {
        this.view = view;
        this.params = new HashMap<>();
    }

    public RequestedView(Class<? extends Page> clsPage) {
        this.view = ModuleUtil.getPage(clsPage);
        this.params = new HashMap<>();
    }

    public RequestedView addParam(String key) {
        currentKey = key;
        return this;
    }

//    public RequestedView withValues(String... values) {
//        params.put(currentKey, Arrays.asList(values));
//        return this;
//    }
    public RequestedView withValues(Object... values) {
        CDI<Object> ctx = CDI.current();
        List<String> paramValue = Stream.of(values)
                .filter(value -> value != null)
                .map(value -> {
                    Converter converter = ConverterUtil.findConverter(ctx, value.getClass());
                    if (converter != null) {
                        return converter.getAsString(null, null, value);
                    } else {
                        return value.toString();
                    }
                })
                .map(Object::toString)
                .collect(toList());
        if (!paramValue.isEmpty()) {
            params.put(currentKey, paramValue);
        }
        return this;
    }

    public RequestedView withValues(String converterName, Object... values) {
        Converter converter = ConverterUtil.findConverter(CDI.current(), converterName);
        if (converter != null) {
            return withValues(converter, values);
        } else {
            return withValues(values);
        }
    }

    public RequestedView withValues(Converter converter, Object... values) {
        List<String> paramValue = Stream.of(values)
                .filter(value -> value != null)
                .map(value -> converter.getAsString(null, null, value))
                .collect(toList());
        if (!paramValue.isEmpty()) {
            params.put(currentKey, paramValue);
        }
        return this;
    }

    public void open() {
        RequestUtil.goToView(this);
    }

    @Override
    public String toString() {
        return RequestUtil.encode(getView(), getParams());
    }

    public String getView() {
        return view;
    }

    public String getCurrentKey() {
        return currentKey;
    }

    public Map<String, List<String>> getParams() {
        return params;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.view);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RequestedView other = (RequestedView) obj;
        return Objects.equals(this.toString(), other.toString());
    }

}
