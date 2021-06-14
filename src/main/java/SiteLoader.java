import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * @author Roman Rusanov
 * @since 11.06.2021
 * email roman9628@gmail.com
 */
public class SiteLoader {
    /**
     * The instance with logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SiteLoader.class.getName());
    /**
     * The marker for logger.
     */
    private static final Marker MARKER = MarkerFactory.getMarker("Site");

    /**
     * The method get Jsoup Document from string url.
     *
     * @param url string with site.
     * @return Document.
     */
    public Document getDocFromUrl(String url) {
        Optional<Document> currentPage = Optional.empty();
        try {
            currentPage = Optional.of(Jsoup.connect(url).get());
            LOG.info(MARKER, "DOM from url: {} loaded OK!", url);
        } catch (IOException e) {
            LOG.error(MARKER, "Can't get page from url: {}", url, e);
        }
        return currentPage.orElseThrow(() ->
                new IllegalArgumentException(String.format("Can't get page from url(%s)", url)));
    }

    public String getUrlPageWithFileDownload(Document document) {
        Element element = document.select(
                "td:contains(Сведения о среднесписочной численности работников организации) > a").first();
        String baseUri = element.baseUri().replace("/rn77/opendata/", "");
        String href = element.attr("href");
        return baseUri + href;
    }

    public static URL getURLFromString(String stringUrl) {
        Optional<URL> url = Optional.empty();
        try {
            url = Optional.of(new URL(stringUrl));
        } catch (MalformedURLException e) {
            LOG.error(MARKER,
                    "Can't create url from string({}). Exception:{}",
                    stringUrl, e);
        }
        return url.orElseThrow(() ->
                new IllegalArgumentException(String.format("Can't create url from string(%s)", stringUrl)));
    }

    public String getLinkForFileDownload(String url) {
        Document document = getDocFromUrl(url);
        Element element = document.select(
                "td:contains(Гиперссылка (URL) на набор)").next().first().child(0);
        String link = element.attr("href");
        if (link.isEmpty()) {
            throw new IllegalArgumentException(String.format("Can't create url from string(%s)", url));
        }
        LOG.info(MARKER, "Link to file: {} OK!", link);
        return link;
    }
}