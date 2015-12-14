package com.example.bubblepedia;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import Utility.HelperClass;
import Utility.HttpAsyncTask;
import Utility.IDoAsyncAction;
import Utility.WikiSearchResultTextView;

public class MainActivity extends ActionBarActivity implements IDoAsyncAction {

    private String WIKI_SEARCH_SERVLET_ENDPOINT;
    private ArrayAdapter adapter = null;
    private List<String> wikiSearchResult = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WIKI_SEARCH_SERVLET_ENDPOINT = getResources().getString(R.string.wiki_search_servlet_endpoint);

        adapter = new ArrayAdapter<String>(this, R.layout.wikisearch_listview_row, wikiSearchResult);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WikiSearchResultTextView wikiView = (WikiSearchResultTextView) view;
                wikiView.setToggle(!wikiView.isToggle());
                if (wikiView.isToggle()) {
                    wikiView.setBackgroundColor(getResources().getColor(R.color.wikisearch_textview_backgroundcolor_1));
                } else {
                    wikiView.setBackgroundColor(0);
                }
                //adapter.notifyDataSetChanged();
            }
        });

        Intent intent = getIntent();
        if (intent.ACTION_SEARCH.equals(Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            try {
                dosearch(query);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void dosearch(String query) throws UnsupportedEncodingException {
        if (query != null && query != "" && HelperClass.IsNetworkConnectionAvailable(this)) {
            query = URLEncoder.encode(query, "UTF-8");
            String wikisearchservletUrl = WIKI_SEARCH_SERVLET_ENDPOINT + "?wikisearch=" + query;
            HttpAsyncTask asyncTask = new HttpAsyncTask(this);
            asyncTask.execute(wikisearchservletUrl);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //Before we can use the SearchView: we need to associate the Search Widget with the Searchable Configuration
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public String DoBackgroundAction(String buffer) {

        return buffer;
    }

    @Override
    public void DoResult(String doBackgroundString) {
        try {
            JSONArray jarray = new JSONArray(doBackgroundString);
            String title = jarray.getString(0).toString();
            JSONArray jarraySearch = new JSONArray(jarray.get(1).toString());
            int l = jarraySearch.length();
            for (int i = 0; i < l; i++) {
                wikiSearchResult.add(jarraySearch.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }
}