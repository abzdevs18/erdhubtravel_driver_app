package com.sf_ert.ertdriver.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sf_ert.ertdriver.Model.Messages;
import com.sf_ert.ertdriver.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private List<Messages> messages;
    private Context context;

    public MessageAdapter(List<Messages> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",context.MODE_PRIVATE);
        String id = sharedPreferences.getString("p_k","");

        if (id.equals(messages.get(position).getSender())){
            return R.layout.sender;
        }
            return R.layout.reciever;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(i,viewGroup,false);
            return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
//        Messages msgs_r = messages.get(i);
//        holder.msgs_r = msgs_r;
        holder.Chat.setText(messages.get(i).getContent());
        holder.Time.setText(messages.get(i).getTime());
        holder.sender_pk.setText(messages.get(i).getSender());
        holder.reciever_pk.setText(messages.get(i).getReciever());
//        Uri uri = Uri.parse("https://primelinetools.com/pub/media/catalog/product/placeholder/default/ajax-loader02_4.gif");
//        Glide.with(context)
//                .load(messages.get(i).getSenderImg())
//                .centerCrop()
//                .crossFade()
//                .into(holder.msgSenderIcon);
//        String img = messages.get(i).getSenderImg();
//        Log.d("Image",img);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView Chat,Time,sender_pk,reciever_pk;
        CircleImageView msgSenderIcon;
//        Messages msgs_r;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
//            msgSenderIcon = itemView.findViewById(R.id.msgSenderIcon);
            sender_pk = itemView.findViewById(R.id.sender_id);
            reciever_pk = itemView.findViewById(R.id.reciever_id);
            Chat = itemView.findViewById(R.id.chat);
            Time = itemView.findViewById(R.id.Time);
        }
    }
}
// TODO: 18/04/2019 Left here NullPointerException Always Occus for no  Reason
