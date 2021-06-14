import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Roman Rusanov
 * @since 12.06.2021
 * email roman9628@gmail.com
 */
public class UnZip {
    /**
     * The instance with logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(UnZip.class.getName());
    /**
     * The marker for logger.
     */
    private static final Marker MARKER = MarkerFactory.getMarker("File");

    private String extractedFileName;

    private Path unZipDirectory;

    public void unzip(Path source, Path targetDir) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {
            unZipDirectory = Files.createDirectory(Paths.get(targetDir + File.separator + "unZip"));
            ZipEntry zipEntry = zis.getNextEntry();
            LOG.info(MARKER, "Unzip the file: {} in progress OK! Please wait!", source);
            while (zipEntry != null) {
                extractedFileName = zipEntry.getName();
                Path targetFile = Paths.get(unZipDirectory + File.separator + extractedFileName);
                Files.copy(zis, targetFile, StandardCopyOption.REPLACE_EXISTING);
                LOG.debug(MARKER, "File: {} extracted from zip OK!", extractedFileName);
                zipEntry = zis.getNextEntry();
            }
            LOG.info(MARKER, "Archive: {} unpacked successfully OK!", source);
            zis.closeEntry();
        } catch (IOException e) {
            LOG.error(MARKER, "Can't unzip file: {} From source zip: {}", extractedFileName, source);
        }
    }

    public Path getUnZipDirectory() {
        return unZipDirectory;
    }
}