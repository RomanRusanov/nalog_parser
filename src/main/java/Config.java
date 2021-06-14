import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author Roman Rusanov
 * @since 12.06.2021
 * email roman9628@gmail.com
 */
public class Config {
    /**
     * The field contain properties.
     */
    private final Properties config = new Properties();
    /**
     * The instance with logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Config.class.getName());
    /**
     * The marker for logger.
     */
    private static final Marker MARKER = MarkerFactory.getMarker("Config");

    /**
     * The default constructor.
     * Initiate connection.
     *
     * @param configFile string file name.
     */
    public Config(String configFile) {
        this.init(configFile);
    }

    /**
     * Getter for properties.
     *
     * @return properties.
     */
    public Properties getConfig() {
        return config;
    }

    /**
     * The method load properties from file example (app.properties).
     *
     * @param configFile string file name.
     */
    public void init(String configFile) {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream(configFile)) {
            config.load(in);
            LOG.info(MARKER, "Config from file: {} loaded OK!. ", configFile);
        } catch (Exception e) {
            LOG.error(MARKER, "Config from file: {} not loaded. ", configFile, e);
        }
    }
}