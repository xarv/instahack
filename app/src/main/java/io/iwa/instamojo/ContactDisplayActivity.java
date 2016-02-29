package io.iwa.instamojo;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.Manifest.permission.READ_CONTACTS;

public class ContactDisplayActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int REQUEST_READ_CONTACTS = 0;
    private CoordinatorLayout coordinatorLayout;
    private ListView listView;
    private List<String> contactCollection;
    private CustomViewGroup customView;
    private Map<Integer,Integer> contactPositionSet = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_display);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_layout);
        listView = (ListView)findViewById(R.id.contacts_list_container);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        customView = (CustomViewGroup)findViewById(R.id.custom_view);
        populateContacts();
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(coordinatorLayout, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return true;
    }


    private void populateContacts() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }
    private void addContactsToListView(List<String> contacts) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        this.contactCollection = contacts;
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(ContactDisplayActivity.this,
                        android.R.layout.simple_list_item_multiple_choice, contactCollection);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(contactPositionSet.get(position)!= null) {
                    if(customView!= null && position < customView.getChildCount()) {
                        customView.removeViewAt(contactPositionSet.get(position));
                        contactPositionSet.remove(position);
                    }
                }
                else{
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText(contactCollection.get(position));
                    textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    textView.setPadding(10,10,10,10);
                    textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
                    customView.addView(textView);
                    contactPositionSet.put(position,customView.getChildCount()-1);

                    Log.d("Listview POsition", String.valueOf(position));
                }
            }
        });
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateContacts();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                this,
                ContactsContract.Contacts.CONTENT_URI,
                ProfileQuery.PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> names = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            names.add(cursor.getString(ProfileQuery.NAME_INDEX));
            cursor.moveToNext();
        }

        addContactsToListView(names);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        };

        int NAME_INDEX = 0;
        int PRIMARY_NAME_INDEX = 1;
    }

}
