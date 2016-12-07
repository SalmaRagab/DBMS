package parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import dbms.DBMS;
import dbms.DatabaseException;
import dtd.DTDException;

public class Parser implements IParser {


	private int operationNumber;
	private String databaseName;
	private int i; // counter for the query loop
	private String tableName;
	private int wordsIndex = 0; //the index of the words array list
	private ArrayList<String> words; //carry the words of the query
	private ArrayList<String> values;
	private ArrayList<String> columns;
	private ArrayList<String> types;
	private LinkedHashMap<String, ArrayList<String>> selected;
	private DBMS dbms = new DBMS();
	private String[] where;           //the values after the where command, its size is always THREE

	public void parser() {
		 wordsIndex = 0;
	}

	public int getOperationNumber() {
		return operationNumber;
	}


	public String getDatabaseName() {
		return databaseName;
	}

	public LinkedHashMap<String, ArrayList<String>> getSelected() {
		return selected;
	}

	public void setDatabaseName(String databaseName) throws Exception {
		this.databaseName = databaseName;
	}



	@Override
    public void parse(String query)throws Exception {
        split(query);
        if (words.size() == 0) {
            operationNumber = -1;
        } else {
        if (!words.get(words.size()-1).equals(";")) {
            throw new ParserException("The query is missing a semicolon");

        }
        try {
            switch (words.get(wordsIndex++).toUpperCase()) {
            case "USE":
                use();
                break;
            case "CREATE":
                if (create().equals("DATABASE")) {
                    dbms.createDatabase(databaseName);
                    operationNumber = 1;
                } else {
                    dbms.useDatabase(databaseName);
                    dbms.createTable(tableName, columns, types);
                    operationNumber = 3;
                }
                break;
            case "DROP":
                if (drop().equals("DATABASE")) {
                    dbms.dropDatabase(databaseName);
                    operationNumber = 2;
                } else {
                    dbms.useDatabase(databaseName);
                    dbms.dropTable(tableName);
                    operationNumber = 4;
                }
                break;
            case "SELECT":
                select();
                dbms.useDatabase(databaseName);
                selected = dbms.selectFromTable(tableName, columns, where);
                operationNumber = 6;
                break;
            case "DELETE":
                delete();
                dbms.useDatabase(databaseName);
                dbms.deleteFromTable(tableName, where);
                operationNumber = 7;
                break;
            case "UPDATE":
                update();
                dbms.useDatabase(databaseName);
                dbms.updateTable(tableName, columns, values, where);
                operationNumber = 8;
                break;
            case "INSERT":
                insert();
                dbms.useDatabase(databaseName);
                dbms.insertIntoTable(tableName, columns, values);
                operationNumber = 5;
                break;
            default:
                throw new ParserException("The query is undefined!");
            }
        } catch (ParserException p) {
            throw new ParserException(p.getErrorMessage());
        }
        }

    }

	private void split(String query) throws Exception {
		words = new ArrayList<String>();
		i = 0;
		String word = "";
		Character c ;
		while (i < query.length()) {
			c = query.charAt(i++);
			if (isSpecialCharacter(c)) {
				if (word.length() > 0) {
					words.add(word);
				}
				if (!Character.isWhitespace(c)) { //to make sure not to add spaces in the array
					words.add(""+c);
				}
				word = "";
			} else {
				if (c.equals('"') || c.equals('\'')) {
					word += getInQuotes(c, word, query);
				} else {
					word += c;
				}
			}
		}
		if (word.length() > 0) {
			throw new ParserException("Your query is wrong!");
		}

	}

	private void use() throws DatabaseException, ParserException {
        databaseName = words.get(wordsIndex);
        dbms.useDatabase(databaseName);
        operationNumber = 0;
        wordsIndex++; // should be on ;
        checkEnd();

	}

