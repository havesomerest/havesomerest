package hu.hevi.havesomerest.integration;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Test {

    private ScriptObjectMirror request;
    private ScriptObjectMirror response;
}
