package com.roninaks.enqueue.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.roninaks.enqueue.R;
import com.roninaks.enqueue.databinding.QueueListItemHorizontalBinding;
import com.roninaks.enqueue.databinding.QueueListItemVerticalBinding;
import com.roninaks.enqueue.models.QueueModel;
import com.roninaks.enqueue.models.ServicePrimaryModel;

import java.util.ArrayList;
import java.util.List;

public class RxQueuesAdapter extends RecyclerView.Adapter<RxQueuesAdapter.ViewHolder> {
    public static int INFLATE_TYPE_VERTICAL = 0;
    public static int INFLATE_TYPE_HORIZONTAL = 1;
    private Context context;
    private List<QueueModel> queueModels = new ArrayList<>();
    private OnItemClickListener listener;
    private ServicePrimaryModel servicePrimaryModel;
    private int inflateType;

    public RxQueuesAdapter(Context context, int inflateType) {
        this.context = context;
        this.inflateType = inflateType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflateType == INFLATE_TYPE_HORIZONTAL) {
            QueueListItemHorizontalBinding horizontalBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.queue_list_item_horizontal, parent, false);
            return new ViewHolder(horizontalBinding);
        } else {
            QueueListItemVerticalBinding verticalBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.queue_list_item_vertical, parent, false);
            return new ViewHolder(verticalBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (inflateType == INFLATE_TYPE_HORIZONTAL) {
            holder.mHorizontalBinding.setQueue(queueModels.get(position));
            holder.mHorizontalBinding.setService(servicePrimaryModel);
            if(queueModels.get(position).getStatus().equals(context.getString(R.string.queue_status_ongoing))){
                holder.mHorizontalBinding.cmServiceTime.setBase(queueModels.get(position).getStartTime());
                holder.mHorizontalBinding.cmServiceTime.start();
            }else{
                holder.mHorizontalBinding.cmServiceTime.stop();
            }
        } else {
            holder.mVerticalBinding.setQueue(queueModels.get(position));
            holder.mVerticalBinding.setService(servicePrimaryModel);
            if(queueModels.get(position).getStatus().equals(context.getString(R.string.queue_status_ongoing))){
                holder.mVerticalBinding.cmDuration.setBase(queueModels.get(position).getStartTime());
                holder.mVerticalBinding.cmDuration.start();
            }else{
                holder.mVerticalBinding.cmDuration.stop();
            }
        }

    }

    @Override
    public int getItemCount() {
        return queueModels.size();
    }

    //Getter for QueueModels
    public List<QueueModel> getQueueModels() {
        return queueModels;
    }

    public void setQueueModels(List<QueueModel> queueModels) {
        this.queueModels = queueModels;
        notifyDataSetChanged();
    }

    //Getter for ServicePrimaryModels
    public ServicePrimaryModel getServicePrimaryModel() {
        return servicePrimaryModel;
    }

    public void setServicePrimaryModel(ServicePrimaryModel servicePrimaryModel) {
        this.servicePrimaryModel = servicePrimaryModel;
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        QueueListItemHorizontalBinding mHorizontalBinding;
        QueueListItemVerticalBinding mVerticalBinding;

        public ViewHolder(@NonNull QueueListItemVerticalBinding itemView) {
            super(itemView.getRoot());
            mVerticalBinding = itemView;
            mVerticalBinding.imgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onClick(v, queueModels.get(position));
                    }
                }
            });
        }

        public ViewHolder(@NonNull QueueListItemHorizontalBinding itemView) {
            super(itemView.getRoot());
            mHorizontalBinding = itemView;
        }

    }

    public interface OnItemClickListener {
        void onClick(View v, QueueModel queueModel);
    }
}
