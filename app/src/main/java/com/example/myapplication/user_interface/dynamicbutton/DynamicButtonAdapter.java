package com.example.myapplication.user_interface.dynamicbutton;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.List;


public class DynamicButtonAdapter extends RecyclerView.Adapter<DynamicButtonAdapter.ViewHolder> {

    private Context context;
    private List<DynamicButton> dynamicButtonList;
    DynamicButtonClickListener listener;

    public interface DynamicButtonClickListener {
        public void onDynamicButtonClicked(DynamicButton dynamicButton);
    }

    public DynamicButtonAdapter(Context context, List<DynamicButton> dynamicButtonList,
                         DynamicButtonClickListener mListener) {
        this.context = context;
        this.dynamicButtonList = dynamicButtonList;
        listener = mListener;
    }

    public DynamicButtonAdapter(Context context,
                                DynamicButtonClickListener mListener) {
        this.context = context;
        this.dynamicButtonList = dynamicButtonList;
        listener = mListener;
    }

    public void addAll(List<DynamicButton> list){
        dynamicButtonList.addAll(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_dynamic_button, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final DynamicButton dynamicButton = dynamicButtonList.get(position);
        String buttonTitle = dynamicButton.getValue();

        holder.button.setText(buttonTitle);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDynamicButtonClicked(dynamicButton);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dynamicButtonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button);
        }
    }
}
