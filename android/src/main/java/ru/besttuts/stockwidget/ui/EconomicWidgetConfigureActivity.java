package ru.besttuts.stockwidget.ui;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.model.Model;
import ru.besttuts.stockwidget.model.QuoteType;
import ru.besttuts.stockwidget.provider.QuoteDataSource;
import ru.besttuts.stockwidget.provider.QuoteDatabaseHelper;

import java.util.ArrayList;


/**
 * The configuration screen for the {@link EconomicWidget EconomicWidget} AppWidget.
 */
public class EconomicWidgetConfigureActivity extends ActionBarActivity
        implements GoodsItemFragment.OnFragmentInteractionListener,
        PlaceStockItemsFragment.OnFragmentInteractionListener {

    final String LOG_TAG = "EconomicWidget.EconomicWidgetConfigureActivity";

    QuoteDatabaseHelper mDbHelper;

    private QuoteDataSource mDataSource;

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
//    EditText mAppWidgetText;
    private static final String PREFS_NAME = "ru.besttuts.stockwidget.ui.EconomicWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    // Whether or not we are in dual-pane mode
    boolean mIsDualPane = true;

    // The fragment where the headlines are displayed
    ConfigureMenuFragment mHeadlinesFragment;

    // The fragment where the article is displayed (null if absent)
    GoodsItemFragment mArticleFragment;

    // Порядковый номер котировки на виджете
    int widgetItemPosition = 0;

    // Текущий, выбранный тип котировки
    int position = 0;

    public EconomicWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onConfigureMenuFragmentInteraction(QuoteType quoteType) {
        switch (quoteType) {
            case CURRENCY_EXCHANGE:
                CurrencyExchangeFragment fragment = CurrencyExchangeFragment
                        .newInstance(widgetItemPosition, quoteType.getValue());
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_place, fragment).commit();

                break;
            case GOODS:
                GoodsItemFragment fragment1 = GoodsItemFragment
                        .newInstance(widgetItemPosition, quoteType.getValue());
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_place, fragment1).commit();
                break;
            default:
                Toast.makeText(EconomicWidgetConfigureActivity.this, "Not implemented!", Toast.LENGTH_SHORT);
                break;
        }
    }

    @Override
    public void setWidgetItemPosition(int widgetItemPosition) {
        this.widgetItemPosition = widgetItemPosition;
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_accept:
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_place);
                if(fragment instanceof PlaceStockItemsFragment) {
                    acceptBtnPressed();
                } else if (fragment instanceof CurrencyExchangeFragment) {
                    mDataSource.addSettingsRec(mAppWidgetId, widgetItemPosition,
                            QuoteType.CURRENCY_EXCHANGE.toString(),
                            ((CurrencyExchangeFragment) fragment).getSymbol());

                    fragment = PlaceStockItemsFragment.newInstance(mAppWidgetId, null);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_place, fragment).commit();

                } else if (fragment instanceof GoodsItemFragment) {
                    mDataSource.addSettingsRec(mAppWidgetId, widgetItemPosition,
                            QuoteType.GOODS.toString(),
                            ((GoodsItemFragment) fragment).getSymbol());

                    fragment = PlaceStockItemsFragment.newInstance(mAppWidgetId, null);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_place, fragment).commit();

                }
                if (null != fragment) {
                    Log.d(LOG_TAG, "onOptionsItemSelected: fragment: " + fragment.getClass().getName());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void acceptBtnPressed(){
        final Context context = EconomicWidgetConfigureActivity.this;

        // When the button is clicked, store the string locally
        saveTitlePref(context, mAppWidgetId, "EXAMPLE");

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        EconomicWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId, new ArrayList<Model>());

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.economic_widget_configure);

        if (savedInstanceState != null) position = savedInstanceState.getInt("position");

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        Fragment fragment = PlaceStockItemsFragment.newInstance(mAppWidgetId, null);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.fragment_place, fragment);
//        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();

        // создаем объект для создания и управления версиями БД
        mDataSource = new QuoteDataSource(this);
        mDataSource.open();
//        mAppWidgetText.setText(loadTitlePref(EconomicWidgetConfigureActivity.this, mAppWidgetId));

        Log.d(LOG_TAG, "onCreate");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mDataSource) mDataSource.close();
    }

    void showQuoteFragment(QuoteType quoteType) {
//        if (mIsDualPane) {
//            GoodsItemFragment details = (GoodsItemFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.cont);
//            if (details == null || details.getQuoteTypeValue() != quoteTypeValue) {
//                details = GoodsItemFragment.newInstance(widgetItemPosition, quoteTypeValue);
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.cont, details).commit();
//            }
//        } else {
//            startActivity(new Intent(this, SecondConfigureActivity.class)
//                    .putExtra("quoteTypeValue", quoteType.getValue())
//                    .putExtra("widgetItemPosition", widgetItemPosition));
//        }
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = EconomicWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
//            String widgetText = mAppWidgetText.getText().toString();
            saveTitlePref(context, mAppWidgetId, "EXAMPLE");

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            EconomicWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId, new ArrayList<Model>());

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.commit();
    }
}



