package com.example.henry.picyourbrain;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Dialog;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener, View.OnLongClickListener{
    private TextView mPromptTextView;
    private DrawingView mDrawView;
    private FloatingActionButton mPaintOptionsButton;
    private ImageButton currPaint, mDrawBtn, mEraserBtn, mNewBtn, mSaveBtn;
    private float smallBrush, mediumBrush, largeBrush;
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private boolean mPaintMenuIsOpen = false;
    private final String PROMPT_KEY = "prompt_key";


    @Override
    /*Initializes mDrawView, buttons, paint, and brush sizes
     *Requests permission to write to external storage
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null && savedInstanceState.containsKey(PROMPT_KEY)) {
            String prompt = savedInstanceState.getString(PROMPT_KEY);
            recoverPrompt(prompt);
        }


        mPaintOptionsButton = (FloatingActionButton) findViewById(R.id.paint_options_floating_action_button);
        mPaintOptionsButton.setLongClickable(true);
        mPaintOptionsButton.setOnLongClickListener(this);
        mPaintOptionsButton.setOnClickListener(this);
        View mainView = findViewById(R.id.main_view);
        mainView.setLongClickable(true);
        mainView.setOnClickListener(this);

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);


        initializeDrawingView(R.id.drawing, smallBrush);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }


    }

    private void initializeDrawingView(int id, float brushSize) {
        mDrawView = findViewById(id);
        mDrawView.setBrushSize(brushSize);

        mDrawView.setLongClickable(true);
        mDrawView.setOnLongClickListener(this);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPromptTextView != null) {
            String prompt = mPromptTextView.getText().toString();
            outState.putString(PROMPT_KEY, prompt);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    /*
     *OptionsMenu Actions:
     *  New Prompt Button
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        /*
         * Sets the view to a new subject from string/subject array
         *       and appends a new situation from string/situation_array,
         *       forming a title for the drawing
         * */
        if (itemId == R.id.action_get_prompt) {
            createNewPrompt();
        }
        return true;
    }

    private void createNewPrompt() {
        PromptTool promptTool = new PromptTool(this);
        String subject = promptTool.getRandomSubject() + " ";
        String situation = promptTool.getRandomSituation();
        ViewGroup view = (ViewGroup) findViewById(android.R.id.content);
        getLayoutInflater().inflate(R.layout.prompt_textview, view);
        mPromptTextView = findViewById(R.id.tv_prompt);
        mPromptTextView.setText(subject);
        mPromptTextView.append(situation);
    }

    private void recoverPrompt(String prompt) {
        ViewGroup view = (ViewGroup) findViewById(android.R.id.content);
        getLayoutInflater().inflate(R.layout.prompt_textview, view);
        mPromptTextView = findViewById(R.id.tv_prompt);
        mPromptTextView.setText(prompt);
    }


    @Override
    public void onClick(View view) {

        if (mPaintMenuIsOpen) {
            switch (view.getId()) {
                case (R.id.brush_size_btn):
                    onClickSetBrushSize();
                    break;
                case (R.id.erase_btn):
                    onClickSetEraserSize();
                    break;
                case (R.id.new_btn):
                    onClickNewPage();
                    break;
                case (R.id.save_btn):
                    onClickSavePainting();
                    break;
            }

        }
        if (view.getId() == R.id.paint_options_floating_action_button) {
            onClickPaintOptions((ViewGroup) view.getParent());
        }


    }

    private void onClickPaintOptions(ViewGroup parent) {
        if (!mPaintMenuIsOpen) {
            LayoutInflater inflater = getLayoutInflater();
            inflater.inflate(R.layout.paint_options_menu, parent);
            setUpPaintOptions();
            mPaintMenuIsOpen = true;
        } else {
            View menu = findViewById(R.id.paint_options_menu);
            parent.removeView(menu);
            mPaintMenuIsOpen = false;
        }
    }

    private void setUpPaintOptions() {
        mSaveBtn = findViewById(R.id.save_btn);
        mSaveBtn.setOnClickListener(this);
        mNewBtn = findViewById(R.id.new_btn);
        mNewBtn.setOnClickListener(this);
        mEraserBtn = findViewById(R.id.erase_btn);
        mEraserBtn.setOnClickListener(this);
        LinearLayout paintLayout = findViewById(R.id.paint_colors);
        currPaint = (ImageButton) paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        mDrawBtn = findViewById(R.id.brush_size_btn);
        mDrawBtn.setOnClickListener(this);
    }


    private void onClickSetBrushSize() {
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setTitle("Brush Size:");
        brushDialog.setContentView(R.layout.brush_chooser);

        ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
        smallBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setBrushSize(smallBrush);
                mDrawView.setLastBrushSize(smallBrush);
                mDrawView.setEraseFlag(false);
                brushDialog.dismiss();
            }
        });
        ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
        mediumBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setBrushSize(mediumBrush);
                mDrawView.setLastBrushSize(mediumBrush);
                mDrawView.setEraseFlag(false);
                brushDialog.dismiss();
            }
        });
        ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
        largeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setBrushSize(largeBrush);
                mDrawView.setLastBrushSize(largeBrush);
                mDrawView.setEraseFlag(false);
                brushDialog.dismiss();
            }
        });
        brushDialog.show();
    }

    private void onClickSetEraserSize() {

        final Dialog brushDialog = new Dialog(this);
        brushDialog.setTitle("Eraser Size:");
        brushDialog.setContentView(R.layout.brush_chooser);

        ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
        smallBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawView.setEraseFlag(true);
                mDrawView.setBrushSize(smallBrush);
                brushDialog.dismiss();
            }
        });
        ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
        mediumBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawView.setEraseFlag(true);
                mDrawView.setBrushSize(mediumBrush);
                brushDialog.dismiss();
            }
        });
        ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
        largeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawView.setEraseFlag(true);
                mDrawView.setBrushSize(largeBrush);
                brushDialog.dismiss();
            }
        });
        brushDialog.show();
    }

    private void onClickNewPage() {

        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle("New Drawing");
        newDialog.setMessage("Start new drawing?");
        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mDrawView.startNew();
                dialog.dismiss();
            }
        });
        newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        newDialog.show();
    }

    private void onClickSavePainting() {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save Drawing");
        saveDialog.setMessage("Save this drawing?");
        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDrawView.setDrawingCacheEnabled(true);

                File sdcard = Environment.getExternalStorageDirectory();
                if (sdcard != null) {
                    File mediaDir = new File(sdcard, "DCIM/Camera");
                    if (!mediaDir.exists()) {
                        mediaDir.mkdirs();
                    }
                }

                String savedImgUrl =
                        MediaStore.Images.Media.insertImage(
                                getContentResolver(), mDrawView.getDrawingCache(),
                                UUID.randomUUID().toString() + ".png", "drawing");

                if (savedImgUrl != null) {
                    Toast savedToast = Toast.makeText(getApplicationContext(),
                            "Drawing saved to Gallery", Toast.LENGTH_SHORT);
                    savedToast.show();
                } else {
                    Toast failedSaveToast = Toast.makeText(getApplicationContext(),
                            "Save Failed.", Toast.LENGTH_SHORT);
                    failedSaveToast.show();
                }

                mDrawView.destroyDrawingCache();
            }

        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        saveDialog.show();


    }


    public void onPaintClicked(View view) {
        mDrawView.setEraseFlag(false);
        mDrawView.setBrushSize(mDrawView.getLastBrushSize());
        if (view != currPaint) {
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            mDrawView.setColor(color);

            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(null);
            currPaint = imgView;
        }
    }

    @Override
    public boolean onLongClick(View v) {

        Toast toast = Toast.makeText(getApplicationContext(), "Long Click", Toast.LENGTH_LONG);
        toast.show();
        onClickPaintOptions((ViewGroup) mDrawView.getParent());
        return true;


    }
}


