package com.example.myapplication.projectdetails;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.etiennelawlor.imagegallery.library.ImageGalleryFragment;
import com.etiennelawlor.imagegallery.library.activities.FullScreenImageGalleryActivity;
import com.etiennelawlor.imagegallery.library.activities.ImageGalleryActivity;
import com.etiennelawlor.imagegallery.library.adapters.FullScreenImageGalleryAdapter;
import com.etiennelawlor.imagegallery.library.adapters.ImageGalleryAdapter;
import com.etiennelawlor.imagegallery.library.enums.PaletteColorType;
import com.example.myapplication.R;
import com.example.myapplication.adapter.ProjectAdapter;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.model.ProjectModel;
import com.example.myapplication.network.VolleyResponseListener;
import com.example.myapplication.network.VolleyUtils;
import com.example.myapplication.util.JsonUtil;
import com.example.myapplication.util.NetworkUtil;
import com.example.myapplication.util.ToastUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Response;

import static com.example.myapplication.function.CheckHideShowHelper.DEBUG_TAG;

public class GallaryActivity extends AppCompatActivity implements ImageGalleryAdapter.ImageThumbnailLoader, FullScreenImageGalleryAdapter.FullScreenImageLoader {
    public static final String API_KEY = "AIzaSyBx7v0YOb140fDO7EbfMx4l87raxezDWFw";
    public static final String VIDEO_ID = "zSc1jynV32Y";
    //  public static final String VIDEO_ID = "-m3V8w_7vhk";
    private GoogleMap mMap;

    private ViewPager mPager;
    private int currentPage = 0;
    private int NUM_PAGES = 0;
    private ArrayList<ImageModel> imageModelArrayList;
    // private String videoUri = "https://clips.vorwaerts-gmbh.de/VfE_html5.mp4";
    // private String videoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
    // private String videoUri = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4";
    //  private String videoUri = "https://www.youtube.com/watch?v=zSc1jynV32Y";
    // private String videoUri = "https://www.youtube.com/watch?v=gvfoN5akanY&feature=youtu.be";
    //  private String videoUri = "https://www.youtube.com/watch?v=d-nxFHYsxN0";
    //  private String videoUri = "https://youtu.be/gXH65tmB2e4";
    //  private String videoUri = "www.youtube.com/watch?v=gXH65tmB2e4&feature=youtu.be";
    //  private String videoUri = "https://www.youtube.com/watch?v=gXH65tmB2e4&feature=youtu.be";
    private String videoUri = "https://youtu.be/gXH65tmB2e4";
    // private String videoUri = "";
    final String youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
    final String[] videoIdRegex = {"\\?vi?=([^&]*)", "watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"};
    private SharedPrefManager prefManager;


