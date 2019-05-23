package ensias.um5.com.event_gl2;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class imagesActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {
   private RecyclerView mRecyclerView ;
   private  ImageAdapter mAdapter ;
   private FirebaseStorage mStorage ;
   private DatabaseReference mDatabaseRef ;
   private ValueEventListener mDBListener ;
   private List<Upload> mUploads ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>() ;
        mAdapter = new ImageAdapter(imagesActivity.this,mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(imagesActivity.this);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mStorage = FirebaseStorage.getInstance();
       mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
              for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                  Upload upload = postSnapshot.getValue(Upload.class);
                  upload.setmKey(postSnapshot.getKey());
                  mUploads.add(upload);
              }

               mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(imagesActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this,"Normal click at position" + position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(this,"Whatever click at position" + position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {

        Upload selectedItem = mUploads.get(position);
        final String selectdKey = selectedItem.getmKey() ;
        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getmImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
              mDatabaseRef.child(selectdKey).removeValue();
                Toast.makeText(imagesActivity.this,"Item deleted",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}
