import com.github.javaparser.ast.type.Type;
import com.github.javaparser.utils.Pair;

import java.text.MessageFormat;
import java.util.*;

public class Endpoint {
    private String name;
    private String URL;
    private String encoding;
    private String method;
    private HashMap<String, Pair<String, Type>> params;
    private List<String> modifiers;

    public Endpoint() {
        this.name = "";
        this.URL = "";
        this.encoding = "";
        this.method = "";
        this.params = new HashMap<>();
        this.modifiers = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getURL() {
        return this.URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Pair<String, Type> getParam(String key) {
        return this.params.get(key);
    }

    public void addParam(String key, String aname, Type atype) {
        Pair<String, Type> val = new Pair<>(aname, atype);
        this.params.put(key, val);
    }

    public List<String> getModifiers() {
        return this.modifiers;
    }

    public void addModifier(String mod) {
        this.modifiers.add(mod);
    }

    public boolean isValid() {
        if (    !this.name.isEmpty() &&
                !this.URL.isEmpty() &&
                !this.method.isEmpty()) {
            return true;
        }

        return false;
    }

    private String formatParams() {
        List<String> strEntries = new ArrayList<>();

        for (Map.Entry<String, Pair<String, Type>> entry : this.params.entrySet()) {
            String key = entry.getKey();
            Pair<String, Type> pair = entry.getValue();

            if (key.equals(pair.a)) {
                strEntries.add(MessageFormat.format("{0}: {1}", key, pair.b.toString()));
            } else {
                strEntries.add(MessageFormat.format("{1} {0}: {2}", key, pair.a, pair.b.toString()));
            }
        }

        return String.join(", ", strEntries);
    }

    public String toString() {
        return MessageFormat.format("{0} | {1} | {2} | {3} | {4} | {5}",
                this.name, this.method, this.URL, this.encoding,
                formatParams(), this.modifiers);
    }

}
