package com.roninaks.enqueue.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roninaks.enqueue.R;
import com.roninaks.enqueue.activities.MainActivity;
import com.roninaks.enqueue.adapters.RxQueuesAdapter;
import com.roninaks.enqueue.databinding.FragmentServiceIndividualQueueBinding;
import com.roninaks.enqueue.models.QueueModel;
import com.roninaks.enqueue.models.ServicePrimaryModel;
import com.roninaks.enqueue.viewmodels.QueueViewModel;
import com.roninaks.enqueue.viewmodels.ServicePrimaryViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ServiceIndividualQueue.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ServiceIndividualQueue#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceIndividualQueue extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "service_id";
    private static final String ARG_PARAM2 = "user_id";

    private ServicePrimaryModel currentServicePrimaryModel;
    FragmentServiceIndividualQueueBinding mBinding;
    private int serviceId = -1;
    private int userId = -1;
    private int currentPosition = 0;
    private String currentStatus;
    private QueueModel currentQueueModel;
    private int queueSize;
    private Context context;
    private OnFragmentInteractionListener mListener;
    private ServicePrimaryViewModel servicePrimaryViewModel;
    private QueueViewModel queueViewModel;
    private RxQueuesAdapter queuesAdapter;

    private Observer observerService = new Observer<ServicePrimaryModel>() {
        @Override
        public void onChanged(ServicePrimaryModel servicePrimaryModel) {
            if (mBinding != null && servicePrimaryModel != null) {
                mBinding.setServiceData(servicePrimaryModel);
                queuesAdapter.setServicePrimaryModel(servicePrimaryModel);
                currentServicePrimaryModel = servicePrimaryModel;
                if(servicePrimaryModel.getWaitingCount() <= 0){
                    setEnabledButtons(false);
                }
            }
        }
    };

    private Observer observerQueue = new Observer<List<QueueModel>>() {
        @Override
        public void onChanged(List<QueueModel> queueModels) {
            queuesAdapter.setQueueModels(queueModels);
            queueSize = queueModels.size();
            queueLogic(queueModels);
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_delete_token: {
                    currentQueueModel.setStatus(context.getString(R.string.queue_status_cancelled));
                    queueViewModel.update(currentQueueModel);
                    currentServicePrimaryModel.setWaitingCount(currentServicePrimaryModel.getWaitingCount() - 1);
                    servicePrimaryViewModel.update(currentServicePrimaryModel);
                }
                break;
                case R.id.btn_not_present: {
                    if (currentStatus.equals(context.getString(R.string.queue_status_repeat_one))) {
                        currentQueueModel.setStatus(context.getString(R.string.queue_status_repeat_two));
                    } else if (currentStatus.equals(context.getString(R.string.queue_status_repeat_two))) {
                        currentQueueModel.setStatus(context.getString(R.string.queue_status_cancelled));
                        currentServicePrimaryModel.setWaitingCount(currentServicePrimaryModel.getWaitingCount() - 1);
                    } else if (currentStatus.equals(context.getString(R.string.queue_status_pending))) {
                        currentQueueModel.setStatus(context.getString(R.string.queue_status_repeat_one));
                    }
                    queueViewModel.update(currentQueueModel);
                    servicePrimaryViewModel.update(currentServicePrimaryModel);
                }
                break;
                case R.id.btn_next_token: {
                    currentQueueModel.setStatus(context.getString(R.string.queue_status_completed));
                    currentQueueModel.setServicedTime(SystemClock.elapsedRealtime() - currentQueueModel.getStartTime());
                    queueViewModel.update(currentQueueModel);
                    currentServicePrimaryModel.setServed(currentServicePrimaryModel.getServed() + 1);
                    currentServicePrimaryModel.setWaitingCount(currentServicePrimaryModel.getWaitingCount() - 1);
                    servicePrimaryViewModel.update(currentServicePrimaryModel);
                }
                break;
                case R.id.tv_stop_service: {
                    currentServicePrimaryModel.setStatus(context.getString(R.string.service_status_inactive));
                    servicePrimaryViewModel.update(currentServicePrimaryModel);
                }
                break;
                case R.id.tv_take_break: {
                    currentServicePrimaryModel.setStatus(context.getString(R.string.service_status_break));
                    servicePrimaryViewModel.update(currentServicePrimaryModel);
                }
                break;
                case R.id.ll_container_power: {
                    currentServicePrimaryModel.setStatus(context.getString(R.string.service_status_active));
                    servicePrimaryViewModel.update(currentServicePrimaryModel);
                }
                break;
            }
        }
    };

    public ServiceIndividualQueue() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Service Id.
     * @param param2 User Id.
     * @return A new instance of fragment ServiceIndividualQueue.
     */
    public static ServiceIndividualQueue newInstance(int param1, int param2) {
        ServiceIndividualQueue fragment = new ServiceIndividualQueue();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        currentStatus = context.getString(R.string.queue_status_ongoing);
        if (getArguments() != null) {
            serviceId = getArguments().getInt(ARG_PARAM1);
            userId = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_service_individual_queue, container, false);
        View view = mBinding.getRoot();

        //Buttons Default State
        setEnabledButtons(false);

        //Setup click listeners
        mBinding.btnDeleteToken.setOnClickListener(onClickListener);
        mBinding.btnNotPresent.setOnClickListener(onClickListener);
        mBinding.btnNextToken.setOnClickListener(onClickListener);
        mBinding.tvStopService.setOnClickListener(onClickListener);
        mBinding.tvTakeBreak.setOnClickListener(onClickListener);
        mBinding.llContainerPower.setOnClickListener(onClickListener);

        //Viewpager
        queuesAdapter = new RxQueuesAdapter(context, RxQueuesAdapter.INFLATE_TYPE_HORIZONTAL);
        mBinding.vpTokens.setAdapter(queuesAdapter);
        mBinding.vpTokens.setClipToPadding(false);
        mBinding.vpTokens.setClipChildren(false);
        mBinding.vpTokens.setOffscreenPageLimit(3);
        mBinding.vpTokens.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer pageTransformer = new CompositePageTransformer();
        pageTransformer.addTransformer(new MarginPageTransformer(40));
        pageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        mBinding.vpTokens.setPageTransformer(pageTransformer);

        //Viewmodel setup
        servicePrimaryViewModel = new ViewModelProvider.AndroidViewModelFactory(((MainActivity) context).getApplication()).create(ServicePrimaryViewModel.class);
        servicePrimaryViewModel.getService(serviceId).observe(this, observerService);
        queueViewModel = new ViewModelProvider.AndroidViewModelFactory(((MainActivity) context).getApplication()).create(QueueViewModel.class);
        queueViewModel.getServiceQueues(serviceId).observe(this, observerQueue);

        //Search
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void setCurrentServicePrimaryModel(ServicePrimaryModel currentServicePrimaryModel) {
        this.currentServicePrimaryModel = currentServicePrimaryModel;
        mBinding.setServiceData(currentServicePrimaryModel);
    }

    private void queueLogic(List<QueueModel> queueModels) {
        //Scroll to position
        queueScrollToPosition(queueModels);

    }

    private void queueScrollToPosition(List<QueueModel> queueModels) {
        int countRepeat = -1;
        int countFactor = 0;
        boolean flag = false;
        for (int i = 0; i < queueModels.size(); i++) {
            QueueModel queueModel = queueModels.get(i);
            if (queueModel.getStatus().toLowerCase().equals(context.getString(R.string.queue_status_ongoing))) {
                flag = true;
                mBinding.vpTokens.setCurrentItem(i);
                currentPosition = i;
                currentQueueModel = queueModel;
                setEnabledButtons(true);
                break;
            } else if (queueModel.getStatus().toLowerCase().equals(context.getString(R.string.queue_status_pending))) {
                flag = true;
                currentStatus = queueModel.getStatus();
                queueModel.setStatus(context.getString(R.string.queue_status_ongoing));
                queueViewModel.update(queueModel);
                break;
            } else if (queueModel.getStatus().toLowerCase().equals(context.getString(R.string.queue_status_repeat_one))) {
                countRepeat = i;
                countFactor = 1;
            } else if (queueModel.getStatus().toLowerCase().equals(context.getString(R.string.queue_status_repeat_two))) {
                countRepeat = i;
                countFactor = 2;
            }
            if ((countRepeat >= 0) && (i - countRepeat >= (countFactor * 5))) {
                flag = true;
                currentStatus = countFactor == 1 ? context.getString(R.string.queue_status_repeat_one) : context.getString(R.string.queue_status_repeat_two);
                queueModels.get(countRepeat).setStatus(context.getString(R.string.queue_status_ongoing));
                queueViewModel.update(queueModels.get(countRepeat));
                break;
            }
        }
        if (!flag && countRepeat >= 0) {
            QueueModel queueModel = queueModels.get(countRepeat);
            currentStatus = countFactor == 1 ? context.getString(R.string.queue_status_repeat_one) : context.getString(R.string.queue_status_repeat_two);
            queueModel.setStatus(context.getString(R.string.queue_status_ongoing));
            queueViewModel.update(queueModel);
        }
    }

    private void setEnabledButtons(boolean enabled){
        if(enabled){
            mBinding.btnNextToken.setEnabled(true);
            mBinding.btnNotPresent.setEnabled(true);
            mBinding.btnDeleteToken.setEnabled(true);
            mBinding.btnNextToken.setBackground(context.getDrawable(R.drawable.button_background_filled_primary));
            mBinding.btnNotPresent.setBackground(context.getDrawable(R.drawable.button_background_filled));
            mBinding.btnDeleteToken.setBackground(context.getDrawable(R.drawable.button_background_filled));
        }else{
            mBinding.btnNextToken.setEnabled(false);
            mBinding.btnNotPresent.setEnabled(false);
            mBinding.btnDeleteToken.setEnabled(false);
            mBinding.btnNextToken.setBackground(context.getDrawable(R.drawable.button_background_filled_disabled));
            mBinding.btnNotPresent.setBackground(context.getDrawable(R.drawable.button_background_filled_disabled_white));
            mBinding.btnDeleteToken.setBackground(context.getDrawable(R.drawable.button_background_filled_disabled_white));
        }
    }

}
