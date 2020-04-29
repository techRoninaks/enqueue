package com.roninaks.enqueue.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.roninaks.enqueue.R;
import com.roninaks.enqueue.adapters.RvServicesAdapter;
import com.roninaks.enqueue.databinding.ActivityServicesBinding;
import com.roninaks.enqueue.helpers.StringHelper;
import com.roninaks.enqueue.models.ServicePrimaryModel;
import com.roninaks.enqueue.models.UserModel;
import com.roninaks.enqueue.viewmodels.ServicePrimaryViewModel;
import com.roninaks.enqueue.viewmodels.UserViewModel;

import java.util.List;

public class ServicesActivity extends AppCompatActivity {

    public static String INTENT_PARAM_USERID = "user_id";
    private final String VISIBILITY_PARAM_SERVICES = "services";
    private ServicePrimaryViewModel servicePrimaryViewModel;
    private ActivityServicesBinding mBinding;
    private UserViewModel userViewModel;
    private int userId = -1;
    private RvServicesAdapter adapter;
    private Observer servicePrimaryModelObserver;

    //Listener for Services List
    RvServicesAdapter.OnItemClickListener onItemClickListener = new RvServicesAdapter.OnItemClickListener() {
        @Override
        public void onClick(View v, final ServicePrimaryModel servicePrimaryModel) {
            switch (v.getId()){
                case R.id.img_more:{
                    PopupMenu popupMenu = new PopupMenu(ServicesActivity.this, v);
                    popupMenu.getMenuInflater().inflate(R.menu.services_popup_menu, popupMenu.getMenu());
                    switch (servicePrimaryModel.getStatus()){
                        case "active":
                            popupMenu.getMenu().removeItem(R.id.start_service);
                            popupMenu.getMenu().removeItem(R.id.resume_service);
                            popupMenu.getMenu().removeItem(R.id.delete_service);
                            break;
                        case "inactive":
                            popupMenu.getMenu().removeItem(R.id.stop_service);
                            popupMenu.getMenu().removeItem(R.id.resume_service);
                            popupMenu.getMenu().removeItem(R.id.break_service);
                            break;
                        case "break":
                            popupMenu.getMenu().removeItem(R.id.start_service);
                            popupMenu.getMenu().removeItem(R.id.break_service);
                            break;
                    }
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.start_service:
                                case R.id.resume_service:
                                    servicePrimaryModel.setStatus("active");
                                    servicePrimaryViewModel.update(servicePrimaryModel);
                                    break;
                                case R.id.stop_service:
                                    servicePrimaryModel.setStatus("inactive");
                                    servicePrimaryViewModel.update(servicePrimaryModel);
                                    break;
                                case R.id.break_service:
                                    servicePrimaryModel.setStatus("break");
                                    servicePrimaryViewModel.update(servicePrimaryModel);
                                    break;
                                case R.id.delete_service:
                                    servicePrimaryViewModel.delete(servicePrimaryModel);
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
                break;
                default:{
                    Intent intent = new Intent(ServicesActivity.this, MainActivity.class);
                    intent.putExtra(MainActivity.INTENT_PARAM_SERVICEID, servicePrimaryModel.getServiceId());
                    intent.putExtra(MainActivity.INTENT_PARAM_USERID, userId);
                    startActivity(intent);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        userId = intent.getIntExtra(INTENT_PARAM_USERID, -1);
        adapter = new RvServicesAdapter(this);
        //Goto login if user id does not exist
        performChecks();
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_services);
        mBinding.executePendingBindings();

        //Viewmodels
        servicePrimaryViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(ServicePrimaryViewModel.class);
        userViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(UserViewModel.class);
        setupObservers();

        //Recyclerview
        mBinding.rvServices.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvServices.setHasFixedSize(true);
        mBinding.rvServices.setAdapter(adapter);
        adapter.setListener(onItemClickListener);

        //SearchView
        mBinding.svSearch.setSubmitButtonEnabled(false);
        mBinding.svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                servicePrimaryViewModel.filterServices("%" + query + "%").observe(ServicesActivity.this, servicePrimaryModelObserver);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 2) {
                    servicePrimaryViewModel.filterServices("%" + newText + "%").observe(ServicesActivity.this, servicePrimaryModelObserver);
                }
                return false;
            }
        });
        mBinding.svSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                servicePrimaryViewModel.getServicePrimaryModels().observe(ServicesActivity.this, servicePrimaryModelObserver);
                mBinding.svSearch.clearFocus();
                return false;
            }
        });

        //Image More Button
        mBinding.imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ServicesActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.services_main_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        servicePrimaryViewModel.deleteAll();
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

    }
    private void setupObservers() {
        //ServicePrimaryModel Observer
        servicePrimaryModelObserver = new Observer<List<ServicePrimaryModel>>() {
            @Override
            public void onChanged(List<ServicePrimaryModel> servicePrimaryModels) {
                if (servicePrimaryModels.size() > 0) {
                    toggleVisibility(VISIBILITY_PARAM_SERVICES, true);
                    adapter.setServicePrimaryModels(servicePrimaryModels);
                }else{
                    toggleVisibility(VISIBILITY_PARAM_SERVICES, false);
                }
            }
        };
        servicePrimaryViewModel.getServicePrimaryModels().observe(this, servicePrimaryModelObserver);

        //UserViewModel Observer
        userViewModel.getUser(userId).observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                if (userModel != null) {
                    mBinding.setUser(userModel);
                }
            }
        });
    }

    private void toggleVisibility(String parameter, boolean visible){
        switch (parameter){
            case "services":{
                if(visible){
                    mBinding.tvSearchNoResults.setVisibility(View.GONE);
                    mBinding.rvServices.setVisibility(View.VISIBLE);
                }else{
                    mBinding.tvSearchNoResults.setVisibility(View.VISIBLE);
                    mBinding.rvServices.setVisibility(View.GONE);
                }
            }
        }
    }

    private void performChecks(){
        if(!isLoggedIn()){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private boolean isLoggedIn(){
        if (userId < 0) {
            SharedPreferences sharedPreferences = getSharedPreferences(StringHelper.SHARED_PREFERENCE_KEY, 0);
            return (userId = sharedPreferences.getInt(StringHelper.SHARED_PREFERENCE_USER_ID, -1)) >= 0;
        }
        return true;
    }
}
