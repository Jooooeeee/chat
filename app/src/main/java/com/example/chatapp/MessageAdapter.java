package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Bean.MessageBean;
import com.example.chatapp.Bean.Type;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MessageBean> messageBeanList;

    public MessageAdapter(List<MessageBean> messageBeanList) {
        this.messageBeanList = messageBeanList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder=null;
        switch (viewType){
            case Type
                    .MESSAGE_TYPE_IMAGE:
                View  view= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_image,parent,false);
            holder=new ViewHolderImage(view);
            break;
            case Type.MESSAGE_TYPE_TEXT:
                View view1= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_text,parent,false);
                holder=new ViewHolderText(view1);
                default:
                    break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case Type.MESSAGE_TYPE_IMAGE:
                imagelayout(holder,position);
                break;
                case Type.MESSAGE_TYPE_TEXT:
                    textlayout(holder,position);
                    break;
                    default:
                        break;
        }
    }

    private void imagelayout(@NonNull RecyclerView.ViewHolder holder, int position){
        ViewHolderImage viewHolderImage= (ViewHolderImage) holder;
        switch (messageBeanList.get(position).getType().getWhereType()){
            case Type.WHERE_TYPE_OWN:
                viewHolderImage.leftImage.setVisibility(View.GONE);
                viewHolderImage.rightImage.setImageBitmap(messageBeanList.get(position).getBitmap());
                break;
                case Type.WHERE_TYPE_OTHERS:
                    viewHolderImage.rightImage.setVisibility(View.GONE);
                    viewHolderImage.leftImage.setImageBitmap(messageBeanList.get(position).getBitmap());
                    break;
        }
    }

    private void textlayout(@NonNull RecyclerView.ViewHolder holder, int position){
        ViewHolderText viewHolderText= (ViewHolderText) holder;
        switch (messageBeanList.get(position).getType().getWhereType()){
            case Type.WHERE_TYPE_OWN:
                viewHolderText.leftLayout.setVisibility(View.GONE);
                viewHolderText.rightText.append(messageBeanList.get(position).getText());
                break;
            case Type.WHERE_TYPE_OTHERS:
                viewHolderText.rightLayout.setVisibility(View.GONE);
                viewHolderText.leftText.append(messageBeanList.get(position).getText());
                break;
        }
    }
    @Override
    public int getItemCount() {
        return messageBeanList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return messageBeanList.get(position).getType().getMessageType();
    }

    static class ViewHolderText extends RecyclerView.ViewHolder{
        LinearLayout leftLayout,rightLayout;
        TextView leftText,rightText;
       ViewHolderText(View view){
          super(view);
          leftText=view.findViewById(R.id.message_left_text);
          leftLayout=view.findViewById(R.id.left_layout);
          rightText=view.findViewById(R.id.message_right_text);
          rightLayout=view.findViewById(R.id.right_layout);
      }
    }
    static class ViewHolderImage extends RecyclerView.ViewHolder{
        ImageView leftImage,rightImage;
         ViewHolderImage(View view){
            super(view);
            leftImage=view.findViewById(R.id.message_left_image);
            rightImage=view.findViewById(R.id.message_right_image);
        }
    }
}
