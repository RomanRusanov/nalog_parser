import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Roman Rusanov
 * @since 13.06.2021
 * email roman9628@gmail.com
 */
public class FolderProcessor {

    public void processDirectory(Path workDir) {
        FilesHandler filesHandler = new FilesHandler(p -> p.toFile().getName().endsWith("xml"));
        try {
            Files.walkFileTree(workDir, filesHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}