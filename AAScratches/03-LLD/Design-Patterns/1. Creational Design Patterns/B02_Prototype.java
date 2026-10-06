/*
 * =====================================================================
 *  Prototype - product variants from a base listing      Creational | Easy
 * =====================================================================
 *
 * PROBLEM
 *   A seller lists "Classic Tee" once, with fabric, fit, wash care, images
 *   and so on, then needs Red/S, Red/M, Red/L... variants. Re-entering every
 *   field per variant is slow and error-prone. Copy the base listing and
 *   change only colour and size.
 *
 * KEY INSIGHT
 *   The copy must be DEEP for every mutable field. A shallow copy shares the
 *   attribute map, so setting the variant's colour silently repaints the
 *   base listing too (case 2). A copy constructor makes the deep copy
 *   explicit; a registry of templates makes "clone, then tweak" the normal
 *   way a new listing is born.
 *
 * ROLES IN THIS CODE
 *   ProductListing      Prototype (copy() = deep copy via copy constructor)
 *   TemplateRegistry    Prototype registry: template name -> listing to clone
 *
 * INTERVIEW FOLLOW-UPS
 *   - Copy constructor vs Cloneable: clone() skips the constructor, is
 *     shallow by default and throws a checked exception.
 *   - Strings are immutable, so sharing them is safe; only the Map and List
 *     need fresh copies.
 *   - Seen in: new ArrayList<>(other), Spring's prototype bean scope.
 *
 * RUN
 *   5 cases: deep copy isolated, shallow-copy bug, registry spawns three
 *   sizes, image lists independent, unknown template.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class ProductListing {
    final String title;
    final Map<String, String> attributes;
    final List<String> images;

    ProductListing(String title, Map<String, String> attributes, List<String> images) {
        this.title = title;
        this.attributes = attributes;
        this.images = images;
    }

    /** Copy constructor: fresh containers, the same immutable Strings inside. */
    ProductListing(ProductListing other) {
        this(other.title, new LinkedHashMap<>(other.attributes), new ArrayList<>(other.images));
    }

    /** The Prototype operation: a deep copy. */
    ProductListing copy() {
        return new ProductListing(this);
    }

    /** The bug code review catches: a new object holding the SAME map and list. */
    ProductListing shallowCopy() {
        return new ProductListing(title, attributes, images);
    }

    ProductListing with(String key, String value) {
        attributes.put(key, value);
        return this;
    }

    String sku() {
        return title.replace(" ", "") + "-" + attributes.get("colour")
                + "-" + attributes.get("size");
    }
}

class TemplateRegistry {
    private final Map<String, ProductListing> templates = new HashMap<>();

    void register(String name, ProductListing template) {
        templates.put(name, template);
    }

    ProductListing newFrom(String name) {
        ProductListing template = templates.get(name);
        if (template == null) {
            throw new IllegalArgumentException("no template " + name);
        }
        return template.copy(); // callers never get the template itself
    }
}

class PrototypeExample {

    public static void main(String[] args) {
        // Case 1: typical. Deep copy, then change colour - the base is untouched.
        ProductListing base = classicTee();
        ProductListing red = base.copy().with("colour", "red");
        print("case 1a variant", red.sku(), "ClassicTee-red-M");
        print("case 1b base   ", base.sku(), "ClassicTee-white-M");

        // Case 2: the bug. Shallow copy shares the map, so the base turns black too.
        ProductListing base2 = classicTee();
        base2.shallowCopy().with("colour", "black");
        print("case 2 shallow ", base2.sku(), "ClassicTee-black-M");

        // Case 3: the registry spawns one listing per size from one template.
        TemplateRegistry registry = new TemplateRegistry();
        registry.register("tee", classicTee());
        List<String> skus = new ArrayList<>();
        for (String size : List.of("S", "M", "L")) {
            skus.add(registry.newFrom("tee").with("colour", "red").with("size", size).sku());
        }
        print("case 3a sizes  ", skus, "[ClassicTee-red-S, ClassicTee-red-M, ClassicTee-red-L]");
        print("case 3b template", registry.newFrom("tee").sku(), "ClassicTee-white-M");

        // Case 4: the image list is copied too, not just the attribute map.
        red.images.add("red-closeup.jpg");
        print("case 4 images  ", base.images.size() + " vs " + red.images.size(), "2 vs 3");

        // Case 5: edge. Asking for a template nobody registered.
        String outcome;
        try {
            registry.newFrom("hoodie");
            outcome = "no exception";
        } catch (IllegalArgumentException e) {
            outcome = e.getMessage();
        }
        print("case 5 unknown ", outcome, "no template hoodie");
    }

    private static ProductListing classicTee() {
        Map<String, String> attributes = new LinkedHashMap<>();
        attributes.put("fabric", "cotton");
        attributes.put("fit", "regular");
        attributes.put("colour", "white");
        attributes.put("size", "M");
        return new ProductListing("Classic Tee", attributes,
                new ArrayList<>(List.of("front.jpg", "back.jpg")));
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
