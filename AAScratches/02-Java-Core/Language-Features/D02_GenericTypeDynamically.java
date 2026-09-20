/*
 * =====================================================================
 *  Super type token: picking Response<T> at runtime      Java core | Hard
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   Every API here answers with the same envelope, Response<T>, where T depends on a
 *   "payloadType" string known only at runtime. Erasure means Response<Customer> and
 *   Response<Address> are the same class at run time, so you cannot say
 *   fromJson(json, Response<Customer>.class) - that is not even legal Java. The super
 *   type token trick recovers the full generic type and lets a binder use it.
 *
 * WHAT YOU WILL SEE
 *   case 1  a customer envelope binds to Response<Customer> and prints the name.
 *   case 2  an address envelope binds to Response<Address> and prints the street.
 *   case 3  an order envelope binds to Response<Order> and prints the amount (a double).
 *   case 4  a customer literally named "order" still routes to Customer - the old
 *           json.contains("order") dispatch got this wrong.
 *   case 5  erasure proof: the two envelopes share one Class object, yet the two type
 *           tokens report different types.
 *
 * HOW IT WORKS
 *   1. new TypeToken<Response<Customer>>() {} creates an ANONYMOUS SUBCLASS. Its
 *      superclass is recorded in the class file as TypeToken<Response<Customer>> - a
 *      generic supertype is metadata, so it survives erasure.
 *   2. The TypeToken constructor reads getClass().getGenericSuperclass(), casts it to
 *      ParameterizedType and takes getActualTypeArguments()[0]. That java.lang.reflect.Type
 *      is the whole Response<Customer>, raw class plus argument.
 *   3. MiniJson parses the text once into maps, lists, Strings and Doubles.
 *   4. MiniJson.bind walks the Type: for Response<Customer> it instantiates Response and,
 *      when it meets the field declared as T, resolves T to Customer by position and
 *      recurses. That is a 40-line version of what Gson and Jackson do.
 *
 * KEY INSIGHT
 *   Erasure removes the type argument from OBJECTS, not from CLASS DECLARATIONS. Anything
 *   written into a class file - a superclass, an implemented interface, a field or method
 *   signature - keeps its type arguments. Subclassing is therefore the standard smuggling
 *   route: that is why the token is always "new TypeToken<...>() {}" with braces, and why
 *   Gson's TypeToken, Jackson's TypeReference and Spring's ParameterizedTypeReference are
 *   all abstract classes you must subclass.
 *
 * GOTCHAS
 *   - Drop the {} and you get a raw TypeToken whose generic superclass is a plain Class;
 *     the constructor here fails fast with IllegalStateException (case 5 proves it).
 *   - The token must be built at a site where T is a real type. new TypeToken<T>() {}
 *     inside a generic method captures the type VARIABLE T, not the caller's argument.
 *   - Dispatching on json.contains("customer") reads the whole document, so any field
 *     value containing that word hijacks the route. Parse first, then switch on the
 *     discriminator field.
 *   - Fixed: dispatch now uses the parsed payloadType field, not substring matching.
 *   - Fixed: removed the external Gson dependency and the "package casting" line so the
 *     file compiles and runs standalone.
 *
 * INTERVIEW FOLLOW-UPS
 *   - How does Jackson do the same thing? (TypeReference, or
 *     mapper.getTypeFactory().constructParametricType(Response.class, Customer.class))
 *   - What is the RestTemplate/WebClient equivalent? (ParameterizedTypeReference)
 *   - Which generic information does erasure actually keep, and where is it stored?
 *   - How would you make the payloadType to type mapping open for extension?
 *
 * RUN
 *   main() runs 5 cases (three payload types, the misleading value, erasure proof) and
 *   prints actual vs expected.
 */

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class GenericTypeDynamically {

    // The braces at the end are the whole trick: they create an anonymous subclass whose
    // generic superclass records Response<Customer> in the class file.
    private static final Type CUSTOMER_RESPONSE_TYPE = new TypeToken<Response<Customer>>() {
    }.getType();
    private static final Type ADDRESS_RESPONSE_TYPE = new TypeToken<Response<Address>>() {
    }.getType();
    private static final Type ORDER_RESPONSE_TYPE = new TypeToken<Response<Order>>() {
    }.getType();

    /** payloadType value -> the fully generic envelope type to bind it to. */
    private static final Map<String, Type> TYPE_BY_PAYLOAD = Map.of(
            "customer", CUSTOMER_RESPONSE_TYPE,
            "address", ADDRESS_RESPONSE_TYPE,
            "order", ORDER_RESPONSE_TYPE);

    /**
     * Parses once, reads the discriminator, binds with the matching super type token, and
     * returns the one interesting field so main() can compare it against an expected value.
     */
    public static String processJson(String json) {
        Object tree = MiniJson.parse(json);                  // maps / lists / String / Double
        String payloadType = String.valueOf(((Map<?, ?>) tree).get("payloadType"));

        Type responseType = TYPE_BY_PAYLOAD.get(payloadType);
        if (responseType == null) {
            throw new IllegalArgumentException("unknown payloadType: " + payloadType);
        }

        switch (payloadType) {
            case "customer" -> {
                Response<Customer> response = MiniJson.bind(tree, responseType);
                return response.payload.name;
            }
            case "address" -> {
                Response<Address> response = MiniJson.bind(tree, responseType);
                return response.payload.street;
            }
            default -> {
                Response<Order> response = MiniJson.bind(tree, responseType);
                return String.valueOf(response.payload.amount);
            }
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        String customerJson = """
                {
                  "status": "OK",
                  "message": "fetched",
                  "payloadType": "customer",
                  "payload": {
                               "name": "John Doe",
                               "age": 30
                             }
                }""";

        String addressJson = """
                {
                  "status": "OK",
                  "message": "fetched",
                  "payloadType": "address",
                  "payload": {
                               "street": "123 Main St",
                               "city": "Anytown"
                             }
                }""";

        String orderJson = """
                {
                  "status": "OK",
                  "message": "fetched",
                  "payloadType": "order",
                  "payload": {
                               "orderId": "ORD-8019",
                               "amount": 42.50
                             }
                }""";

        // A customer whose name would fool a substring-based dispatch.
        String trickyJson = """
                {
                  "status": "OK",
                  "message": "fetched",
                  "payloadType": "customer",
                  "payload": {
                               "name": "order",
                               "age": 7
                             }
                }""";

        print("case 1 customer name", processJson(customerJson), "John Doe");
        print("case 2 address street", processJson(addressJson), "123 Main St");
        print("case 3 order amount", processJson(orderJson), "42.5");
        print("case 4 tricky name", processJson(trickyJson), "order");

        // case 5: erasure loses the argument on objects, the token keeps it on the class.
        Response<Customer> c = new Response<>();
        Response<Address> a = new Response<>();
        print("case 5 same runtime class", c.getClass() == a.getClass(), true);
        print("case 5 customer token", CUSTOMER_RESPONSE_TYPE.getTypeName(),
                "Response<Customer>");
        print("case 5 tokens differ",
                !CUSTOMER_RESPONSE_TYPE.equals(ADDRESS_RESPONSE_TYPE), true);
        print("case 5 raw token (no braces)", rawTokenMessage(),
                "IllegalStateException: TypeToken must be subclassed with a type argument");
    }

    /** Building a token without the trailing {} is the classic mistake; prove it fails. */
    private static String rawTokenMessage() {
        try {
            Type bare = new RawToken().getType();
            return "no exception, got " + bare;
        } catch (IllegalStateException e) {
            return "IllegalStateException: " + e.getMessage();
        }
    }

    /** A direct subclass that forgets to supply a type argument - same failure as no {}. */
    @SuppressWarnings("rawtypes")
    private static class RawToken extends TypeToken {
    }
}

/**
 * Hand-rolled equivalent of Gson's TypeToken. Abstract on purpose: you MUST subclass it,
 * because the type argument is only recorded on a subclass's generic superclass.
 */
abstract class TypeToken<T> {
    private final Type type;

    protected TypeToken() {
        Type superclass = getClass().getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType parameterized)) {
            throw new IllegalStateException(
                    "TypeToken must be subclassed with a type argument");
        }
        this.type = parameterized.getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }
}

