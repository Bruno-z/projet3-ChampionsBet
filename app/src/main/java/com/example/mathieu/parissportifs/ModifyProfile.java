package com.example.mathieu.parissportifs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class ModifyProfile extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private EditText  etName;
    private CircleImageView civProfilePic;
    private TextView modifyPicture, modifyPassword, removeAccount, logOut, etEmail;
    private Button save;
    private FirebaseUser user;
    private String pseudo;
    private String email, favoriteTeam;
    private Uri photoUrl;
    private FirebaseDatabase userDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String newPseudo;

    private static final int PICK_IMAGE_REQUEST = 256;
    private static final int REQUEST_IMAGE_CAPTURE = 234;
    private static final String TAG = "TAG";


    private Uri imageUri;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference("users_avatar");

        etEmail = (TextView) findViewById(R.id.editTextModifyEmail);
        etName = (EditText) findViewById(R.id.editTextModifyPseudo);

        civProfilePic = (CircleImageView)findViewById(R.id.playerPic);
        civProfilePic.setOnClickListener(this);


        pseudo = user.getDisplayName();
        email = user.getEmail();
        photoUrl = user.getPhotoUrl();

        etEmail.setText(email);
        etName.setText(pseudo);
        civProfilePic.setImageURI(photoUrl);

        modifyPicture = (TextView) findViewById(R.id.textViewModifyPicture);
        modifyPicture.setOnClickListener(this);

        modifyPassword = (TextView) findViewById(R.id.textViewResetMyPassword);
        modifyPassword.setOnClickListener(this);

        removeAccount = (TextView) findViewById(R.id.textViewRemoveMyAccount);
        removeAccount.setOnClickListener(this);

        logOut = (TextView) findViewById(R.id.textViewDisconnect);
        logOut.setOnClickListener(this);

        save = (Button) findViewById(R.id.buttonSave);
        save.setOnClickListener(this);




        downloadPicture();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }




    public void pushUserOnFirebase() {

        userDatabase = FirebaseDatabase.getInstance();
        myRef = userDatabase.getReference("users").child(user.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserModel userActuel = dataSnapshot.getValue(UserModel.class);
                userActuel.setUserName(newPseudo);
                myRef.setValue(userActuel);
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void changeProfil() {


        newPseudo = etName.getText().toString().trim();

        //Uri newPhoto = userImg.

        //Update Pseudo & Photo
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newPseudo)
                //.setPhotoUri(newPhoto)
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                }
            }
        });
    }

    public void deleteProfil() {

        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                }
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent();

        gallery.setType("image/*");

        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {

            if (data == null) {
                //Display an error
                return;
            }
            imageUri = data.getData();
            uploadPicture(imageUri);
            downloadPicture();

        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            imageUri=data.getData();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            civProfilePic.setImageBitmap(imageBitmap);

            galleryAddPic();
            uploadPicture(imageUri);
            downloadPicture();

            return;
        }
    }

    private void uploadPicture(final Uri uri) {

        if (uri!=null) {
            StorageReference picRef = mStorageRef.child(mAuth.getCurrentUser().getUid() + "_avatar");

            picRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Get a URL to the uploaded content
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUrl)
                                    .build();

                            user.updateProfile(profileUpdates);
                            downloadPicture();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });
        }
        else{

            civProfilePic.setDrawingCacheEnabled(true);
            Bitmap imagebitmap = civProfilePic.getDrawingCache();


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imagebitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            StorageReference picRef = mStorageRef.child(mAuth.getCurrentUser().getUid() + "_avatar");



            UploadTask uploadTask = picRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // Get a URL to the uploaded content
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUrl)
                            .build();

                    user.updateProfile(profileUpdates);
                    downloadPicture();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(ModifyProfile.this, "Raté", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void downloadPicture () {

        StorageReference userPicture = mStorageRef.child(mAuth.getCurrentUser().getUid()+"_avatar");
        userPicture.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ModifyProfile.this)
                        .load(uri)
                        .placeholder(R.drawable.profile_test)
                        .into(civProfilePic)
                        ;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });

    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        /**Ensure there is a camera activity to handle the Intent*/
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            /** Create the file where the photo should go
             */
            File photoFile=null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                //Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,"com.example.mathieu.parissportifs", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            }


        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String sdf = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + sdf + "_";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onClick (View v) {
        if (v == save) {
            changeProfil();
            // __________A VIRER SI BUG_______________
            pushUserOnFirebase();
            // __________A VIRER SI BUG_______________

            finish();
            startActivity(new Intent(this, CreateOrJoinCompetition.class));
        }
        if (v == removeAccount) {

            // Boite de dialogue confirmation suppression
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Tu veux nous quitter ?")
                    .setContentText("Êtes-vous sûr de vouloir supprimer votre compte ?")
                    .setCancelText("Non")
                    .setConfirmText("Yes")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            // reuse previous dialog instance, keep widget user state, reset them if you need
                            sDialog.setTitleText("Super choix !")
                                    .setContentText("Nous sommes ravis que tu veuilles continuer l'aventure avec nous !")
                                    .setConfirmText("OK")
                                    .showCancelButton(false)
                                    .setCancelClickListener(null)
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            deleteProfil();
                            sDialog.setTitleText("Suppression !")
                                    .setContentText("Vous ne faites plus partie de la communauté Wild Socks!")
                                    .setConfirmText("OK")
                                    .showCancelButton(false)
                                    .setCancelClickListener(null)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            finish();
                                            startActivity(new Intent(ModifyProfile.this,MainActivity.class));
                                        }
                                    })
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                        }

                    })
                    .show();
        }
        if (v == modifyPassword){

            startActivity(new Intent(ModifyProfile.this,ResetPasswordActivity.class));
        }
        if (v == modifyPicture || v == civProfilePic) {


            new SweetAlertDialog(this)
                    .setTitleText("Alors tu ressembles à quoi!")
                    .setCancelText("Gallery")
                    .setConfirmText("Camera")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            // reuse previous dialog instance, keep widget user state, reset them if you need
                            openGallery();
                            sDialog.cancel();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            dispatchTakePictureIntent();
                            sDialog.cancel();

                        }

                    })
                    .show();
        }
        if (v == logOut){
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
