package com.roninaks.enqueue.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.roninaks.enqueue.R;
import com.roninaks.enqueue.databinding.ActivityMainBinding;
import com.roninaks.enqueue.fragments.CreateTokenFragment;
import com.roninaks.enqueue.fragments.ManageFragment;
import com.roninaks.enqueue.fragments.ServiceIndividualQueue;
import com.roninaks.enqueue.fragments.ServiceUsers;
import com.roninaks.enqueue.helpers.StringHelper;
import com.roninaks.enqueue.models.ServicePrimaryModel;
import com.roninaks.enqueue.viewmodels.ServicePrimaryViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String INTENT_PARAM_SERVICEID = "service_id";
    public static String INTENT_PARAM_USERID = "user_id";
    public static String NAVIGATION_FRAGMENT_TAG_SERVICE = "service";
    public static String NAVIGATION_FRAGMENT_TAG_QUEUE = "queue";
    public static String NAVIGATION_FRAGMENT_TAG_MANAGE = "manage";
    public static String NAVIGATION_FRAGMENT_TAG_DIALOG = "create_token";
    private boolean isFirst = true;
    private ServicePrimaryViewModel servicePrimaryViewModel;
    private ActivityMainBinding mBinding;
    private int serviceId = -1;
    private int userId = -1;
    private AlertDialog alertDialog;

    /***
     * Observer for the current service
     */
    Observer servicePrimaryModelObserver = new Observer<ServicePrimaryModel>() {
        @Override
        public void onChanged(ServicePrimaryModel servicePrimaryModel) {
            mBinding.setService(servicePrimaryModel);
        }
    };

    /***
     * Observer for list of services
     */
    Observer servicePrimaryModelsObserver = new Observer<List<ServicePrimaryModel>>() {
        @Override
        public void onChanged(final List<ServicePrimaryModel> servicePrimaryModels) {
            List<String> itemsList = new ArrayList<>();
            for (ServicePrimaryModel servicePrimaryModel : servicePrimaryModels) {
                itemsList.add(servicePrimaryModel.getServiceName() + " (" + servicePrimaryModel.getDescription() + ")");
            }
            CharSequence[] items = itemsList.toArray(new CharSequence[itemsList.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (serviceId != servicePrimaryModels.get(which).getServiceId()) {
                        Fragment currentFragment = getCurrentFragment();
                        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                            if (fragment != null) {
                                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                            }
                        }
                        MainActivity.this.setServiceId(servicePrimaryModels.get(which).getServiceId());
                        servicePrimaryViewModel.getService(serviceId).observe(MainActivity.this, servicePrimaryModelObserver);
                        if (currentFragment == null || currentFragment instanceof ServiceIndividualQueue) {
                            mBinding.tvNavService.performClick();
                        } else if (currentFragment instanceof ServiceUsers) {
                            mBinding.tvNavQueue.performClick();
                        }
                    }
                }
            });
            alertDialog = builder.create();
        }
    };

    private View.OnClickListener onNavigationItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_container_drop_down:
                    alertDialog.show();
                    break;
                case R.id.img_back:
                    finish();
                    break;
                case R.id.img_add: {
                    CreateTokenFragment fragment = CreateTokenFragment.newInstance(CreateTokenFragment.CREATE_TOKEN_MODE, 0, serviceId);
                    initFragment(fragment, NAVIGATION_FRAGMENT_TAG_DIALOG);
                }
                break;
                case R.id.tv_nav_service: {
                    ServiceIndividualQueue fragment = ServiceIndividualQueue.newInstance(serviceId, userId);
                    initFragment(fragment, NAVIGATION_FRAGMENT_TAG_SERVICE);
                    toggleSelection(v.getId());
                }
                break;
                case R.id.tv_nav_queue: {
                    ServiceUsers fragment = ServiceUsers.newInstance(serviceId, userId);
                    initFragment(fragment, NAVIGATION_FRAGMENT_TAG_QUEUE);
                    toggleSelection(v.getId());
                }
                break;
                case R.id.tv_nav_manage: {
                    ManageFragment fragment = ManageFragment.newInstance(serviceId);
                    initFragment(fragment, NAVIGATION_FRAGMENT_TAG_MANAGE);
                    toggleSelection(v.getId());
                }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        serviceId = intent.getIntExtra(INTENT_PARAM_SERVICEID, -1);
        userId = intent.getIntExtra(INTENT_PARAM_USERID, -1);
        performChecks();

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.executePendingBindings();

        //On click listeners
        mBinding.tvNavService.setOnClickListener(onNavigationItemClickListener);
        mBinding.tvNavQueue.setOnClickListener(onNavigationItemClickListener);
        mBinding.tvNavManage.setOnClickListener(onNavigationItemClickListener);
        mBinding.rlContainerDropDown.setOnClickListener(onNavigationItemClickListener);
        mBinding.tvNavService.performClick();
        mBinding.imgAdd.setOnClickListener(onNavigationItemClickListener);
        mBinding.imgBack.setOnClickListener(onNavigationItemClickListener);

        //Viewmodels and observers
        servicePrimaryViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(ServicePrimaryViewModel.class);
        servicePrimaryViewModel.getService(serviceId).observe(this, servicePrimaryModelObserver);
        servicePrimaryViewModel.getServicePrimaryModels().observe(this, servicePrimaryModelsObserver);
    }

    /***
     * Perform required checks
     */
    private void performChecks() {
        if (!isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        if (serviceId < 0) {
            startActivity(new Intent(this, ServicesActivity.class));
            finish();
        }
    }

    /***
     * Checks whether a user is logged in
     * @return True if a user is logged in. False if no users are logged in.
     */
    private boolean isLoggedIn() {
        if (userId < 0) {
            SharedPreferences sharedPreferences = getSharedPreferences(StringHelper.SHARED_PREFERENCE_KEY, 0);
            return (userId = sharedPreferences.getInt(StringHelper.SHARED_PREFERENCE_USER_ID, -1)) >= 0;
        }
        return true;
    }

    public void toggleSelection(int selection) {
        clearSelection();
        isFirst = false;
        TextView tv = null;
        switch (selection) {
            case R.id.tv_nav_service:
                tv = mBinding.tvNavService;
                break;
            case R.id.tv_nav_queue:
                tv = mBinding.tvNavQueue;
                break;
            case R.id.tv_nav_manage:
                tv = mBinding.tvNavManage;
                break;
        }
        if (tv != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                tv.setTextColor(getColor(R.color.colorTextWhitePrimary));
//            }else{
//                tv.setTextColor(getResources().getColor(R.color.colorTextWhitePrimary));
//            }
            tv.setTextColor(ContextCompat.getColor(this, R.color.colorTextWhitePrimary));
            tv.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    public void clearSelection() {
        //Set Color
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            mBinding.tvNavService.setTextColor(getColor(R.color.colorTextWhiteSecondary));
//            mBinding.tvNavManage.setTextColor(getColor(R.color.colorTextWhiteSecondary));
//            mBinding.tvNavQueue.setTextColor(getColor(R.color.colorTextWhiteSecondary));
//
//        }else{
//            mBinding.tvNavService.setTextColor(getResources().getColor(R.color.colorTextWhiteSecondary));
//            mBinding.tvNavManage.setTextColor(getResources().getColor(R.color.colorTextWhiteSecondary));
//            mBinding.tvNavQueue.setTextColor(getResources().getColor(R.color.colorTextWhiteSecondary));
//
//        }
        //Set Typeface
        mBinding.tvNavService.setTypeface(Typeface.DEFAULT);
        mBinding.tvNavManage.setTypeface(Typeface.DEFAULT);
        mBinding.tvNavQueue.setTypeface(Typeface.DEFAULT);
        //Set Color
        mBinding.tvNavQueue.setTextColor(ContextCompat.getColor(this, R.color.colorTextWhiteSecondary));
        mBinding.tvNavManage.setTextColor(ContextCompat.getColor(this, R.color.colorTextWhiteSecondary));
        mBinding.tvNavQueue.setTextColor(ContextCompat.getColor(this, R.color.colorTextWhiteSecondary));
    }

    public void initFragment(Fragment fragment) {
        initFragment(fragment, "");
    }

    public void initFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, tag);
        if (!isFirst) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();
    }

    public void initFragment(DialogFragment fragment, String tag){
        fragment.show(getSupportFragmentManager(), tag);
    }

    public void initFragment(BottomSheetDialogFragment fragment, String tag){
        fragment.show(getSupportFragmentManager(), tag);
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();
        if (backStackCount > 0) {
            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            return fragmentManager.findFragmentByTag(fragmentTag);
        }
        return null;
    }
}
