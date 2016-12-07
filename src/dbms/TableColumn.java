package dbms;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import xml.XMLHandler;

public class TableColumn implements ITableColumn {

	private ArrayList<String> columnNames;
	private ArrayList<String> columnValues;
	private ArrayList<ArrayList<String>> columnIdentifiers;
	private XMLHandler xmlHandler;
	private String tablePath;
	private String columnName;
	private String columnType;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public TableColumn(String tablePath, ArrayList<String> columnNames, ArrayList<String> columnValues) {
		this.tablePath = tablePath;
		this.columnNames = columnNames;
		this.columnValues = columnValues;
	}

	public TableColumn(String columnName, String columnType) {
		this.columnName = columnName;
		this.columnType = columnType;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	@Override
	public void getColumnIdentifiers() throws XMLStreamException, IOException {

		xmlHandler = new XMLHandler(this.tablePath);
		xmlHandler.XMLReader(tablePath);
		xmlHandler.XMLFastForward("TableIdentifier");
		columnIdentifiers = xmlHandler.XMLReadRow("TableIdentifier");
		xmlHandler.XMLEndReader();

	}

	@Override
	public boolean hasValidIdentifiers() throws XMLStreamException, IOException {
		getColumnIdentifiers();
		if (columnNames == null && (columnValues.size() != columnIdentifiers.get(0).size())) { 
			return false;
		} else if (columnNames != null && (columnNames.size() != columnValues.size())) {
			return false;
		}

		if (columnNames != null) { 
			if (!validateColumnNames()) {
				return false;
			}
			ArrayList<ArrayList<String>> arrangedArray = rearrangeColumn();
			columnNames = arrangedArray.get(0);
			columnValues = arrangedArray.get(1);

		} else {
			columnNames = columnIdentifiers.get(0);
		}
		if (!validateColumnValues()) {
			return false;
		}
		return true;

	}

	@Override
	public ArrayList<ArrayList<String>> rearrangeColumn() {
		ArrayList<String> tempNames = new ArrayList<String>();
		ArrayList<String> tempValues = new ArrayList<String>();
		ArrayList<ArrayList<String>> mergeArray = new ArrayList<ArrayList<String>>();

		for (int i = 0; i < columnIdentifiers.get(0).size(); i++) {
			if (columnNames.contains(columnIdentifiers.get(0).get(i))) {
				int index = columnNames.indexOf(columnIdentifiers.get(0).get(i));
				tempNames.add(columnNames.get(index));
				tempValues.add(columnValues.get(index));
			} else {
				tempNames.add(columnIdentifiers.get(0).get(i));
				tempValues.add(null);
			}

		}
		mergeArray.add(tempNames);
		mergeArray.add(tempValues);
		return mergeArray;

	}

	@Override
	public boolean isValidQuery(String[] where) throws XMLStreamException, IOException {
		getColumnIdentifiers();
		boolean whereFlag = true, namesFlag = true, valuesFlag = true;
		if (where != null) {
			whereFlag = validateWhereInput(where);
		}
		if (columnNames != null) {
			namesFlag = validateColumnNames();
		}
		if (columnValues != null) {
			valuesFlag = validateColumnValues();
		}
		return (whereFlag && namesFlag && valuesFlag);
	}

	private boolean validateColumnNames() {
		for (int i = 0; i < columnNames.size(); i++) {
			if (!columnIdentifiers.get(0).contains(columnNames.get(i))) {
				return false;
			}
		}
		return true;
	}

	private boolean validateWhereInput(String[] where) {
		if ((!where[1].equals("=")) && (!where[1].equals(">")) && (!where[1].equals("<"))) {
			return false;
		}
		if (!columnIdentifiers.get(0).contains(where[0])) {
			return false;
		}
		int index = columnIdentifiers.get(0).indexOf(where[0]);
		String type = columnIdentifiers.get(1).get(index); 
		boolean isInteger = isInteger(where[2]);
		if ((type.equals("int") && (hasQuotes(where[2]) || !isInteger))) {
			return false;
		}
		if (type.equals("varchar") && (!hasQuotes(where[2]) || isWhiteSpace(where[2]))) {
			return false;
		}
		return true;
	}

	private boolean validateColumnValues() {
		int valueIndex;
		for (int i = 0; i < columnNames.size(); i++) {
			if (columnIdentifiers.get(0).contains(columnNames.get(i))) {
				if (columnValues.get(i) == null) {
					continue;
				}
				valueIndex = columnIdentifiers.get(0).indexOf(columnNames.get(i));
				if (columnIdentifiers.get(1).get(valueIndex).equals("int")) {
					if (hasQuotes(columnValues.get(i)) || !isInteger(columnValues.get(i))) {
						return false;
					}
				} else {
					if (!hasQuotes(columnValues.get(i)) || isWhiteSpace(columnValues.get(i))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean hasQuotes(String s) {
		return (s.startsWith("\"") || s.startsWith("\'"));

	}

	public boolean isWhiteSpace(String s) {
		String f = s.replaceAll("\\s", "");
		return (f.length() == 2);
	}

	public boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
