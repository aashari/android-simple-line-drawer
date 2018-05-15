package id.aashari.code.linedrawer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

import id.aashari.code.linedrawer.helpers.MyConstants;

public class MainActivity extends AppCompatActivity {

    Button btnOpenGallery;

    ImageView imageViewStroke;
    ImageView imagePreview;
    ImageView imageView;

    Uri selectedImage;
    Bitmap selectedBitmap;

    PointF startPoint;
    PointF endPoint;

    Bitmap bitmap;
    Canvas canvas;
    Paint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeElement();
        initializeEvent();
    }

    private void initializeElement() {
        this.imageView = (ImageView) findViewById(R.id.imageView);
        this.imagePreview = (ImageView) findViewById(R.id.imagePreview);
        this.imageViewStroke = (ImageView) findViewById(R.id.imageViewStroke);
        this.btnOpenGallery = (Button) findViewById(R.id.btnOpenGallery);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeEvent() {
        this.btnOpenGallery.setOnClickListener(this.btnOpenGalleryClick);

        this.imageView.setOnTouchListener(new ImageViewOnTouch());

    }

    private class ImageViewOnTouch implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (selectedBitmap == null) return false;

            int action = event.getAction();

            PointF point = new PointF(event.getX(), event.getY());


            float maxWidth = (imageView.getWidth() / 2);
            float maxHeight = (imageView.getHeight() / 2);

            point.x = point.x - maxWidth;
            point.y = point.y - maxHeight;

            point.x = (point.x / maxWidth) * 100;
            point.y = (point.y / maxHeight) * 100;

            int width = 100;
            int height = 100;

            PointF imagePoint = new PointF(0, 0);

            float imageMaxWidth = (selectedBitmap.getWidth() / 2);
            float imageMaxHeight = (selectedBitmap.getHeight() / 2);

            imagePoint.x = imageMaxWidth + (point.x * imageMaxWidth / 100) - (width / 2);
            imagePoint.y = imageMaxHeight + (point.y * imageMaxHeight / 100) - (width / 2);

            if (imagePoint.x <= 0) imagePoint.x = 0;
            if (imagePoint.y <= 0) imagePoint.y = 0;
            if (imagePoint.x + (width) > selectedBitmap.getWidth())
                imagePoint.x = selectedBitmap.getWidth() - (width);
            if (imagePoint.y + (height) > selectedBitmap.getHeight())
                imagePoint.y = selectedBitmap.getHeight() - (height);

            //buat tampilin thumbnail
            Bitmap thumbnail = Bitmap.createBitmap(selectedBitmap, (int) imagePoint.x, (int) imagePoint.y, width, height);
            imagePreview.setImageBitmap(thumbnail);

            if (action == MotionEvent.ACTION_DOWN) {
                Log.v("aashari-tag", "Start H " + point.x + " " + point.y);
                Log.v("aashari-tag", "Start I " + imagePoint.x + " " + imagePoint.y);
                imagePreview.setVisibility(View.VISIBLE);
                startPoint = new PointF(event.getX(), event.getY());
            }

            if (action == MotionEvent.ACTION_MOVE) {
                Log.v("aashari-tag", "To H " + point.x + " " + point.y);
                Log.v("aashari-tag", "To I " + imagePoint.x + " " + imagePoint.y);
                imagePreview.setVisibility(View.VISIBLE);
                endPoint = new PointF(event.getX(), event.getY());
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);
                imageView.invalidate();
            }

            if (action == MotionEvent.ACTION_UP) {
                Log.v("aashari-tag", "End H " + point.x + " " + point.y);
                Log.v("aashari-tag", "End I " + imagePoint.x + " " + imagePoint.y);
                imagePreview.setVisibility(View.INVISIBLE);
            }

            return true;

        }


    }

    private View.OnClickListener btnOpenGalleryClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, MyConstants.GALLERY_IMAGE_LOADED);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyConstants.GALLERY_IMAGE_LOADED && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            this.loadImage();
        }
    }

    private void loadImage() {
        try {

            InputStream inputStream = getContentResolver().openInputStream(this.selectedImage);

            selectedBitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(selectedBitmap);

            Display currentDisplay = getWindowManager().getDefaultDisplay();
            float dw = currentDisplay.getWidth();
            float dh = currentDisplay.getHeight();

            bitmap = Bitmap.createBitmap((int) dw, (int) dh, Bitmap.Config.ARGB_8888);

            canvas = new Canvas(bitmap);

            paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(10);

            imageView.setImageBitmap(selectedBitmap);
            imageViewStroke.setImageBitmap(bitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
