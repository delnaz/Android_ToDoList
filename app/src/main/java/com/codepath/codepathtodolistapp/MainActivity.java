package com.codepath.codepathtodolistapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
                return true;
            }
        });
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent secondActivity = new Intent(MainActivity.this,EditItemActivity.class);
                ToDoItem item = todoItems.get(position);
                secondActivity.putExtra(EditItemActivity.EXTRA_NAME, item.getName());
                secondActivity.putExtra(EditItemActivity.EXTRA_PRI,item.getPriority());
                secondActivity.putExtra(EditItemActivity.EXTRA_DUE_DATE,item.getDuedate());
                secondActivity.putExtra(EditItemActivity.EXTRA_ID,item.getId()+"");
                secondActivity.putExtra(EditItemActivity.EXTRA_POSITION, position);
                startActivityForResult(secondActivity, REQUEST_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            int pos = data.getExtras().getInt(EditItemActivity.EXTRA_POSITION);
            ToDoItem item = new ToDoItem();
            item.setName(data.getExtras().getString(EditItemActivity.EXTRA_NAME));
            item.setId(Integer.parseInt(data.getExtras().getString(EditItemActivity.EXTRA_ID)));
            item.setPriority(data.getExtras().getString(EditItemActivity.EXTRA_PRI));
            item.setDuedate(data.getExtras().getLong(EditItemActivity.EXTRA_DUE_DATE));
            todoItems.set(pos,item);
            aToDoItemAdapter.notifyDataSetChanged();
            updateItem(item);
        }
    }

    private void updateItem(ToDoItem item) {
        sqlDatabase.updateItem(item);
    }


    public void populateArrayItems(){
        todoItems = new ArrayList<ToDoItem>();
        readItems();
        aToDoItemAdapter = new TodoListAdapter(this,R.layout.todo_list_item,R.id.textViewName,todoItems);
    }

    private void readItems(){

        if(sqlDatabase.getAllItems() != null){
            List<ToDoItem> dbItems = sqlDatabase.getAllItems();
            todoItems.clear();
            todoItems.addAll(dbItems);
        }
    }

    private void writeItems(ToDoItem temp){

        long id = sqlDatabase.addDb(temp);
        temp.setId(id);
        aToDoItemAdapter.notifyDataSetChanged();
    }


    public void onAdd(View view) {
        String editText = etEditText.getText().toString();
        if(editText == null || editText.isEmpty()){
            Toast.makeText(this, "Text field is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        ToDoItem temp = new ToDoItem();
        temp.setName(editText);
        temp.setPriority("High");
        temp.setDuedate(new Date().getTime() + (24 * 60 * 60 * 1000));

        aToDoItemAdapter.add(temp);
        etEditText.setText("");
        writeItems(temp);
    }


}
