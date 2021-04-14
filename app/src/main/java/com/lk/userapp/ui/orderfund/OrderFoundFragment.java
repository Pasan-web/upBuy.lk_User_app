package com.lk.userapp.ui.orderfund;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lk.userapp.Model.Complain;
import com.lk.userapp.R;
import com.lk.userapp.ViewComplainActivity;
import com.lk.userapp.ui.profile.ProfileFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class OrderFoundFragment extends Fragment {

    private String docid;
    private ImageView img,img2;
    Button b;
    String currentPhotoPath;
    private String TAG = "ProfileFragment";
    private Button saveBtn,viewHistory,browse,browse2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView reason,invoiceId,description;
    private StorageReference storageReference;
    public String downloadImageUrl;
    public CollectionReference refundCollection;
    private static final int FILE_CHOOSE_ACTIVITY_RESULT_CODE = 1;
    private Uri contentUri2;
    private String imageFileName2;
    private Uri contentUri;
    private String imageFileName;
    public String downloadImageUrl2;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private ProgressDialog loading;

    public OrderFoundFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_order_found, container, false);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(OrderFoundFragment.super.getContext());
        String mail = sp.getString("email", "empty");
        docid = sp.getString("docid", "empty");

        storageReference = FirebaseStorage.getInstance().getReference().child("ComplainImages");

        refundCollection = db.collection("Complain");

        img = root.findViewById(R.id.com_img);
        img2 = root.findViewById(R.id.com_img2);
        reason = root.findViewById(R.id.reason_field);
        invoiceId = root.findViewById(R.id.invoiceId_field);
        description = root.findViewById(R.id.description_field);
        saveBtn = root.findViewById(R.id.refund_save_btn);
        viewHistory = root.findViewById(R.id.view_history);
        browse = root.findViewById(R.id.brows_photo);
        browse2 = root.findViewById(R.id.brows_photo2);

        loading = new ProgressDialog(OrderFoundFragment.super.getContext());

        viewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent(OrderFoundFragment.super.getContext(), ViewComplainActivity.class);
                viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(viewIntent);
            }
        });

        browse2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, 3);
            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveComplain(docid);
            }
        });

        return root;
    }

    private void saveComplain(String docid) {
        loading.setTitle("Sending Complain");
        loading.setMessage("Please Wait, while we are checking the credentials.");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        StorageReference filePath = storageReference.child(imageFileName);

        final UploadTask uploadTask = filePath.putFile(contentUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(OrderFoundFragment.super.getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw  task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();

                            StorageReference filePath2 = storageReference.child(imageFileName2);

                            final UploadTask uploadTask2 = filePath2.putFile(contentUri2);
                            uploadTask2.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    String message2 = e.toString();
                                    Toast.makeText(OrderFoundFragment.super.getContext(), "Error: " + message2, Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> uriTask = uploadTask2.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                            if (!task.isSuccessful()){
                                                throw  task.getException();
                                            }

                                            downloadImageUrl2 = filePath2.getDownloadUrl().toString();
                                            return filePath2.getDownloadUrl();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()){
                                                downloadImageUrl2 = task.getResult().toString();

                                                Calendar calendar = Calendar.getInstance();
                                                SimpleDateFormat currentDate = new SimpleDateFormat("MM:dd:yyyy");
                                                saveCurrentDate = currentDate.format(calendar.getTime());
                                                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                                                saveCurrentTime = currentTime.format(calendar.getTime());

                                                Complain newCom = new Complain();
                                                newCom.setInvoice(invoiceId.getText().toString());
                                                newCom.setUserDocId(docid);
                                                newCom.setDateTime(new Date());
                                                newCom.setDescription(description.getText().toString());
                                                newCom.setInvoiceUrl(downloadImageUrl2);
                                                newCom.setProductUrl(downloadImageUrl);
                                                newCom.setReason(reason.getText().toString());
                                                newCom.setStatus("send");

                                                refundCollection.add(newCom).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        loading.dismiss();
                                                        reason.setText("");
                                                        invoiceId.setText("");
                                                        description.setText("");
                                                        img.setImageResource(R.drawable.ic_menu_camera);
                                                        img2.setImageResource(R.drawable.ic_smartphone);
                                                        Toast.makeText(OrderFoundFragment.super.getContext(), "Complain Sent Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderFoundFragment.super.getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    askCameraPermissions();
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent, 2);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(currentPhotoPath);
                img.setImageURI(Uri.fromFile(f));
                Log.d(TAG,"ABSolute Uri of image is" + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                getContext().sendBroadcast(mediaScanIntent);
                imageFileName  = f.getName();


            } else if (requestCode == 2) {
                contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imageFileName = "JPEG_" + timeStamp + "_"+ getFileExt(contentUri);
                Log.d(TAG,"Gallery Image Uri :" + imageFileName);
                img.setImageURI(contentUri);


            }else if (requestCode == 3){
                contentUri2 = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imageFileName2 = "JPEG_" + timeStamp + "_"+ getFileExt(contentUri2);
                Log.d(TAG,"Gallery Image Uri :" + imageFileName2);
                img2.setImageURI(contentUri2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else{
                Toast.makeText(OrderFoundFragment.super.getContext(), "Camera Permission is Required to Use Camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(OrderFoundFragment.super.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
        }else{
            dispatchTakePictureIntent();
        }

    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null){
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(getContext(),"com.lk.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(takePictureIntent,1);
            }
        }
    }

    private  File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

}