    private Toolbar toolbar;
    private PaletteColorType paletteColorType;
    private ImageView iv_fb, iv_link;
    private ImageView iv_gplus;
    private ImageView iv_call, iv_walkthrou, iv_tiwiter;
    private List<AmenitiesModel> amenitiesModelList;
    private RecyclerView rv_amenities, rv_floor_plan, rv_price_list, tv_payment_plan, rv_Brochure, rv_Banners;
    private SharedPreferences sharedPref;
    private ShimmerFrameLayout mShimmerViewContainer;
    private WebView webview;
    private YouTubePlayerSupportFragment frag;
    private String phoneCall = "";
    private String fb = "";
    private String gPlus = "";
    private String walkthrow = "";
    private String tw = "";
    private String link = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallary);
        sharedPref = getSharedPreferences("customerpp", Context.MODE_PRIVATE);
        webview = (WebView) findViewById(R.id.webView1);
        frag = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        prefManager = new SharedPrefManager(GallaryActivity.this);


        ImageGalleryActivity.setImageThumbnailLoader(GallaryActivity.this);
        ImageGalleryFragment.setImageThumbnailLoader(GallaryActivity.this);
        FullScreenImageGalleryActivity.setFullScreenImageLoader(GallaryActivity.this);

        // optionally set background color using Palette for full screen images
        paletteColorType = PaletteColorType.VIBRANT;

        imageModelArrayList = new ArrayList<>();
        final Intent intent = getIntent();
        final String projectName = intent.getStringExtra("projectName");
        final String projectCode = intent.getStringExtra("projectCode");

        ((TextView) findViewById(R.id.tv_title)).setText(projectName);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);


        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitleTextColor(Color.WHITE);


        //placing toolbar in place of actionbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");

        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
       /* String clientLogo = sharedPref.getString("clientImageUrl", "");

        ImageView iv_logo =  (ImageView) findViewById(R.id.iv_logo);
        Picasso.with(GallaryActivity.this)
                .load(clientLogo)
                .error(R.mipmap.ic_launcher)
                .into(iv_logo);*/
        init();
        mShimmerViewContainer.startShimmerAnimation();

        getProjectDetails(projectCode);

    }


    private void init() {



     /*   YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player_view);
        youTubePlayerView.initialize(API_KEY, this);
*/
        /*VideoView videoView = (VideoView) findViewById(R.id.video_view);
        videoView.setVideoPath(videoUri).getPlayer().start();*/

        iv_fb = (ImageView) findViewById(R.id.iv_fb);
        iv_link = (ImageView) findViewById(R.id.iv_link);
        iv_gplus = (ImageView) findViewById(R.id.iv_gplus);
        iv_call = (ImageView) findViewById(R.id.iv_call);
        iv_walkthrou = (ImageView) findViewById(R.id.iv_walkthrou);
        iv_tiwiter = (ImageView) findViewById(R.id.iv_tiwiter);
        rv_amenities = (RecyclerView) findViewById(R.id.rv_amenities);
        rv_floor_plan = (RecyclerView) findViewById(R.id.rv_floor_plan);
        rv_price_list = (RecyclerView) findViewById(R.id.rv_price_list);
        tv_payment_plan = (RecyclerView) findViewById(R.id.tv_payment_plan);
        rv_Brochure = (RecyclerView) findViewById(R.id.rv_Brochure);
        rv_Banners = (RecyclerView) findViewById(R.id.rv_Banners);
        mPager = (ViewPager) findViewById(R.id.pager);

        findViewById(R.id.rv_floor_plan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GallaryActivity.this, ImageGalleryActivity.class);

                String[] images = getResources().getStringArray(R.array.unsplash_images);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(ImageGalleryActivity.KEY_IMAGES, new ArrayList<>(Arrays.asList(images)));
                bundle.putString(ImageGalleryActivity.KEY_TITLE, "Floor Plans");
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });


       /* Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "Titillium-Regular.otf");
        ((TextView) findViewById(R.id.tv_title)).setTypeface(custom_font1);*/


        webview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

       /* findViewById(R.id.iv_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
       *//* Intent intent = new Intent(GallaryActivity.this,MapsActivity.class);
        startActivity(intent);
        http://maps.google.com/maps?daddr=28.605989,77.372970"

        *//*
         *//* String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 19.130558, 72.932270);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
       startActivity(intent);*//*
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=19.130558,72.932270"));
                startActivity(intent);
            }
        }); findViewById(R.id.ll_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=19.130558,72.932270"));
                startActivity(intent);
            }
        });*/
        findViewById(R.id.iv_fb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fb));
                startActivity(browserIntent);
            }
        });
        findViewById(R.id.iv_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);

            }
        });
        findViewById(R.id.iv_gplus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(gPlus));
                startActivity(browserIntent);

            }
        });
        findViewById(R.id.iv_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneCall));
                startActivity(intent);

            }
        });
        findViewById(R.id.iv_walkthrou).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(walkthrow));
                startActivity(browserIntent);
            }
        });
        findViewById(R.id.iv_tiwiter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tw));
                startActivity(browserIntent);

            }
        });


    }

    @Override
    public void loadFullScreenImage(final ImageView iv, String imageUrl, int width, final LinearLayout bgLinearLayout) {


        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.with(iv.getContext())
                    .load(imageUrl)
                    .resize(width, 0)
                    .into(iv, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
                            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette palette) {
                                    applyPalette(palette, bgLinearLayout);
                                }
                            });
                        }

                        @Override
                        public void onError() {

                        }
                    });
        } else {
            iv.setImageDrawable(null);
        }

    }

    @Override
    public void loadImageThumbnail(ImageView iv, String imageUrl, int dimension) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.with(iv.getContext())
                    .load(imageUrl)
                    .resize(dimension, dimension)
                    .centerCrop()
                    .into(iv);
        } else {
            iv.setImageDrawable(null);
        }

    }

    private void applyPalette(Palette palette, ViewGroup viewGroup) {
        int bgColor = getBackgroundColor(palette);
        if (bgColor != -1)
            viewGroup.setBackgroundColor(bgColor);
    }

    private int getBackgroundColor(Palette palette) {
        int bgColor = -1;

        int vibrantColor = palette.getVibrantColor(0x000000);
        int lightVibrantColor = palette.getLightVibrantColor(0x000000);
        int darkVibrantColor = palette.getDarkVibrantColor(0x000000);

        int mutedColor = palette.getMutedColor(0x000000);
        int lightMutedColor = palette.getLightMutedColor(0x000000);
        int darkMutedColor = palette.getDarkMutedColor(0x000000);

        if (paletteColorType != null) {
            switch (paletteColorType) {
                case VIBRANT:
                    if (vibrantColor != 0) { // primary option
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) { // fallback options
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    } else if (mutedColor != 0) {
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    }
                    break;
                case LIGHT_VIBRANT:
                    if (lightVibrantColor != 0) { // primary option
                        bgColor = lightVibrantColor;
                    } else if (vibrantColor != 0) { // fallback options
                        bgColor = vibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    } else if (mutedColor != 0) {
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    }
                    break;
                case DARK_VIBRANT:
                    if (darkVibrantColor != 0) { // primary option
                        bgColor = darkVibrantColor;
                    } else if (vibrantColor != 0) { // fallback options
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (mutedColor != 0) {
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    }
                    break;
                case MUTED:
                    if (mutedColor != 0) { // primary option
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) { // fallback options
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    } else if (vibrantColor != 0) {
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    }
                    break;
                case LIGHT_MUTED:
                    if (lightMutedColor != 0) { // primary option
                        bgColor = lightMutedColor;
                    } else if (mutedColor != 0) { // fallback options
                        bgColor = mutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    } else if (vibrantColor != 0) {
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    }
                    break;
                case DARK_MUTED:
                    if (darkMutedColor != 0) { // primary option
                        bgColor = darkMutedColor;
                    } else if (mutedColor != 0) { // fallback options
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (vibrantColor != 0) {
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    }
                    break;
                default:
                    break;
            }
        }

        return bgColor;
    }

    private void getProjectDetails(String projectCode) {
        if (NetworkUtil.isNetworkOnline(GallaryActivity.this)) {
            String URL_GET = prefManager.getClientServerUrl() + "/pages/sqls/customerappsqls.jsp?action=projectdetails&t=t&token=" + prefManager.getAuthToken() + "&cloudcode=" + prefManager.getCloudCode() + "&project_code=" + projectCode;
           /* String URL_GET = String.format(Constant.URL_SINGLE_CHART_ID,
                    prefManager.getClientServerUrl(),
                    VFORM_ID,
                    prefManager.getAuthToken(),
                    prefManager.getCloudCode());*/

            Log.e(DEBUG_TAG, "callGetChartIdApi url = " + URL_GET);

            VolleyUtils.GET_METHOD(GallaryActivity.this, URL_GET, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    System.out.println("Error" + message);
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

                }

                @Override
                public void onResponse(Object response) {
                    Log.e("SUCCESS", (String) response);
                    try {
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        JSONArray jsonArray = new JSONArray((String) response);
                        //  List<ProjectDetailsModel> projectModelList = (List<ProjectDetailsModel>) JsonUtil.jsonArrayToListObject(jsonArray.toString(),ProjectDetailsModel.class);
                        List<ProjectDetailsModel> projectModelList = new ArrayList<ProjectDetailsModel>();


                        Log.e("SUCCESS", (String) response);


                        for (int y = 0; y < jsonArray.length(); y++) {
                            ProjectDetailsModel projectModel = new ProjectDetailsModel();
                            JSONObject jObj = jsonArray.getJSONObject(y);
                            if (jObj.has("PROJECT")) {
                                projectModel.setPROJECT(jObj.getString("PROJECT"));
                            }
                            if (jObj.has("PROJECTDESC")) {
                                projectModel.setPROJECTDESC(jObj.getString("PROJECTDESC"));
                            }
                            if (jObj.has("LOGO")) {
                                projectModel.setLOGO(jObj.getString("LOGO"));
                            }
                            if (jObj.has("RERANO")) {
                                projectModel.setRERANO(jObj.getString("RERANO"));
                            }
                            if (jObj.has("WALKTHROUGH")) {
                                projectModel.setWALKTHROUGH(jObj.getString("WALKTHROUGH"));
                            }
                            if (jObj.has("VIDEOURL")) {
                                projectModel.setVIDEOURL(jObj.getString("VIDEOURL"));
                            }
                            if (jObj.has("CALLUS")) {
                                projectModel.setCALLUS(jObj.getString("CALLUS"));
                            }
                            if (jObj.has("FB")) {
                                projectModel.setFB(jObj.getString("FB"));
                            }
                            if (jObj.has("IN")) {
                                projectModel.setIN(jObj.getString("IN"));
                            }
                            if (jObj.has("GPLUS")) {
                                projectModel.setGPLUS(jObj.getString("GPLUS"));
                            }
                            if (jObj.has("TWITTER")) {
                                projectModel.setTWITTER(jObj.getString("TWITTER"));
                            }
                            if (jObj.has("LOCATION")) {
                                projectModel.setLOCATION(jObj.getString("LOCATION"));
                            }
                            if (jObj.has("PRICELIST")) {
                                // projectModel.setLOCATION(jObj.getString("PRICELIST"));
                                JSONArray jsonArrayPRICELIST = new JSONArray(jObj.getString("PRICELIST"));
                                List<PriceListModel> priceListModelList = new ArrayList<PriceListModel>();


                                for (int k = 0; k < jsonArrayPRICELIST.length(); k++) {
                                    PriceListModel priceListModel = new PriceListModel();
                                    JSONObject jObjk = jsonArrayPRICELIST.getJSONObject(k);

                                    if (jObjk.has("id")) {
                                        priceListModel.setId(jObjk.getString("id"));
                                    }
                                    if (jObjk.has("pricedesc")) {
                                        priceListModel.setPricedesc(jObjk.getString("pricedesc"));
                                    }
                                    if (jObjk.has("priceimage")) {
                                        priceListModel.setPriceimage(jObjk.getString("priceimage"));
                                    }
                                    priceListModelList.add(priceListModel);
                                }
                                projectModel.setPRICELIST(priceListModelList);
                            }
                            if (jObj.has("PAYMENTPLAN")) {
                                // projectModel.setLOCATION(jObj.getString("PRICELIST"));
                                JSONArray jsonArrayPRICELIST = new JSONArray(jObj.getString("PAYMENTPLAN"));
                                List<PaymentPlanModel> priceListModelList = new ArrayList<PaymentPlanModel>();


                                for (int k = 0; k < jsonArrayPRICELIST.length(); k++) {
                                    PaymentPlanModel priceListModel = new PaymentPlanModel();
                                    JSONObject jObjk = jsonArrayPRICELIST.getJSONObject(k);

                                    if (jObjk.has("id")) {
                                        priceListModel.setId(jObjk.getString("id"));
                                    }
                                    if (jObjk.has("paymentdesc")) {
                                        priceListModel.setPaymentdesc(jObjk.getString("paymentdesc"));
                                    }
                                    if (jObjk.has("paymentimage")) {
                                        priceListModel.setPaymentimage(jObjk.getString("paymentimage"));
                                    }
                                    priceListModelList.add(priceListModel);
                                }
                                projectModel.setPAYMENTPLAN(priceListModelList);
                            }
                            if (jObj.has("BROCHURE")) {
                                // projectModel.setLOCATION(jObj.getString("PRICELIST"));
                                JSONArray jsonArrayPRICELIST = new JSONArray(jObj.getString("BROCHURE"));
                                List<BrochureModel> priceListModelList = new ArrayList<BrochureModel>();


                                for (int k = 0; k < jsonArrayPRICELIST.length(); k++) {
                                    BrochureModel priceListModel = new BrochureModel();
                                    JSONObject jObjk = jsonArrayPRICELIST.getJSONObject(k);

                                    if (jObjk.has("id")) {
                                        priceListModel.setId(jObjk.getString("id"));
                                    }
                                    if (jObjk.has("brochuredesc")) {
                                        priceListModel.setBrochuredesc(jObjk.getString("brochuredesc"));
                                    }
                                    if (jObjk.has("brochureimage")) {
                                        priceListModel.setBrochureimage(jObjk.getString("brochureimage"));
                                    }
                                    priceListModelList.add(priceListModel);
                                }
                                projectModel.setBROCHURE(priceListModelList);
                            }
                            if (jObj.has("BANNERS")) {
                                // projectModel.setLOCATION(jObj.getString("PRICELIST"));
                                JSONArray jsonArrayPRICELIST = new JSONArray(jObj.getString("BANNERS"));
                                List<BannersModel> priceListModelList = new ArrayList<BannersModel>();


                                for (int k = 0; k < jsonArrayPRICELIST.length(); k++) {
                                    BannersModel priceListModel = new BannersModel();
                                    JSONObject jObjk = jsonArrayPRICELIST.getJSONObject(k);

                                    if (jObjk.has("id")) {
                                        priceListModel.setId(jObjk.getString("id"));
                                    }
                                    if (jObjk.has("bannerdesc")) {
                                        priceListModel.setBannerdesc(jObjk.getString("bannerdesc"));
                                    }
                                    if (jObjk.has("bannerimage")) {
                                        priceListModel.setBannerimage(jObjk.getString("bannerimage"));
                                    }
                                    priceListModelList.add(priceListModel);
                                }
                                projectModel.setBANNERS(priceListModelList);
                            }
                            if (jObj.has("AMENITIES")) {
                                // projectModel.setLOCATION(jObj.getString("PRICELIST"));
                                JSONArray jsonArrayPRICELIST = new JSONArray(jObj.getString("AMENITIES"));
                                List<AmenitiesModel> priceListModelList = new ArrayList<AmenitiesModel>();


                                for (int k = 0; k < jsonArrayPRICELIST.length(); k++) {
                                    AmenitiesModel priceListModel = new AmenitiesModel();
                                    JSONObject jObjk = jsonArrayPRICELIST.getJSONObject(k);

                                    if (jObjk.has("id")) {
                                        priceListModel.setId(jObjk.getString("id"));
                                    }
                                    if (jObjk.has("amenitiesdesc")) {
                                        priceListModel.setAmenitiesdesc(jObjk.getString("amenitiesdesc"));
                                    }
                                    if (jObjk.has("amenitiesimage")) {
                                        priceListModel.setAmenitiesimage(jObjk.getString("amenitiesimage"));
                                    }
                                    priceListModelList.add(priceListModel);
                                }
                                projectModel.setAMENITIES(priceListModelList);
                            } if (jObj.has("FLOORPLAN")) {
                                // projectModel.setLOCATION(jObj.getString("PRICELIST"));
                                JSONArray jsonArrayPRICELIST = new JSONArray(jObj.getString("FLOORPLAN"));
                                List<FloorPlanModel> priceListModelList = new ArrayList<FloorPlanModel>();


                                for (int k = 0; k < jsonArrayPRICELIST.length(); k++) {
                                    FloorPlanModel priceListModel = new FloorPlanModel();
                                    JSONObject jObjk = jsonArrayPRICELIST.getJSONObject(k);

                                    if (jObjk.has("id")) {
                                        priceListModel.setId(jObjk.getString("id"));
                                    }
                                    if (jObjk.has("floorplandesc")) {
                                        priceListModel.setFloorplandesc(jObjk.getString("floorplandesc"));
                                    }
                                    if (jObjk.has("floorplanimage")) {
                                        priceListModel.setFloorplanimage(jObjk.getString("floorplanimage"));
                                    }
                                    priceListModelList.add(priceListModel);
                                }
                                projectModel.setFLOORPLAN(priceListModelList);
                            }if (jObj.has("GALLERY")) {
                                // projectModel.setLOCATION(jObj.getString("PRICELIST"));
                                JSONArray jsonArrayPRICELIST = new JSONArray(jObj.getString("GALLERY"));
                                List<GalleryModel> priceListModelList = new ArrayList<GalleryModel>();


                                for (int k = 0; k < jsonArrayPRICELIST.length(); k++) {
                                    GalleryModel priceListModel = new GalleryModel();
                                    JSONObject jObjk = jsonArrayPRICELIST.getJSONObject(k);

                                    if (jObjk.has("id")) {
                                        priceListModel.setId(jObjk.getString("id"));
                                    }
                                    if (jObjk.has("gallerydesc")) {
                                        priceListModel.setGallerydesc(jObjk.getString("gallerydesc"));
                                    }
                                    if (jObjk.has("galleryimage")) {
                                        priceListModel.setGalleryimage(jObjk.getString("galleryimage"));
                                    }
                                    priceListModelList.add(priceListModel);
                                }
                                projectModel.setGALLERY(priceListModelList);
                            }


                            projectModelList.add(projectModel);
                        }
                        getsetProjectDetails(projectModelList);

                    } catch (Exception e) {
                        e.printStackTrace();

                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            ToastUtil.showToastMessage("Please check your internet connection and try again", GallaryActivity.this);
        }
    }

    private void getsetProjectDetails(List<ProjectDetailsModel> projectDetailsModelList) {
        try {

            //  Toast.makeText(GallaryActivity.this, projectDetailsModelList.get(0).getGsplus() + "", Toast.LENGTH_SHORT).show();


            if (projectDetailsModelList.get(0).getFB() != null && !projectDetailsModelList.get(0).getFB().isEmpty()) {
                iv_fb.setVisibility(View.VISIBLE);
                fb = projectDetailsModelList.get(0).getFB();
            }
            if (projectDetailsModelList.get(0).getGPLUS() != null && !projectDetailsModelList.get(0).getGPLUS().isEmpty()) {
                // iv_gplus.setVisibility(View.VISIBLE);
                iv_gplus.setVisibility(View.GONE);
                gPlus = projectDetailsModelList.get(0).getGPLUS();

            }
            if (projectDetailsModelList.get(0).getIN() != null && !projectDetailsModelList.get(0).getIN().isEmpty()) {
                iv_link.setVisibility(View.VISIBLE);
                link = projectDetailsModelList.get(0).getIN();
            }

            if (projectDetailsModelList.get(0).getCALLUS() != null && !projectDetailsModelList.get(0).getCALLUS().isEmpty()) {
                iv_call.setVisibility(View.VISIBLE);
                phoneCall = projectDetailsModelList.get(0).getCALLUS();

            }
            if (projectDetailsModelList.get(0).getTWITTER() != null && !projectDetailsModelList.get(0).getTWITTER().isEmpty()) {
                iv_tiwiter.setVisibility(View.VISIBLE);
                tw = projectDetailsModelList.get(0).getTWITTER();

            }
            if (projectDetailsModelList.get(0).getWALKTHROUGH() != null && !projectDetailsModelList.get(0).getWALKTHROUGH().isEmpty()) {
                iv_walkthrou.setVisibility(View.VISIBLE);
                walkthrow = projectDetailsModelList.get(0).getWALKTHROUGH();

            }
            if (projectDetailsModelList.get(0).getVIDEOURL() != null && !projectDetailsModelList.get(0).getVIDEOURL().isEmpty()) {
                findViewById(R.id.ll_video).setVisibility(View.VISIBLE);
                videoUri = (projectDetailsModelList.get(0).getVIDEOURL());
                frag.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
                        Toast.makeText(GallaryActivity.this, "Failed to initialize.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                        if (null == player) return;

                        // Start buffering
                        String videoId = null;
                        if (!wasRestored) {
                            //  extractVideoIdFromUrl(videoUri);

                            String regex = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";
                            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(videoUri);
                            if (matcher.find()) {
                                videoId = matcher.group(1);
                                player.cueVideo(videoId);
                            } else {
                                String youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(videoUri);

                                for (String regexx : videoIdRegex) {
                                    Pattern compiledPattern = Pattern.compile(regexx);
                                    Matcher matcherr = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain);

                                    if (matcherr.find()) {
                                        videoId = matcherr.group(1);
                                        player.cueVideo(videoId);
                                    }
                                }
                            }


                        }


                        // Add listeners to YouTubePlayer instance
                        player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                            @Override
                            public void onAdStarted() {
                            }

                            @Override
                            public void onError(YouTubePlayer.ErrorReason arg0) {
                            }

                            @Override
                            public void onLoaded(String arg0) {
                            }

                            @Override
                            public void onLoading() {
                            }

                            @Override
                            public void onVideoEnded() {
                            }

                            @Override
                            public void onVideoStarted() {
                            }
                        });


                        player.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
                            @Override
                            public void onBuffering(boolean arg0) {
                            }

                            @Override
                            public void onPaused() {
                            }

                            @Override
                            public void onPlaying() {
                            }

                            @Override
                            public void onSeekTo(int arg0) {
                            }

                            @Override
                            public void onStopped() {
                            }
                        });
                    }
                });

            }
            String mapLoaction = projectDetailsModelList.get(0).getLOCATION();
            if (mapLoaction != null && !mapLoaction.isEmpty()) {
                findViewById(R.id.ll_loaction).setVisibility(View.VISIBLE);

                ((TextView) findViewById(R.id.tv_project_dec)).setText(projectDetailsModelList.get(0).getPROJECTDESC());
                // mapLoaction ="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d121012.41103115327!2d73.75410913501464!3d18.590359114281842!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3bc2c1cbead49d6f%3A0x791a98b8df7ebf03!2sSagitarius%20Ecospaces%20LLP!5e0!3m2!1sen!2sin!4v1580818503113!5m2!1sen!2sin";
                String iframe = "<iframe src=\"" + mapLoaction + "\" width=\"400\" height=\"300\" frameborder=\"0\" style=\"border:0;\" allowfullscreen=\"\"></iframe>\n";

                webview.setWebViewClient(new WebViewClient());
                webview.getSettings().setJavaScriptEnabled(true);
                webview.loadData(iframe, "text/html", "utf-8");
                webview.setEnabled(false);
                MyWebViewClient webViewClient = new MyWebViewClient(GallaryActivity.this);
                webview.setWebViewClient(webViewClient);
            } else {
                findViewById(R.id.ll_loaction).setVisibility(View.GONE);
            }


                      /*  new AlertDialog.Builder(GallaryActivity.this)
                                .setTitle("Really Exit?")
                                .setMessage(projectDetailsModelList + "")
                                .setNegativeButton(android.R.string.no, null)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0, int arg1) {
                                        arg0.dismiss();
                                    }
                                }).create().show();*/


            PriceListAdapter priceListAdapter = new PriceListAdapter(GallaryActivity.this, projectDetailsModelList.get(0).getPRICELIST(), prefManager);
            rv_price_list.setAdapter(priceListAdapter);
            LinearLayoutManager linearLayoutManagerpriceList = new LinearLayoutManager(GallaryActivity.this);
            linearLayoutManagerpriceList.setOrientation(LinearLayoutManager.HORIZONTAL);
            rv_price_list.setLayoutManager(linearLayoutManagerpriceList);

            PaymentPlanAdapter paymetpriceListAdapter = new PaymentPlanAdapter(GallaryActivity.this, projectDetailsModelList.get(0).getPAYMENTPLAN(), prefManager);
            tv_payment_plan.setAdapter(paymetpriceListAdapter);
            LinearLayoutManager paymentlinearLayoutManagerpriceList = new LinearLayoutManager(GallaryActivity.this);
            paymentlinearLayoutManagerpriceList.setOrientation(LinearLayoutManager.HORIZONTAL);
            tv_payment_plan.setLayoutManager(paymentlinearLayoutManagerpriceList);


            BrochureAdapter paymetpriceListAdapterBrochureAdapter = new BrochureAdapter(GallaryActivity.this, projectDetailsModelList.get(0).getBROCHURE(), prefManager);
            rv_Brochure.setAdapter(paymetpriceListAdapterBrochureAdapter);
            LinearLayoutManager paymentlinearLayoutManagerpriceListpaymetpriceListAdapterBrochureAdapter = new LinearLayoutManager(GallaryActivity.this);
            paymentlinearLayoutManagerpriceListpaymetpriceListAdapterBrochureAdapter.setOrientation(LinearLayoutManager.HORIZONTAL);
            rv_Brochure.setLayoutManager(paymentlinearLayoutManagerpriceListpaymetpriceListAdapterBrochureAdapter);

            BannersAdapter paymetpriceListAdapterBannersAdapter = new BannersAdapter(GallaryActivity.this, projectDetailsModelList.get(0).getBANNERS(), prefManager);
            rv_Banners.setAdapter(paymetpriceListAdapterBannersAdapter);
            LinearLayoutManager banners = new LinearLayoutManager(GallaryActivity.this);
            banners.setOrientation(LinearLayoutManager.HORIZONTAL);
            rv_Banners.setLayoutManager(banners);


            AmenitiesAdapter projectAdapter = new AmenitiesAdapter(GallaryActivity.this, projectDetailsModelList.get(0).getAMENITIES(), prefManager);
            rv_amenities.setAdapter(projectAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GallaryActivity.this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rv_amenities.setLayoutManager(linearLayoutManager);

            FloorPlanAdapter floorPlanAdapter = new FloorPlanAdapter(GallaryActivity.this, projectDetailsModelList.get(0).getFLOORPLAN(), prefManager);
            rv_floor_plan.setAdapter(floorPlanAdapter);
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(GallaryActivity.this);
            linearLayoutManager1.setOrientation(linearLayoutManager1.HORIZONTAL);
            rv_floor_plan.setLayoutManager(linearLayoutManager1);

            mPager.setAdapter(new SlidingImage_Adapter(GallaryActivity.this, projectDetailsModelList.get(0).getGALLERY(), prefManager));

            CirclePageIndicator indicator = (CirclePageIndicator)
                    findViewById(R.id.indicator);

            indicator.setViewPager(mPager);

            final float density = getResources().getDisplayMetrics().density;

            indicator.setRadius(5 * density);

            NUM_PAGES = imageModelArrayList.size();

            // Auto start of viewpager
            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == NUM_PAGES) {
                        currentPage = 0;
                    }
                    mPager.setCurrentItem(currentPage++, true);
                }
            };
            Timer swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 1500, 1500);

            // Pager listener over indicator
            indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;

                }

                @Override
                public void onPageScrolled(int pos, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int pos) {

                }
            });


        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(GallaryActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            //hud.dismiss();
        }

    }

