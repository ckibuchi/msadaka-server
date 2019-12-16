package msadaka.enums;


        import java.util.Collections;
        import java.util.Map;
        import java.util.concurrent.ConcurrentHashMap;

public enum PaymentMethod {
    MPESA("MPESA"),VISA("VISA"),APP("APP"),UNKNOWN("UNKNOWN");

    private String name;

    private static final Map<String,PaymentMethod> ENUM_MAP;

    PaymentMethod (String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    // Build an immutable map of String name to enum pairs.
    // Any Map impl can be used.

    static {
        Map<String,PaymentMethod> map = new ConcurrentHashMap<String,PaymentMethod>();
        for (PaymentMethod instance : PaymentMethod.values()) {
            map.put(instance.getName(),instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static PaymentMethod get (String name) {
        return ENUM_MAP.get(name);
    }

}
