package org.sairaa.scholarquiz.ui.Moderator;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.sairaa.scholarquiz.ui.Moderator.Question.ModeratorQuestionListAdapter;
import org.sairaa.scholarquiz.ui.Moderator.Question.QuestionAddActivity;
import org.sairaa.scholarquiz.ui.Moderator.Question.QuestionListActivity;
import org.sairaa.scholarquiz.R;
import org.sairaa.scholarquiz.SharedPreferenceConfig;
import org.sairaa.scholarquiz.model.LessonQuizModel;

import java.util.ArrayList;

public class QuizModeratorActivity extends AppCompatActivity {

    //    private ListView modListView;
//    private ArrayAdapter adapter;
//    private ArrayList<String> quizList;
    public LessonQuizModel quizModel;
    private Button newQuizButton;
    private EditText newQuizEditText;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessageDatabaseReferance;
    private SharedPreferenceConfig sharedPreferenceConfig;
    private TextView quizNotPublished;
    //public ListView questionListView;
    private ModeratorQuestionListAdapter adapter;

    private String channelId;
    private ListView listView;
    private String quizIdPublished;
    private final int ONLYREAD = 200;
    private final int READWRITE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_moderator);



        Intent intent = getIntent();
        channelId = intent.getStringExtra("channelId");
        Toast.makeText(QuizModeratorActivity.this,"Moderator Page "+channelId,Toast.LENGTH_SHORT).show();

        newQuizButton = findViewById(R.id.go_create_new_quiz_button);
        newQuizEditText = findViewById(R.id.mod_new_quiz_name_edittext);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

//        final QuizAdapter quizAdapter;
        listView = findViewById(R.id.moderator_lesson_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                quizNameAndId quizDeatils = (quizNameAndId) listView.getItemAtPosition(position);
//                Toast.makeText(QuizModeratorActivity.this,"quiz "+quizDeatils.getQuizId(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(QuizModeratorActivity.this,QuestionListActivity.class);
                intent.putExtra("readWrite",ONLYREAD);
                intent.putExtra("channelId",channelId);
                intent.putExtra("quizId",quizDeatils.getQuizId());
                intent.putExtra("quizName",quizDeatils.getQuizName());
                startActivity(intent);
            }
        });
