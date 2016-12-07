package junitTesting;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import dbms.DBMS;
import dbms.DBMSException;
import dbms.DatabaseException;

public class JunitTest {

	@org.junit.Test
	public void createDatabases() throws Exception {
		DBMS dbms = new DBMS();
		try {
			dbms.createDatabase("database1");
			dbms.createDatabase("database2");
			dbms.createDatabase("database3");
		} catch (Exception e) {
			dbms.dropDatabase("database1");
			dbms.createDatabase("database1");
			dbms.dropDatabase("database2");
			dbms.createDatabase("database2");
			dbms.dropDatabase("database3");
			dbms.createDatabase("database3");
		}
		String databasesPath = dbms.getDatabasesPath();
		Path path1 = Paths.get(databasesPath + File.separator + "database1");
		Path path2 = Paths.get(databasesPath + File.separator + "database2");
		Path path3 = Paths.get(databasesPath + File.separator + "database3");
		assertEquals("database creation failed", true, Files.exists(path1));
		assertEquals("database creation failed", true, Files.exists(path2));
		assertEquals("database creation failed", true, Files.exists(path3));

	}

	@org.junit.Test
	public void dropDatabase() {
	     DBMS dbms = new DBMS(); 
	     try {
			dbms.createDatabase("databaseForDropping");
 
		} catch (Exception e) {
 
		  fail("ERROR! creating database!");
		}
 
	     String databaseMSPath = dbms.getDatabasesPath();
	     String databasePath = databaseMSPath + File.separator + "databaseForDropping";
 
	     File databaseFile = new File(databasePath);
 
	     try {
			dbms.dropDatabase("databaseForDropping");
		} catch (Exception e) {
			fail("Error dropping database!");
		}
	     assertEquals("Dropping Database", false,databaseFile.exists());
 
 
 
	}
 
 
	@org.junit.Test
	public void updateDatabase() {
		 DBMS dbms = new DBMS(); 
		 ArrayList<String> columnNames = new ArrayList<String>();
		 columnNames.add("Name");
		 columnNames.add("age");
		 columnNames.add("gender");
 
		 ArrayList<String> columnTypes = new ArrayList<String>();
		 columnTypes.add("varchar");
		 columnTypes.add("int");
		 columnTypes.add("varchar");
 
		 ArrayList<String> rowValues1 = new ArrayList<String>();
		 rowValues1.add("\"Bassent\"");
		 rowValues1.add("21");
		 rowValues1.add("\"female\"");
 
		 ArrayList<String> rowValues2 = new ArrayList<String>();
		 rowValues2.add("\"Mira\"");
		 rowValues2.add("20");
		 rowValues2.add("\"female\"");
 
		 ArrayList<String> rowValues3 = new ArrayList<String>();
		 rowValues3.add("\"Aya\"");
		 rowValues3.add("20");
		 rowValues3.add("\"female\"");
 
		 ArrayList<String> rowValues4 = new ArrayList<String>();
		 rowValues4.add("\"Salma\"");
		 rowValues4.add("21");
		 rowValues4.add("\"female\"");
	     try {
			dbms.createDatabase("databaseForDropping");
 
		} catch (Exception e) {
 
		  fail("ERROR! creating database!");
		}
	     try {
			dbms.useDatabase("databaseForDropping");
 
	     try {
			dbms.createTable("Table", columnNames, columnTypes);
		} catch (Exception e) {
			 fail("ERROR! creating table!");
		}
 
	     try {
	    	dbms.useDatabase("databaseForDropping"); 
			dbms.insertIntoTable("Table", columnNames, rowValues1);
			dbms.useDatabase("databaseForDropping");
			dbms.insertIntoTable("Table", columnNames, rowValues2);
			dbms.useDatabase("databaseForDropping");
	     dbms.insertIntoTable("Table", columnNames, rowValues3);
	     dbms.useDatabase("databaseForDropping");
	     dbms.insertIntoTable("Table", columnNames, rowValues4);
		} catch (Exception e) {
			fail("Error! while inserting into Table");
		}
	     }catch (DatabaseException e1) {
			fail("error using database!");
		}
 
 
	     ArrayList<String> incorrectValue = new ArrayList<String>();
	     incorrectValue.add("Medhat");
	     incorrectValue.add("male");
	     incorrectValue.add("011100002");
 
	     ArrayList<String> column = new ArrayList<String>();
	     column.add("Name");
	     column.add("gender");
	     column.add("telephone Number");
 
	     String[] where = new String[3];
	     where[0] = "age";
	     where[1] = "<";
	     where[2] = "21";
 
 
	     try {
	    	 dbms.useDatabase("databaseForDropping");
			dbms.updateTable("Table", column, incorrectValue, where);
			fail("ERROR! Accepting wrong column entries!");
		} catch (Exception e) {
 
		}
	     column.clear();
	     column.add("gender");
	     incorrectValue.clear();
	     incorrectValue.add("\"male\"");
	     try {
	    	 dbms.useDatabase("databaseForDropping");
				dbms.updateTable("lol", column, incorrectValue, where);
				fail("ERROR! Accepting wrong Table name entry!");
			} catch (Exception e) {
 
			}
	     try {
	    	 dbms.useDatabase("databaseForDropping");
				dbms.updateTable("Table", column, incorrectValue, where);
 
			} catch (Exception e) {
				fail("ERROR! While updating table");
 
			}
	     LinkedHashMap<String,ArrayList<String>> linkedHashMap = new LinkedHashMap<String,ArrayList<String>>();
	      try {
	    	  dbms.useDatabase("databaseForDropping");
			linkedHashMap =  dbms.selectFromTable("Table", columnNames, null);
		} catch (Exception e) {
			fail("Error! while selecting from table");
		}
	      assertEquals("Updating table", "female", linkedHashMap.get("gender").get(0));
	      assertEquals("Updating table", "male", linkedHashMap.get("gender").get(1) );
	      assertEquals("Updating table", "male", linkedHashMap.get("gender").get(2));
	      assertEquals("Updating table", "female", linkedHashMap.get("gender").get(3) );
 
	      try {
			dbms.dropDatabase("databaseForDropping");
		} catch (Exception e) {
			fail("ERROR IN DROPPING DATABASE!");
		}
 
	}
 
	
	@org.junit.Test
	public void testDropTable() throws Exception {
		DBMS dbms = new DBMS();
		ArrayList<String> columnNames = new ArrayList<String>();
		ArrayList<String> columnTypes = new ArrayList<String>();
		columnNames.add("Name");
		columnNames.add("age");
		columnNames.add("gender");
		columnTypes.add("varchar");
		columnTypes.add("int");
		columnTypes.add("varchar");

		try {
			dbms.createDatabase("testDropTable");
		} catch (Exception e) {
			dbms.dropDatabase("testDropTable");
			dbms.createDatabase("testDropTable");
		}

		// test 01 not using USE
		try {
			dbms.createTable("dropTable01", columnNames, columnTypes);
			fail("expected DBMS Exception");
		} catch (Exception e) {
			assertTrue("Expected DBMS Exception", e instanceof DBMSException);
		}

		dbms.useDatabase("testDropTable");
		dbms.createTable("dropTable01", columnNames, columnTypes);
		dbms.dropTable("dropTable01");

		// test drop table already deleted
		try {
			dbms.dropTable("dropTable01");
			fail("expected Database Exception");
		} catch (Exception e) {
			assertTrue("Expected Database Exception", e instanceof DatabaseException);
		}

		// test drop a table doesnot exist

		try {
			dbms.dropTable("dropTableDoestnotExist");
			fail("expected Database Exception");
		} catch (Exception e) {
			assertTrue("Expected Database  Exception", e instanceof DatabaseException);
		}
	}

