package uny.ac.id.crudfirestore.firestoreAdapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import uny.ac.id.crudfirestore.Key;
import uny.ac.id.crudfirestore.MainActivity;
import uny.ac.id.crudfirestore.R;
import uny.ac.id.crudfirestore.model.ModelResponse;

public class MainFirestore extends AppCompatActivity {

    RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_firestore);

        init();
        show();
    }

    public void btn_toSHow(View view) {
        startActivity(new Intent(MainFirestore.this, MainActivity.class));
    }

    private void init() {
        recyclerView = findViewById(R.id.recylceview_firestore);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    private void show() {
        final Query query = db.collection("Documents");
        final FirestoreRecyclerOptions<ModelResponse> modelResponse = new FirestoreRecyclerOptions.Builder<ModelResponse>()
                .setQuery(query, ModelResponse.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<ModelResponse, ModelsHolder>(modelResponse) {
            @NonNull
            @Override
            public ModelsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout, viewGroup, false);
                return new ModelsHolder(view);
            }

            @Override
            protected void onBindViewHolder(final ModelsHolder holder, final int position, final ModelResponse model) {
                holder.tv_title.setText(model.getTitle());
                holder.tv_desc.setText(model.getDescription());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = model.getTitle();
                        String desc = model.getDescription();
                        Toast.makeText(getApplicationContext(), title + "," + desc, Toast.LENGTH_SHORT).show();
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainFirestore.this);
                        final CharSequence[] options = {"Update", "Delete"};
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String choice = options[which].toString();
                                if (choice.equals("Update")) {
                                    String myId = modelResponse.getSnapshots().getSnapshot(position).getId();
                                    Intent intent = new Intent(MainFirestore.this, MainActivity.class);
//                                    intent.putExtra(Key.ID, model.getId());
                                    intent.putExtra(Key.ID, myId);
                                    intent.putExtra(Key.TITLE, holder.tv_title.getText().toString().trim());
                                    intent.putExtra(Key.DESC, holder.tv_desc.getText().toString().trim());
                                    startActivity(intent);
                                }
                                if (choice.equals("Delete")) {
                                    String myPath = modelResponse.getSnapshots().getSnapshot(position).getId();
                                    db.collection("Documents").document(myPath)
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getApplicationContext(), "deleted", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "gagal deleted", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                        return true;
                    }
                });
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private class ModelsHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_desc;

        public ModelsHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_desc = itemView.findViewById(R.id.tv_description);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
