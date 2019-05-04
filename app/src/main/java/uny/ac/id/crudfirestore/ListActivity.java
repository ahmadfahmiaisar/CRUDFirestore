package uny.ac.id.crudfirestore;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import uny.ac.id.crudfirestore.adapter.ListAdapter;
import uny.ac.id.crudfirestore.model.ModelResponse;

public class ListActivity extends AppCompatActivity {
    List<ModelResponse> modelList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        init();
        showData();
    }

    private void showData() {
        progressDialog.setTitle("loading");
        progressDialog.show();
        db.collection("Documents")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        for (DocumentSnapshot doc : task.getResult()) {
                            ModelResponse model = new ModelResponse(doc.getString("id"),
                                    doc.getString("title"),
                                    doc.getString("description"));
                            modelList.add(model);
                        }
                        ListAdapter adapter = new ListAdapter(ListActivity.this, modelList);
                        recyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ListActivity.this, "gagal", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init() {
        recyclerView = findViewById(R.id.recylceview);
        db = FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        progressDialog = new ProgressDialog(this);
    }
}
