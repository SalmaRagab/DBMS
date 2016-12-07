package dbms;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import dtd.DTD;
import dtd.DTDException;
import xml.XMLHandler;

public class Database implements IDatabase {

	private Table table;
	private String databasePath;
	private XMLHandler xmlHandler;
	private DTD dtd;


	public Database(String databasePath) {
		this.databasePath = databasePath;
	}

	@Override
	public void createDBTable(String tableName, ArrayList<String> columnNames, ArrayList<String> columnTypes)
			throws Exception {
		String tableNamePath = this.databasePath + File.separator + tableName+".xml";
		String tablePathDTD = this.databasePath + File.separator + tableName;
		if (containsTable(tableName) || (columnNames.size()!= columnTypes.size())) {
			throw new DatabaseException("Table exists!");
		} else {
			xmlHandler = new XMLHandler(tableNamePath);
			xmlHandler.XMLWriter(tableName);
			xmlHandler.XMLCreateTableIdentifier(tableName, columnNames, columnTypes);
			xmlHandler.XMLEndWriter(tableName);
			dtd = new DTD(tablePathDTD);
			dtd.writeDTD(tableName, columnNames, columnTypes);
		}

	}

	@Override
	public void insertIntoTable(String tableName, ArrayList<String> columnNames, ArrayList<String> columnValues)
			throws Exception {
		if (containsTable(tableName)) {
		String tablePath = this.databasePath + File.separator + tableName +".xml";
		table = new Table(tableName, tablePath);
		table.insertRow(columnNames, columnValues);
		} else {
			throw new DatabaseException("ERROR! TABLE DOESNOT EXIST!");
		}
	}

	@Override
	public void dropTable(String tableName) throws DatabaseException, DTDException {
		String tableNamePath = this.databasePath + File.separator + tableName+".xml";
		String tablePathDTD = this.databasePath + File.separator + tableName;
		if (containsTable(tableName)) {
			File file = new File(tableNamePath);
			dtd = new DTD(tablePathDTD);
			dtd.deleteDTDFile();
			if (!file.delete()) {
				 throw new DatabaseException("ERROR! CANNOT DELETE TABLE FILE!");
			}
		} else {
			 throw new DatabaseException("ERROR! TABLE DOESNOT EXIST!");
		}

	}

	@Override
	public void deleteFromDBTable(String tableName, String[] where) throws Exception{
        String tablePath = this.databasePath + File.separator + tableName+".xml";

        if (containsTable(tableName)) {
            table = new Table(tableName, tablePath);
            table.deleteRows(tableName, where);
        } else {
            throw new DatabaseException ("The file doesn't exist!");
        }
    }

	@Override
	public void updateTable(String tableName, ArrayList<String> columnsNames, ArrayList<String> columnValues, String[] where) throws Exception {
		String tablePath = this.databasePath + File.separator + tableName+".xml";
		table = new Table(tableName, tablePath);
//		Check that database has this table
		if (!containsTable(tableName)) {
			throw new DatabaseException("Cannot update table not in your database!");
		}
		table.updateRows(columnsNames, columnValues, where);

	}

	@Override
	public LinkedHashMap<String, ArrayList<String>> selectFromTable(String tableName, ArrayList<String> columnNames, String[] where) throws Exception {
		String tablePath = this.databasePath + File.separator + tableName+".xml";
		table = new Table(tableName, tablePath);
//		Check that database has this table
		if (containsTable(tableName)) {
            return table.selectFromTable(columnNames, where);
		}  else {
            throw new DatabaseException ("The file doesn't exist!");
        }


	}
	private boolean containsTable(String tableName) {
		String tableNamePath = this.databasePath + File.separator + tableName+".xml";
		Path path = Paths.get(tableNamePath);
		if (Files.exists(path)) {
			return true;
		}
		return false;
	}


}