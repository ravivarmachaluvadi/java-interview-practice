import java.util.*;

// https://leetcode.com/problems/search-suggestions-system/description/
// 1268. Search Suggestions System
class SearchSuggestionsSystem {

    public static List<List<String>> suggestedProducts(String[] products, String searchWord) {
        Arrays.sort(products);
        List<List<String>> result = new ArrayList<>();
        int n = products.length;
        String prefix = "";
        int left = 0; // we can maintain a left pointer to the earliest candidate
        for (int i = 0; i < searchWord.length(); i++) {
            char c = searchWord.charAt(i);
            prefix += c;
            // move left to the first product that matches prefix
            while (left < n && !products[left].startsWith(prefix)) {
                left++;
            }
            List<String> suggestions = new ArrayList<>();
            // check up to next 3 products from left
            for (int j = left; j < Math.min(n, left + 3); j++) {
                if (products[j].startsWith(prefix)) {
                    suggestions.add(products[j]);
                } else {
                    break;
                }
            }
            result.add(suggestions);
        }
        return result;
    }

    public static void main(String[] args) {
        String[] products = {"mobile", "mouse", "moneypot", "monitor", "mousepad"};
        String searchWord = "mouse";
        List<List<String>> suggestions = suggestedProducts(products, searchWord);
        System.out.println("Suggestions for each prefix of \"" + searchWord + "\":");
        for (int i = 0; i < suggestions.size(); i++) {
            System.out.println("After typing \"" + searchWord.substring(0, i + 1) + "\": " + suggestions.get(i));
        }
    }
}
