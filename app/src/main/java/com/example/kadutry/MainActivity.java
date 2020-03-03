package com.example.kadutry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.ByteArrayOutputStream;

import static androidx.recyclerview.widget.RecyclerView.*;

public class MainActivity extends AppCompatActivity {
LinearLayoutManager mLayoutManager;
SharedPreferences mSharedPref;
RecyclerView mRecyclerView;
FirebaseDatabase mFirebaseDatabase;
DatabaseReference mRef;
FirebaseRecyclerAdapter<Model,ViewHolder>firebaseRecyclerAdapter;
FirebaseRecyclerOptions<Model>options;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar=getSupportActionBar() ;
        assert actionBar != null;
        actionBar.setTitle("Post List");
        mSharedPref=getSharedPreferences("SortSettings",MODE_PRIVATE);

        String mSorting =mSharedPref.getString("Sort","newest");
        if (mSorting.equals("newest")){
            mLayoutManager=new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);

        }
        else if(mSorting.equals("oldest")){
            mLayoutManager=new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(false);
            mLayoutManager.setStackFromEnd(false);
        }
        mRecyclerView=findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mRef=mFirebaseDatabase.getReference("Data");
            showData();

}
 private void showData(){
options = new FirebaseRecyclerOptions.Builder<Model>().setQuery(mRef,Model.class).build();
firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Model, ViewHolder>(options) {
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int i, @NonNull Model model) {
                      holder.setDetails(getApplicationContext(),model.getTitle(),model.getDescription(),model.getImage());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        ViewHolder viewHolder=new ViewHolder(itemView);
        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String mTitle=getItem(position).getTitle();
                String mDesc=getItem(position).getDescription();
                String mImage=getItem(position).getImage();
                Intent intent=new Intent(view.getContext(),DetailActivity.class);
                intent.putExtra("title",mTitle);
                intent.putExtra("description",mDesc);
                intent.putExtra("image",mImage);
                startActivity(intent);
            }

            @Override
            public void onItemlongClick(View view, int position) {
//delete cha code takaychay!
            }
        });

        return viewHolder;
    }

};
     mRecyclerView.setLayoutManager(mLayoutManager);
     firebaseRecyclerAdapter.startListening();
     mRecyclerView.setAdapter(firebaseRecyclerAdapter);

 }

    private void firebaseSearch(String searchText){
    String query=searchText.toLowerCase();
        Query firebaseSearchQuery=mRef.orderByChild("title").startAt(searchText).endAt(searchText+"\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Model>().setQuery(firebaseSearchQuery,Model.class).build();
        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Model, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int i, @NonNull Model model) {
                holder.setDetails(getApplicationContext(),model.getTitle(),model.getDescription(),model.getImage());
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
                ViewHolder viewHolder=new ViewHolder(itemView);
                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String mTitle=getItem(position).getTitle();
                        String mDesc=getItem(position).getDescription();
                        String mImage=getItem(position).getImage();
                        Intent intent=new Intent(view.getContext(),DetailActivity.class);
                        intent.putExtra("title",mTitle);
                        intent.putExtra("description",mDesc);
                        intent.putExtra("image",mImage);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemlongClick(View view, int position) {
//delete cha code takaychay!
                    }
                });

                return viewHolder;
            }

        };
        mRecyclerView.setLayoutManager(mLayoutManager);
        firebaseRecyclerAdapter.startListening();
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }
    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter!=null){
            firebaseRecyclerAdapter.startListening();
        }
            }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId() ;
        if (id==R.id.action_sort){
            showSortDialog();
            return true;
        }
        if (id==R.id.action_add){
            startActivity(new Intent(MainActivity.this,AddPostActivity.class));

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
    String [] sortoptions={"Newest","Oldest"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Sort By").setIcon(R.drawable.ic_action_sort).setItems(sortoptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which ==0) {

                    SharedPreferences.Editor editor=mSharedPref.edit();
                    editor.putString("Sort","newest");

                    editor.apply();
                    recreate();


                }else if(which==1){
                    SharedPreferences.Editor editor=mSharedPref.edit();
                    editor.putString("Sort","oldest");

                    editor.apply();
                    recreate();
                }

                }

        });
        builder.show() ;
}
}

