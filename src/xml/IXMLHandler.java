package xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;


/**
 * The Interface IXMLHandler.
 */
public interface IXMLHandler {

	/**
	 * XML writer.
	 * Initializes StAX writing variables.
	 * Writes .XML file default head
	 * @param using XMLHandler initializer parameter => tablePath
	 * @throws XMLStreamException the XML stream exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void XMLWriter(String tableName) throws XMLStreamException, IOException;


	/**
	 * XML end writer.
	 * Ends StAX writer variables
	 * writes end node of the file.
	 * @param using XMLHandler initializer parameter => tablePath
	 * @throws XMLStreamException the XML stream exception
	 * @throws IOException
	 */
	public void XMLEndWriter(String tableName) throws XMLStreamException, IOException;

	/**
	 * XML reader.
	 * Initialized StAX reading variables
	 * @param path of the file to be read
	 * @throws XMLStreamException the XML stream exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void XMLReader(String path) throws XMLStreamException, IOException;

	/**
	 * XML end reader.
	 * Ends StAX reading variables.
	 * @param uses XMLReader param
	 * @throws XMLStreamException the XML stream exception
	 * @throws IOException
	 */
	public void XMLEndReader() throws XMLStreamException, IOException;

	/**
	 * XML fast forward.
	 * Runs the StAX reading pointer to the desired node.
	 * @param parentNode => the node to start reading from (not included)
	 * @throws XMLStreamException the XML stream exception
	 */
	public void XMLFastForward(String parentNode) throws XMLStreamException;

	/**
	 * XML create table identifier.
	 * Writes Indentifiers of table (row 0)
	 *
	 *
	 * ====> Must initialize XMLHandler and XMLWriter first
	 * @param tableName the table name
	 * @param Array list of column names
	 * @param Array list of  column types
	 * @throws Exception the exception
	 */
	public void XMLCreateTableIdentifier(String tableName, ArrayList<String> columnNames, ArrayList<String> columnTypes)
			throws Exception;

	/**
	 * XML read row.
	 * Reads row and returns [[Name, age][varchar, int]]
	 * @param parent node to start reading from
	 * ===> Must use XMLFastForward to reach first occurrance of node only
	 * @return the array list
	 * @throws XMLStreamException the XML stream exception
	 */
	public ArrayList<ArrayList<String>> XMLReadRow(String parentNode) throws XMLStreamException;

	/**
	 * XML write row.
	 *
	 * @param Array list of column names
	 * @param Array list of column values
	 * ==> writes null in front of each null node
	 * @throws Exception the exception
	 */
	public void XMLWriteRow(ArrayList<String> columnNames, ArrayList<String> columnValues) throws Exception;

	/**
	 * Copy file.
	 * Copy file content to another.
	 * @param source the source file
	 * @param dest the destination file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void copyFile(File source, File dest) throws IOException;

}