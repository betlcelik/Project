package com.example.roommateapplication;

import static android.app.Activity.RESULT_OK;


import static com.google.firebase.database.FirebaseDatabase.getInstance;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roommateapplication.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    StorageReference storageReference;
    String storagePath="Users_Profile_Images";
    String profilePhoto;
    ImageView iv_avatar;
    EditText et_firstName,et_lastName,et_department,et_email,et_phoneNumber;
    FloatingActionButton fab;
    Button btn_update,btn_changepassword;
    Spinner sp_state,sp_grade,sp_distance,sp_time;
    private Spinner stateSpinner;

    private ArrayAdapter<String> stateAdapter;
    private ArrayAdapter<String> gradeAdapter;
    private MapsFragment mapsFragment;


    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    private static final int IMAGE_PICK_GALLERY_CODE=300;
    private static final int IMAGE_PICK_CAMERA_CODE=400;

    String cameraPermissions[];
    String storagePermissions[];

    Uri image_uri;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile,menu);

        //super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id= item.getItemId();

        if(id==R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();

        }
        if(id==R.id.action_updatePassword){
            ShowUpdatePasswordDialog();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase= getInstance();
        databaseReference=firebaseDatabase.getReference("Users");
        storageReference= FirebaseStorage.getInstance().getReference();



        cameraPermissions= new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        iv_avatar=view.findViewById(R.id.iv_avatar);
        et_firstName=view.findViewById(R.id.et_firstName);
        et_lastName=view.findViewById(R.id.et_lastName);
        et_email=view.findViewById(R.id.et_email);
        et_phoneNumber=view.findViewById(R.id.et_phoneNumber);
        et_department=view.findViewById(R.id.et_department);

        sp_grade=view.findViewById(R.id.sp_grade);
        sp_state=view.findViewById(R.id.sp_state);
        sp_distance=view.findViewById(R.id.sp_distance);
        sp_time=view.findViewById(R.id.sp_time);

        fab=view.findViewById(R.id.fab_map);
        btn_update=view.findViewById(R.id.btn_update);
        btn_changepassword=view.findViewById(R.id.btn_changepassword);


        getSpinnerAdapter(sp_grade);
        getSpinnerAdapter(sp_state);
        getSpinnerAdapter(sp_distance);
        getSpinnerAdapter(sp_time);

       mapsFragment=(MapsFragment) getChildFragmentManager().findFragmentById(R.id.map);


        if(user != null) {
            getInfo();
        }

        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
                getInfo();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                mapsFragment = new MapsFragment();


                if (mapsFragment != null) {


                    FragmentTransaction ftMap= getActivity().getSupportFragmentManager().beginTransaction();
                    ftMap.replace(R.id.content,mapsFragment,"");
                    ftMap.commit();


                    //mapsFragment.getLocation();
                }
            }
        });

        btn_changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowUpdatePasswordDialog();
                checkUserStatus();
            }
        });


        // Inflate the layout for this fragment
        return view;
    }



    private void getSpinnerAdapter(Spinner type) {

        if (sp_state.equals(type)) {
            ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.state_options, android.R.layout.simple_spinner_item);
            stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_state.setAdapter(stateAdapter);
        } else if (sp_grade.equals(type)) {
            ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.grade_options, android.R.layout.simple_spinner_item);
            gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_grade.setAdapter(gradeAdapter);
        }
        else if (sp_distance.equals(type)) {
            ArrayAdapter<CharSequence> distanceAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.distance_options, android.R.layout.simple_spinner_item);
            distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_distance.setAdapter(distanceAdapter);
        }
        else if (sp_time.equals(type)) {
            ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.time_options, android.R.layout.simple_spinner_item);
            timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_time.setAdapter(timeAdapter);
        }
    }

    private void showImagePicDialog() {

        String options[] = {"Camera","Gallery"};
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    if(!checkCameraPermissions()){
                        requestCameraPermissions();
                    }
                    else{
                        pickFromCamera();
                    }
                }
                else if(which==1){

                    if(!checkStoragePermissions()){
                        requestStoragePermissions();
                    }
                    else{
                        pickFromGallery();
                    }

                }


            }
        });
        builder.show();
    }

    private void updateInfo() {
        String firstName = et_firstName.getText().toString();
        String lastName = et_lastName.getText().toString();
        String email = et_email.getText().toString();
        String phoneNumber = et_phoneNumber.getText().toString();
        String department = et_department.getText().toString();
        String grade = sp_grade.getSelectedItem().toString();
        String state = sp_state.getSelectedItem().toString();
        String maxDistance = sp_distance.getSelectedItem().toString();
        String time = sp_time.getSelectedItem().toString();

       // String image= image_uri.toString();

        Toast.makeText(getActivity(), state, Toast.LENGTH_SHORT).show();

        DatabaseReference userRef=firebaseDatabase.getReference("Users/"+user.getUid());
        userRef.child("firstName").setValue(firstName);
        userRef.child("lastName").setValue(lastName);
        userRef.child("email").setValue(email);
        userRef.child("phoneNumber").setValue(phoneNumber);
        userRef.child("department").setValue(department);
        userRef.child("grade").setValue(grade);
        userRef.child("state").setValue(state);
        userRef.child("maxDistance").setValue(maxDistance);
        userRef.child("time").setValue(time);



        Toast.makeText(getActivity(), "Informations updated successfully", Toast.LENGTH_SHORT).show();


    }

    private boolean checkStoragePermissions(){

        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;

    }

    private void requestStoragePermissions(){
        //ActivityCompat.requestPermissions(getActivity(),storagePermissions,STORAGE_REQUEST_CODE);
        requestPermissions(storagePermissions,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions(){

        boolean result1= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result2= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result1 && result2;

    }

    private void requestCameraPermissions(){
        //ActivityCompat.requestPermissions(getActivity(),cameraPermissions,CAMERA_REQUEST_CODE);
        requestPermissions(cameraPermissions,CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted=grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }
                    else{
                        Toast.makeText(getActivity(), "You need to enable camera and storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){

                    boolean writeStorageAccepted=grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if( writeStorageAccepted){
                        pickFromGallery();
                    }
                    else{
                        Toast.makeText(getActivity(), "You need to enable  storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
       // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void pickFromGallery() {

        Intent galleryIntent= new Intent(Intent.ACTION_PICK);

        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }
    private void pickFromCamera() {
        ContentValues values= new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        Toast.makeText(getActivity(), "after values put", Toast.LENGTH_SHORT).show();
        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();
        Intent cameraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Toast.makeText(getActivity(), "2", Toast.LENGTH_SHORT).show();
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        Toast.makeText(getActivity(), "3", Toast.LENGTH_SHORT).show();
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("ProfileFragment", "onActivityResult called");
        Log.d("ProfileFragment", "requestCode: " + requestCode);
        Log.d("ProfileFragment", "resultCode: " + resultCode);
        Toast.makeText(getActivity(), "On activity result çağırıldı", Toast.LENGTH_SHORT).show();
        if(resultCode == RESULT_OK){

            if(requestCode==IMAGE_PICK_GALLERY_CODE){
                if(data != null && data.getData() != null){
                    image_uri=data.getData();
                    uploadProfilePhoto(image_uri);
                }


                Log.d("ProfileFragment", "image_uri: " + image_uri.toString());
                Toast.makeText(getActivity(), "imageuri : " +image_uri.toString(), Toast.LENGTH_SHORT).show();
                ;

            }

            if(requestCode == IMAGE_PICK_CAMERA_CODE){

                    //image_uri=data.getData();
                    uploadProfilePhoto(image_uri);
                Toast.makeText(getActivity(), "imageuri : " +image_uri.toString(), Toast.LENGTH_SHORT).show();

            }
        }

        super.onActivityResult(requestCode,resultCode,data);

    }

    private void uploadProfilePhoto(Uri uri) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Uploading photo...");
         progressDialog.show();
        String filePathAndName= storagePath + "_"+user.getUid();
        StorageReference storageReference1=storageReference.child(filePathAndName);
        storageReference1.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri=uriTask.getResult();

                        if(uriTask.isSuccessful()){
                            //HashMap<String,Object> results= new HashMap<>();
                            //results.put("image",downloadUri.toString());
                            //final String downloadUriAdress = uriTask.getResult().toString();

                            //image uploaded
                            databaseReference.child(user.getUid()).child("image").setValue(downloadUri.toString())

                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), "Image updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();

                                        }
                                    });

                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Error occured", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void getInfo() {

        Query query=databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                for(DataSnapshot ds:snapshot.getChildren()){
                    String firstName=""+ds.child("firstName").getValue();
                    String lastName=""+ds.child("lastName").getValue();
                    String email=""+ds.child("email").getValue();
                    String phoneNumber=""+ds.child("phoneNumber").getValue();
                    String department=""+ds.child("department").getValue();
                    String grade=""+ds.child("grade").getValue();
                    String state=""+ds.child("state").getValue();
                    String maxDistance=""+ds.child("maxDistance").getValue();
                    String time=""+ds.child("time").getValue();
                    String image =""+ds.child("image").getValue();


                    if (!firstName.equals("null")) {
                        et_firstName.setText(firstName);
                    } else {
                        et_firstName.setHint("First Name");
                    }

                    if (!lastName.equals("null")) {
                        et_lastName.setText(lastName);
                    } else {
                        et_lastName.setHint("Last Name");
                    }

                    if(email != null && !email.equals("null")){
                        et_email.setText(email);
                    }else{
                        et_email.setHint("Email");
                    }

                    if(phoneNumber != null && !phoneNumber.equals("null")){
                        et_phoneNumber.setText(phoneNumber);
                    }
                    else{
                        et_phoneNumber.setHint("Phone Number");
                    }

                    if (!department.equals("null")) {
                        et_department.setText(department);
                    } else {
                        et_department.setHint("Department");
                    }

                    if (!state.equals("null")) {
                        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.state_options, android.R.layout.simple_spinner_item);
                        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_state.setAdapter(stateAdapter);

                        int statePosition = stateAdapter.getPosition(state);
                        sp_state.setSelection(statePosition);
                    } else {
                        sp_state.setSelection(0); // Set hint as default selection
                    }

                    if(!grade.equals("null")){
                        ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.grade_options, android.R.layout.simple_spinner_item);
                        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_grade.setAdapter(gradeAdapter);

                        int gradePosition=gradeAdapter.getPosition(grade);
                        sp_grade.setSelection(gradePosition);

                    }
                    else{

                        sp_grade.setSelection(0);
                    }

                    if(!maxDistance.equals("null")){
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.distance_options, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_distance.setAdapter(adapter);

                        int distancePosition=adapter.getPosition(maxDistance);
                        sp_distance.setSelection(distancePosition);

                    }
                    else{
                        sp_distance.setSelection(0);


                    }

                    if(!time.equals("null")){
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.time_options, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_time.setAdapter(adapter);
                        int position=adapter.getPosition(time);
                        sp_time.setSelection(position);

                    }
                    else{

                        sp_time.setSelection(0);


                    }


                        try{
                            Picasso.get().load(image).into(iv_avatar);
                        }catch (Exception e){

                            Picasso.get().load(R.drawable.ic_default_profile).into(iv_avatar);
                        }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    private void ShowUpdatePasswordDialog() {

        View view=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_update_password,null);
        EditText et_oldPassword=view.findViewById(R.id.et_oldPassword);
        EditText et_newPassword=view.findViewById(R.id.et_newPassword);
        Button btn_updatePassword=view.findViewById(R.id.btn_updatePassword);


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        final AlertDialog dialog=builder.create();
        dialog.show();

        btn_updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword=et_oldPassword.getText().toString().trim();
                String newPassword=et_newPassword.getText().toString().trim();

                if(TextUtils.isEmpty(oldPassword)){
                    Toast.makeText(getActivity(), "Enter your current password...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newPassword.length()<6){
                    if(TextUtils.isEmpty(oldPassword)){
                        Toast.makeText(getActivity(), "Password length must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
                updatePassword(oldPassword,newPassword);
            }
        });

    }

    private void updatePassword(String oldPassword, String newPassword) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        AuthCredential authCredential= EmailAuthProvider.getCredential(user.getEmail(),oldPassword);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Password Updated Sucessfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void checkUserStatus(){

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user!=null){

        }else{
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
}