	@org.junit.Test
	public void testInsertIntoTable() throws Exception {
		DBMS dbms = new DBMS();
		ArrayList<String> columnNames = new ArrayList<String>();
		ArrayList<String> columnTypes = new ArrayList<String>();
		ArrayList<String> columnvalues = new ArrayList<String>();
		ArrayList<String> columnvaluesWrong = new ArrayList<String>();
		ArrayList<String> columnnamesWrong = new ArrayList<String>();
		try {
			dbms.createDatabase("testInsert");
		} catch (Exception e) {
			dbms.dropDatabase("testInsert");
			dbms.createDatabase("testInsert");
		}

		columnNames.add("Name");
		columnNames.add("age");
		columnNames.add("gender");

		columnTypes.add("varchar");
		columnTypes.add("int");
		columnTypes.add("varchar");

		columnvalues.add("Aya");
		columnvalues.add("20");

		dbms.useDatabase("testInsert");
		dbms.createTable("insertTable01", columnNames, columnTypes);

		// test insert with wrong table name
		try {
			dbms.insertIntoTable("notTableName", columnNames, columnvalues);
			fail("Expected dbms exception");

		} catch (Exception e) {
			assertTrue("Expected db exception", e instanceof DatabaseException);
		}

		// test insert null table name
		try {
			dbms.insertIntoTable(null, columnNames, columnvalues);
			fail("Expected dbms exception");

		} catch (Exception e) {
			assertTrue("Expected db exception", e instanceof DatabaseException);
		}

		// test insert values less than no of columns
		try {
			dbms.insertIntoTable("insertTable01", null, columnvalues);
			fail("Expected db exception");

		} catch (Exception e) {
			assertTrue("Expected db exception", e instanceof DatabaseException);
		}
		// test insert column names not in table
		columnnamesWrong.add("notName");
		columnnamesWrong.add("notage");
		columnnamesWrong.add("notgender");
		columnvalues.add("female");

		try {
			dbms.insertIntoTable("insertTable01", columnnamesWrong, columnvalues);
			fail("Expected db exception");

		} catch (Exception e) {
			assertTrue("Expected db exception", e instanceof DatabaseException);
		}

		// test inserting values more that table columns
		columnvalues.add("50"); // more entry
		try {
			dbms.insertIntoTable("insertTable01", columnNames, columnvalues);
			fail("Expected db exception");

		} catch (Exception e) {
			assertTrue("Expected db exception", e instanceof DatabaseException);
		}

		// test inserting wrong column type
		columnvaluesWrong.add("15"); // it was varchar
		columnvaluesWrong.add("20");
		columnvaluesWrong.add("female");

		try {
			dbms.insertIntoTable("insertTable01", columnNames, columnvalues);
			fail("Expected db exception");

		} catch (Exception e) {
			assertTrue("Expected db exception", e instanceof DatabaseException);
		}
	}


