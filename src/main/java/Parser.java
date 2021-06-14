import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Roman Rusanov
 * @since 12.06.2021
 * email roman9628@gmail.com
 */
public class Parser {
    /**
     * The instance with logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Parser.class.getName());
    /**
     * The marker for logger.
     */
    private static final Marker MARKER = MarkerFactory.getMarker("Parser");
    private final String siteUrl;
    private final SiteLoader siteLoader;
    private final Downloader downloader;
    private final UnZip unZip;
    private final FolderProcessor folderProcessor;
    private static final ConcurrentHashMap<String, Integer> allRecords = new ConcurrentHashMap<>();

    public Parser(String siteUrl, SiteLoader siteLoader, Downloader downloader, UnZip unZip, FolderProcessor folderProcessor) {
        this.siteUrl = siteUrl;
        this.siteLoader = siteLoader;
        this.downloader = downloader;
        this.unZip = unZip;
        this.folderProcessor = folderProcessor;
    }

    public void execute() {
        if (checkSiteIsUp(siteUrl)) {
            Document startPage = siteLoader.getDocFromUrl(siteUrl);
            String pageWithLink = siteLoader.getUrlPageWithFileDownload(startPage);
            String linkFileDownload = siteLoader.getLinkForFileDownload(pageWithLink);
            Path zipDownloaded = downloader.downloadFileFromLink(linkFileDownload);
            unZip.unzip(zipDownloaded, downloader.getTempDir());
            LOG.info(MARKER, "Start parse all xml in folder: {} OK! Please wait!", downloader.getTempDir());
            folderProcessor.processDirectory(unZip.getUnZipDirectory());
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            LOG.info(MARKER, "Parser load: {} records(INN, Workers)", allRecords.size());
            LOG.info(MARKER, "five companies with a large number of employees [INN, Workers]: {} ", findGreatest(allRecords, 5));
            downloader.deleteTempDir();
        } else {
            throw new IllegalStateException("Can't connect to site: " + siteUrl);
        }
    }

    private boolean checkSiteIsUp(String url) {
        URL siteUrl = SiteLoader.getURLFromString(url);
        return CheckAccessible.isSiteUp(siteUrl);
    }

    public static ConcurrentHashMap<String, Integer> getAllRecords() {
        return allRecords;
    }

    private static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> findGreatest(Map<K, V> map, int n) {
        Comparator<? super Map.Entry<K, V>> comparator = (Comparator<Map.Entry<K, V>>) (e0, e1) -> {
            V v0 = e0.getValue();
            V v1 = e1.getValue();
            return v0.compareTo(v1);
        };
        PriorityQueue<Map.Entry<K, V>> highest = new PriorityQueue<>(n, comparator);
        for (Map.Entry<K, V> entry : map.entrySet()) {
            highest.offer(entry);
            while (highest.size() > n) {
                highest.poll();
            }
        }
        List<Map.Entry<K, V>> result = new ArrayList<>();
        while (highest.size() > 0) {
            result.add(highest.poll());
        }
        return result;
    }
}