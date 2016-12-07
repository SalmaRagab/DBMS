package dbms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.xml.stream.XMLStreamException;

import xml.XMLHandler;

public class Table implements ITable{

	private String tableName;
	private TableColumn tableColumn;
	private String tablePath;
	private XMLHandler xmlHandler;
	private LinkedHashMap<String, ArrayList<String>> selectedColumns;
	private File tempFile, table;
	private TestWhereCondition testWhereCondition;

	public Table(String tableName, String tablePath) {
		this.tablePath = tablePath;
		this.tableName = tableName;
	}

	@Override
	public void insertRow(ArrayList<String> columnNames, ArrayList<String> columnValues) throws Exception {
		tableColumn = new TableColumn(tablePath, columnNames, columnValues);
		if (tableColumn.hasValidIdentifiers()) {
			ArrayList<ArrayList<String>> arrangedArray = new ArrayList<ArrayList<String>>();
			arrangedArray = tableColumn.rearrangeColumn();
			performInsertion(arrangedArray);
		} else {
			throw new DatabaseException("Invalid Column Entry !!");
		}

	}

	private void performInsertion(ArrayList<ArrayList<String>> arrangedArray) throws Exception {
		ArrayList<ArrayList<String>> row = initializeTempFile();
		while (true) {
			xmlHandler.XMLFastForward("Row");
			row = xmlHandler.XMLReadRow("Row");
			if (row.size() == 0) {
				break;
			}
			xmlHandler.XMLWriteRow(row.get(0), row.get(1));
		}

		xmlHandler.XMLWriteRow(arrangedArray.get(0), arrangedArray.get(1));
		xmlHandler.XMLEndWriter(this.tableName);
		xmlHandler.XMLEndReader();
		tempFile.delete(); 
	}

	@Override
	public void deleteRows(String tableName, String[] where) throws Exception {
        tableColumn = new TableColumn(tablePath, null, null);
        if (tableColumn.isValidQuery(where)) {
            performDeletion(where);
        } else {
            throw new DatabaseException("Invalid Query");
        }

    }

    private void performDeletion(String[] where) throws Exception {
        ArrayList<ArrayList<String>> row = initializeTempFile();
        testWhereCondition = new TestWhereCondition(where);
      
            while (true) {
                xmlHandler.XMLFastForward("Row");
                row = xmlHandler.XMLReadRow("Row");
                if (row.size() == 0) {
                    break;
                }

                if ((!testWhereCondition.isTrueCondition(row))) {
                    xmlHandler.XMLWriteRow(row.get(0), row.get(1));
                }
            }

            xmlHandler.XMLEndWriter(this.tableName);
            xmlHandler.XMLEndReader();
            tempFile.delete();
  
    }

	@Override
	public void updateRows(ArrayList<String> columnsNames, ArrayList<String> columnValues, String[] where)
			throws Exception {
		tableColumn = new TableColumn(tablePath, columnsNames, columnValues);
		if (!tableColumn.isValidQuery(where)) { // check that the where is valid
												// condition
			throw new DatabaseException("There is error in your  statement!");
		}
		performUpdate(columnsNames, columnValues, where);
	}

	private void performUpdate(ArrayList<String> columnsNames, ArrayList<String> columnValues, String[] where)
			throws Exception {
		ArrayList<ArrayList<String>> row = initializeTempFile();
		ArrayList<String> newColumnsNames;// the values which will be put (updated)
		ArrayList<String> newColumnsValues;
		int valueIndex;
		 testWhereCondition = new TestWhereCondition(where);
			while (true) {
				xmlHandler.XMLFastForward("Row");
				row = xmlHandler.XMLReadRow("Row");
				if (row.size() == 0) {
					break;
				}
				newColumnsNames = row.get(0); // as default the same values
				newColumnsValues = row.get(1);
				if (testWhereCondition.isTrueCondition(row)) { // the row applies condition
					for (int i = 0; i < newColumnsNames.size(); i++) { // find which col will be changed

						if (columnsNames.contains(newColumnsNames.get(i))) {
							valueIndex = columnsNames.indexOf(newColumnsNames.get(i)); // index of the value = same of column which will be updated
							newColumnsValues.set(i, columnValues.get(valueIndex)); // change values to new values
						}
					}
				}
			   xmlHandler.XMLWriteRow(newColumnsNames, newColumnsValues); // write the row (updated or not)
			}
			xmlHandler.XMLEndWriter(this.tableName);
			xmlHandler.XMLEndReader();
			tempFile.delete();
		}


	@Override
	public LinkedHashMap<String, ArrayList<String>> selectFromTable(ArrayList<String> columnNames, String[] where)
			throws Exception {
		tableColumn = new TableColumn(tablePath, columnNames, null);
		if (tableColumn.isValidQuery(where)) {
			return performSelection(columnNames, where);
		} else {
			throw new DatabaseException("Invalid Query");
		}

	}

	private LinkedHashMap<String, ArrayList<String>> performSelection(ArrayList<String> columnNames, String[] where)
			throws XMLStreamException, IOException {
		selectedColumns = new LinkedHashMap<String, ArrayList<String>>();
		xmlHandler = new XMLHandler(tablePath);
		xmlHandler.XMLReader(tablePath);
		xmlHandler.XMLFastForward("TableIdentifier");
		if (columnNames == null) {
			columnNames = xmlHandler.XMLReadRow("TableIdentifier").get(0);
		}
		for (int i = 0; i < columnNames.size(); i++) {
			ArrayList<String> columns = new ArrayList<String>();
			selectedColumns.put(columnNames.get(i), columns);
		}
		ArrayList<ArrayList<String>> row = new ArrayList<ArrayList<String>>();
		  testWhereCondition = new TestWhereCondition(where);
		while (true) {
			xmlHandler.XMLFastForward("Row");
			row = xmlHandler.XMLReadRow("Row");
			if (row.size() == 0) {
				break;
			}
			if (where == null || (where != null && testWhereCondition.isTrueCondition(row))) {
				fillHashMapWithSelectedColumns(row);
			}
		}
		xmlHandler.XMLEndReader();
		return selectedColumns;
	}

	private void fillHashMapWithSelectedColumns(ArrayList<ArrayList<String>> row) {
		for (String key : selectedColumns.keySet()) {
			int index = row.get(0).indexOf(key);
			String value = row.get(1).get(index);
			if (value.equals("null") || value == null) {
				value = "-";
			} else {
				value = value.replaceAll("^\"|^\'|\"$|\'$", "");
			
			}
			selectedColumns.get(key).add(value);
		}
	}
	private ArrayList<ArrayList<String>> initializeTempFile() {
		try {
			xmlHandler = new XMLHandler(tablePath);
			tempFile = new File(tablePath + "temp");
			table = new File(tablePath);
			xmlHandler.copyFile(table, tempFile);
			xmlHandler.XMLWriter(this.tableName);
			xmlHandler.XMLReader(tablePath + "temp");
			xmlHandler.XMLFastForward("TableIdentifier");
			ArrayList<ArrayList<String>> identifiers = xmlHandler.XMLReadRow("TableIdentifier");
			xmlHandler.XMLCreateTableIdentifier(this.tableName, identifiers.get(0), identifiers.get(1));
			ArrayList<ArrayList<String>> row = new ArrayList<ArrayList<String>>();
			return row;
		} catch (Exception e) {
			System.out.println("Error gathering information about needed file!");
		}
		return null;
	}

}