package com.example.roommateapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roommateapplication.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterNotifications extends RecyclerView.Adapter<AdapterNotifications.MyNotificationHolder> {
    Context context;
    List<User> userList;
    private FragmentActivity mActivity;


    public AdapterNotifications(Context context, List<User> userList,FragmentActivity mActivity) {
        this.context=context;
        this.userList=userList;
        this.mActivity=mActivity;
    }

    @NonNull
    @Override
    public MyNotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.row_notifications,parent,false);


        return new MyNotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyNotificationHolder holder, int position) {

        String userImage=userList.get(position).getImage();
        String firstName=userList.get(position).getFirstName();
        String lastName=userList.get(position).getLastName();

        if(firstName != null && !firstName.equals("null")){

            holder.tv_firstName.setText(firstName);
        }else{
            holder.tv_firstName.setText(" ");
        }
        if(lastName != null   && !lastName.equals("null") ){
            holder.tv_lastName.setText(lastName);
        }else{
            holder.tv_lastName.setText(" ");
        }
        try{
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_default_profile)
                    .into(holder.iv_avatar);
        }catch (Exception e){

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition=holder.getAdapterPosition();
                if(clickedPosition != RecyclerView.NO_POSITION){


                    User selectedUser = userList.get(clickedPosition);
                    String selectedUid = selectedUser.getUid();

                    UserProfileFragment fragmentProfile=new UserProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("uid", selectedUid);
                    fragmentProfile.setArguments(bundle);

                    FragmentTransaction ftProfile=mActivity.getSupportFragmentManager().beginTransaction();
                    ftProfile.replace(R.id.content,fragmentProfile,"");
                    ftProfile.commit();
                    Toast.makeText(context, ""+firstName, Toast.LENGTH_SHORT).show();
                }  }
        });

        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User selectedUser = userList.get(position);
                String selectedUid = selectedUser.getUid();

                acceptMatchRequestState(selectedUid);

                // Perform any additional actions you want when the "Accept" button is clicked

                Toast.makeText(context, "Match request accepted", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User selectedUser = userList.get(position);
                String selectedUid = selectedUser.getUid();

                rejectMatchRequestState(selectedUid);

                // Perform any additional actions you want when the "Accept" button is clicked

                Toast.makeText(context, "Match request rejected", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void rejectMatchRequestState(String selectedUid) {
        DatabaseReference matchRequestRef = FirebaseDatabase.getInstance().getReference("MatchDemands");
        matchRequestRef.orderByChild("sendingUid").equalTo(selectedUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String matchRequestId = snapshot.getKey();
                    if (matchRequestId != null) {
                        DatabaseReference requestRef = matchRequestRef.child(matchRequestId);
                        requestRef.child("state").setValue("rejected");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }


    private void acceptMatchRequestState(String uid) {
        DatabaseReference matchRequestRef = FirebaseDatabase.getInstance().getReference("MatchDemands");
        matchRequestRef.orderByChild("sendingUid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String matchRequestId = snapshot.getKey();
                    if (matchRequestId != null) {
                        DatabaseReference requestRef = matchRequestRef.child(matchRequestId);
                        requestRef.child("state").setValue("accepted");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyNotificationHolder extends RecyclerView.ViewHolder {
        // Declare your view variables here
        ImageView iv_avatar;
        TextView tv_firstName,tv_lastName;
        Button btn_accept,btn_reject;
        public MyNotificationHolder(@NonNull View itemView) {
            super(itemView);

            iv_avatar=itemView.findViewById(R.id.iv_avatar);
            tv_firstName=itemView.findViewById(R.id.tv_firstName);
            tv_lastName=itemView.findViewById(R.id.tv_lastName);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_reject=itemView.findViewById(R.id.btn_reject);

            // Initialize your view variables here
        }
    }
}
