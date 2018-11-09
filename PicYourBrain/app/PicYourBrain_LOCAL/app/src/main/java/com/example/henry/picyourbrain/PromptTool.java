package com.example.henry.picyourbrain;

import android.content.Context;

import java.util.Random;

public class PromptTool {
    private final String[] mSubjects;
    private final String[] mSituations;
    private final int NUM_SUBJECTS;
    private final int NUM_SITUATIONS;


    PromptTool(Context context){
        mSubjects = context.getResources().getStringArray(R.array.subjects);
        mSituations = context.getResources().getStringArray(R.array.situations);
        NUM_SUBJECTS = mSubjects.length;
        NUM_SITUATIONS = mSituations.length;
    }


    public String getSubject(int index) {return mSubjects[index];}
    public String getSituation(int index) {return mSituations[index];}

    public String getRandomSubject(){
        Random rand = new Random();
        int position = rand.nextInt(NUM_SUBJECTS);
        return getSubject(position);
    }

    public String getRandomSituation(){
        Random rand = new Random();
        int position = rand.nextInt(NUM_SITUATIONS);
        return getSituation(position);
    }

}
