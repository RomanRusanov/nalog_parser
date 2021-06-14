import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

/**
 * @author Roman Rusanov
 * @since 11.06.2021
 * email roman9628@gmail.com
 */
public class Downloader {
    /**
     * The instance with logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Downloader.class.getName());
    /**
     * The marker for logger.
     */
    private static final Marker MARKER = MarkerFactory.getMarker("File");

    private Path tempDir;

    private File file;

    public Path downloadFileFromLink(String link) {
        Optional<Path> downloadedFile = Optional.empty();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_k.m.s#").format(Calendar.getInstance().getTime());
        URL urlLink = SiteLoader.getURLFromString(link);
        try {
            tempDir = Files.createTempDirectory(timeStamp);
            String fileName = FilenameUtils.getName(urlLink.getPath());
            file = new File(tempDir.toString() + File.separator + fileName);
            Files.createFile(file.toPath());
            ReadableByteChannel rbc = Channels.newChannel(urlLink.openStream());
            FileOutputStream fos = new FileOutputStream(file);
            LOG.info(MARKER, "File {} download started OK! Please wait!", fileName);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            downloadedFile = Optional.of(file.toPath());
            LOG.info(MARKER, "File {} download complete OK!", file);
        } catch (IOException e) {
            LOG.error(MARKER, "Can't download file: {} From link: {}", file, link);
        }
        return downloadedFile.orElseThrow(() ->
                new IllegalArgumentException(
                        String.format("Can't get downloaded file path from link(%s)", link)));
    }

    public Path getTempDir() {
        return tempDir;
    }

    public void deleteTempDir() {
        try {
            FileUtils.deleteDirectory(tempDir.toFile());
            LOG.info(MARKER, "Delete temp dir: {} OK!", tempDir);
        } catch (IOException e) {
            LOG.error(MARKER, "Can't delete temp directory: {}", tempDir);
        }
    }
}