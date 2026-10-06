/*
 * =====================================================================
 *  Builder - product search request                Creational | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   The search page sends a keyword ("running shoes") plus any of: category,
 *   price range, brands, sort order, page number. A 7-argument constructor
 *   is unreadable, and nothing stops a caller passing minPrice and maxPrice
 *   the wrong way round.
 *
 * KEY INSIGHT
 *   Required values go in the builder's constructor, optional ones are named
 *   methods, and build() is the single gate that validates. An invalid
 *   SearchRequest can never exist, and once built it is immutable.
 *
 * ROLES IN THIS CODE
 *   SearchRequest           Product (immutable, private constructor)
 *   SearchRequest.Builder   Builder (static nested, fluent)
 *   toBuilder()             copy-and-modify, e.g. "same search, next page"
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not setters? The object would be mutable and could be half-built.
 *   - Why Set.copyOf in the constructor? Case 5: the builder's own set keeps
 *     changing after build().
 *   - Seen in: HttpRequest.newBuilder(), StringBuilder, Lombok @Builder.
 *
 * RUN
 *   6 cases: full build, defaults, bad price range, missing keyword, next
 *   page via toBuilder, builder reused after build.
 */

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

class SearchRequest {
    enum Sort { RELEVANCE, PRICE_LOW_TO_HIGH, NEWEST }

    private final String keyword;
    private final String category; // null = all categories
    private final int minPrice;
    private final int maxPrice;
    private final Set<String> brands;
    private final Sort sort;
    private final int page;

    private SearchRequest(Builder b) {
        keyword = b.keyword;
        category = b.category;
        minPrice = b.minPrice;
        maxPrice = b.maxPrice;
        brands = Set.copyOf(b.brands); // snapshot: later builder changes cannot leak in
        sort = b.sort;
        page = b.page;
    }

    static Builder builder(String keyword) {
        return new Builder(keyword);
    }

    /** Copy-and-modify. The original request is untouched. */
    Builder toBuilder() {
        Builder b = new Builder(keyword).category(category).price(minPrice, maxPrice)
                .sortBy(sort).page(page);
        b.brands.addAll(brands);
        return b;
    }

    @Override
    public String toString() {
        return "q=" + keyword + (category == null ? "" : " cat=" + category)
                + " price=" + minPrice + "-" + (maxPrice == Integer.MAX_VALUE ? "any" : maxPrice)
                + " brands=" + new TreeSet<>(brands) + " sort=" + sort + " page=" + page;
    }

    static class Builder {
        private final String keyword;
        private String category;
        private int minPrice = 0;
        private int maxPrice = Integer.MAX_VALUE;
        private final Set<String> brands = new HashSet<>();
        private Sort sort = Sort.RELEVANCE;
        private int page = 1;

        private Builder(String keyword) {
            this.keyword = keyword;
        }

        Builder category(String category) {
            this.category = category;
            return this;
        }

        Builder price(int min, int max) {
            this.minPrice = min;
            this.maxPrice = max;
            return this;
        }

        Builder brand(String brand) {
            brands.add(brand);
            return this;
        }

        Builder sortBy(Sort sort) {
            this.sort = sort;
            return this;
        }

        Builder page(int page) {
            this.page = page;
            return this;
        }

        /** The one gate: every invariant is checked before the object exists. */
        SearchRequest build() {
            if (keyword == null || keyword.isBlank()) {
                throw new IllegalStateException("keyword is required");
            }
            if (minPrice > maxPrice) {
                throw new IllegalStateException("minPrice " + minPrice + " > maxPrice " + maxPrice);
            }
            return new SearchRequest(this);
        }
    }
}

class BuilderExample {

    public static void main(String[] args) {
        // Case 1: typical. Every value is labelled by its method name.
        SearchRequest shoes = SearchRequest.builder("running shoes")
                .category("footwear")
                .price(2000, 5000)
                .brand("Nike").brand("Asics")
                .sortBy(SearchRequest.Sort.PRICE_LOW_TO_HIGH)
                .build();
        print("case 1 full    ", shoes, "q=running shoes cat=footwear price=2000-5000"
                + " brands=[Asics, Nike] sort=PRICE_LOW_TO_HIGH page=1");

        // Case 2: edge. Only the required keyword; defaults fill the rest.
        print("case 2 defaults", SearchRequest.builder("tv").build(),
                "q=tv price=0-any brands=[] sort=RELEVANCE page=1");

        // Case 3-4: build() refuses an invalid request, so none ever exists.
        print("case 3 bad range",
                attempt(() -> SearchRequest.builder("tv").price(5000, 2000).build()),
                "IllegalStateException minPrice 5000 > maxPrice 2000");
        print("case 4 no keyword", attempt(() -> SearchRequest.builder(" ").build()),
                "IllegalStateException keyword is required");

        // Case 5: "load more" - same search, next page. Page 1 is not touched.
        SearchRequest page2 = shoes.toBuilder().page(2).build();
        print("case 5a page 2  ", page2.toString().endsWith("page=2"), true);
        print("case 5b page 1  ", shoes.toString().endsWith("page=1"), true);

        // Case 6: tricky. Reuse a builder after build(); the first request keeps
        // its own brand set because the constructor copied it.
        SearchRequest.Builder reused = SearchRequest.builder("phone").brand("Sony");
        SearchRequest first = reused.build();
        reused.brand("LG").build();
        print("case 6 reuse    ", first, "q=phone price=0-any brands=[Sony] sort=RELEVANCE page=1");
    }

    private static String attempt(Supplier<Object> call) {
        try {
            return String.valueOf(call.get());
        } catch (RuntimeException e) {
            return e.getClass().getSimpleName() + " " + e.getMessage();
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
