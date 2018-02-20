package com.tabqy1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tabqy1.R;
import com.tabqy1.models.FeedbackResponse;
import com.tabqy1.models.QuestionList;

import java.util.ArrayList;
import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {

    private List<QuestionList> dataSet;
    private Context context ;
    public static final int QUESTION_TYPE_ONE = 1;
    public static final int QUESTION_TYPE_TWO = 2;
    public static final int QUESTION_TYPE_THREE = 3;
 
 
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }
 
    public class DataOne extends ViewHolder {
        TextView tvQuestion;
        RadioButton radio1,radio2,radio3,radio4,radio5;
        RadioGroup containerAnswers ;

        public DataOne(View v) {
            super(v);
            this.tvQuestion = (TextView) v.findViewById(R.id.tv_question);

            // Now
            this.containerAnswers = (RadioGroup) v.findViewById(R.id.containerAnswers);

            // Before
//            this.radio1 = (RadioButton) v.findViewById(R.id.radio1);
//            this.radio2 = (RadioButton) v.findViewById(R.id.radio2);
//            this.radio3 = (RadioButton) v.findViewById(R.id.radio3);
//            this.radio4 = (RadioButton) v.findViewById(R.id.radio4);
//            this.radio5 = (RadioButton) v.findViewById(R.id.radio5);
        }
    }
 
    public class DataTwo extends ViewHolder {
        TextView tvQuestion;
        RadioButton radio1,radio2;

        public DataTwo(View v) {
            super(v);
            this.tvQuestion = (TextView) v.findViewById(R.id.tv_question);
            this.radio1 = (RadioButton) v.findViewById(R.id.radio1);
            this.radio2 = (RadioButton) v.findViewById(R.id.radio2);
        }
    }
 
    public class DataThree extends ViewHolder {
        TextView tvQuestion;
        EditText etAnswer;
 
        public DataThree(View v) {
            super(v);
            this.tvQuestion = (TextView) v.findViewById(R.id.tv_question);
            this.etAnswer = (EditText) v.findViewById(R.id.et_answer);
        }
    }
 
 
    public FeedbackAdapter(Context context, List<QuestionList> dataSet) {
       this.dataSet = dataSet;
        this.context =  context ;
    }
 
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == QUESTION_TYPE_ONE) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.row_feedback_type_one, viewGroup, false);
             return new DataOne(v);
        } else if (viewType == QUESTION_TYPE_TWO) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.row_feedback_type_two, viewGroup, false);
            return new DataTwo(v);
        } else {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.row_feedback_type_three, viewGroup, false);
            return new DataThree(v);
        }
    }
 
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == QUESTION_TYPE_ONE) {
            final DataOne holder = (DataOne) viewHolder;
            holder.tvQuestion.setText(dataSet.get(position).question.name);

            // Now
            holder.containerAnswers.removeAllViews();
            String choicesCSVString = dataSet.get(position).question.choices ;

            String[] choices =  choicesCSVString.split("[,]");
            for (int indexChoice = 0; indexChoice <choices.length; indexChoice++) {
                String choice  = choices[indexChoice].trim() ;
                final RadioButton radioButton =  new RadioButton(this.context) ;
                // Before
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        dataSet.get(position).answer= radioButton.getText().toString();
                    }
                });
                radioButton.setText(choice);

                holder.containerAnswers.addView(radioButton);
            }

        }
        else if (viewHolder.getItemViewType() == QUESTION_TYPE_TWO) {
           final DataTwo holder = (DataTwo) viewHolder;
            holder.tvQuestion.setText(dataSet.get(position).question.name);
            holder.radio1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    dataSet.get(position).answer= holder.radio1.getText().toString();
                }
            });
            holder.radio2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    dataSet.get(position).answer= holder.radio2.getText().toString();
                }
            });
        }
        else {
           final DataThree holder = (DataThree) viewHolder;
            holder.tvQuestion.setText(dataSet.get(position).question.name);

            holder.etAnswer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    dataSet.get(position).answer= holder.etAnswer.getText().toString();
                }
            });

        }
    }
 
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
 
   @Override
    public int getItemViewType(int position) {
        return dataSet.get(position).question_type;
    }

    public List<FeedbackResponse> getFeedbacks(String orderId,String restaurantId){
        List<FeedbackResponse> feedbackResponses= new ArrayList<>();
        for (QuestionList list: dataSet) {
            FeedbackResponse feedbackResponse= new FeedbackResponse();
            feedbackResponse.answer = list.answer;
            feedbackResponse.order_no = orderId;
            feedbackResponse.question_id = list.question_id;
            feedbackResponse.question_type=list.question_type;
            feedbackResponse.resturant_id=restaurantId;
            feedbackResponses.add(feedbackResponse);
        }
        return feedbackResponses;
    }
}