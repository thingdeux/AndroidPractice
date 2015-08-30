package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class QuizActivity extends ActionBarActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private Boolean isInitialLoad = false;
    private int mCurrentIndex = 0;
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String CHEATING_ARRAY = "cheating_array";
    private static final int REQUEST_CODE_CHEAT = 0;
    private boolean mIsCheater;
    private ArrayList<Integer> mCheatingQuestions;

    private Question[] mQuestionBank = new Question[] {
        new Question(R.string.question_oceans, true),
        new Question(R.string.question_mideast, true),
        new Question(R.string.question_africa, true),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true),
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mCheatingQuestions = savedInstanceState.getIntegerArrayList(CHEATING_ARRAY);
        } else {
            mCheatingQuestions = new ArrayList<Integer>();
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        updateQuestion();
        setBuild();

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = CheatActivity.newIntent(QuizActivity.this, mQuestionBank[mCurrentIndex].isAnswerTrue());
                startActivityForResult(i, REQUEST_CODE_CHEAT);
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putIntegerArrayList(CHEATING_ARRAY, mCheatingQuestions);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean isAnswerTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId;

        if (userPressedTrue == isAnswerTrue) {
            messageResId = (mCheatingQuestions.contains(mCurrentIndex)) ? R.string.judgment_toast : R.string.correct_toast;
        } else {
            messageResId = (mCheatingQuestions.contains(mCurrentIndex)) ? R.string.judgment_toast : R.string.incorrect_toast;
        }

        Toast.makeText(QuizActivity.this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
            // If the user has cheated for this particular question, add the index id to the
            // Cheating arraylist
            if (mIsCheater) {
                if (!mCheatingQuestions.contains(mCurrentIndex)) {
                    mCheatingQuestions.add(mCurrentIndex);
                }
                Log.d("CHEATING ARRAY: ", String.valueOf(mCheatingQuestions.toString()));
            }
        }
    }

    protected void setBuild() {
        TextView mBuildTextView;
        mBuildTextView = (TextView) findViewById(R.id.build_number);
        mBuildTextView.setText("API Level " + String.valueOf(Build.VERSION.SDK_INT));
    }
}
