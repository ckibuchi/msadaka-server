package msadaka.enums;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum PayBillStatus {
    ACTIVE("ACTIVE"), INACTIVE("INACTIVE");

    private String name;

    private static final Map<String, PayBillStatus> ENUM_MAP;

    PayBillStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

// Build an immutable map of String name to enum pairs.
// Any Map impl can be used.

    static {
        Map<String, PayBillStatus> map = new ConcurrentHashMap<String, PayBillStatus>();
        for (PayBillStatus instance : PayBillStatus.values()) {
            map.put(instance.getName(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static PayBillStatus get(String name) {
        return ENUM_MAP.get(name);
    }
}