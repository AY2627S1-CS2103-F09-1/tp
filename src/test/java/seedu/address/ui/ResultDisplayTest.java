package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Tests the layout of the result display. The layout file is read as XML, so that no JavaFX toolkit, and
 * therefore no screen, is needed to run the tests.
 */
public class ResultDisplayTest {

    private static final String LAYOUT_FILE = "/view/ResultDisplay.fxml";
    private static final String TEXT_AREA_TAG = "TextArea";

    @Test
    public void layout_textArea_wrapsTextToWidthOfWindow() throws Exception {
        Element textArea = getTextArea();

        assertEquals("true", textArea.getAttribute("wrapText"),
                "Long messages must wrap, as the user would otherwise have to scroll sideways to read them");
    }

    @Test
    public void layout_textArea_cannotBeEdited() throws Exception {
        assertEquals("false", getTextArea().getAttribute("editable"));
    }

    private Element getTextArea() throws Exception {
        try (InputStream layout = ResultDisplay.class.getResourceAsStream(LAYOUT_FILE)) {
            assertNotNull(layout, "Missing layout file " + LAYOUT_FILE);
            NodeList textAreas = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(layout)
                    .getElementsByTagName(TEXT_AREA_TAG);
            assertEquals(1, textAreas.getLength());
            return (Element) textAreas.item(0);
        }
    }
}
