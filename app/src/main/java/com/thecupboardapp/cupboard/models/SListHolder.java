package com.thecupboardapp.cupboard.models;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.activities.SListEditActivity;
import com.thecupboardapp.cupboard.database.Repository;
import com.thecupboardapp.cupboard.models.SList;

import io.reactivex.schedulers.Schedulers;

/**
 * Created by Kyle on 3/9/2018.
 */

public class SListHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "SListHolder";
    private SList mSList;
    private TextView mTitleTextView;

    public SListHolder(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.shopping_list_title_holder, parent, false));

        mTitleTextView = itemView.findViewById(R.id.shopping_list_title);
    }

    public void bind(SList sList) {
        mSList = sList;
        mTitleTextView.setText(sList.getName());
        setClickListeners();
    }

    private void setClickListeners(){
        mTitleTextView.setOnClickListener(view -> {
            Intent intent = SListEditActivity.newIntent(view.getContext(), mSList.getId());
            view.getContext().startActivity(intent);
        });

        mTitleTextView.setOnLongClickListener(view -> {
            // String name = mTitleTextView.getText().toString();
            // String[] options = {"option 1"};
            //
            // AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            // builder.setTitle(String.format("\"%s\" Options", name));
            // builder.setItems(options, new DialogInterface.OnClickListener() {
            //     @Override
            //     public void onClick(DialogInterface dialog, int which) {
            //         Log.d(TAG, "onClick:");
            //     }
            // });
            // builder.show();

            Repository.getDatabase(mTitleTextView.getContext())
                    .sListDao()
                    .getListById(mSList.getId())
                    .subscribeOn(Schedulers.io())
                    .subscribe(sList -> {
                Log.d(TAG, sList.toString());
            });


            return true;
        });
    }
}