//
//        adapter = new ModeratorQuestionListAdapter(this,questionListModels);
//        questionListView.setAdapter(adapter);

        sharedPreferenceConfig = new SharedPreferenceConfig(getApplicationContext());

        quizNotPublished = findViewById(R.id.quiz_not_published_textview);

        String newQuiz = sharedPreferenceConfig.readNewQuizName();
        if(newQuiz != null){
            quizNotPublished.setText(newQuiz);
        }
        quizNotPublished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sharedPreferenceConfig.readPublishedOrNot()){
                    String quizId = sharedPreferenceConfig.readQuizId();
                    String quizName = sharedPreferenceConfig.readNewQuizName();
                    Intent intent = new Intent(QuizModeratorActivity.this,QuestionListActivity.class);
                    intent.putExtra("readWrite",READWRITE);
                    intent.putExtra("channelId",channelId);
                    intent.putExtra("quizId",quizId);
                    intent.putExtra("quizName",quizName);
                    startActivity(intent);
//                    quizAdapter.clear();
                }

            }
        });
        newQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newQuizEditText.getText().toString().trim().equals("")){
                    //please enter the Quiz name
                }else {



//                    Check whether the last quiz published or not
                    if(sharedPreferenceConfig.readPublishedOrNot()){
                        sharedPreferenceConfig.writePublishedOrNot(false);
                        Toast.makeText(QuizModeratorActivity.this,"on Way in : "+sharedPreferenceConfig.readPublishedOrNot(),Toast.LENGTH_SHORT).show();
                        mMessageDatabaseReferance = mFirebaseDatabase.getReference().child("Quiz");
                        String quizId = mMessageDatabaseReferance.push().getKey();//.setValue(mMessageDatabaseReferance.push().getKey().toString());//.push().setValue(new QuizModel(1,"How are you ?","Fine","Well","Good","Very Fine",1));
//                    mMessageDatabaseReferance.child(quizId).child("2").setValue(new QuizModel("How are you ?","Fine","Well","Good","Very Fine",1));
                        Toast.makeText(QuizModeratorActivity.this,"quiz Id : "+quizId,Toast.LENGTH_SHORT).show();
                        // store the quizid and quiz name in shared preference till published.

                        sharedPreferenceConfig.writeQuizId(quizId);
//                        String qa = sharedPreferenceConfig.readQuizId();
//                        Toast.makeText(QuizModeratorActivity.this,"quiz share : "+qa,Toast.LENGTH_SHORT).show();
                        sharedPreferenceConfig.writeNewQuizName(newQuizEditText.getText().toString().trim());
                        String quizName = sharedPreferenceConfig.readNewQuizName();
//                        Toast.makeText(QuizModeratorActivity.this,"quiz Name : "+quizName,Toast.LENGTH_SHORT).show();

                        quizNotPublished.setText(quizName);
                        // store the quizid in shared preference till published.
                        // go to another activity to create question and answer.
                        Intent intent = new Intent(QuizModeratorActivity.this,QuestionListActivity.class);
                        intent.putExtra("quizId",quizId);
                        intent.putExtra("quizName",quizName);
                        startActivity(intent);
                    }else{
                        //first complete the not published quiz
                    }

                    Toast.makeText(QuizModeratorActivity.this,"on Way out: "+sharedPreferenceConfig.readPublishedOrNot(),Toast.LENGTH_SHORT).show();
                    // create new quiz id


                }
            }
        });



    }

    public class quizNameAndId{
        String quizId, quizName,createdBY;

        public quizNameAndId(String quizId, String quizName, String createdBY){
            this.quizId = quizId;
            this.quizName = quizName;
            this.createdBY = createdBY;

        }

        public String getQuizId() {
            return quizId;
        }

        public String getQuizName() {
            return quizName;
        }

        public String getCreatedBy() {
            return createdBY;
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        final QuizAdapter quizAdapter;

        final ArrayList<quizNameAndId> arrayOfQuiz = new ArrayList<quizNameAndId>();
        quizAdapter = new QuizAdapter(this, arrayOfQuiz);
        listView = findViewById(R.id.moderator_lesson_list);
        // Retribing quizes that exist in the channel
        FirebaseDatabase.getInstance().getReference().child("ChannelQuiz").child(channelId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//
                        for (final DataSnapshot quizListSnapshot : dataSnapshot.getChildren()) {
//
                            String quizId = quizListSnapshot.getKey().toString();
                            String createdBy = quizListSnapshot.child("createdBy").getValue(String.class);
                            String quizName = quizListSnapshot.child("quizName").getValue(String.class);
                            arrayOfQuiz.add(new quizNameAndId(quizId,quizName,createdBy));
//                            arrayOfQuiz.add(quizName);
//                              quizModel = dataSnapshot.getValue(LessonQuizModel.class);
//                            Toast.makeText(QuizModeratorActivity.this,"quiz "+quizId,Toast.LENGTH_SHORT).show();
                            quizAdapter.notifyDataSetChanged();
                        }
//                        LessonQuizModel quizModel = dataSnapshot.getChildren(LessonQuizModel)
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//        arrayOfQuiz.add("Quiz 1");
//        arrayOfQuiz.add("Quiz 2");
//        arrayOfQuiz.add("Quiz 3");

        quizAdapter.notifyDataSetChanged();
        Toast.makeText(QuizModeratorActivity.this,"on resume quiz",Toast.LENGTH_SHORT).show();
        listView.setAdapter(quizAdapter);

        String quizName = sharedPreferenceConfig.readNewQuizName();
//                        Toast.makeText(QuizModeratorActivity.this,"quiz Name : "+quizName,Toast.LENGTH_SHORT).show();

        quizNotPublished.setText(quizName);

    }


    @Override
    protected void onPause() {
        super.onPause();
//        quizAdapter.clear();
    }

    private class QuizAdapter extends ArrayAdapter<quizNameAndId>{

        public QuizAdapter(Context context, ArrayList<quizNameAndId> quizes) {
            super(context, 0, quizes);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            quizNameAndId quizNameId = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.mod_quiz_list, parent, false);
            }

            TextView quizNameTextView = convertView.findViewById(R.id.moderator_quiz_name);

            quizNameTextView.setText(quizNameId.getQuizName());

            notifyDataSetChanged();

            return convertView;

        }
    }
}
