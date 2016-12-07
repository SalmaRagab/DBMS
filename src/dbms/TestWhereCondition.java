package dbms;

import java.util.ArrayList;

public class TestWhereCondition {

	private String[] where;


	public TestWhereCondition (String[] where) {
      this.where = where;


	}


	public boolean isTrueCondition(ArrayList<ArrayList<String>> row) {
		TableColumn tableColumn = new TableColumn(null, null);
		if (!tableColumn.hasQuotes(where [2]) && tableColumn.isInteger(where[2])) {
			return isTrueConditionInteger(row);
		} else if (tableColumn.hasQuotes(where[2]) && !tableColumn.isWhiteSpace(where[2])) {
			return isTrueConditionString(row);
		} else {
			return false;
		}
	}

	private boolean isTrueConditionInteger(ArrayList<ArrayList<String>> row) {
		String value = row.get(1).get(row.get(0).indexOf(where[0]));
		if(value == "null") {
			return false;
		}
		int val = Integer.parseInt(value);
		int comparedValue = Integer.parseInt(where[2]);
		if (where[1].equals("=")) {
			return (val == comparedValue);
		} else if (where[1].equals(">")) {
			return (val > comparedValue);
		} else {
			return (val < comparedValue);
		}

	}

	private boolean isTrueConditionString(ArrayList<ArrayList<String>> row) { // checks the equality of 2 strings
		String rowValue = row.get(1).get(row.get(0).indexOf(where[0]));
		if(rowValue.equals("null")) {
			return false;
		}
		rowValue = rowValue.replaceAll("^\'|^\"|\'$|\"$", "");
		String toBeComparedToValue = where[2].replaceAll("^\'|^\"|\'$|\"$", "");
		
	    int comparisonResult = rowValue.compareToIgnoreCase(toBeComparedToValue);
		if (where[1].equals("=")) {
			return comparisonResult == 0;
		} else if (where[1].equals(">")) {
			return comparisonResult >0;
		} else {
			return (comparisonResult <0);
		}
	}



}
