package com.codepath.codepathtodolistapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<ToDoItem> todoItems;
    TodoListAdapter aToDoItemAdapter;
    ListView lvItems;
    EditText etEditText;
    private final int REQUEST_CODE = 20;
    SqlDatabaseHelper sqlDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sqlDatabase = new SqlDatabaseHelper(this.getApplicationContext());
        populateArrayItems();
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(aToDoItemAdapter);
        etEditText = (EditText) findViewById(R.id.etEditText);
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                sqlDatabase.deleteDb(todoItems.get(position).getName());
                todoItems.remove(position);
                aToDoItemAdapter.notifyDataSetChanged();
                //delete from database

                return true;
            }
        });
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent secondActivity = new Intent(MainActivity.this,EditItemActivity.class);
             //   Log.d("1111", todoItems.get(position));
                ToDoItem item = todoItems.get(position);
                secondActivity.putExtra(EditItemActivity.EXTRA_NAME, item.getName());
                secondActivity.putExtra(EditItemActivity.EXTRA_PRI,item.getPriority());
                secondActivity.putExtra(EditItemActivity.EXTRA_DUE_DATE,item.getDuedate());
                secondActivity.putExtra(EditItemActivity.EXTRA_ID,item.getId()+"");
                Log.d("1111", "item.getId() id: "+ item.getId());
                secondActivity.putExtra(EditItemActivity.EXTRA_POSITION, position);
                Log.d("1111", "secondActivity id: "+ secondActivity.getExtras().getString(EditItemActivity.EXTRA_ID));
                startActivityForResult(secondActivity, REQUEST_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            int pos = data.getExtras().getInt(EditItemActivity.EXTRA_POSITION);
            ToDoItem item = new ToDoItem();
            Log.d("1111", "data.getExtras().getString(EditItemActivity.EXTRA_ID)" +data.getExtras().getString(EditItemActivity.EXTRA_ID));
            item.setName(data.getExtras().getString(EditItemActivity.EXTRA_NAME));
            item.setId(Integer.parseInt(data.getExtras().getString(EditItemActivity.EXTRA_ID)));
            item.setPriority(data.getExtras().getString(EditItemActivity.EXTRA_PRI));
            item.setDuedate(data.getExtras().getLong(EditItemActivity.EXTRA_DUE_DATE));
            todoItems.set(pos,item);
            aToDoItemAdapter.notifyDataSetChanged();
            updateItem(item);
            //  Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
            // Log.d("333","length - " + pos);
            //todoItems.set(pos,name);
            //TODO update db, update adapter and notify
        }
    }

    private void updateItem(ToDoItem item) {
        sqlDatabase.updateItem(item);
    }


    public void populateArrayItems(){
        todoItems = new ArrayList<ToDoItem>();
        readItems();
       // aToDoItemAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,todoItems);
        aToDoItemAdapter = new TodoListAdapter(this,R.layout.todo_list_item,R.id.textViewName,todoItems);
    }

    private void readItems(){

        if(sqlDatabase.getAllItems() != null){
            List<ToDoItem> dbItems = sqlDatabase.getAllItems();
            todoItems.clear();
            todoItems.addAll(dbItems);


        }

        /*
        File fileDir = getFilesDir();
        File file = new File(fileDir,"todo.txt");
        try{
            Date date = new Date();
            List<String> fileItems = new ArrayList<String>(FileUtils.readLines(file));
            ToDoItem temp = null;
            for(String itm : fileItems){
                temp = new ToDoItem();
                temp.setName(itm);
                temp.setPriority("High");
                temp.setDuedate(date.getTime());
            }
            todoItems.add(temp);

        } catch(IOException e ){

        }*/
    }

    private void writeItems(ToDoItem temp){

        long id = sqlDatabase.addDb(temp);
        temp.setId(id);
        aToDoItemAdapter.notifyDataSetChanged();
        /* File fileDir = getFilesDir();
        File file = new File(fileDir,"todo.txt");
        try{
            FileUtils.writeLines(file,todoItems);
        } catch(IOException e ){

        }*/
    }


    public void onAdd(View view) {
        ToDoItem temp = new ToDoItem();
        temp.setName(etEditText.getText().toString());
        temp.setPriority("High");
        temp.setDuedate(new Date().getTime() + (24 * 60 * 60 * 1000));

        aToDoItemAdapter.add(temp);
        etEditText.setText("");
        writeItems(temp);
    }


}
