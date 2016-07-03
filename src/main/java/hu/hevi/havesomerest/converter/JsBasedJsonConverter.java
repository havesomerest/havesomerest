package hu.hevi.havesomerest.converter;

import hu.hevi.havesomerest.config.JsProperties;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;

@Component
public class JsBasedJsonConverter {

    @Autowired
    private JsProperties jsProperties;

    private ScriptEngineManager engineManager;
    private ScriptEngine engine;

    public ScriptObjectMirror convertToObject(String val){
        ClassPathResource resource = new ClassPathResource(jsProperties.getLocation());
        InputStreamReader reader = null;
        ScriptObjectMirror converted = null;
        try {
            reader = new InputStreamReader(resource.getInputStream());
            engineManager = new ScriptEngineManager();
            engine = engineManager.getEngineByName("nashorn");
            eval(reader);

            ScriptObjectMirror json = eval("JSON");
            converted = eval(MessageFormat.format("convertToObject({0})", val));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        return converted;
    }

    public ScriptObjectMirror convertToJson(String val){
        ClassPathResource resource = new ClassPathResource(jsProperties.getLocation());
        InputStreamReader reader = null;
        ScriptObjectMirror converted = null;
        try {
            reader = new InputStreamReader(resource.getInputStream());
            engineManager = new ScriptEngineManager();
            engine = engineManager.getEngineByName("nashorn");
            eval(reader);

            ScriptObjectMirror json = eval("JSON");
            converted = eval(MessageFormat.format("convertToJson({0})", val));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        return converted;
    }

    private ScriptObjectMirror eval(String evaluatable) throws ScriptException {
        return (ScriptObjectMirror) engine.eval(evaluatable);
    }

    private ScriptObjectMirror eval(InputStreamReader evaluatable) throws ScriptException {
        return (ScriptObjectMirror) engine.eval(evaluatable);
    }

}
