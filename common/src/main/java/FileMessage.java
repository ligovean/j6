import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class FileMessage extends AbstractMessage {
    private String filename;
    private byte[] data;

    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }

    public FileMessage(UUID clientId, Path path) throws IOException {
        this.clientId =  clientId;
        filename = path.getFileName().toString();
        data = Files.readAllBytes(path);
    }
}
