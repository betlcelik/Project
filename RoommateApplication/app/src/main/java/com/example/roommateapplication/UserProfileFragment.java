package com.example.roommateapplication;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roommateapplication.Models.MatchRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {
    ImageView iv_avatar,iv_match,iv_wp,iv_email;
    FirebaseAuth firebaseAuth;
    String mUid;
    String uid;
    FirebaseUser user;
    Boolean matched;
    DatabaseReference databaseReference;
    private String phone,recipient;
    String message="Roommate Application Message Initiation Service " ,messageEmail="Roommate Application Message Initiation Service ",subject="Roommate Application";
    TextView tv_firstName,tv_lastName,tv_department,tv_grade,tv_distance,tv_time,tv_state;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_user_profile, container, false);

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();


        tv_firstName=view.findViewById(R.id.tv_firstName);
        tv_lastName = view.findViewById(R.id.tv_lastName);
        tv_department = view.findViewById(R.id.tv_department);
        tv_grade = view.findViewById(R.id.tv_grade);
        tv_state = view.findViewById(R.id.tv_state);
        tv_distance = view.findViewById(R.id.tv_distance);
        tv_time = view.findViewById(R.id.tv_time);

        iv_avatar = view.findViewById(R.id.iv_avatar);
        iv_match=view.findViewById(R.id.iv_match);
        iv_wp=view.findViewById(R.id.iv_wp);
        iv_email=view.findViewById(R.id.iv_email);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        mUid=currentUser.getUid().toString();


        Bundle bundle = getArguments();
        if (bundle != null) {
            uid = bundle.getString("uid");
            getInfo(uid);

        }

        iv_wp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkIsMatched(mUid, uid, new MatchedCheckCallback() {
                    @Override
                    public void onMatchedChecked(boolean isMatched) {
                        if (isMatched) {
                            if(isWhatsappInstalled()){

                                Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://api.whatsapp.com/send?phone="+phone+"&text="+message));
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(getActivity(), "Whatsapp is not installed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "You need to match for sending message", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });


        iv_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIsMatched(mUid, uid, new MatchedCheckCallback() {
                    @Override
                    public void onMatchedChecked(boolean isMatched) {
                        if (isMatched) {
                            sendEmail(recipient, subject, messageEmail);
                        } else {
                            Toast.makeText(getActivity(), "You need to match for sending email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        iv_match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMacthDemand(mUid,uid);
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

public interface MatchedCheckCallback {
    void onMatchedChecked(boolean isMatched);
}


    private void checkIsMatched(String mUid, String uid, MatchedCheckCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("MatchDemands");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean control = false;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MatchRequest matchRequest = dataSnapshot.getValue(MatchRequest.class);

                    if (matchRequest != null && matchRequest.getReceivingUid() != null &&
                            matchRequest.getReceivingUid().equals(mUid) && matchRequest.getSendingUid().equals(uid) && matchRequest.getState().equals("accepted")) {
                        control = true;
                        break;
                    } else if (matchRequest != null &&
                            matchRequest.getReceivingUid() != null &&
                            matchRequest.getReceivingUid().equals(uid) && matchRequest.getSendingUid().equals(mUid) && matchRequest.getState().equals("accepted")) {
                        control = true;
                        break;
                    }
                }

                callback.onMatchedChecked(control);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }


    private void addMacthDemand(String mUid, String uid) {
        databaseReference=FirebaseDatabase.getInstance().getReference("MatchDemands");
        String sendingUid = mUid;
        String receivingUid=uid;
        String state="waiting";

        MatchRequest matchRequest= new MatchRequest(sendingUid,receivingUid,state);
        String requestId = databaseReference.push().getKey();
        databaseReference.child(requestId).setValue(matchRequest);

        Toast.makeText(getActivity(), "Match request sent", Toast.LENGTH_SHORT).show();




    }

    private void sendEmail(String recipient, String subject, String messageEmail) {
        Intent mEmailIntent= new Intent(Intent.ACTION_SEND);
        mEmailIntent.setData(Uri.parse("mailto:"));
        mEmailIntent.setType("text/plain");

        mEmailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{recipient});
        mEmailIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
        mEmailIntent.putExtra(Intent.EXTRA_TEXT,message);

        try{
            startActivity(Intent.createChooser(mEmailIntent,"Choose an Email Client"));
        }catch(Exception e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void getInfo(String uid) {

       // DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
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


                    if(firstName != null  && !firstName.equals("null")) {
                        tv_firstName.setText(firstName);
                    }
                    if(lastName != null  && !lastName.equals("null")){
                        tv_lastName.setText(lastName);
                    }
                   if(email != null  && !email.equals("null")){

                       recipient=email;
                   }

                    if(phoneNumber != null  && !phoneNumber.equals("null")){

                        phone = phoneNumber;
                    }

                    if (!department.equals("null")  && !department.equals("null")) {
                        tv_department.setText(department);
                    }
                    if(grade != null  && !grade.equals("null")){
                        tv_grade.setText(grade);
                    }

                    if (!maxDistance.equals("null") && !maxDistance.equals("null")) {
                        tv_distance.setText(maxDistance);
                    }
                    if(state != null  && !state.equals("null")){
                        tv_state.setText(state);
                    }

                    if (!time.equals("null") && !time.equals("null")) {
                        tv_time.setText(time);
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

  private boolean isWhatsappInstalled(){
        PackageManager packageManager= getActivity().getPackageManager();
        boolean whatsappInstalled;

        try{
            packageManager.getPackageInfo("com.whatsapp",PackageManager.GET_ACTIVITIES);
            whatsappInstalled =true;

        }catch(PackageManager.NameNotFoundException e){
            whatsappInstalled=false;
        }

        return whatsappInstalled;
  }

}