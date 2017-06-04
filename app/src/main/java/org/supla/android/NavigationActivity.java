package org.supla.android;

/*
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

 Author: Przemyslaw Zygmunt p.zygmunt@acsoftware.pl [AC SOFTWARE]
 */


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.supla.android.lib.SuplaClient;

import java.util.Date;
import java.util.concurrent.TimeUnit;


public class NavigationActivity extends BaseActivity implements View.OnClickListener {

    public static final String INTENTSENDER = "sender";
    public static final String INTENTSENDER_MAIN = "main";
    private static Activity CurrentActivity = null;

    private RelativeLayout RootLayout;
    private RelativeLayout ContentLayout;
    private RelativeLayout MenuBarLayout;
    private RelativeLayout MenuItemsLayout;
    private ViewGroup Content;

    private Button MenuButton;

    private Button MiSettings;
    private Button MiAbout;
    private Button MiFeedback;

    private Button SettingsButton;
    private Button AboutButton;
    private Button FeedbackButton;
    private Button HomepageButton;

    private boolean Anim = false;

    @Override
    protected void onResume() {

        super.onResume();
        CurrentActivity = this;
    };

    @Override
    protected void onPause() {
        super.onPause();

        if ( CurrentActivity == this ) {
            CurrentActivity = null;
        }
    }


    protected RelativeLayout getRootLayout() {

        if ( RootLayout == null ) {

            RootLayout = new RelativeLayout(this);
            RootLayout.setId(ViewHelper.generateViewId());
            RootLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            super.setContentView(RootLayout);
        }

        return RootLayout;
    }

    protected View Inflate(int resID, ViewGroup root) {
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(resID, root);
    }

    private RelativeLayout getMenuBarLayout() {

        if ( MenuBarLayout == null ) {

            MenuBarLayout = (RelativeLayout)Inflate(R.layout.menubar, null);
            MenuBarLayout.setVisibility(View.GONE);

            TextView title = (TextView)MenuBarLayout.findViewById(R.id.menubar_title);
            Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Quicksand-Regular.ttf");
            title.setTypeface(type);

            getRootLayout().addView(MenuBarLayout);

            MenuButton = (Button)findViewById(R.id.menubutton);
            MenuButton.setVisibility(View.GONE);
            MenuButton.setOnClickListener(this);

        }

        return MenuBarLayout;
    }

    private RelativeLayout getMenuItemsLayout() {

        if ( MenuItemsLayout == null ) {
            MenuItemsLayout = (RelativeLayout)Inflate(R.layout.menuitems, null);
            MenuItemsLayout.setVisibility(View.GONE);

            MiSettings = (Button)MenuItemsLayout.findViewById(R.id.menuitem_settings);
            MiAbout = (Button)MenuItemsLayout.findViewById(R.id.menuitem_about);
            MiFeedback = (Button)MenuItemsLayout.findViewById(R.id.menuitem_feedback);

            MiSettings.setOnClickListener(this);
            MiAbout.setOnClickListener(this);
            MiFeedback.setOnClickListener(this);

            SettingsButton = (Button)MenuItemsLayout.findViewById(R.id.btn_settings);
            AboutButton = (Button)MenuItemsLayout.findViewById(R.id.btn_about);
            FeedbackButton = (Button)MenuItemsLayout.findViewById(R.id.btn_feedback);
            HomepageButton = (Button)MenuItemsLayout.findViewById(R.id.btn_homepage);

            SettingsButton.setOnClickListener(this);
            AboutButton.setOnClickListener(this);
            FeedbackButton.setOnClickListener(this);
            HomepageButton.setOnClickListener(this);

            Typeface type = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Regular.ttf");
            SettingsButton.setTypeface(type);
            AboutButton.setTypeface(type);
            FeedbackButton.setTypeface(type);

            type = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Bold.ttf");
            HomepageButton.setTypeface(type);

            SettingsButton.setTransformationMethod(null);
            AboutButton.setTransformationMethod(null);
            FeedbackButton.setTransformationMethod(null);
            HomepageButton.setTransformationMethod(null);

            getRootLayout().addView(MenuItemsLayout);
        }

        return MenuItemsLayout;
    }

    protected RelativeLayout getContentLayout() {

        if ( ContentLayout == null ) {

            ContentLayout = new RelativeLayout(this);
            ContentLayout.setId(ViewHelper.generateViewId());
            ContentLayout.setBackgroundColor(getResources().getColor(R.color.activity_bg));

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);

            ContentLayout.setLayoutParams(lp);
            getRootLayout().addView(ContentLayout);
        }