/*
    private void getProjectDetailsf(String projectCode) {

        final SharedPreferences sharedPref = getSharedPreferences("customerpp", Context.MODE_PRIVATE);

        Api service = RetrofitClientInstance.getRetrofitInstance(sharedPref.getString("Client_Server_URL", "")).create(Api.class);


        Call<List<ProjectDetailsModel>> call = service.getProjectDetailss(sharedPref.getString("auth_token", ""), sharedPref.getString("cloudCode", ""), projectCode);

      */
/*  final KProgressHUD hud = KProgressHUD.create(GallaryActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setMaxProgress(100)
                .show();
        hud.setProgress(90);*//*

        call.enqueue(new retrofit2.Callback<List<ProjectDetailsModel>>() {
            @Override
            public void onResponse(Call<List<ProjectDetailsModel>> call, Response<List<ProjectDetailsModel>> response) {
                if (response.body() != null) {
                    String responsedd = null;
                    try {
                        List<ProjectDetailsModel> projectDetailsModelList = response.body();

                        //  Toast.makeText(GallaryActivity.this, projectDetailsModelList.get(0).getGsplus() + "", Toast.LENGTH_SHORT).show();


                        if (projectDetailsModelList.get(0).getFB() != null && !projectDetailsModelList.get(0).getFB().isEmpty()) {
                            iv_fb.setVisibility(View.VISIBLE);
                            fb =projectDetailsModelList.get(0).getFB();
                        }
                        if (projectDetailsModelList.get(0).getGPLUS() != null && !projectDetailsModelList.get(0).getGPLUS().isEmpty()) {
                           // iv_gplus.setVisibility(View.VISIBLE);
                            iv_gplus.setVisibility(View.GONE);
                           gPlus = projectDetailsModelList.get(0).getGPLUS();

                        }
                        if (projectDetailsModelList.get(0).getIN()!= null && !projectDetailsModelList.get(0).getIN().isEmpty()) {
                            iv_link.setVisibility(View.VISIBLE);
                            link =projectDetailsModelList.get(0).getIN();
                        }

                        if (projectDetailsModelList.get(0).getCALLUS()!= null && !projectDetailsModelList.get(0).getCALLUS().isEmpty()) {
                            iv_call.setVisibility(View.VISIBLE);
                            phoneCall = projectDetailsModelList.get(0).getCALLUS();

                         }
                        if (projectDetailsModelList.get(0).getTWITTER()!=null && !projectDetailsModelList.get(0).getTWITTER().isEmpty()) {
                            iv_tiwiter.setVisibility(View.VISIBLE);
                            tw =projectDetailsModelList.get(0).getTWITTER();

                        }
                        if (projectDetailsModelList.get(0).getWALKTHROUGH()!= null && !projectDetailsModelList.get(0).getWALKTHROUGH().isEmpty()) {
                            iv_walkthrou.setVisibility(View.VISIBLE);
                            walkthrow = projectDetailsModelList.get(0).getWALKTHROUGH();

                        }if (projectDetailsModelList.get(0).getVIDEOURL()!= null && !projectDetailsModelList.get(0).getVIDEOURL().isEmpty()) {
                            findViewById(R.id.ll_video).setVisibility(View.VISIBLE);
                           videoUri = (projectDetailsModelList.get(0).getVIDEOURL());
                            frag.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
                                @Override
                                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
                                    Toast.makeText(GallaryActivity.this, "Failed to initialize.", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                                    if (null == player) return;

                                    // Start buffering
                                    String videoId = null;
                                    if (!wasRestored) {
                                      //  extractVideoIdFromUrl(videoUri);

                                        String regex = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";
                                       Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                                       Matcher matcher = pattern.matcher(videoUri);
                                       if (matcher.find()) {
                                            videoId = matcher.group(1);
                                            player.cueVideo(videoId);
                                        }else{
                                               String youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(videoUri);

                                        for(String regexx : videoIdRegex) {
                                            Pattern compiledPattern = Pattern.compile(regexx);
                                            Matcher matcherr = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain);

                                            if(matcherr.find()){
                                                videoId = matcherr.group(1);
                                                player.cueVideo(videoId);
                                            }
                                        }
                                       }



                                    }


                                    // Add listeners to YouTubePlayer instance
                                    player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                                        @Override
                                        public void onAdStarted() {
                                        }

                                        @Override
                                        public void onError(YouTubePlayer.ErrorReason arg0) {
                                        }

                                        @Override
                                        public void onLoaded(String arg0) {
                                        }

                                        @Override
                                        public void onLoading() {
                                        }

                                        @Override
                                        public void onVideoEnded() {
                                        }

                                        @Override
                                        public void onVideoStarted() {
                                        }
                                    });


                                    player.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
                                        @Override
                                        public void onBuffering(boolean arg0) {
                                        }

                                        @Override
                                        public void onPaused() {
                                        }

                                        @Override
                                        public void onPlaying() {
                                        }

                                        @Override
                                        public void onSeekTo(int arg0) {
                                        }

                                        @Override
                                        public void onStopped() {
                                        }
                                    });
                                }
                            });

                        }
                     String mapLoaction =   projectDetailsModelList.get(0).getLOCATION();
                        ((TextView) findViewById(R.id.tv_project_dec)).setText(projectDetailsModelList.get(0).getPROJECTDESC());

                        if(mapLoaction!=null && !mapLoaction.isEmpty()) {
                            findViewById(R.id.ll_loaction).setVisibility(View.VISIBLE);

                            // mapLoaction ="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d121012.41103115327!2d73.75410913501464!3d18.590359114281842!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3bc2c1cbead49d6f%3A0x791a98b8df7ebf03!2sSagitarius%20Ecospaces%20LLP!5e0!3m2!1sen!2sin!4v1580818503113!5m2!1sen!2sin";
                            String iframe = "<iframe src=\"" + mapLoaction + "\" width=\"400\" height=\"300\" frameborder=\"0\" style=\"border:0;\" allowfullscreen=\"\"></iframe>\n";

                            webview.setWebViewClient(new WebViewClient());
                            webview.getSettings().setJavaScriptEnabled(true);
                            webview.loadData(iframe, "text/html", "utf-8");
                            webview.setEnabled(false);
                            MyWebViewClient webViewClient = new MyWebViewClient(GallaryActivity.this);
                            webview.setWebViewClient(webViewClient);
                        }else {
                            findViewById(R.id.ll_loaction).setVisibility(View.GONE);
                        }


                      */
