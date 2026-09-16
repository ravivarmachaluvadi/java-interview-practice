import java.util.HashMap;
import java.util.Map;

class FindTopIpaddress {
    public static String findTopIpaddress(String[] inputs) {
        Map<String, Integer> ipCountMap = new HashMap<>();

        for (String logEntry : inputs) {
            String ip = logEntry.split(" ")[0];
            ipCountMap.put(ip, ipCountMap.getOrDefault(ip, 0) + 1);
        }
        String topIp = "";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : ipCountMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                topIp = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return topIp;
    }

    public static void main(String[] args) {
        String[] input = new String[]{
                "10.0.0.1 - log entry 1 11",
                "10.0.0.1 - log entry 2 213",
                "10.0.0.2 - log entry 133132"
        };

        String result = findTopIpaddress(input);

        if (result.equals("10.0.0.1")) {
            System.out.println("Test passed");
        } else {
            System.out.println("Test failed");
        }
    }
}
