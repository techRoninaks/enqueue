package com.roninaks.enqueue.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roninaks.enqueue.R;
import com.roninaks.enqueue.activities.MainActivity;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     ManageFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class ManageFragment extends BottomSheetDialogFragment {

    //Arguments
    private static final String ARG_PARAMS1 = "service_id";

    private int serviceId = -1;
    private final int argCount = 3;
    private Context context;
    public static ManageFragment newInstance(int serviceId) {
        final ManageFragment fragment = new ManageFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_PARAMS1, serviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ItemAdapter(argCount));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        if(bundle != null) {
            serviceId = bundle.getInt(ARG_PARAMS1);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;
        final View separator;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_manage_list_dialog_item, parent, false));
            text = itemView.findViewById(R.id.text);
            separator = itemView.findViewById(R.id.separator);
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final int mItemCount;
        private final String[] args = {"Create Token", "Dashboard", "Change Password"};

        ItemAdapter(int itemCount) {
            mItemCount = itemCount;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.text.setText(args[position]);
            holder.separator.setVisibility(position == args.length - 1 ? View.GONE : View.VISIBLE);
            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (args[position]){
                        case "Create Token":{
                            CreateTokenFragment fragment = CreateTokenFragment.newInstance(CreateTokenFragment.CREATE_TOKEN_MODE, 0, serviceId);
                            ((MainActivity) context).initFragment(fragment, MainActivity.NAVIGATION_FRAGMENT_TAG_DIALOG);
                        }
                        break;
                        case "Dashboard":{

                        }
                        break;
                        case "Change Password":{

                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }

    }

}