	@org.junit.Test
	public void testSelectFromTable() throws Exception {
		DBMS dbms = new DBMS();
		ArrayList<String> columnNames = new ArrayList<String>();
		ArrayList<String> columnTypes = new ArrayList<String>();
		ArrayList<String> selectedWrongColumnNames = new ArrayList<String>();
		ArrayList<String> selectedColumnNames = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		String[] where = new String[3];

		try {
			dbms.createDatabase("testSelect");
		} catch (Exception e) {
			dbms.dropDatabase("testSelect");
			dbms.createDatabase("testSelect");
		}

		columnNames.add("Password");
		columnNames.add("Id");
		columnNames.add("Gender");

		columnTypes.add("int");
		columnTypes.add("int");
		columnTypes.add("varchar");

		values.add("12");
		values.add("1233");
		values.add("\"female\"");
		selectedWrongColumnNames.add("name");

		dbms.useDatabase("testSelect");
		dbms.createTable("SelectTable", columnNames, columnTypes);

		// test select columnNames donot exist
		try {
			dbms.selectFromTable("SelectTable", selectedWrongColumnNames, null);
			fail("Expected db exception");

		} catch (Exception e) {
			assertTrue("Expected db exception", e instanceof DatabaseException);
		}

		// test select all
		LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<String, ArrayList<String>>();
		ArrayList<String> value1 = new ArrayList<String>();
		value1.add("12");
		ArrayList<String> value2 = new ArrayList<String>();
		value2.add("1233");
		ArrayList<String> value3 = new ArrayList<String>();
		value3.add("female");
		map.put("Password", value1);
		map.put("Id", value2);
		map.put("Gender", value3);
		dbms.insertIntoTable("SelectTable", columnNames, values);
		assertEquals("Selection Error ", map, dbms.selectFromTable("SelectTable", null, null));

		// test select specific column with where condition
		LinkedHashMap<String, ArrayList<String>> map1 = new LinkedHashMap<String, ArrayList<String>>();
		ArrayList<String> value4 = new ArrayList<String>();
		value4.add("1233");
		map1.put("Id", value4);

		where[0] = "Password";
		where[1] = "=";
		where[2] = "12";
		selectedColumnNames.add("Id");
		assertEquals("Selection Error ", map1, dbms.selectFromTable("SelectTable", selectedColumnNames, where));

	}
	@org.junit.Test
	    public void testCreateTable() throws Exception {
	        DBMS dbms = new DBMS();
	        ArrayList<String> columnNames = new ArrayList<String>();
	        ArrayList<String> columnTypes = new ArrayList<String>();
	 
	        try {
	            dbms.createDatabase("databaseOne");
	        } catch (Exception d) {
	            dbms.dropDatabase("databaseOne");
	            dbms.createDatabase("databaseOne");
	        }
	        dbms.useDatabase("databaseOne");
	 
	        columnNames.add("Names");
	        columnNames.add("Age");
	 
	        columnTypes.add("varchar");
	        columnTypes.add("int");
	        columnTypes.add("int");
	 
	        // Test entering unequal arraylists
	 
	        try {
	            dbms.createTable("tableOne", columnNames, columnTypes);
	            fail("Expected Database Exception!");
	        } catch (Exception e) {
	            assertTrue(e instanceof DatabaseException);
	        }
	 
	        columnNames.add("Height");
	 
	 
	        // entering right values
	        try {
	            dbms.createTable("TableTwo", columnNames, columnTypes);
	        } catch (Exception e) {
	            fail("Expected Database Exception!");
	        }
	 
	        // naming the table "null"
	        try {
	            dbms.createTable("null", columnNames, columnTypes);
	        } catch (Exception e) {
	            fail("Expected Database Exception!");
	        }
	 
	        columnNames = new ArrayList<String>();
	 
	        //inserting a null columnNames array
	 
	        try {
	            dbms.createTable("TableThree", columnNames, columnTypes);
	        } catch (Exception e) {
	            assertTrue(e instanceof DatabaseException);
	        }
	    }
	 
	 
	@org.junit.Test
	    public void testDeleteFromTable() throws Exception {
	        DBMS dbms = new DBMS();
	        ArrayList<String> columnNames = new ArrayList<String>();
	        ArrayList<String> columnTypes = new ArrayList<String>();
	        ArrayList<String> columnValues1 = new ArrayList<String>();
	        ArrayList<String> columnValues2 = new ArrayList<String>();
	        ArrayList<String> columnValues3 = new ArrayList<String>();
	        String[] where = new String[3];
	 
	        try {
	            dbms.createDatabase("databaseOne");
	        } catch (Exception d) {
	            dbms.dropDatabase("databaseOne");
	            dbms.createDatabase("databaseOne");
	        }
	        dbms.useDatabase("databaseOne");
	 
	        columnNames.add("Names");
	        columnNames.add("Age");
	 
	        columnTypes.add("varchar");
	        columnTypes.add("int");
	 
	        try {
	            dbms.createTable("TableFour", columnNames, columnTypes);
	        } catch (Exception e) {
	            dbms.dropTable("TableFour");
	            dbms.createTable("TableFour", columnNames, columnTypes);
	        }
	 
	        columnValues1.add("\"Student1\"");
	        columnValues1.add("21");
	 
	        columnValues2.add("\"Student2\"");
	        columnValues2.add("20");
	 
	        columnValues3.add("\"Student3\"");
	        columnValues3.add("19");
	 
	        where[0] = "Names";
	        where[1] = "=";
	        where[2] = "\"Student1\"";
	 
	 
	        dbms.insertIntoTable("TableFour", columnNames, columnValues1);
	        dbms.insertIntoTable("TableFour", columnNames, columnValues2);
	        dbms.insertIntoTable("TableFour", columnNames, columnValues3);
	 
	        try {
	            dbms.deleteFromTable("TableFour", where);
	        } catch (Exception e) {
	            fail("Exception is not needed");
	        }
	 
	        //delete from a table that doesn't exist
	        try {
	            dbms.deleteFromTable("NoTable", where);
	        } catch (Exception e) {
	            assertTrue(e instanceof DatabaseException);
	        }
	 
	        where[0] = "Age";
	        where[1] = "<";
	        where[2] = "1";
	 
	        //less than all available integers
	        try {
	            dbms.deleteFromTable("TableFour", where);
	        } catch (Exception e) {
	            fail("No Exception needed!");
	        }
	    }
}