/** A 60-line stand-in for Gson: parse JSON text, then bind a tree onto a generic Type. */
final class MiniJson {

    private final String src;
    private int pos;

    private MiniJson(String src) {
        this.src = src;
    }

    // ---- parsing: text -> Map / List / String / Double / Boolean / null ---------------

    static Object parse(String json) {
        MiniJson p = new MiniJson(json);
        Object value = p.readValue();
        p.skipWhitespace();
        if (p.pos != json.length()) {
            throw new IllegalArgumentException("trailing junk at index " + p.pos);
        }
        return value;
    }

    private Object readValue() {
        skipWhitespace();
        char c = src.charAt(pos);
        return switch (c) {
            case '{' -> readObject();
            case '[' -> readArray();
            case '"' -> readString();
            case 't' -> readLiteral("true", Boolean.TRUE);
            case 'f' -> readLiteral("false", Boolean.FALSE);
            case 'n' -> readLiteral("null", null);
            default -> readNumber();
        };
    }

    private Map<String, Object> readObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        pos++;                                   // consume '{'
        skipWhitespace();
        if (src.charAt(pos) == '}') {
            pos++;
            return map;
        }
        while (true) {
            skipWhitespace();
            String key = readString();
            skipWhitespace();
            expect(':');
            map.put(key, readValue());
            skipWhitespace();
            char c = src.charAt(pos++);
            if (c == '}') return map;
            if (c != ',') throw new IllegalArgumentException("expected , or } at " + pos);
        }
    }

    private List<Object> readArray() {
        List<Object> list = new ArrayList<>();
        pos++;                                   // consume '['
        skipWhitespace();
        if (src.charAt(pos) == ']') {
            pos++;
            return list;
        }
        while (true) {
            list.add(readValue());
            skipWhitespace();
            char c = src.charAt(pos++);
            if (c == ']') return list;
            if (c != ',') throw new IllegalArgumentException("expected , or ] at " + pos);
        }
    }

    private String readString() {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = src.charAt(pos++);
            if (c == '"') return sb.toString();
            if (c == '\\') {                     // only the escapes these payloads need
                char esc = src.charAt(pos++);
                sb.append(switch (esc) {
                    case 'n' -> '\n';
                    case 't' -> '\t';
                    default -> esc;              // covers \" and \\
                });
            } else {
                sb.append(c);
            }
        }
    }

    private Double readNumber() {
        int start = pos;
        while (pos < src.length() && "+-.eE0123456789".indexOf(src.charAt(pos)) >= 0) {
            pos++;
        }
        return Double.valueOf(src.substring(start, pos));
    }

    private Object readLiteral(String word, Object value) {
        if (!src.startsWith(word, pos)) {
            throw new IllegalArgumentException("bad literal at " + pos);
        }
        pos += word.length();
        return value;
    }

    private void expect(char c) {
        if (src.charAt(pos) != c) {
            throw new IllegalArgumentException("expected " + c + " at index " + pos);
        }
        pos++;
    }

    private void skipWhitespace() {
        while (pos < src.length() && Character.isWhitespace(src.charAt(pos))) {
            pos++;
        }
    }

    // ---- binding: tree + Type -> object ----------------------------------------------

    /** The only method that needs the super type token: it reads the type ARGUMENTS. */
    @SuppressWarnings("unchecked")
    static <T> T bind(Object tree, Type type) {
        Class<?> raw;
        Type[] typeArguments = {};
        if (type instanceof ParameterizedType parameterized) {
            raw = (Class<?>) parameterized.getRawType();
            typeArguments = parameterized.getActualTypeArguments();   // e.g. {Customer}
        } else {
            raw = (Class<?>) type;
        }

        if (raw == String.class) return (T) String.valueOf(tree);
        if (raw == int.class || raw == Integer.class) {
            return (T) Integer.valueOf(((Number) tree).intValue());
        }
        if (raw == double.class || raw == Double.class) {
            return (T) Double.valueOf(((Number) tree).doubleValue());
        }

        Map<?, ?> map = (Map<?, ?>) tree;
        try {
            Object target = raw.getDeclaredConstructor().newInstance();
            for (var field : raw.getFields()) {
                Object value = map.get(field.getName());
                if (value == null) continue;
                field.set(target, bind(value, resolve(field.getGenericType(),
                        raw, typeArguments)));
            }
            return (T) target;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("cannot bind " + raw.getSimpleName(), e);
        }
    }

    /** Turn a field declared as T into the concrete type the token supplied. */
    private static Type resolve(Type fieldType, Class<?> owner, Type[] typeArguments) {
        if (!(fieldType instanceof TypeVariable<?> variable)) return fieldType;
        TypeVariable<?>[] declared = owner.getTypeParameters();
        for (int i = 0; i < declared.length && i < typeArguments.length; i++) {
            if (declared[i].getName().equals(variable.getName())) return typeArguments[i];
        }
        throw new IllegalStateException("unresolved type variable " + variable);
    }
}

class Address {
    public String street;
    public String city;
}

class Order {
    public String orderId;
    public double amount;
}

class Customer {
    public String name;
    public int age;
}

/** The envelope every endpoint returns; only the payload's type changes. */
class Response<T> {
    public String status;
    public String message;
    public String payloadType;
    public T payload;
}
