package com.example.roommateapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roommateapplication.Models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context;
    List<User> userList;
    private FragmentActivity mActivity;

    public AdapterUsers(Context context, List<User> userList,FragmentActivity mActivity) {
        this.context=context;
        this.userList=userList;
        this.mActivity=mActivity;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.row_users,parent,false);


        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String userImage=userList.get(position).getImage();
        String firstName=userList.get(position).getFirstName();
        String lastName=userList.get(position).getLastName();
        String maxDistance=userList.get(position).getMaxDistance();
        String time=userList.get(position).getTime();

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
        if(maxDistance != null  && !maxDistance.equals("null")){
            holder.tv_distance.setText(maxDistance);
        }else{
            holder.tv_distance.setText("Unspecified");
        }

        if(time != null  && !time.equals("null")){
            holder.tv_time.setText(time);
        }else{
            holder.tv_time.setText("Unspecified");
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


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView iv_avatar;
        TextView tv_firstName,tv_lastName,tv_distance,tv_time;



        public MyHolder(@NonNull View itemView) {
            super(itemView);

            iv_avatar=itemView.findViewById(R.id.iv_avatar);
            tv_firstName=itemView.findViewById(R.id.tv_firstName);
            tv_lastName=itemView.findViewById(R.id.tv_lastName);
            tv_distance=itemView.findViewById(R.id.tv_distance);
            tv_time=itemView.findViewById(R.id.tv_time);


        }
    }
}
