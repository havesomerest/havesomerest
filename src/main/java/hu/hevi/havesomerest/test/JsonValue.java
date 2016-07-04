package hu.hevi.havesomerest.test;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Map;
import java.util.Set;

@Value
@AllArgsConstructor
public class JsonValue {

    private final ScriptObjectMirror scriptObjectMirror;

    public JsonValue(JsonValue that) {
        this.scriptObjectMirror = that.scriptObjectMirror;
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return scriptObjectMirror.entrySet();
    }

    public Set<String> children() {
        return scriptObjectMirror.keySet();
    }

    public Object get(final Object key) {
        return scriptObjectMirror.get(key);
    }

    public boolean isJsonValue(final Object key) {
        return scriptObjectMirror.get(key).getClass().equals(ScriptObjectMirror.class);
    }

    public boolean containsKey(final Object key) {
        return scriptObjectMirror.containsKey(key);
    }
}
