package com.example.appdev.sparktify2019.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.appdev.sparktify2019.R;
import com.example.appdev.sparktify2019.activities.BaseActivity;
import com.example.appdev.sparktify2019.interfaces.Single_player_blank_fields_ViewEvents;

import java.util.Arrays;
import java.util.Collections;


public class Single_player_blank_fields extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button btn;
    private TextView black_text_filed;
    private ImageButton clearButton;

    private Single_player_blank_fields_ViewEvents viewClicked;
    private int listOfButtons[] = {R.id.s_4_btn1, R.id.s_4_btn2, R.id.s_4_btn3, R.id.s_4_btn4, R.id.s_4_btn5,
            R.id.s_4_btn6, R.id.s_4_btn7, R.id.s_4_btn8, R.id.s_4_btn9, R.id.s_4_btn10,
            R.id.s_4_btn11, R.id.s_4_btn12, R.id.s_4_btn13, R.id.s_4_btn14, R.id.s_4_btn15,};
    private String strtext;

    public Single_player_blank_fields() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Single_player_blank_fields.
     */
    // TODO: Rename and change types and number of parameters
    public static Single_player_blank_fields newInstance(String param1, String param2) {
        Single_player_blank_fields fragment = new Single_player_blank_fields();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_single_player_blank_fields, container, false);
        strtext = getArguments().getString("correct_answer");
        black_text_filed = view.findViewById(R.id.black_text_filed);
        clearButton = view.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                clearTextField(view);
            }
        });

        convertAnswerToUnderLine(view, strtext);

        loadButtons(view, strtext);

        return view;
    }

    private void clearTextField(View v) {
        char[] myNameChars = black_text_filed.getText().toString().toCharArray();
        for (int x = black_text_filed.getText().toString().length() - 1; x >= 0; x--) {
            String str = String.valueOf(black_text_filed.getText().toString().charAt(x));

            if (isAlpha(str)) {
                myNameChars[x] = BaseActivity.SONG_SPACE.charAt(0);
                black_text_filed.setText(String.valueOf(myNameChars));

                // Enable button
                //Fill out buttons without text
                for (int i = 0; i < listOfButtons.length; i++) {
                    btn = v.findViewById(listOfButtons[i]);
                    if (btn.getText().toString().equalsIgnoreCase(str)) {
                        btn.setEnabled(true);
                    }
                }

                if(x ==0 ){
                    clearButton.setVisibility(View.GONE);
                }

                return;
            }
        }
    }

    public boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }

    private void convertAnswerToUnderLine(View v, String correctAnswer) {
        String new_word = "";
        String extraSpace = "&#160;";

        for (int x = 0; x < correctAnswer.length(); x++) {
            if (String.valueOf(correctAnswer.charAt(x)).equalsIgnoreCase(BaseActivity.SONG_SPACE)) {
                new_word = new_word + extraSpace + extraSpace;
            } else {
                new_word = new_word + "_" + extraSpace;
            }
        }

        TextView txt = v.findViewById(R.id.black_text_filed);
        txt.setText(Html.fromHtml(new_word));
    }

    private void loadButtons(View v, String answerText) {
        // Clear text
        for (int x = 0; x < listOfButtons.length; x++) {
            btn = v.findViewById(listOfButtons[x]);
            btn.setText("");
            btn.setOnClickListener(this);
        }

        // Buttons
        // Shuffle Buttons arrangement
        Integer[] shuffleButtons = new Integer[listOfButtons.length];
        for (int i = 0; i < listOfButtons.length; i++) {
            shuffleButtons[i] = i;
        }
        Collections.shuffle(Arrays.asList(shuffleButtons));

        // Populate correct answer first

        String newanswerText = answerText.replace(BaseActivity.SONG_SPACE, "");
        for (int x = 0; x < newanswerText.length(); x++) {
            btn = v.findViewById(listOfButtons[shuffleButtons[x]]);
            btn.setText(String.valueOf(newanswerText.charAt(x)));
        }

        //Fill out buttons without text
        for (int i = 0; i < listOfButtons.length; i++) {
            btn = v.findViewById(listOfButtons[i]);
            if (btn.getText().equals("")) {
                char randomLetter = (char) ('a' + Math.random() * ('z' - 'a' + 1));
                btn.setText(String.valueOf(randomLetter));
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Single_player_blank_fields_ViewEvents) {
            viewClicked = (Single_player_blank_fields_ViewEvents) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        if (view instanceof Button) {
            clearButton.setVisibility(View.VISIBLE);
            for (int i = 0; i < listOfButtons.length; i++) {
                if (view.getId() == listOfButtons[i]) {
                    btn = view.findViewById(listOfButtons[i]);
                    boolean isGood = sendTextToLabel((String) btn.getText());
                    btn.setEnabled(isGood);
                    checkIfAnswerIsCorrect();
                }
            }
        }
    }

    private void checkIfAnswerIsCorrect() {
        String currentAnswer = black_text_filed.getText().toString().replaceAll("\\s", "");
        String correctAnswer = this.strtext.replaceAll(BaseActivity.SONG_SPACE, "");

        if(currentAnswer.equalsIgnoreCase(correctAnswer)){
            viewClicked.fieldText_anyButtonPress(true);
        }else{
            viewClicked.fieldText_anyButtonPress(false);
        }
    }

    private boolean sendTextToLabel(String text) {
        StringBuilder answerText = new StringBuilder(black_text_filed.getText());
        for (int i = 0; i <= black_text_filed.getText().toString().length() - 1; i++) {
            if ((i + 1) % 2 != 0 && answerText.charAt(i) == '_') {
                answerText.setCharAt(i, text.charAt(0));
                black_text_filed.setText("");
                black_text_filed.setText(answerText.toString().toUpperCase());
                return false;
            }
        }
        return true;
    }
}
