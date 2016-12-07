package dtd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import dbms.TableColumn;
//import javafx.scene.transform.Scale;

public class DTD implements IDTD {
	private String path;
	private File dtdFile;
	private BufferedWriter buffer;
	private FileWriter writer;
	private String newLine = System.getProperty("line.separator");
	
	public DTD(String path) {
		this.path = path;
		dtdFile = new File(path +".dtd");
	}
	@Override
	public void writeDTD(String tableName, ArrayList<String> columns,
			ArrayList<String> types) throws DTDException {

		createFile(tableName);
		writeInFile(tableName, columns, types);

	}


	public ArrayList<TableColumn> readFromFile(String tableName) throws DTDException {
		ArrayList<TableColumn> tempColumns = new ArrayList<TableColumn>();


		try {
			String cName;
			String cType;
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(new File (path + File.separator + tableName + ".dtd"));

			String line = "";
			while (scanner.hasNextLine()) {
				line = scanner.next();
				if ((line.equals("<!DOCTYPE") || (line.equals("<!ELEMENT")))) {
					if (!tableName.equals(scanner.next())) {
						throw new DTDException("the DTD file of this table doesnot exist");
					}
				} else if (line.equals("<!ATTLIST")) {
					line = scanner.next();
					cName = line;
					line = scanner.next();
					if (line.equals("type")) {
						line = scanner.next();
						if (line.equals("CDATA")) {
							line = scanner.next();
							cType = getColumnType(line);
							TableColumn tempTableColumn = new TableColumn(cName, cType);
//							System.out.println("NAME: " + cName + "   TYPE: " + cType);
							tempColumns.add(tempTableColumn);
						}
					}
				}

			}
			return tempColumns;
		} catch (FileNotFoundException e) {

			throw new DTDException("Cannot find file!");

		}
	}


	public ArrayList<TableColumn> readDTD(String tableName) throws DTDException{
		ArrayList<TableColumn> columns = new ArrayList<TableColumn>();
		try {
			columns = readFromFile(tableName);
		} catch (DTDException e) {
			throw new DTDException("ERROR in reading the file");
		}
		return columns;
	}

	public String getColumnType(String line) throws DTDException {

		String cType = "";

		try {
			if (line.equals("\"varchar\">")) {
				cType = line.substring(1, 8);
//				System.out.println(cType);
			} else if (line.equals("\"int\">")){
				cType = line.substring(1, 4);
//				System.out.println(cType);
			} else {
				throw new DTDException("There is an incompatible type");
			}
		} catch (Exception e) {
			//System.out.println("There is an incompatible type!");
			throw new DTDException("There is an incompatible type!");
		}
		return cType;


	}
	
	@Override
	public  void deleteDTDFile () throws DTDException {
		File dtd = new File (this.path + ".dtd");
		if(!dtd.delete()) {
			throw new DTDException("CANOT DELETE DTD FILE");
		}
	}


	private void createFile(String fileName) throws DTDException {
//		dtdFile = new File(path +".dtd");
		dtdFile.getParentFile().mkdirs();
		try {
			dtdFile.createNewFile();
		} catch (IOException e) {
			throw new DTDException("ERROR in creating the file");

		}

	}

	private void writeInFile(String tableName, ArrayList<String> columns,
			ArrayList<String> types) throws DTDException {
		try {
			writer = new FileWriter(dtdFile.getAbsoluteFile());
			buffer = new BufferedWriter(writer);

			writeStart(tableName);
			writeElements(columns, types);

			buffer.close();
			writer.close();
//			dtdFile.delete();

		} catch (IOException e) {
			throw new DTDException("ERROR in writing the file");
		}

	}

	private void writeStart(String tableName) throws DTDException {
		String table = "<!ELEMENT "+ tableName + " (TableIdentifier, Row*)>" + newLine;
		try {
			buffer.write(table);
		} catch (IOException e) {
			throw new DTDException("ERROR in writing the file begining");

		}

	}

	private void writeElements( ArrayList<String> columns, ArrayList<String> types) throws DTDException {
		String elements ="";
		String identifier = "<!ELEMENT TableIdentifier (" ;
		String row = "<!ELEMENT Row (" ;

		for (int i = 0; i<columns.size(); i++) {
			if (i > 0) {
				identifier += ", ";
				row += ", ";
			}
			identifier += columns.get(i);
			row += columns.get(i);
			elements += "<!ELEMENT " + columns.get(i) +" (#PCDATA)>" + newLine;
			elements += "<!ATTLIST " + columns.get(i) +" type CDATA "+ "\""+types.get(i)+"\">" + newLine;
		}

		identifier += ")>" + newLine;
		row += ")>" + newLine;
		try {
			buffer.write(identifier);
			buffer.write(row);
			buffer.write(elements);
		} catch (IOException e) {
			throw new DTDException("ERROR in writing the elements");

		}
	}

}
