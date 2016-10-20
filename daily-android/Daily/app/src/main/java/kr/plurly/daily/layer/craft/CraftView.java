package kr.plurly.daily.layer.craft;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.UUID;

import javax.inject.Inject;

import kr.plurly.daily.R;
import kr.plurly.daily.bus.RxBus;
import kr.plurly.daily.component.activity.DailyView;
import kr.plurly.daily.databinding.LayoutViewCraftBinding;
import kr.plurly.daily.dialog.DismissDialog;
import kr.plurly.daily.dialog.EmotionDialog;
import kr.plurly.daily.domain.Event;
import kr.plurly.daily.util.FileUtil;

import static kr.plurly.daily.util.Constraints.MIME_TYPE_IMAGE;

public class CraftView extends DailyView implements EmotionDialog.OnEmotionClickListener, DialogInterface.OnClickListener {

    private static final int COUNTER_MAX_LENGTH = 200;

    private static final int REQUEST_CODE_PERMISSION_EXTERNAL_STORAGE = 0x0001;

    private static final int REQUEST_CODE_PLACE_PICKER = 0x0001;

    private static final int REQUEST_CODE_PHOTO_PICKER_BACKPORT = 0x0002;
    private static final int REQUEST_CODE_PHOTO_PICKER_KITKAT = 0x0003;

    private LayoutViewCraftBinding binding;


    @Inject
    DismissDialog dismissDialog;

    @Inject
    EmotionDialog emotionDialog;

    @Inject
    CraftPresenter presenter;

    @Inject
    RxBus bus;

    Event event = new Event();

    String errorTitle;
    String errorContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String uuid = UUID.randomUUID().toString();
        event.setUUID(uuid);

        errorTitle = getString(R.string.label_craft_error_empty_title);
        errorContent = getString(R.string.label_craft_error_empty_content);

        activityComponent.inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.layout_view_craft);

        binding.setPresenter(presenter);
        binding.setView(this);

        binding.setEvent(event);

        binding.inputTitle.addTextChangedListener(new TextWatcher() {

            final String label = getString(R.string.label_craft_title_hint);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {

                binding.inputTitleContainer.setHint(label);
                binding.inputTitle.setError(null);
            }
        });

        binding.inputContentContainer.setCounterEnabled(true);
        binding.inputContentContainer.setCounterMaxLength(COUNTER_MAX_LENGTH);

        binding.inputContent.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(COUNTER_MAX_LENGTH) });
        binding.inputContent.addTextChangedListener(new TextWatcher() {

            final String label = getString(R.string.label_craft_content_hint);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {

                binding.inputContentContainer.setHint(label);
                binding.inputContent.setError(null);
            }
        });

        setSupportActionBar(binding.toolbar);

        final ActionBar toolbar = getSupportActionBar();

        if (toolbar != null) {

            toolbar.setDisplayShowTitleEnabled(false);
            toolbar.setHomeButtonEnabled(true);
            toolbar.setDisplayHomeAsUpEnabled(true);
        }

        dismissDialog.setOnClickListener(this);
        emotionDialog.setOnEmotionClickListener(this);
    }

    public void selectEmotion() {

        emotionDialog.show(getSupportFragmentManager(), EmotionDialog.class.getName());
    }

    public void selectPhoto() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_CODE_PERMISSION_EXTERNAL_STORAGE);
            }
            else {

                Intent intent = getDocumentIntent();
                startActivityForResult(intent, REQUEST_CODE_PHOTO_PICKER_KITKAT);
            }
        }
        else {

            Intent intent = getContentIntent();
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.label_picker_title)), REQUEST_CODE_PHOTO_PICKER_BACKPORT);
        }

    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Intent getDocumentIntent() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(MIME_TYPE_IMAGE);

        return intent;
    }

    @NonNull
    private Intent getContentIntent() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(MIME_TYPE_IMAGE);

        return intent;
    }

    public void deletePhoto() {

        event.setPath(null);

        binding.imageViewContent.setImageURI(null);
        binding.invalidateAll();
    }

    public void selectLocation() {

        try {

            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), REQUEST_CODE_PLACE_PICKER);
        }
        catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {

            Snackbar.make(binding.getRoot(), R.string.label_craft_error_unavailable_google_services, Snackbar.LENGTH_LONG).show();
        }
    }

    public void newEvent() {

        presenter.newEvent(event);
    }

    public void hintEmptyTitle() { binding.inputTitle.setError(errorTitle); }
    public void hintEmptyContent() { binding.inputContentContainer.setError(errorContent); }
    public void hintFailedPost() { snack(binding.getRoot(), R.string.label_craft_error_post_new_event); }

    @Override
    public void onEmotionClick(int emotion) {

        event.setEmotion(emotion);
        binding.invalidateAll();
    }

    @SuppressWarnings("deprecation")
    public Drawable convertDrawable(int emotion) {

        switch (emotion) {

            case Event.EMOTION_HAPPY: return getResources().getDrawable(R.drawable.ic_emotion_happy);
            case Event.EMOTION_GOODIE: return getResources().getDrawable(R.drawable.ic_emotion_goodie);
            case Event.EMOTION_NEUTRAL: return getResources().getDrawable(R.drawable.ic_emotion_neutral);
            case Event.EMOTION_SAD: return getResources().getDrawable(R.drawable.ic_emotion_sad);
            case Event.EMOTION_DISAPPOINTED: return getResources().getDrawable(R.drawable.ic_emotion_disappointed);

            default: return getResources().getDrawable(R.drawable.ic_emotion);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_craft, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home : {

                dismissDialog.show(getSupportFragmentManager(), DismissDialog.class.getName());
                break;
            }

            case R.id.action_post : {

                binding.inputTitle.setError(null);
                binding.inputContent.setError(null);

                newEvent();
                break;
            }
        }

        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {

            case DialogInterface.BUTTON_POSITIVE : {

                presenter.delete(event);
                finish();

                break;
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (emotionDialog.isVisible()) {

            emotionDialog.dismiss();
        }
        else if (dismissDialog.isVisible()) {

            dismissDialog.dismiss();
        }
        else {

            dismissDialog.show(getSupportFragmentManager(), DismissDialog.class.getName());
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {

        if (requestCode == REQUEST_CODE_PERMISSION_EXTERNAL_STORAGE) {

            if (permissions.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent = getDocumentIntent();
                startActivityForResult(intent, REQUEST_CODE_PHOTO_PICKER_KITKAT);
            }
            else {

                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_CODE_PERMISSION_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PLACE_PICKER) {

            if (resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(this, data);

                event.setLocation(String.valueOf(place.getName()));
                event.setLatitude(place.getLatLng().latitude);
                event.setLongitude(place.getLatLng().longitude);

                binding.invalidateAll();
            }
        }

        if (requestCode == REQUEST_CODE_PHOTO_PICKER_KITKAT && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (resultCode == RESULT_OK) {

                Uri uri = data.getData();
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                event.setPath(FileUtil.getPath(this, uri));

                binding.imageViewContent.setImageURI(uri);
                binding.invalidateAll();
            }
            else {

                snack(binding.getRoot(), R.string.label_craft_error_load_image);
            }
        }

        if (requestCode == REQUEST_CODE_PHOTO_PICKER_BACKPORT) {

            if (resultCode == RESULT_OK) {

                Uri uri = data.getData();

                event.setPath(FileUtil.getPath(this, uri));

                binding.imageViewContent.setImageURI(uri);
                binding.invalidateAll();
            }
            else {

                snack(binding.getRoot(), R.string.label_craft_error_load_image);
            }
        }
    }
}
