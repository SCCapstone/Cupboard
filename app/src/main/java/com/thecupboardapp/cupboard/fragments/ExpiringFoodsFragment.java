package com.thecupboardapp.cupboard.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.thecupboardapp.cupboard.models.FoodItem;
import com.thecupboardapp.cupboard.R;

import java.util.List;

/**
 * Created by Jacob Strom on 2/13/2018.
 */

public class ExpiringFoodsFragment extends Fragment {

    private List<FoodItem> mFoods;
    private TextView mNextExpiring;
    /*
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)

            /*View view= inflater.inflate(R.layout.dashboard_expriring_fragment, container, false);
            UserData.get(getActivity()).getFoodsFromFirebase();
            mFoods = UserData.get(getActivity()).getFoodItems();
            //Collections.sort(mFoods);
            //FoodItem mExpFood1 = ;
            mNextExpiring = (TextView) view.findViewById(R.id.next_expiring);
            if (mFoods!=null) {
                Log.d("mFoods", "not null");
                FoodItem f = mFoods.get(0);
                for (FoodItem food: mFoods) {
                    Log.d("mFoods", food.getName());
                }
                mNextExpiring.setText(f.getName());
            }
            else {
                Log.d("mFoods", "mFoods equals null");
                //Log.d("mFoods", "UID = );
                String s = "foods doesn't exist";
                mNextExpiring.setText(s);
            }

            return view;
        }
    */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        ExpiringFoodsFragment expFragment = (ExpiringFoodsFragment) fm.findFragmentByTag("expFragment");
        if (expFragment == null) {
            expFragment = new ExpiringFoodsFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.expiring_foods_fragment_container, expFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        //expFragment.getMapAsync(callback);
    }
/*public void onDestroyView() {
        super.onDestroyView();
        ExpiringFoodsFragment f = (ExpiringFoodsFragment) getFragmentManager()
                .findFragmentById(R.id.expiring_foods_fragment);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
    }
    /*public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().setTitle(R.string.);
        mNextExpiring = (TextView) getActivity().findViewById(R.id.next_expiring);
        mNextExpiring.setText("change");
    }
    /*public void setNextExpiring(){
        mNextExpiring = (TextView) getActivity().findViewById(R.id.next_expiring);
        mNextExpiring.setText("change");
    }

    //@Override
    /*public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFoods = UserData.get(getActivity()).getFoodItems();
        mNextExpiring = (TextView) getActivity().findViewById(R.id.next_expiring);
        //Collections.sort(mFoods);
        //FoodItem mExpFood1 = ;
        //final TextView mNextExpiring = (TextView) getActivity().findViewById(R.id.next_expiring);
        //Log.d("createView", mFoods.get(0).getName());
        //mNextExpiring.setText("change");
    }

    @Override
    public void onResume() {
        super.onResume();
        //mNextExpiring.setText("change");
    }

    /*public FoodItem NextThreeExpiring() {
        mFoods = UserData.get(getActivity()).getFoodItems();
        Collections.sort(mFoods);


    }*/
}