/*  new AlertDialog.Builder(GallaryActivity.this)
                                .setTitle("Really Exit?")
                                .setMessage(projectDetailsModelList + "")
                                .setNegativeButton(android.R.string.no, null)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0, int arg1) {
                                        arg0.dismiss();
                                    }
                                }).create().show();*//*



                        AmenitiesAdapter projectAdapter = new AmenitiesAdapter(GallaryActivity.this, projectDetailsModelList.get(0).getAMENITIES());
                        rv_amenities.setAdapter(projectAdapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GallaryActivity.this);
                        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        rv_amenities.setLayoutManager(linearLayoutManager);

                        FloorPlanAdapter floorPlanAdapter = new FloorPlanAdapter(GallaryActivity.this, projectDetailsModelList.get(0).getFLOORPLAN());
                        rv_floor_plan.setAdapter(floorPlanAdapter);
                        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(GallaryActivity.this);
                        linearLayoutManager1.setOrientation(linearLayoutManager1.HORIZONTAL);
                        rv_floor_plan.setLayoutManager(linearLayoutManager1);

                        mPager.setAdapter(new SlidingImage_Adapter(GallaryActivity.this, projectDetailsModelList.get(0).getGALLERY()));

                        CirclePageIndicator indicator = (CirclePageIndicator)
                                findViewById(R.id.indicator);

                        indicator.setViewPager(mPager);

                        final float density = getResources().getDisplayMetrics().density;

                        indicator.setRadius(5 * density);

                        NUM_PAGES = imageModelArrayList.size();

                        // Auto start of viewpager
                        final Handler handler = new Handler();
                        final Runnable Update = new Runnable() {
                            public void run() {
                                if (currentPage == NUM_PAGES) {
                                    currentPage = 0;
                                }
                                mPager.setCurrentItem(currentPage++, true);
                            }
                        };
                        Timer swipeTimer = new Timer();
                        swipeTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                handler.post(Update);
                            }
                        }, 1500, 1500);

                        // Pager listener over indicator
                        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                            @Override
                            public void onPageSelected(int position) {
                                currentPage = position;

                            }

                            @Override
                            public void onPageScrolled(int pos, float arg1, int arg2) {

                            }

                            @Override
                            public void onPageScrollStateChanged(int pos) {

                            }
                        });


                    } catch (Exception e) {

                        e.printStackTrace();
                        Toast.makeText(GallaryActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        //hud.dismiss();
                    }


                }
              //  hud.dismiss();
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<List<ProjectDetailsModel>> call, Throwable t) {
                Toast.makeText(GallaryActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                //hud.dismiss();

            }
        });






       */
