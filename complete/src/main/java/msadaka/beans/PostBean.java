package msadaka.beans;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;

public class PostBean {
    private HashMap<String, String> map;

    @JsonAnyGetter
    public HashMap<String, String> getMap() {
        return map;
    }

    @JsonAnySetter
    public void setMap(String name, String value) {
        if (this.map == null) map = new HashMap<String, String>();
        this.map.put(name, value);
    }
}