        return ContentLayout;
    }

    protected ViewGroup getContentView() {
        return Content;
    }

    @Override
    public void setContentView(int layoutResID) {

        if ( Content != null ) {
            getContentLayout().removeView(Content);
            Content = null;
        }

        Content = (ViewGroup)Inflate(layoutResID, getContentLayout());

    }

    public void showMenuButton() {
        getMenuBarLayout();
        MenuButton.setVisibility(View.VISIBLE);
    }

    public void hideMenuButton() {
        getMenuBarLayout();
        MenuButton.setVisibility(View.GONE);
    }

    public boolean menuIsVisible() {
        return getMenuItemsLayout().getVisibility() == View.VISIBLE;
    }

    private void showHideMenu(boolean Show, boolean Animated) {

        if ( Show && menuIsVisible() ) return;
        if ( !Show && !menuIsVisible() ) return;

        if ( Show ) {

            if ( Anim ) return;

            getMenuItemsLayout().setTop(getMenuItemsLayout().getHeight() * -1 + getMenuBarLayout().getHeight() );
            getMenuItemsLayout().setVisibility(View.VISIBLE);
            getMenuItemsLayout().bringToFront();
            getMenuBarLayout().bringToFront();

            if ( Animated ) {

                Anim = true;

                getMenuItemsLayout()
                        .animate()
                        .translationY(getMenuBarLayout().getHeight())
                        .setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                Anim = false;
                            }
                        });
            } else {
                getMenuItemsLayout().setTop(getMenuBarLayout().getHeight());
            }

        } else {

            if ( Animated ) {

                if ( Anim ) return;
                Anim = true;

                getMenuItemsLayout()
                        .animate()
                        .translationY(getMenuItemsLayout().getHeight() * -1 + getMenuBarLayout().getHeight())
                        .setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                getMenuItemsLayout().setVisibility(View.GONE);
                                Anim = false;
                            }
                        });
            } else {
                getMenuItemsLayout().setVisibility(View.GONE);
            }

        }


    }

    public void showMenuBar() {

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.BELOW, getMenuBarLayout().getId());
        getContentLayout().setLayoutParams(lp);

        if ( MenuBarLayout != null )
            MenuBarLayout.setVisibility(View.VISIBLE);
    }

    public void hideMenuBar() {

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        getContentLayout().setLayoutParams(lp);

        if ( MenuBarLayout != null )
            MenuBarLayout.setVisibility(View.GONE);
    }

    public void showMenu(boolean Animated) {

        showHideMenu(true, Animated);

    }

    public void hideMenu(boolean Animated) {

        showHideMenu(false, Animated);
    }

    public void openHomepage() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.homepage_url)));
        startActivity(browserIntent);
    }

    private static void showActivity(Activity sender,  Class<?> cls, int flags) {

        Intent i = new Intent(sender.getBaseContext(), cls);
        i.setFlags(flags == 0 ? Intent.FLAG_ACTIVITY_REORDER_TO_FRONT : flags);
        i.putExtra(INTENTSENDER, sender instanceof MainActivity ? INTENTSENDER_MAIN : "");
        sender.startActivity(i);

        sender.overridePendingTransition( R.anim.fade_in, R.anim.fade_out);
    }

    public static void showMain(Activity sender) {



        SuplaClient client = SuplaApp.getApp().getSuplaClient();

        if ( client != null
                && client.Registered() ) {

            showActivity(sender, MainActivity.class, 0);

        } else {
            showStatus(sender);
        }



    }

    public static void showStatus(Activity sender) {
        showActivity(sender, StatusActivity.class, 0);
    }

    public static void showCfg(Activity sender) {
        showActivity(sender, CfgActivity.class, 0);
    }

    public void showAbout() {
        showActivity(this, AboutActivity.class, 0);
    }

    public void gotoMain() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {

        if (  v != MenuButton
              && menuIsVisible() ) {

            hideMenu(true);
        }

        if ( v == MenuButton )  {

            if ( menuIsVisible() )
                hideMenu(true);
            else
                showMenu(true);

        } else if ( v == MiSettings || v == SettingsButton) {

            showCfg(this);

        } else if ( v == MiAbout || v == AboutButton ) {

            showAbout();

        } else if ( v == MiFeedback || v == FeedbackButton ) {

            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("text/plain");

            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ getResources().getString(R.string.feedback_email) });
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback_subject));
            startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.feedback_title)));

        } else if ( v == HomepageButton ) {

            openHomepage();

        }

    }

    @Override
    protected void BeforeStatusMsg() {
        super.BeforeStatusMsg();

        if (  CurrentActivity != null
                && !(CurrentActivity instanceof StatusActivity)
                && !(CurrentActivity instanceof CfgActivity) ) {
            showStatus(this);
        }
    }

}
