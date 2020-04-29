package com.roninaks.enqueue.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.roninaks.enqueue.R;
import com.roninaks.enqueue.databinding.ServicesListItemBinding;
import com.roninaks.enqueue.models.ServicePrimaryModel;

import java.util.ArrayList;
import java.util.List;

public class RvServicesAdapter extends RecyclerView.Adapter<RvServicesAdapter.ViewHolder> {
    private Context context;
    private List<ServicePrimaryModel> servicePrimaryModels = new ArrayList<>();
    private OnItemClickListener listener;

    public RvServicesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ServicesListItemBinding servicesListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.services_list_item, parent, false);
        return new ViewHolder(servicesListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mBinding.setService(servicePrimaryModels.get(position));
    }

    @Override
    public int getItemCount() {
        return servicePrimaryModels.size();
    }

    //Getter for Primary
    public List<ServicePrimaryModel> getServicePrimaryModels() {
        return servicePrimaryModels;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setServicePrimaryModels(List<ServicePrimaryModel> servicePrimaryModels) {
        this.servicePrimaryModels = servicePrimaryModels;
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ServicesListItemBinding mBinding;

        public ViewHolder(@NonNull ServicesListItemBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
            mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onClick(v, servicePrimaryModels.get(position));
                    }
                }
            });
            mBinding.imgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onClick(v, servicePrimaryModels.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClick(View v, ServicePrimaryModel servicePrimaryModel);
    }
}
