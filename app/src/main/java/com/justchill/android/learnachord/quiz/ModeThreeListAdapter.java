package com.justchill.android.learnachord.quiz;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.intervalOrChord.Chord;
import com.justchill.android.learnachord.intervalOrChord.ChordsList;
import com.justchill.android.learnachord.intervalOrChord.IntervalsList;
import com.justchill.android.learnachord.database.DataContract;
import com.justchill.android.learnachord.database.DatabaseData;

import java.util.ArrayList;

public class ModeThreeListAdapter extends ArrayAdapter<Integer> {

    private ArrayList<Integer> listOfIDs;

    public ModeThreeListAdapter(Activity context, ArrayList<Integer> list) {
        super(context, 0, list);

        listOfIDs = list;
        QuizData.quizModeThreeListViews = new View[list.size()];
    }

    public interface onViewClickListener {
        void onViewClick();
    }
    private ModeThreeListAdapter.onViewClickListener onViewClickListener;

    public void setListener(ModeThreeListAdapter.onViewClickListener listener) {
        onViewClickListener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if(itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_quiz_mode_three, parent, false);
        }

        final Integer itemID = listOfIDs.get(position);

        if(QuizData.quizModeThreeSelectedID != null && QuizData.quizModeThreeSelectedID.equals(itemID)) {
            itemView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.quizModeThreeListViewSelectedBackgroundColor));
        } else {
            itemView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.quizModeThreeListViewUnselectedBackgroundColor));
        }


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuizData.quizModeThreeOnListItemClick(position);
                if(onViewClickListener != null) {
                    onViewClickListener.onViewClick();
                }
            }
        });


        if (itemID != null) {
            TextView intervalLabel = itemView.findViewById(R.id.list_item_quiz_mode_three_interval_text_view);
            TextView chordNumOneLabel = itemView.findViewById(R.id.list_item_quiz_mode_three_chord_one_text_view);
            TextView chordNumTwoLabel = itemView.findViewById(R.id.list_item_quiz_mode_three_chord_two_text_view);

            if(DatabaseData.appLanguage == DataContract.UserPrefEntry.LANGUAGE_ENGLISH && itemID- QuizData.quizModeThreeChordIDAdd == MyApplication.MD9_ID) {
                // Mali durski/dominantni 9
                MyApplication.updateTextView(intervalLabel, MyApplication.MD9_ENG_TEXT, chordNumOneLabel, MyApplication.MD9_ENG_ONE, chordNumTwoLabel, MyApplication.MD9_ENG_TWO);
            } else {
                String tempName = null, tempNumberOne = null, tempNumberTwo = null;

                if(itemID < QuizData.quizModeThreeIntervalIDAdd) {
                    // Id is tone id
                    tempName = MyApplication.getKeyName(itemID- QuizData.quizModeThreeToneIDAdd);
                } else if(itemID < QuizData.quizModeThreeChordIDAdd) {
                    // Id is interval id
                    tempName = IntervalsList.getInterval(itemID- QuizData.quizModeThreeIntervalIDAdd).getName();
                } else {
                    // Id is chord id
                    Chord tempChord = ChordsList.getChord(itemID- QuizData.quizModeThreeChordIDAdd);
                    tempName = tempChord.getName();
                    tempNumberOne = tempChord.getNumberOneAsString();
                    tempNumberTwo = tempChord.getNumberTwoAsString();
                }

                MyApplication.updateTextView(intervalLabel, tempName, chordNumOneLabel, tempNumberOne, chordNumTwoLabel, tempNumberTwo);
            }
        }


        QuizData.quizModeThreeListViews[position] = itemView;

        return itemView;
    }
}
