package com.roninaks.enqueue.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roninaks.enqueue.R;
import com.roninaks.enqueue.activities.MainActivity;
import com.roninaks.enqueue.databinding.FragmentCreateTokenBinding;
import com.roninaks.enqueue.helpers.StringHelper;
import com.roninaks.enqueue.models.QueueModel;
import com.roninaks.enqueue.models.ServicePrimaryModel;
import com.roninaks.enqueue.viewmodels.QueueViewModel;
import com.roninaks.enqueue.viewmodels.ServicePrimaryViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateTokenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateTokenFragment extends DialogFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM2 = "queue_id";
    private static final String ARG_PARAM1 = "mode";
    private static final String ARG_PARAM3 = "service_id";
    public static final int EDIT_TOKEN_MODE = 1;
    public static final int CREATE_TOKEN_MODE = 0;

    private int mode = 0;
    private int queueId = 0;
    private int serviceId = -1;
    private ServicePrimaryModel servicePrimaryModel;
    private QueueModel queueModel;
    private FragmentCreateTokenBinding mBinding;
    private ServicePrimaryViewModel servicePrimaryViewModel;
    private QueueViewModel queueViewModel;
    private Context context;
    private Observer queueObserver = new Observer<QueueModel>() {
        @Override
        public void onChanged(QueueModel queueModel) {
            CreateTokenFragment.this.queueModel = queueModel;
            mBinding.setQueue(queueModel);
        }
    };

    private Observer serviceObserver = new Observer<ServicePrimaryModel>() {
        @Override
        public void onChanged(ServicePrimaryModel servicePrimaryModel) {
            CreateTokenFragment.this.servicePrimaryModel = servicePrimaryModel;
            mBinding.setService(servicePrimaryModel);
        }
    };

    private Observer queueTokenObserver = new Observer<QueueModel>() {
        @Override
        public void onChanged(QueueModel queueModel) {
            if (queueModel != null) {
                if (CreateTokenFragment.this.queueModel == null) {
                    CreateTokenFragment.this.queueModel = new QueueModel();
                }
                CreateTokenFragment.this.queueModel.setToken(queueModel.getToken() + 1);
                queueId = queueModel.getQueueId() + 1;
                mBinding.setQueue(CreateTokenFragment.this.queueModel);
            }
        }
    };

    //On click listener for button
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (validate()) {
                if (mode == CREATE_TOKEN_MODE) {
                    queueModel = new QueueModel(queueId, serviceId, mBinding.etName.getText().toString(),
                            mBinding.etPhone.getText().toString(), 0, context.getString(R.string.queue_status_pending),
                            queueModel.getToken(), StringHelper.getToday());
                    queueViewModel.insert(queueModel);
                    servicePrimaryModel.setWaitingCount(servicePrimaryModel.getWaitingCount() + 1);
                    servicePrimaryModel.setStatus(context.getString(R.string.service_status_active));
                    servicePrimaryViewModel.update(servicePrimaryModel);
                } else if (mode == EDIT_TOKEN_MODE) {
                    queueModel.setUserName(mBinding.etName.getText().toString());
                    queueModel.setPhoneNumber(mBinding.etPhone.getText().toString());
                    queueViewModel.update(queueModel);
                }
                CreateTokenFragment.this.dismiss();
            }
        }
    };

    public CreateTokenFragment() {
        // Required empty public constructor
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mode    Edit or Create New mode.
     * @param queueId For Edit mode, pass the queue_id.
     * @return A new instance of fragment CreateTokenFragment.
     */
    public static CreateTokenFragment newInstance(int mode, int queueId, int serviceId) {
        CreateTokenFragment fragment = new CreateTokenFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, mode);
        args.putInt(ARG_PARAM2, queueId);
        args.putInt(ARG_PARAM3, serviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (getArguments() != null) {
            mode = getArguments().getInt(ARG_PARAM1);
            queueId = getArguments().getInt(ARG_PARAM2);
            serviceId = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_token, container, false);
        View view = mBinding.getRoot();
        this.getDialog().setCanceledOnTouchOutside(true);
        mBinding.setMode(mode);

        //Viewmodels
        queueViewModel = new QueueViewModel(((MainActivity) context).getApplication());
        servicePrimaryViewModel = new ServicePrimaryViewModel(((MainActivity) context).getApplication());
        if (mode == EDIT_TOKEN_MODE) {
            queueViewModel.getQueue(queueId).observe(this, queueObserver);
        } else {
            queueViewModel.getLatestToken().observe(this, queueTokenObserver);
        }
        servicePrimaryViewModel.getService(serviceId).observe(this, serviceObserver);

        //Onclicklisteners
        mBinding.btnCreateToken.setOnClickListener(onClickListener);
        return view;
    }

    public void setServicePrimaryModel(ServicePrimaryModel servicePrimaryModel) {
        this.servicePrimaryModel = servicePrimaryModel;
        mBinding.setService(servicePrimaryModel);
    }

    public void setQueueModel(QueueModel queueModel) {
        this.queueModel = queueModel;
        mBinding.setQueue(queueModel);
    }

    private boolean validate() {
        boolean isValid = true;
        if (mBinding.etName.getText().toString().isEmpty()) {
            isValid = false;
            mBinding.etName.setError(context.getString(R.string.required_field));
        }
        if (mBinding.etPhone.getText().toString().isEmpty()) {
            isValid = false;
            mBinding.etPhone.setError(context.getString(R.string.required_field));
        }
        return isValid;
    }
}
