package com.example.personalfinancetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalfinancetracker.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;




public class DashboardFragment extends Fragment {


    //Floating button
    private FloatingActionButton fab_main;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;


    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    private boolean isOpen=false;
    private Animation FadOpen,FadClose,Rob,Rof;

    //Dashboard income and expense result

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;
    private TextView totalBalanceResult;

    static int totalsumexpense=0;
    static int totalsumincome=0;
    static int balance;

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //recycler view

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview=inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();

        if(mAuth.getCurrentUser()!=null) {

            String uid = mUser.getUid();

            mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
            mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        }


        fab_main=myview.findViewById(R.id.fb_main_lus_btn);
        fab_income_btn=myview.findViewById(R.id.income_ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_ft_btn);
        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_text);

        //Tatal inc and exp

        totalIncomeResult=myview.findViewById(R.id.income_set_result);
        totalExpenseResult=myview.findViewById(R.id.expense_set_result);
        totalBalanceResult=myview.findViewById(R.id.balance_set_result);

        //Recycler

        mRecyclerIncome=myview.findViewById(R.id.recycler_income);
        mRecyclerExpense=myview.findViewById(R.id.recycler_expense);

        FadOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fab_open);
        FadClose=AnimationUtils.loadAnimation(getActivity(),R.anim.fab_close);
        Rob=AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_backward);
        Rof=AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_forward);


