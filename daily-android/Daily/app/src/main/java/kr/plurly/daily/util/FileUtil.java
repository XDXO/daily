package kr.plurly.daily.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class FileUtil {

    private static final String SIGNATURE_MEDIA_DOCUMENT = "com.android.providers.media.documents";
    private static final String SIGNATURE_DOWNLOADS_DOCUMENT = "com.android.providers.downloads.documents";
    private static final String SIGNATURE_EXTERNAL_STORAGE_DOCUMENT = "com.android.externalstorage.documents";

    private static final String TYPE_PRIMARY = "primary";

    private static final String SCHEME_CONTENT_PROVIDER = "content";
    private static final String SCHEME_FILE = "file";

    private static final String PATH_DOCUMENT_DOWNLOADS = "content://downloads/public_downloads";

    private static final String FILE_TYPE_IMAGE = "image";
    private static final String FILE_TYPE_VIDEO = "video";
    private static final String FILE_TYPE_AUDIO = "audio";

    public static String getPath(final Context context, final Uri uri) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (DocumentsContract.isDocumentUri(context, uri)) {

                if (isExternalStorageDocument(uri)) {

                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if (TYPE_PRIMARY.equalsIgnoreCase(type)) {

                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse(PATH_DOCUMENT_DOWNLOADS), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                else if (isMediaDocument(uri)) {

                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri;

                    if (FILE_TYPE_IMAGE.equals(type))
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    else if (FILE_TYPE_VIDEO.equals(type))
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    else if (FILE_TYPE_AUDIO.equals(type))
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    else
                        contentUri = MediaStore.Files.getContentUri("external");

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] { split[1] };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        }
        else {

            if (SCHEME_CONTENT_PROVIDER.equalsIgnoreCase(uri.getScheme())) {

                return getDataColumn(context, uri, null, null);
            }
            else if (SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {

                return uri.getPath();
            }
        }

        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) { return SIGNATURE_EXTERNAL_STORAGE_DOCUMENT.equals(uri.getAuthority()); }
    private static boolean isDownloadsDocument(Uri uri) { return SIGNATURE_DOWNLOADS_DOCUMENT.equals(uri.getAuthority()); }
    private static boolean isMediaDocument(Uri uri) { return SIGNATURE_MEDIA_DOCUMENT.equals(uri.getAuthority()); }

    private static String getDataColumn(Context context, Uri uri, String selection,  String[] selectionArgs) {

        Cursor cursor = null;

        String path = null;

        final String column = "_data";
        final String[] projection = { column };

        try {

            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {

                final int column_index = cursor.getColumnIndexOrThrow(column);
                path = cursor.getString(column_index);
            }
        }
        finally {

            if (cursor != null)
                cursor.close();
        }

        return path;
    }
}
