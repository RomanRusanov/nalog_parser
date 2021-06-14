import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * @author Roman Rusanov
 * @since 11.06.2021
 * email roman9628@gmail.com
 */
public class CheckAccessible {
    /**
     * The instance with logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(CheckAccessible.class.getName());
    /**
     * The marker for logger.
     */
    private static final Marker MARKER = MarkerFactory.getMarker("Site");

    public static boolean isSiteUp(URL site) {
        boolean result = false;
        try {
            HttpURLConnection conn = (HttpURLConnection) site.openConnection();
            conn.getContent();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                result = true;
                LOG.info(MARKER, "URL: {} response OK!", site);
            } else {
                LOG.error(MARKER, "URL: {} not response", site);
            }
            return result;
        } catch (SocketTimeoutException tout) {
            LOG.error(MARKER, "Timeout connection to site {}", site, tout);
            return result;
        } catch (IOException ioex) {
            LOG.error(MARKER, "IO error. Attempt connect to site {}", site, ioex);
            return result;
        }
    }

}