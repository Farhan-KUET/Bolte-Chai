package com.farhan.boltechai.ui.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.farhan.boltechai.AppConstants;
import com.farhan.boltechai.R;
import com.farhan.boltechai.data.db.entity.TileEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * ShareHelper — Handles sharing boards and sentences via Android ShareSheet.
 *
 * Lab Topic: Implicit Intent (ACTION_SEND), ContentProvider (FileProvider)
 */
public final class ShareHelper {

    private ShareHelper() {}

    /**
     * Shares sentence text via Android ShareSheet.
     */
    public static void shareSentence(Context context, String sentence) {
        if (sentence == null || sentence.trim().isEmpty()) {
            Toast.makeText(context, R.string.ui_build_sentence, Toast.LENGTH_SHORT).show();
            return;
        }

        String shareBody = sentence + "\n\n— " + AppConstants.DEVELOPER_CREDIT;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);

        context.startActivity(Intent.createChooser(intent, context.getString(R.string.ui_share)));
    }

    /**
     * Shares board details via Android ShareSheet.
     */
    public static void shareBoard(Context context, String boardName, List<TileEntity> tiles) {
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.app_name)).append(" - ").append(boardName).append("\n\n");
        sb.append("Tiles / শব্দসমূহ:\n");
        if (tiles != null) {
            for (TileEntity t : tiles) {
                sb.append("• ").append(t.getLabelBn()).append(" (").append(t.getLabelEn()).append(")\n");
            }
        }
        sb.append("\n").append(AppConstants.DEVELOPER_CREDIT);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, boardName);
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());

        context.startActivity(Intent.createChooser(intent, context.getString(R.string.ui_share)));
    }

    /**
     * Captures a view as a Bitmap image and shares it via FileProvider and ACTION_SEND.
     */
    public static void shareViewAsImage(Context context, View view, String title) {
        try {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            view.draw(canvas);

            File cachePath = new File(context.getCacheDir(), "shared_images");
            cachePath.mkdirs();
            File imageFile = new File(cachePath, "boltechai_board_" + System.currentTimeMillis() + ".png");
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            Uri contentUri = FileProvider.getUriForFile(context,
                    context.getPackageName() + ".fileprovider", imageFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, title + "\n" + AppConstants.DEVELOPER_CREDIT);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.ui_share)));
        } catch (Exception e) {
            Toast.makeText(context, "Could not export image", Toast.LENGTH_SHORT).show();
        }
    }
}