//        barChart=myview.findViewById(R.id.bar_chart);
//        ValueEventListener event2=new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                getBarEntries(snapshot);
//                barDataSet=new BarDataSet(barEntries,"Expenses");
//                barData=new BarData(barDataSet);
//                barChart.setData(barData);
//                barChart.getDescription().setText("Expenses Per Day");
//                XAxis xval=barChart.getXAxis();
//                xval.setDrawLabels(true);
//                xval.setValueFormatter(new IndexAxisValueFormatter(date));
//                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//                barDataSet.setValueTextColor(Color.BLACK);
//                barDataSet.setValueTextSize(16f);
//                barDataSet.notifyDataSetChanged();
//                barChart.notifyDataSetChanged();
//                barChart.invalidate();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//        mExpenseDatabase.addListenerForSingleValueEvent(event2);




        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData();

                if(isOpen){
                    fab_main.startAnimation(Rof);
                    fab_income_btn.startAnimation(FadClose);

                    fab_expense_btn.startAnimation(FadClose);

                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadClose);
                    fab_expense_txt.startAnimation(FadClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen=false;

                }

                else{
                    fab_main.startAnimation(Rob);
                    fab_income_btn.startAnimation(FadOpen);

                    fab_expense_btn.startAnimation(FadOpen);

                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadOpen);
                    fab_expense_txt.startAnimation(FadOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;

                }

            }
        });

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                totalsumexpense=0;
                for(DataSnapshot mysnap:snapshot.getChildren()){

                    Data data=mysnap.getValue(Data.class);
                    totalsumexpense+=data.getAmount();

                    String strResult=String.valueOf(totalsumexpense);

                    totalExpenseResult.setText(strResult+".00");

                }
                balance=totalsumincome-totalsumexpense;
                String strBalance=String.valueOf(balance);
                totalBalanceResult.setText(strBalance+".00");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //calc total income

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                totalsumincome=0;
                for(DataSnapshot mysnap:snapshot.getChildren()){

                    Data data=mysnap.getValue(Data.class);
                    totalsumincome+=data.getAmount();

                    String stResult=String.valueOf(totalsumincome);

                    totalIncomeResult.setText(stResult+".00");

                }
                balance=totalsumincome-totalsumexpense;
                if(balance>0.05*totalsumincome && balance<=0.1*totalsumincome){
                    androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                    builder.setTitle("Less than 10% balance remaining!");
                    builder.setMessage("Need to control your expenses.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finishAffinity();
                            //startActivity(new Intent(getActivity(),DashboardFragment.class));
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                else if(balance>0.0*totalsumincome && balance<=0.05*totalsumincome)
                {androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                    builder.setTitle("Less than 5% balance remaining!");
                    builder.setMessage("Need to control your expenses.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finishAffinity();
                            //startActivity(new Intent(getActivity(),DashboardFragment.class));
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                String strBalance=String.valueOf(balance);
                totalBalanceResult.setText(strBalance+".00");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler

        LinearLayoutManager layoutManagerIncome =new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense =new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);

        return myview;
    }

    private void ftAnimation(){
        if(isOpen){
            fab_main.startAnimation(Rof);
            fab_income_btn.startAnimation(FadClose);

            fab_expense_btn.startAnimation(FadClose);

            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadClose);
            fab_expense_txt.startAnimation(FadClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen=false;

        }

        else{
            fab_main.startAnimation(Rob);
            fab_income_btn.startAnimation(FadOpen);

            fab_expense_btn.startAnimation(FadOpen);

            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadOpen);
            fab_expense_txt.startAnimation(FadOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen=true;

        }


    }

    private void addData(){
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInsert();

            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                expenseDataInsert();
            }
        });


    }

    public void incomeDataInsert(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myviewm=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myviewm);
        AlertDialog dialog=mydialog.create();

        EditText edtAmount=myviewm.findViewById(R.id.amount_edt);
        EditText edtType=myviewm.findViewById(R.id.type_edt);
        EditText edtNote=myviewm.findViewById(R.id.note_edt);

        Button btnSave=myviewm.findViewById(R.id.btnSave);
        Button btnCancel=myviewm.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type=edtType.getText().toString().trim();
                String amount=edtAmount.getText().toString().trim();
                String note=edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(amount)){
                    edtAmount.setError("Required Field..");
                    return;
                }
                int ouramountint=Integer.parseInt(amount);
                if(TextUtils.isEmpty(type)){
                    edtType.setError("Required Field..");
                    return;
                }
                if(TextUtils.isEmpty(note)){
                    edtNote.setError("Required Field..");
                    return;
                }

                if(mAuth.getCurrentUser()!=null) {
                    String id = mIncomeDatabase.push().getKey();
                    String mDate = DateFormat.getDateInstance().format(new Date());
                    Data data = new Data(ouramountint, type, note, id, mDate);

                    mIncomeDatabase.child(id).setValue(data);
                    Toast.makeText(getActivity(),"Data ADDED",Toast.LENGTH_SHORT).show();
                }

                ftAnimation();
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void expenseDataInsert(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myviewm=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myviewm);
        final AlertDialog dialog=mydialog.create();
        dialog.setCancelable(false);

        EditText edtAmount=myviewm.findViewById(R.id.amount_edt);
        EditText edtType=myviewm.findViewById(R.id.type_edt);
        EditText edtNote=myviewm.findViewById(R.id.note_edt);

        Button btnSave=myviewm.findViewById(R.id.btnSave);
        Button btnCancel=myviewm.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type=edtType.getText().toString().trim();
                String amount=edtAmount.getText().toString().trim();
                String note=edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(amount)){
                    edtAmount.setError("Required Field..");
                    return;
                }
                int ouramountinte=Integer.parseInt(amount);
                if(TextUtils.isEmpty(type)){
                    edtType.setError("Required Field..");
                    return;
                }
                if(TextUtils.isEmpty(note)){
                    edtNote.setError("Required Field..");
                    return;
                }
                if(mAuth.getCurrentUser()!=null && balance!=0.0 && balance>0.0) {
                    String id = mExpenseDatabase.push().getKey();
                    String mDate = DateFormat.getDateInstance().format(new Date());
                    Data data = new Data(ouramountinte, type, note, id, mDate);

                    mExpenseDatabase.child(id).setValue(data);
                    Toast.makeText(getActivity(),"Data ADDED",Toast.LENGTH_SHORT).show();
                }
                ftAnimation();
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> incomeOptions = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mIncomeDatabase, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(incomeOptions) {
            @Override
            protected void onBindViewHolder(IncomeViewHolder incomeViewHolder, int position, Data model) {
                incomeViewHolder.setIncomeType(model.getType());
                incomeViewHolder.setIncomeAmount(model.getAmount());
                incomeViewHolder.setIncomeDate(model.getDate());
            }

            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income, parent, false);
                return new IncomeViewHolder(view);
            }
        };

        mRecyclerIncome.setAdapter(incomeAdapter);
        incomeAdapter.startListening();

        FirebaseRecyclerOptions<Data> expenseOptions = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mExpenseDatabase, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(expenseOptions) {
            @Override
            protected void onBindViewHolder(ExpenseViewHolder expenseViewHolder, int position, Data model) {
                expenseViewHolder.setExpenseType(model.getType());
                expenseViewHolder.setExpenseAmount(model.getAmount());
                expenseViewHolder.setExpenseDate(model.getDate());
            }

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense, parent, false);
                return new ExpenseViewHolder(view);
            }
        };

        mRecyclerExpense.setAdapter(expenseAdapter);
        expenseAdapter.startListening();
    }

    //For Income Data
    public static class IncomeViewHolder extends RecyclerView.ViewHolder{

        View mIncomeView;

        public IncomeViewHolder(View itemView)
        {
            super(itemView);
            mIncomeView=itemView;
        }
        public void setIncomeType(String type)
        {
            TextView mtype=mIncomeView.findViewById(R.id.type_Income_ds);
            mtype.setText(type);
        }
        public void setIncomeAmount(int amount)
        {
            TextView mAmount=mIncomeView.findViewById(R.id.amount_Income_ds);
            String strAmount= String.valueOf(amount);
            mAmount.setText(strAmount);
        }
        public void setIncomeDate(String date)
        {
            TextView mDate=mIncomeView.findViewById(R.id.date_Income_ds);
            mDate.setText(date);
        }
    }

    //For expense data
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseView;

        public ExpenseViewHolder(View itemView)
        {
            super(itemView);
            mExpenseView=itemView;
        }
        public void setExpenseType(String type)
        {
            TextView mtype=mExpenseView.findViewById(R.id.type_Expense_ds);
            mtype.setText(type);
        }
        public void setExpenseAmount(int amount)
        {
            TextView mAmount=mExpenseView.findViewById(R.id.amount_Expense_ds);
            String strAmount= String.valueOf(amount);
            mAmount.setText(strAmount);
        }
        public void setExpenseDate(String date)
        {
            TextView mDate=mExpenseView.findViewById(R.id.date_Expense_ds);
            mDate.setText(date);
        }
    }


}