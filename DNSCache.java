import java.util.*;

public class DNSCache {
    static class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, long ttl) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + ttl * 1000;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private final int capacity;
    private final LinkedHashMap<String, DNSEntry> cache;
    private int hits = 0;
    private int misses = 0;

    public DNSCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };
        startCleaner();
    }

    public synchronized String resolve(String domain) {
        long start = System.nanoTime();
        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits++;
            long end = System.nanoTime();
            double time = (end - start) / 1_000_000.0;
            return "Cache HIT → " + entry.ipAddress + " (retrieved in " + time + " ms)";
        }

        if (entry != null && entry.isExpired()) {
            cache.remove(domain);
        }

        misses++;
        String ip = queryUpstreamDNS(domain);
        cache.put(domain, new DNSEntry(domain, ip, 300));
        return "Cache MISS → Query upstream → " + ip + " (TTL: 300s)";
    }

    private String queryUpstreamDNS(String domain) {
        Random r = new Random();
        return "172.217.14." + (200 + r.nextInt(50));
    }

    public synchronized String getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0) / total;
        return "Hit Rate: " + String.format("%.2f", hitRate) + "%";
    }

    private void startCleaner() {
        Thread cleaner = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    synchronized (DNSCache.this) {
                        Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, DNSEntry> e = it.next();
                            if (e.getValue().isExpired()) {
                                it.remove();
                            }
                        }
                    }
                } catch (InterruptedException ignored) {
                }
            }
        });
        cleaner.setDaemon(true);
        cleaner.start();
    }

    public static void main(String[] args) {
        DNSCache dnsCache = new DNSCache(5);

        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.resolve("openai.com"));
        System.out.println(dnsCache.resolve("google.com"));

        System.out.println(dnsCache.getCacheStats());
    }
}