/* Call<List<ProjectDetailsModel>> call = service.getProjectDetails(sharedPref.getString("auth_token", ""), sharedPref.getString("cloudCode", ""),projectCode);
        final KProgressHUD hud = KProgressHUD.create(GallaryActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setMaxProgress(100)
                .show();
        hud.setProgress(90);
        call.enqueue(new retrofit2.Callback<List<ProjectDetailsModel>>() {
            @Override
            public void onResponse(Call<List<ProjectDetailsModel>> call, Response<List<ProjectDetailsModel>> response) {
                if (response.body() != null && !response.body().isEmpty()) {
                    Toast.makeText(GallaryActivity.this, response.body()+"", Toast.LENGTH_SHORT).show();



                } else {

                }
                hud.dismiss();
            }

            @Override
            public void onFailure(Call<List<ProjectDetailsModel>> call, Throwable t) {
                Toast.makeText(GallaryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                hud.dismiss();


            }
        });
*//*

    }
*/


    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

    public class MyWebViewClient extends WebViewClient {
        public MyWebViewClient(GallaryActivity gallaryActivity) {

        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            // Do something with the event here
            return false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            if (Uri.parse(url).getHost().equals("www.google.com")) {
//                // This is my web site, so do not override; let my WebView load the page
//                return false;
//            }

            // reject anything other
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    private String youTubeLinkWithoutProtocolAndDomain(String url) {
        Pattern compiledPattern = Pattern.compile(youTubeUrlRegEx);
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()) {
            return url.replace(matcher.group(), "");
        }
        return url;
    }

    public String extractVideoIdFromUrl(String url) {
        String youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(url);

        for (String regex : videoIdRegex) {
            Pattern compiledPattern = Pattern.compile(regex);
            Matcher matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain);

            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }
}
