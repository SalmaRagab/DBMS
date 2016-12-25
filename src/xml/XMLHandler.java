
package xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import javanet.staxutils.IndentingXMLEventWriter;

public class XMLHandler implements IXMLHandler {

	private String tablePath;
	private XMLEventWriter writer;
	private XMLOutputFactory factory;
	private XMLInputFactory inFactory;
	private XMLEventReader reader;
	private XMLEvent event, end, tab;
	private XMLEventFactory eventFactory;
	private OutputStream outputStream;
	private InputStream inputStream;

	public XMLHandler(String tablePath) {
		this.tablePath = tablePath;
		this.eventFactory = XMLEventFactory.newInstance();
		this.end = this.eventFactory.createDTD("\r");
		this.tab = this.eventFactory.createDTD(" ");
	}

	@Override
	public void XMLWriter(String tableName) throws XMLStreamException, IOException {
		this.factory = XMLOutputFactory.newInstance();
		this.factory.setProperty("escapeCharacters", false);
		Path path = Paths.get(this.tablePath);
		this.outputStream = Files.newOutputStream(path);
		this.writer = factory.createXMLEventWriter(outputStream, "UTF-8");
		this.writer = new IndentingXMLEventWriter(writer);
		this.writer.add(eventFactory.createStartDocument());
		this.writer.add(end);
		this.writer.add(eventFactory.createCharacters("<!DOCTYPE "+tableName+" SYSTEM \""+tableName+".dtd\">"));
		this.writer.add(end);
		this.writer.add(eventFactory.createStartElement("", "", tableName));
		this.writer.add(end);
	}

	@Override
	public void XMLEndWriter(String tableName) throws XMLStreamException, IOException {
		this.writer.add(end);
		this.writer.add(eventFactory.createEndElement("", "", tableName));
		this.writer.add(eventFactory.createEndDocument());
		this.writer.flush();
		this.writer.close();
		this.outputStream.flush();
		this.outputStream.close();
	}

	@Override
	public void XMLReader(String path) throws XMLStreamException, IOException {
		this.inFactory = XMLInputFactory.newInstance();
		Path pathh = Paths.get(path);
		this.inputStream = Files.newInputStream(pathh);
	     this.reader = inFactory.createXMLEventReader(this.inputStream);
	}

	@Override
	public void XMLEndReader() throws XMLStreamException, IOException {
		this.reader.close();
		this.inputStream.close();
	}

	@Override
	public void XMLFastForward(String parentNode) throws XMLStreamException {
		Boolean flag = false;
		while (reader.hasNext() && !flag) {
			event = reader.nextEvent();
			switch (event.getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
				StartElement startElement = event.asStartElement();
				String qName = startElement.getName().getLocalPart();
				if (qName.equalsIgnoreCase(parentNode)) {
					flag = true;
				}
				break;
			}
		}
	}

	@Override
	public void XMLCreateTableIdentifier(String tableName, ArrayList<String> columnNames, ArrayList<String> columnTypes)
			throws Exception {
		this.writer.add(eventFactory.createStartElement("", "", "TableIdentifier"));
		this.writer.add(end);

		for (int i = 0; i < columnNames.size(); i++) {
			writer.add(tab);
			writer.add(eventFactory.createStartElement("", "", columnNames.get(i)));
			writer.add(eventFactory.createAttribute("type", columnTypes.get(i)));
			writer.add(eventFactory.createEndElement("", "", columnNames.get(i)));
			writer.add(end);
		}
		writer.add(eventFactory.createEndElement("", "", "TableIdentifier"));
	}

	@Override
	public void copyFile(File source, File dest) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(dest.getPath());
		Files.copy(source.toPath(), fileOutputStream);
		fileOutputStream.close();
	}



	@Override
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<String>> XMLReadRow(String parentNode) throws XMLStreamException {
		boolean isEnded = false;
		ArrayList<ArrayList<String>> C = new ArrayList<ArrayList<String>>();
		ArrayList<String> A = new ArrayList<String>();
		ArrayList<String> B = new ArrayList<String>();
		while (reader.hasNext() && !isEnded) {
			event = reader.nextEvent();
			switch (event.getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
				StartElement startElement = event.asStartElement();
				A.add(startElement.getName().toString());
				Iterator<Attribute> attributes = startElement.getAttributes();
				if (attributes.hasNext()) {
					String attributeValue = attributes.next().getValue();
					B.add(attributeValue);
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart().equalsIgnoreCase(parentNode)) {
					isEnded = true;
					C.add(A);
					C.add(B);
				}
				break;
			case XMLStreamConstants.CHARACTERS:
				Characters characters = event.asCharacters();
				if (!(characters.isWhiteSpace())) {
					B.add(characters.toString());
				}
				break;
			}
		}
		return C;
	}

	@Override
	public void XMLWriteRow(ArrayList<String> columnNames, ArrayList<String> columnValues) throws Exception {
		writer.add(end);
		writer.add(eventFactory.createStartElement("", "", "Row"));
		writer.add(end);
		for (int i = 0; i < columnNames.size(); i++) {
			writer.add(tab);
			writer.add(eventFactory.createStartElement("", "", columnNames.get(i)));
			if (columnValues.get(i) == (null)) { ///////////////////////
				writer.add(eventFactory.createCharacters("null"));

			} else {
				writer.add(eventFactory.createCharacters(columnValues.get(i)));
			}
			writer.add(eventFactory.createEndElement("", "", columnNames.get(i)));
			writer.add(end);
		}
		writer.add(eventFactory.createEndElement("", "", "Row"));
	}
}