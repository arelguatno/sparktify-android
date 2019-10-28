package com.example.appdev.sparktify2019.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.appdev.sparktify2019.R;
import com.example.appdev.sparktify2019.activities.LocalMultiplayerActivity;
import com.example.appdev.sparktify2019.interfaces.TwoPlayerLocal_ViewEvents;
import com.example.appdev.sparktify2019.pojos.Song;
import com.example.appdev.sparktify2019.pojos.SongsTitleForFillup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TwoPlayerLocal extends Fragment implements TwoPlayerLocal_ViewEvents, View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private LinearLayout playerA_linear_row1, playerA_linear_row2, playerB_linear_row1, playerB_linear_row2;
    private Button playerA_hitme_Button, playerB_hitme_Button, p1_btn1, p1_btn2, p1_btn3, p1_btn4, p2_btn1, p2_btn2, p2_btn3, p2_btn4;
    private ProgressBar progressBar1, progressBar2;
    private int playerA_buttons[] = {R.id.p1_btn1, R.id.p1_btn2, R.id.p1_btn3, R.id.p1_btn4};
    private int playerB_buttons[] = {R.id.p2_btn1, R.id.p2_btn2, R.id.p2_btn3, R.id.p2_btn4};
    private ScheduledExecutorService service;
    MediaPlayer mp;
    SongsTitleForFillup songsTitleForFillup = new SongsTitleForFillup();
    private TwoPlayerLocal_ViewEvents viewClicked;

    private String correctAnswer;
    private int whereIsCorrectAnswer = 0;
    static int songChoice = 0;
    private List<Song> albumSong;
    private boolean playerChance = false;
    private LinearLayout playerB_linearlayout, playerA_linearlayout;
    private LocalMultiplayerActivity activityView;
    private int playerAScore, playerBScore = 0;
    private ObjectAnimator animation, animation2;

    public TwoPlayerLocal() {
        // Required empty public constructor
    }

    public static TwoPlayerLocal newInstance(String param1, String param2) {
        TwoPlayerLocal fragment = new TwoPlayerLocal();
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
        final View view = inflater.inflate(R.layout.fragment_two_player_offline, container, false);
        albumSong = new ArrayList<>();

        activityView = (LocalMultiplayerActivity) getActivity();

        playerA_linear_row1 = view.findViewById(R.id.player1_linear_row1);
        playerA_linear_row2 = view.findViewById(R.id.player1_linear_row2);

        playerB_linear_row1 = view.findViewById(R.id.player2_linear_row1);
        playerB_linear_row2 = view.findViewById(R.id.player2_linear_row2);

        progressBar1 = view.findViewById(R.id.progress1);
        progressBar2 = view.findViewById(R.id.progress2);

        playerB_linearlayout = view.findViewById(R.id.player2_linearlayout);
        playerA_linearlayout = view.findViewById(R.id.player1_linearlayout);

        p1_btn1 = view.findViewById(R.id.p1_btn1);
        p1_btn1.setOnClickListener(this);
        p1_btn2 = view.findViewById(R.id.p1_btn2);
        p1_btn2.setOnClickListener(this);
        p1_btn3 = view.findViewById(R.id.p1_btn3);
        p1_btn3.setOnClickListener(this);
        p1_btn4 = view.findViewById(R.id.p1_btn4);
        p1_btn4.setOnClickListener(this);
        p2_btn1 = view.findViewById(R.id.p2_btn1);
        p2_btn1.setOnClickListener(this);
        p2_btn2 = view.findViewById(R.id.p2_btn2);
        p2_btn2.setOnClickListener(this);
        p2_btn3 = view.findViewById(R.id.p2_btn3);
        p2_btn3.setOnClickListener(this);
        p2_btn4 = view.findViewById(R.id.p2_btn4);
        p2_btn4.setOnClickListener(this);

        // Add songs
        albumSong.clear();

        Song song = new Song("pop5_chandelier", "Chandelier");
        albumSong.add(song);

        song = new Song("pop6_dusk_till_dawn", "Dusk Till Dawn");
        albumSong.add(song);

        song = new Song("pop7_all_of_me", "All of me");
        albumSong.add(song);

        song = new Song("pop1_congratulations", "Congratulations");
        albumSong.add(song);

        song = new Song("pop2_hayaan_mo_sila", "Hayaan Mo Sila");
        albumSong.add(song);

        song = new Song("pop3_humble", "Humble");
        albumSong.add(song);

        song = new Song("pop4_mask_off", "Mask Off");
        albumSong.add(song);



        play_music();

        playerA_hitme_Button = view.findViewById(R.id.player1_hitme_Button);
        playerA_hitme_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                playerA_hitme_Button(view, getCorrectAnswer());
            }
        });

        playerB_hitme_Button = view.findViewById(R.id.player2_hitme_Button);
        playerB_hitme_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                playerB_hitme_Button(view, getCorrectAnswer());
            }
        });


        return view;
    }

    private void playerA_hitme_Button(View view, String answerText) {
        playerA_linear_row1.setVisibility(View.VISIBLE);
        playerA_linear_row2.setVisibility(View.VISIBLE);
        playerB_hitme_Button.setEnabled(false);

        playerA_hitme_Button.setVisibility(View.GONE);

        playerA_linearlayout.setGravity(Gravity.CENTER);

        loadTextButtons(view, answerText, playerA_buttons);

        service.shutdown();
        mp.stop();

        progressBar1.setMax(100);
        progressBar1.setProgress(100);

        progressBar2.setMax(100);
        progressBar2.setProgress(100);

        animation = ObjectAnimator.ofInt(progressBar1, "progress", 0);
        animation.setDuration(5000); // 5 second
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        animation2 = ObjectAnimator.ofInt(progressBar2, "progress", 0);
        animation2.setDuration(5000); // 5 second
        animation2.setInterpolator(new DecelerateInterpolator());
        animation2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Timer end 5 seconds
                playerA_linear_row1.setVisibility(View.GONE);
                playerA_linear_row2.setVisibility(View.GONE);

                playerB_hitme_Button.setEnabled(true);
                playerA_hitme_Button.setEnabled(false);

                playerA_hitme_Button.setVisibility(View.VISIBLE);
                progressBar1.setMax(mp.getDuration());
                progressBar1.setProgress(mp.getDuration());

                playerA_linearlayout.setGravity(Gravity.TOP);

                progressBar2.setMax(mp.getDuration());
                progressBar2.setProgress(mp.getDuration());

                if (!playerChance) {
                    songChoice--; // Goto previous music, for other play chance
                    playerChance = true;
                    play_music();
                } else {
                    // End of chance play new music
                    playerChance = false;
                    playerA_hitme_Button.setEnabled(true);
                    playerB_hitme_Button.setEnabled(true);
                    play_music();
                }
                return;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animation2.start();
    }

    private void playerB_hitme_Button(View view, String humble) {
        playerB_linear_row1.setVisibility(View.VISIBLE);
        playerB_linear_row2.setVisibility(View.VISIBLE);
        playerA_hitme_Button.setEnabled(false);
        playerB_hitme_Button.setVisibility(View.GONE);
        playerB_linearlayout.setGravity(Gravity.CENTER);

        loadTextButtons(view, humble, playerB_buttons);

        service.shutdown();
        mp.stop();

        progressBar1.setMax(100);
        progressBar1.setProgress(100);

        progressBar2.setMax(100);
        progressBar2.setProgress(100);

        animation = ObjectAnimator.ofInt(progressBar1, "progress", 0);
        animation.setDuration(5000); // 5 second
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        animation2 = ObjectAnimator.ofInt(progressBar2, "progress", 0);
        animation2.setDuration(5000); // 5 second
        animation2.setInterpolator(new DecelerateInterpolator());
        animation2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Timer end 5 seconds
                playerB_linear_row1.setVisibility(View.GONE);
                playerB_linear_row2.setVisibility(View.GONE);

                playerA_hitme_Button.setEnabled(true);
                playerB_hitme_Button.setEnabled(false);

                playerB_hitme_Button.setVisibility(View.VISIBLE);
                progressBar1.setMax(mp.getDuration());
                progressBar1.setProgress(mp.getDuration());

                playerB_linearlayout.setGravity(Gravity.BOTTOM);

                progressBar2.setMax(mp.getDuration());
                progressBar2.setProgress(mp.getDuration());

                if (!playerChance) {
                    songChoice--; //Goto previous music, for other play chance
                    playerChance = true;
                    play_music();
                } else {
                    // End of chance play new music
                    playerChance = false;
                    playerA_hitme_Button.setEnabled(true);
                    playerB_hitme_Button.setEnabled(true);
                    play_music();
                }
                return;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animation2.start();
    }

    private void loadTextButtons(View v, String answer, int arrays[]) {
        Button btn;
        // Clear buttons text
        for (int x = 0; x < arrays.length; x++) {
            btn = v.findViewById(arrays[x]);
            btn.setText("");
        }

        int getRandPos = (int) ((Math.random() * 4) + 1);
        whereIsCorrectAnswer = getRandPos;
        switch (getRandPos) {
            case 1:
                btn = v.findViewById(arrays[0]);
                btn.setText(answer);
                break;
            case 2:
                btn = v.findViewById(arrays[1]);
                btn.setText(answer);
                break;
            case 3:
                btn = v.findViewById(arrays[2]);
                btn.setText(answer);
                break;
            case 4:
                btn = v.findViewById(arrays[3]);
                btn.setText(answer);
                break;
        }

        for (int x = 0; x < arrays.length; x++) {
            btn = v.findViewById(arrays[x]);
            if (btn.getText().toString() == "") {
                int getRand = (int) ((Math.random() * songsTitleForFillup.getListofSongs.length));
                btn.setText(songsTitleForFillup.getListofSongs[getRand].toString());

            }
        }

    }

    private void playNextMusic(boolean playerA) {
        // Stop 5 seconds countdown
        animation.removeAllListeners();
        animation.end();
        animation.cancel();
        animation2.removeAllListeners();
        animation2.end();
        animation2.cancel();

        if(playerA){
            playerA_linear_row1.setVisibility(View.GONE);
            playerA_linear_row2.setVisibility(View.GONE);
            playerB_hitme_Button.setEnabled(true);
            playerA_hitme_Button.setEnabled(false);
            playerA_hitme_Button.setVisibility(View.VISIBLE);
            playerA_linearlayout.setGravity(Gravity.TOP);
        }else{
            playerB_linear_row1.setVisibility(View.GONE);
            playerB_linear_row2.setVisibility(View.GONE);
            playerA_hitme_Button.setEnabled(true);
            playerB_hitme_Button.setEnabled(false);
            playerB_hitme_Button.setVisibility(View.VISIBLE);
            playerB_linearlayout.setGravity(Gravity.BOTTOM);
        }

        playerChance = false;
        playerA_hitme_Button.setEnabled(true);
        playerB_hitme_Button.setEnabled(true);
        play_music();
    }

    private void play_music() {
        if (songChoice == albumSong.size()) {
            // no more music
            playerA_hitme_Button.setEnabled(false);
            playerB_hitme_Button.setEnabled(false);
            Toast.makeText(getContext(), "No more song", Toast.LENGTH_SHORT).show();
            return;
        }
        mp = MediaPlayer.create(getContext(), activityView.getSongIdByName(getContext(), albumSong.get(songChoice).getSongID()));
        setCorrectAnswer(albumSong.get(songChoice).getSongTitle());
        ++songChoice; // Increment for the next music

        progressBar1.setMax(mp.getDuration());
        progressBar1.setProgress(mp.getDuration());

        progressBar2.setMax(mp.getDuration());
        progressBar2.setProgress(mp.getDuration());

        if (!mp.isPlaying()) {
            mp.start();

            service = Executors.newScheduledThreadPool(1);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    service.shutdown();
                    playerChance = false;
                    playerA_hitme_Button.setEnabled(true);
                    playerB_hitme_Button.setEnabled(true);

                    progressBar1.setMax(0);
                    progressBar2.setMax(0);

                    //Play next song, if no one hit the button
                    play_music();
                }
            });
            service.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    progressBar1.setProgress(mp.getDuration() - mp.getCurrentPosition());
                    progressBar2.setProgress(mp.getDuration() - mp.getCurrentPosition());
                }
            }, 1, 1, TimeUnit.MICROSECONDS);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TwoPlayerLocal_ViewEvents) {
            viewClicked = (TwoPlayerLocal_ViewEvents) context;
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
    public void onDestroy() {
        super.onDestroy();
        mp.stop();
        mp.release();
    }

    @Override
    public void onback_press() {
        mp.stop();

    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    private void incrementScore(View view, boolean playerA) {
        if (playerA) {
            ++playerAScore;
            playerA_hitme_Button.setText("Player A \n\n " + playerAScore);
            playNextMusic(true);
        } else {
            ++playerBScore;
            playerB_hitme_Button.setText("Player B\n\n " + playerBScore);
            playNextMusic(false);
        }

    }

    @Override
    public void onClick(View view) {
        if (whereIsCorrectAnswer == 1 && view.getId() == R.id.p1_btn1) {
            incrementScore(view, true);
        } else if (whereIsCorrectAnswer == 2 && view.getId() == R.id.p1_btn2) {
            incrementScore(view, true);
        } else if (whereIsCorrectAnswer == 3 && view.getId() == R.id.p1_btn3) {
            incrementScore(view, true);
        } else if (whereIsCorrectAnswer == 4 && view.getId() == R.id.p1_btn4) {
            incrementScore(view, true);
        } else if (whereIsCorrectAnswer == 1 && view.getId() == R.id.p2_btn1) {
            incrementScore(view, false);
        } else if (whereIsCorrectAnswer == 2 && view.getId() == R.id.p2_btn2) {
            incrementScore(view, false);
        } else if (whereIsCorrectAnswer == 3 && view.getId() == R.id.p2_btn3) {
            incrementScore(view, false);
        } else if (whereIsCorrectAnswer == 4 && view.getId() == R.id.p2_btn4) {
            incrementScore(view, false);
        } else {
            // Incorrect
            Toast.makeText(getContext(), "Incorrect", Toast.LENGTH_SHORT).show();
        }

    }
}