	private String create() throws ParserException {
		if (words.get(wordsIndex).toUpperCase().equals("DATABASE")) {
			wordsIndex++;
			databaseName = words.get(wordsIndex++);
			naming(databaseName);
			checkEnd();
			return "DATABASE";
		} else if (words.get(wordsIndex).toUpperCase().equals("TABLE")) {
			wordsIndex++;
			tableName = words.get(wordsIndex);
			naming(tableName);
			wordsIndex++;
			if (words.get(wordsIndex).equals("(")) {
				wordsIndex++;
				creatingTable();
				checkEnd();
				return "TABLE";
			} else {
				throw new ParserException("The CREATE query is wrong formatted! [Mafeesh opened bracket]");
			}
		} else {
			throw new ParserException("The CREATE query is wrong formatted!");
		}
	}

	private String drop() throws ParserException, DatabaseException, DTDException {
		if (words.get(wordsIndex).toUpperCase().equals("DATABASE")) {
			wordsIndex++;
			databaseName = words.get(wordsIndex);
			wordsIndex++;
			checkEnd();
			return "DATABASE";
		} else if (words.get(wordsIndex).toUpperCase().equals("TABLE")) {
			wordsIndex++;
			tableName = words.get(wordsIndex);
			wordsIndex++;
			checkEnd();
			return "TABLE";
		} else {
			throw new ParserException("The DROP query is wrong formatted!");
		}
	}

	private void select() throws ParserException {
		columns = new ArrayList<String>();
		where = new String[3];
		where = null;
		if (words.get(wordsIndex).equals("*")) {
			columns = null;
			wordsIndex++;
		} else {
			columns = createArray();
		}
		if (!words.get(wordsIndex).toUpperCase().equals("FROM")) {
			throw new ParserException("The Select query is not correct");
		} else {
			wordsIndex++;
			tableName = words.get(wordsIndex);
			wordsIndex++; // index on where or ;
		}
		if (words.get(wordsIndex).toUpperCase().equals("WHERE")) {
			wordsIndex++;
			where(); // index returned should be on ;
		}
//		to check that the query ended correctly
		checkEnd();
	}

	private void delete() throws ParserException {
		where = new String[3];
		if(words.get(wordsIndex).equals("*")) {
			wordsIndex++;
		}
		if(!words.get(wordsIndex).toUpperCase().equals("FROM")) {
			throw new ParserException("The delete query is not correct");
		}
		wordsIndex++;
		tableName = words.get(wordsIndex++); // index = index after table_name
		if (wordsIndex + 1 == words.size()) { //the query ended correctly with out where statement
			where = null;
		} else if(words.get(wordsIndex).toUpperCase().equals("WHERE")) { //contains where
			wordsIndex++; // where col = value ; (index is on col)
			where();
			checkEnd();
		} else {
			throw new ParserException("The delete query is not correct");
		}
	}

	private void update() throws ParserException {
		tableName = words.get(wordsIndex);
		wordsIndex++; //index on set
		if (words.get(wordsIndex).toUpperCase().equals("SET")) {
			wordsIndex++; // index on column 1
			set();
			checkEnd();
		}
		else {
			throw new ParserException("The UPDATE query is not correct");
		}
	}

	private void insert() throws ParserException {
		if(!words.get(wordsIndex++).toUpperCase().equals("INTO")) {
			throw new ParserException("Insert query  is WRONG!");
		} // index is after into
		tableName = words.get(wordsIndex++); // index after table name (value or columns)
		if ( words.get(wordsIndex).toUpperCase().equals("VALUES")) {
			columns = new ArrayList<String>();
			columns = null;
			wordsIndex++; // on (
			values(); // it is called on index  '('
			} else if (words.get(wordsIndex).equals("(")) {
			wordsIndex++; // to start reading cols
			columns = new ArrayList<String>();
			columns = createArray(); // return after ')' expect values
			if ( words.get(wordsIndex).toUpperCase().equals("VALUES")) {
				wordsIndex++; // on (
				values(); // it is called on index after '('
				if (columns.size() != values.size()) {
					throw new ParserException("no of values doesnot match no of columns");
				}
			} else {
				throw new ParserException("insert query  is WRONG!");
			}
		} else {
			throw new ParserException("insert query  is WRONG!");
		}
	}

