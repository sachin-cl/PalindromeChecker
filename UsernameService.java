import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UsernameService {

    private ConcurrentHashMap<String, Integer> usernameToUserId = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, AtomicInteger> usernameAttempts = new ConcurrentHashMap<>();
    private AtomicInteger userIdCounter = new AtomicInteger(1);

    public boolean registerUsername(String username) {
        if (checkAvailability(username)) {
            int userId = userIdCounter.getAndIncrement();
            usernameToUserId.put(username, userId);
            return true;
        }
        return false;
    }

    public boolean checkAvailability(String username) {
        usernameAttempts.putIfAbsent(username, new AtomicInteger(0));
        usernameAttempts.get(username).incrementAndGet();
        return !usernameToUserId.containsKey(username);
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int suffix = 1;

        while (suggestions.size() < 3) {
            String suggestion = username + suffix;
            if (!usernameToUserId.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
            suffix++;
        }

        String dotVersion = username.replace("_", ".");
        if (!usernameToUserId.containsKey(dotVersion)) {
            suggestions.add(dotVersion);
        }

        return suggestions;
    }

    public String getMostAttempted() {
        String mostAttempted = null;
        int maxAttempts = 0;

        for (Map.Entry<String, AtomicInteger> entry : usernameAttempts.entrySet()) {
            int count = entry.getValue().get();
            if (count > maxAttempts) {
                maxAttempts = count;
                mostAttempted = entry.getKey();
            }
        }

        return mostAttempted + " (" + maxAttempts + " attempts)";
    }

    public static void main(String[] args) {

        UsernameService service = new UsernameService();

        service.registerUsername("john_doe");
        service.registerUsername("admin");

        System.out.println(service.checkAvailability("john_doe"));
        System.out.println(service.checkAvailability("jane_smith"));
        System.out.println(service.suggestAlternatives("john_doe"));
        System.out.println(service.getMostAttempted());
    }
}