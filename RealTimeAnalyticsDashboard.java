import java.util.*;

public class RealTimeAnalyticsDashboard {

    private final HashMap<String, Integer> pageViews = new HashMap<>();
    private final HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();
    private final HashMap<String, Integer> trafficSources = new HashMap<>();

    public synchronized void processEvent(String pageUrl, String userId, String source) {
        pageViews.put(pageUrl, pageViews.getOrDefault(pageUrl, 0) + 1);

        uniqueVisitors.computeIfAbsent(pageUrl, k -> new HashSet<>()).add(userId);

        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
    }

    public synchronized void displayDashboard() {
        System.out.println("----- DASHBOARD UPDATE -----");

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> e : pageViews.entrySet()) {
            pq.offer(e);
            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<Map.Entry<String, Integer>> topPages = new ArrayList<>();
        while (!pq.isEmpty()) {
            topPages.add(pq.poll());
        }
        Collections.reverse(topPages);

        System.out.println("Top Pages:");
        for (Map.Entry<String, Integer> e : topPages) {
            String page = e.getKey();
            int views = e.getValue();
            int unique = uniqueVisitors.getOrDefault(page, new HashSet<>()).size();
            System.out.println(page + " | Views: " + views + " | Unique Visitors: " + unique);
        }

        System.out.println("Traffic Sources:");
        for (Map.Entry<String, Integer> e : trafficSources.entrySet()) {
            System.out.println(e.getKey() + " : " + e.getValue());
        }
    }

    public static void main(String[] args) throws Exception {
        RealTimeAnalyticsDashboard dashboard = new RealTimeAnalyticsDashboard();

        Thread generator = new Thread(() -> {
            String[] pages = {"/home", "/news", "/sports", "/tech", "/world"};
            String[] sources = {"Google", "Facebook", "Direct", "Twitter"};
            Random rand = new Random();

            while (true) {
                String page = pages[rand.nextInt(pages.length)];
                String source = sources[rand.nextInt(sources.length)];
                String user = "user" + rand.nextInt(1000);

                dashboard.processEvent(page, user, source);

                try {
                    Thread.sleep(10);
                } catch (Exception ignored) {
                }
            }
        });

        generator.setDaemon(true);
        generator.start();

        while (true) {
            Thread.sleep(5000);
            dashboard.displayDashboard();
        }
    }
}