	private ArrayList<String> createArray() throws ParserException {
		//depends on the array list sent, either values or columns
			ArrayList<String> list = new ArrayList<String>();
			try {
				while((!words.get(wordsIndex).equals(")")
						&& !words.get(wordsIndex).toUpperCase().equals("FROM"))){ //didnot finish cols
						if (!words.get(wordsIndex).equals(",")){ // not to add , to cols list
							list.add(words.get(wordsIndex));
						}
						wordsIndex++;
					}
					if(words.get(wordsIndex).equals(")")){ // as a convention we start after each bracket
						wordsIndex++;
					}
			} catch (Exception e) {
				throw new ParserException("The Select query is not correct");
			}

			return list;
		}

	private void creatingTable() throws ParserException { //bt3'yr el type wl columns :3
		columns = new ArrayList<String>();
		types = new ArrayList<String>();

		while (!")".equals(words.get(wordsIndex))) {
			if (words.get(wordsIndex).equals(",")) {
				wordsIndex++;
			} else if (words.get(wordsIndex).equals(";")) {
				throw new ParserException("WRONG FORMAT");
			} else {
				columns.add(words.get(wordsIndex));
				wordsIndex++;
				if (words.get(wordsIndex).toUpperCase().equals("INT") || words.get(wordsIndex).toUpperCase().equals("VARCHAR")) {
					types.add(words.get(wordsIndex));
					wordsIndex++;
				} else {
					throw new ParserException("The entered type is not supported!");
				}
			}
		}
		wordsIndex++; // after )


	}
	private void where() throws ParserException {
		where = new String[3];
			for (int i = 0; i < where.length; i++) {
				where[i] = words.get(wordsIndex);
				wordsIndex++;
			}
	}

	private void set() throws ParserException {
		try {
			columns = new ArrayList<String>();
			values = new ArrayList<String>();

			for (int i = 0; i < words.size(); i++) {
				if (words.get(wordsIndex).equals("=")) {
					wordsIndex++;
					values.add(words.get(wordsIndex));
					wordsIndex++;
					if (words.get(wordsIndex).equals(",")) {
						wordsIndex++;
					} else if (words.get(wordsIndex).toUpperCase().equals("WHERE")) {
						wordsIndex++;
						where();
						break;
					} else {
						break;
					}
				} else { //el c
					columns.add(words.get(wordsIndex));
					wordsIndex++;
				}
			}
		} catch (Exception e) {
			throw new ParserException("Revise your format!");
		}
	}

	private void values() throws ParserException {
//		start index on '('
		if(!words.get(wordsIndex).equals("(")) {
			throw new ParserException("Query is WRONG!");
		} else {
			wordsIndex++;
			values = new ArrayList<String>();
			values = createArray(); // return with index after ')' => expected ;
			checkEnd();
		}
	}

	private void naming(String name) throws ParserException {
		Character c;
		int i = 0;
		c = name.charAt(i);
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c.equals('_'))) {
			// its start is correct
			while ( i < name.length()) {
				c = name.charAt(i);
				if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
					|| (c.equals('_')) ||(c >= '0' && c <= '9'))){
					throw new ParserException(c +" is not allowed in naming");
				}
				i++;

			}
		} else {
			throw new ParserException("The name " + name +" is not allowed");
		}
	}

	private void checkEnd() throws ParserException {
		try {
			if (wordsIndex + 1 != words.size()) {
				throw new ParserException("The query is not correct");
			}
		} catch (Exception e) {
			throw new ParserException("The query is not correct");
		}
	}

	private String getInQuotes (Character c , String word, String query) throws ParserException {
//		to get value inside quotes
		Character quote = c;
		try {
			if (i <= (query.length() - 1)) {
				word += c;
				c = query.charAt(i++);
				while (!c.equals(quote)) {
					word += c;
					c = query.charAt(i++);
				}
				word += c;
			} else {
				throw new ParserException("Error in the query's form!");
			}

		} catch (Exception e) {
			throw new ParserException("error in the quotes in your statement");
		}

		return word;
	}

	private boolean isSpecialCharacter(Character c) {
		if (Character.isWhitespace(c) || c.equals(',') || c.equals(';')
				|| c.equals('(') || c.equals(')') || c.equals('=') || c.equals('*')
				|| c.equals('<') || c.equals('>')) {
			return true;
		}
		return false;
	}
}