package com.jens.ToDo.model.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.User;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;
import com.jens.ToDo.ui.Main.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class SQLiteToDoCRUDOperationsImpl  implements IToDoCRUDOperations {


    /**
     * the logger
     */
    protected static final String logger = SQLiteToDoCRUDOperationsImpl.class
            .getName();

    /**
     * the db name
     */
    public static final String DBNAME = "ToDos.db";

    /**
     * the initial version of the db based on which we decide whether to create
     * the table or not
     */
    public static final int INITIAL_DBVERSION = 0;

    /**
     * the table name
     */
    public static final String TABNAME = "ToDos";

    /**
     * the column names
     *
     * the _id column follows the convention required by the CursorAdapter usage
     */
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_DESCRIPTION = "description";

    public static final String COL_READY = "ready";
    /**
     * the creation query
     */
    public static final String TABLE_CREATION_QUERY = "CREATE TABLE "
            + TABNAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + COL_NAME + " TEXT,\n" + COL_DESCRIPTION
            + " TEXT,\n"+COL_READY+"INTEGER)";

    /**
     * the where clause for item deletion
     */
    private static final String WHERE_IDENTIFY_ITEM = COL_ID + "=?";

    /**
     * the database
     */
    SQLiteDatabase db;



    @Override
    public ToDo createItem(ToDo item) {
        Log.i(logger, "createToDo(): " + item);

        ContentValues insertItem = createDBToDo(item);
        long newItemId = this.db.insert(TABNAME, null, insertItem);
        Log.i(logger, "addItemToDb(): got new item id after insertion: "
                + newItemId);
        item.setId(newItemId);

        return item;
    }

    @Override
    public List<ToDo> readAllItems() {
        List<ToDo> items = new ArrayList<ToDo>();

        // we make a query, which possibly will return an empty list
        SQLiteQueryBuilder querybuilder = new SQLiteQueryBuilder();
        querybuilder.setTables(TABNAME);
        // we specify all columns
        String[] asColumsToReturn = { COL_ID,COL_NAME,
                COL_DESCRIPTION, COL_READY};
        // we specify an ordering
        String ordering = COL_ID + " ASC";

        Cursor c = querybuilder.query(this.db, asColumsToReturn, null, null,
                null, null, ordering);

        Log.i(logger, "getAdapter(): got a cursor: " + c);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            // create a new item and add it to the list
            items.add(createItemFromCursor(c));
            c.moveToNext();
        }

        Log.i(logger, "readOutItemsFromDatabase(): read out items: " + items);

        return items;
    }

    @Override
    public ToDo readItem(long dateItemId) {

        /* JENS: _id weil autogen durch room --> Spalte = _id
         *
         *
         * */
        Cursor cursor = db.query(TABNAME,new String[]{COL_ID,COL_NAME,COL_DESCRIPTION, COL_READY}," _id=?",new String[]{String.valueOf(dateItemId)},null,null,null);
        if(cursor.moveToFirst()){
            ToDo item = createItemFromCursor(cursor);
            return item;
        }
        else{
            return null;
        }
    }

    @Override
    public boolean updateItem(ToDo item) {
        Log.i(logger, "updateToDo(): " + item);

        // do the update in the db
        this.db.update(TABNAME, createDBToDo(item), WHERE_IDENTIFY_ITEM,
                new String[] { String.valueOf(item.getId()) });
        Log.i(logger, "updateItemInDb(): update has been carried out");

        return true;
    }

    @Override
    public boolean deleteItem(long ToDoId) {
        Log.i(logger, "removeItemFromDb(): " + ToDoId);

        // we first delete the item
        this.db.delete(TABNAME, WHERE_IDENTIFY_ITEM,
                new String[] { String.valueOf(ToDoId) });
        Log.i(logger, "deleteToDo(): deletion in db done");
        return true;
    }

    @Override
    public boolean deleteAllItems() {
        Log.i(logger, "removeAllItemFromDb(): ");

        // we first delete the item
        this.db.delete(TABNAME,null,null);

        Log.i(logger, "deleteToDo(): deletion in db done");
        return false;
    }

    @Override
    public boolean syncAllItemsWithLocal() {
        return false;
    }

    @Override
    public boolean syncAllItemsWithRemote(MainActivity activity) {
        return false;
    }



    @Override
    public Call<Boolean> authenticateUser(User user) {
        return null;
    }

    /*
     * helper methods
     */

    /**
     * create a ContentValues object which can be passed to a db query
     *
     * @param item
     * @return
     */
    private ContentValues createDBToDo(ToDo item) {
        ContentValues insertItem = new ContentValues();
        insertItem.put(COL_NAME, item.getName());
        insertItem.put(COL_DESCRIPTION, item.getDescription());

        insertItem.put(COL_READY, item.isDone() ? 1 : 0);
        return insertItem;
    }


    /**
     * create an item from the cursor
     *
     * @param c
     * @return
     */
    public ToDo createItemFromCursor(Cursor c) {
        // create the item
        ToDo currentItem = new ToDo();

        // then populate the item with the results from the cursor
        currentItem.setId(c.getLong(c.getColumnIndex(COL_ID)));
        currentItem.setName(c.getString(c.getColumnIndex(COL_NAME)));
        currentItem.setDescription(c.getString(c
                .getColumnIndex(COL_DESCRIPTION)));
        currentItem.setDone(c.getInt(c.getColumnIndex(COL_READY))==1);
        return currentItem;
    }

    /**
     * prepare the database
     */
    public SQLiteToDoCRUDOperationsImpl(Context context) {

        this.db = context.openOrCreateDatabase(DBNAME,
                Context.MODE_PRIVATE, null);

        // we need to check whether it is empty or not...
        Log.d(logger, "db version is: " + db.getVersion());
        if (this.db.getVersion() == INITIAL_DBVERSION) {
            Log.i(logger,
                    "the db has just been created. Need to create the table...");
            db.setLocale(Locale.getDefault());
            //db.setLockingEnabled(true);
            db.setVersion(INITIAL_DBVERSION + 1);
            db.execSQL(TABLE_CREATION_QUERY);
        } else {
            Log.i(logger, "the db exists already. No need for table creation.");
        }

    }


    /**
     * provide the db (to the cursor adapter activity)
     */
    public SQLiteDatabase getDB() {
        return this.db;
    }
}
