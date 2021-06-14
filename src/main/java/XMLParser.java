import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Roman Rusanov
 * @since 14.06.2021
 * email roman9628@gmail.com
 */
public class XMLParser implements Runnable {
    /**
     * The instance with logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(XMLParser.class.getName());
    /**
     * The marker for logger.
     */
    private static final Marker MARKER = MarkerFactory.getMarker("XML");

    private final Path file;

    private final ConcurrentHashMap<String, Integer> allRecords;

    public XMLParser(Path file, ConcurrentHashMap<String, Integer> allRecords) {
        this.file = file;
        this.allRecords = allRecords;
    }

    public HashMap<String, Integer> parseFile() {
        HashMap<String, Integer> records = new HashMap<>();
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try (FileInputStream fis = new FileInputStream(this.file.toFile())) {
            LOG.debug(MARKER, "Start parse xml file: {}", this.file);
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(fis);
            String inn = "";
            String workers = "";
            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()) {
                        case "СведНП":
                            Attribute innAtr = startElement.getAttributeByName(new QName("ИННЮЛ"));
                            if (innAtr != null) {
                                inn = innAtr.getValue();
                            }
                            break;
                        case "СведССЧР":
                            Attribute workersAtr = startElement.getAttributeByName(new QName("КолРаб"));
                            if (workersAtr != null) {
                                workers = workersAtr.getValue();
                            }
                            break;
                    }
                }
                if (nextEvent.isEndElement()) {
                    EndElement endElement = nextEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("Документ")) {
                        records.put(inn, Integer.parseInt(workers));
                    }
                }
            }
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }
        return records;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        this.allRecords.putAll(this.parseFile());
    }
}