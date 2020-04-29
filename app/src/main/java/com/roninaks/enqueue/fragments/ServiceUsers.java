package com.roninaks.enqueue.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.roninaks.enqueue.R;
import com.roninaks.enqueue.activities.MainActivity;
import com.roninaks.enqueue.adapters.RxQueuesAdapter;
import com.roninaks.enqueue.databinding.FragmentServiceIndividualQueueBinding;
import com.roninaks.enqueue.databinding.FragmentServiceUsersBinding;
import com.roninaks.enqueue.models.QueueModel;
import com.roninaks.enqueue.models.ServicePrimaryModel;
import com.roninaks.enqueue.viewmodels.QueueViewModel;
import com.roninaks.enqueue.viewmodels.ServicePrimaryViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ServiceUsers.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ServiceUsers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceUsers extends Fragment {

    private ServicePrimaryModel servicePrimaryModel;

    FragmentServiceUsersBinding mBinding;
    private static final String ARG_PARAM1 = "service_id";
    private static final String ARG_PARAM2 = "user_id";

    private int serviceId = -1;
    private int userId = -1;
    private Context context;
    private ServiceIndividualQueue.OnFragmentInteractionListener mListener;
    private ServicePrimaryViewModel servicePrimaryViewModel;
    private QueueViewModel queueViewModel;
    private RxQueuesAdapter queuesAdapter;

    public ServiceUsers() {
        // Required empty public constructor
    }

    private Observer observerService = new Observer<ServicePrimaryModel>() {
        @Override
        public void onChanged(ServicePrimaryModel servicePrimaryModel) {
            if (mBinding != null && servicePrimaryModel != null) {
                mBinding.setServiceData(servicePrimaryModel);
                queuesAdapter.setServicePrimaryModel(servicePrimaryModel);
            }
        }
    };
    private Observer observerQueue = new Observer<List<QueueModel>>() {
        @Override
        public void onChanged(List<QueueModel> queueModels) {
            if (queueModels.size() > 0) {
                queuesAdapter.setQueueModels(queueModels);
                mBinding.rvQueue.setVisibility(View.VISIBLE);
                mBinding.tvSearchNoResults.setVisibility(View.GONE);
            } else {
                mBinding.rvQueue.setVisibility(View.GONE);
                mBinding.tvSearchNoResults.setVisibility(View.VISIBLE);
            }
        }
    };

    private RxQueuesAdapter.OnItemClickListener itemClickListener = new RxQueuesAdapter.OnItemClickListener() {
        @Override
        public void onClick(View v, final QueueModel queueModel) {
            switch (v.getId()) {
                case R.id.img_more: {
                    PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.getMenuInflater().inflate(R.menu.queue_popup_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.edit_queue:{
                                    CreateTokenFragment fragment = CreateTokenFragment.newInstance(CreateTokenFragment.EDIT_TOKEN_MODE, queueModel.getQueueId(), serviceId);
                                    ((MainActivity)context).initFragment(fragment, MainActivity.NAVIGATION_FRAGMENT_TAG_DIALOG);
                                    break;
                                }
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
                break;
            }
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Service Id.
     * @param param2 User Id.
     * @return A new instance of fragment ServiceUsers.
     */
    public static ServiceUsers newInstance(int param1, int param2) {
        ServiceUsers fragment = new ServiceUsers();
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
        if (getArguments() != null) {
            serviceId = getArguments().getInt(ARG_PARAM1);
            userId = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_service_users, container, false);
        View view = mBinding.getRoot();
        //Recyclerview
        queuesAdapter = new RxQueuesAdapter(context, RxQueuesAdapter.INFLATE_TYPE_VERTICAL);
        mBinding.rvQueue.setLayoutManager(new LinearLayoutManager(context));
        mBinding.rvQueue.setAdapter(queuesAdapter);
        queuesAdapter.setListener(itemClickListener);

        //Viewmodels
        servicePrimaryViewModel = new ViewModelProvider.AndroidViewModelFactory(((MainActivity) context).getApplication()).create(ServicePrimaryViewModel.class);
        servicePrimaryViewModel.getService(serviceId).observe(this, observerService);
        queueViewModel = new ViewModelProvider.AndroidViewModelFactory(((MainActivity) context).getApplication()).create(QueueViewModel.class);
        queueViewModel.getServiceQueues(serviceId).observe(this, observerQueue);

        //Search
        mBinding.llContainerSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.etSearch.getVisibility() == View.GONE) {
                    mBinding.imgSearch.setImageDrawable(context.getDrawable(R.drawable.ic_close));
                    mBinding.etSearch.setVisibility(View.VISIBLE);
                    mBinding.etSearch.requestFocus();
                } else {
                    if (!mBinding.etSearch.getText().toString().isEmpty()) {
                        mBinding.etSearch.setText("");
                    } else {
                        queueViewModel.getServiceQueues(serviceId).observe(ServiceUsers.this, observerQueue);
                        mBinding.imgSearch.setImageDrawable(context.getDrawable(R.drawable.ic_search));
                        mBinding.etSearch.setVisibility(View.GONE);
                        mBinding.etSearch.clearFocus();
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mBinding.etSearch.getWindowToken(), 0);
                    }
                }
            }
        });

        //Search Filter
        mBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 2) {
                    String searchKey = "%" + s.toString() + "%";
                    queueViewModel.filterQueue(searchKey, serviceId).observe(ServiceUsers.this, observerQueue);
                }
            }
        });
        return view;
    }

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
}
