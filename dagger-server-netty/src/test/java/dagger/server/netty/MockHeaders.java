package dagger.server.netty;

import dagger.lang.NotImplementedYet;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.*;

class MockHeaders extends HttpHeaders {
    private Map<String, List<Object>> headers = new HashMap<>();

    @Override
    public String get(String name) {
        if(headers.containsKey(name))
            return headers.get(name).get(0).toString();
        return null;
    }

    @Override
    public HttpHeaders set(String name, Object value) {
        List<Object> values = new ArrayList<>();
        values.add(value);
        headers.put(name, values);
        return this;
    }

    @Override
    public List<String> getAll(String headerName) {
        List<String> list = new ArrayList<>();
        if(headers.containsKey(headerName))
            for(Object value : headers.get(headerName))
                list.add(value.toString());
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<Map.Entry<String, String>> entries() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean contains(String s) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isEmpty() {
        throw new NotImplementedYet();
    }

    @Override
    public Set<String> names() {
        throw new NotImplementedYet();
    }

    @Override
    public HttpHeaders add(String name, Object value) {
        List<Object> values = headers.get(name);
        if(values == null) {
            values = new ArrayList<>();
            headers.put(name, values);
        }
        values.add(value);
        return this;
    }

    @Override
    public HttpHeaders add(String s, Iterable<?> objects) {
        throw new NotImplementedYet();
    }

    @Override
    public HttpHeaders set(String name, Iterable<?> values) {
        Iterator<?> iterator = values.iterator();
        ArrayList<Object> list = new ArrayList<>();
        while(iterator.hasNext())
            list.add(iterator.next());
        headers.put(name, list);
        return this;
    }

    @Override
    public HttpHeaders remove(String s) {
        throw new NotImplementedYet();
    }

    @Override
    public HttpHeaders clear() {
        throw new NotImplementedYet();
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        throw new NotImplementedYet();
    }
}
