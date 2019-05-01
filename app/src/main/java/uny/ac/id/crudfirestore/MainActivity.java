package uny.ac.id.crudfirestore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import uny.ac.id.crudfirestore.firestoreAdapter.MainFirestore;

public class MainActivity extends AppCompatActivity {

    EditText et_title, et_desc;
    TextView tv_coba;
    Button btn_save, btn_show;
    ProgressDialog progressDialog;
    Bundle bundle;
    //initialize cloud firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mId;

//    String mId, mTitle, mDesc;
//    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //method init
        init();

        if (bundle == null) {
            btn_save.setText("Save");
        } else {
            btn_save.setText("Update");
            mId = bundle.getString(Key.ID);
            String mTitle = bundle.getString(Key.TITLE);
            String mDesc = bundle.getString(Key.DESC);

            tv_coba.setText(mTitle);
            et_title.setText(mTitle);
            et_desc.setText(mDesc);
        }
    }

    public void btn_save(View view) {
        bundle = getIntent().getExtras();
        if (bundle != null) {
            String id = mId;
            String title = et_title.getText().toString().trim();
            String desc = et_desc.getText().toString().trim();
            updateData(id, title, desc);
        } else {
            String title = et_title.getText().toString().trim();
            String desc = et_desc.getText().toString().trim();
            uploadData(title, desc);
        }
    }

    public void btn_show(View view) {
        startActivity(new Intent(MainActivity.this, MainFirestore.class));
        finish();
    }

    private void updateData(String id, String title, String desc) {
        progressDialog.setTitle("update data ke firestore");
        progressDialog.show();

        db.collection("Documents").document(id)
                .update(Key.TITLE, title, Key.DESC, desc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "berhasil", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "gagal", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadData(String title, String desc) {
        //set title in progress bar
        progressDialog.setTitle("nambahin data ke firestore");
        //show progress bar when user click save button
        progressDialog.show();
        //random mid for each data to be stored
        String id = UUID.randomUUID().toString();

        Map<String, Object> doc = new HashMap<>();
        doc.put(Key.ID, id);
        doc.put(Key.TITLE, title);
        doc.put(Key.DESC, desc);

        //add this data
        db.collection("Documents").document().set(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "uploaded..", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "gagal", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init() {
        //initialize views with its xml
        et_title = findViewById(R.id.et_title);
        et_desc = findViewById(R.id.et_desc);
        btn_save = findViewById(R.id.btn_save);
        btn_show = findViewById(R.id.btn_show);
        tv_coba = findViewById(R.id.coba);
        //progress dialog
        progressDialog = new ProgressDialog(this);
        //firestore
        db = FirebaseFirestore.getInstance();
        bundle = getIntent().getExtras();
    }

}
