package com.piccollage.util.config;

import android.graphics.Bitmap;
import android.os.Environment;
import com.piccollage.util.BuildConfig;

public class Consts {

    public static final String TAG = "cb";
    public static final String L_PARENTHESIS = "(";
    public static final String R_PARENTHESIS = ")";

    public static final int MAX_PHOTOS_PER_COLLAGE = 30;
    public static final int MAX_ITEM_PER_GRID = 25;
    public static final int AD_PAGE_PER_SHARE_PAGE = 1;
    public static final String EXTRA_PREF_NAME              = "extra_pref_name";

    public static final String KEY_TOKEN = "twitter_token";
    public static final String KEY_TOKEN_SECRET = "twitter_token_secret";
    public static final String KEY_TWITTER_USER_NAME = "twitter_user_name";
    public static final String KEY_FACEBOOK_DISPLAY_NAME = "facebook_user_name";
    public static final String KEY_FACEBOOK_UID = "facebook_uid";
    public static final String KEY_FACEBOOK_EMAIL = "facebook_email";
    public static final String PREF_DEVICE_UUID = "pref_device_uuid";
    public static final String PREF_IS_BACKGROUND_HINT_SHOWN = "pref_is_display_background_hint";
    public static final String PREF_IS_LAST_SQUARE_CANVAS = "pref_is_last_square_canvas";
    public static final String PREF_EDITOR_HELP_OVERLAY_SHOWED = "pref_help_overlay_showed";
    public static final String PREF_START_HELP_OVERLAY_SHOWED = "pref_start_overlay_showed";
    public static final String PREF_SAVE_DIALOGUE_SHOWED = "pref_save_dialogue_showed";
    public static final String PREF_CHANGE_GRID_TIP_SHOWED = "pref_change_grid_showed";
    public static final String PREF_REMOVE_FRAME_TIP_SHOWED = "pref_remove_frame_tip_showed";
    public static final String PREF_SAVE_AT_MY_COLLAGE_SHOWED = "pref_save_at_my_collage_showed";

    public static final boolean RATING_ENABLE = BuildConfig.RATING_ENABLE;
    public static final boolean NOTIFICATION_SERVICE_ENABLE = BuildConfig.NOTIFICATION_SERVICE_ENABLE;

    public static final boolean ANNOUNCE_ENABLE = true;

    // Activity Request Code for General Purpose.
    /**
     * Usually comes with {@link #PARAMS_PHOTO_INFOS}.
     */
    public static final int REQ_SELECTED_PHOTOS         = 1 << 1;

    // Parameters for general purpose.
    public static final String PARAMS_PATH_ROUTE_NEXT   = "params_path_route_next";
    public static final String PARAMS_PATH_ROUTE_BACK   = "params_path_route_back";
    public static final String PARAMS_POSITION          = "params_position";
    public static final String PARAMS_BUNDLE_ID         = "params_bundle_id";
    public static final String PARAMS_IS_MODIFIED       = "params_is_modified";
    public static final String PARAMS_FROM_WHERE        = "params_from_where";
    public static final String PARAMS_MAX_CHOICES       = "params_max_choices";
    public static final String PARAMS_PHOTO_INFOS       = "params_photo_infos";
    public static final String PARAMS_SHOW_INTRO_FLAG   = "params_show_intro_flag";

    // FIXME need to implement this flag by RemoteConfig.
    public static final boolean SUPPORT_RESIZABLE_GRID = false;

    // Action string for Fragment.
    public static final String FRAGMENT_ACTION          = "com.cardinalblue.piccollage.actions";

    public static final boolean DEBUG = BuildConfig.DEBUG;

    // Use to present general default status
    public static final String DEFAULT = "DEFAULT";

    // Version code
    public static final String VERSION_CODE             = "version_code";
    public static final String VERSION_CODE_HISTORY     = "version_code_history";
    public static final int DEFAULT_VERSION_CODE        = 0;

    // For doing experiment on new install user, the flag will change only when user has seen the exp.
    public static final boolean NEW_USER_FOR_EXP_DEFAULT = true;

    // UserVoice
    public static final String USERVOICE_API_KEY = "hRn1XymXgDWAZQSeLXzg";
    public static final String USERVOICE_API_SECRET = "mpvq1c9o3ntMQoe3N4iJpdye4pwjpayedtipOOeHL4";
    public static final String USERVOICE_SITE = "piccollage.uservoice.com";

    public static final String CONTACT_US_MAIL = "support@pic-collage.com";
    public static final String GCM_SENDER_ID = "773876082784";

    public static final String KEY_IN_APP_NOTIFICATION_ENABLE = "key_create_collage_reminder_notification";

    // GraphQL server
    public static final String SERVER_URL = "piccollage.com";

    public static final boolean DEFAULT_IN_APP_NOTIFICATION_ENABLE = true;

    public static final float MIN_INTERNAL_MEMORY = 1 * 1024 * 1024;//1 MB

    // Image scrap resolution constant
    public static final int IMAGE_SIZE_THUMBNAIL = 150;
    public static final int IMAGE_SIZE_MIN_DEFAULT = 400;
    public static int IMAGE_SIZE_DEFAULT = IMAGE_SIZE_MIN_DEFAULT;
    public static final int IMAGE_SIZE_UPPER_BOUND = 800;
    public static final int IMAGE_SIZE_BACKGROUND = 1024;

    public static final String DEFAULT_COLLAGE_BACKGROUND = "assets://backgrounds/background_stripe_light.png";
    public static final String LEGACY_COLLAGE_BACKGROUND  = "assets://backgrounds/bg_old_01.png";

    // Network Settings
    public static final int MAX_RETRIES = 5;

    public static final long CONNECTION_TIMEOUT_SEC = 15;

    public static final int FB_MIN_SIDE_LENGTH = 600;

    /** keys for flurry event */
    public static final String KEY_NOTIFICATION_FLURRY_EVENT        = "flurry_event";
    public static final String KEY_NOTIFICATION_ID                  = "key_notification_id";
    public static final String KEY_NOTIFICATION_TARGET_ACTIVITY     = "key_notification_target_activity";
    public static final String KEY_NOTIFICATION_PAYLOAD             = "key_notification_payload";
    public static final String KEY_IS_GCM_NOTIFICATION              = "key_is_gcm_notification";

    public static final String PREF_TEXT_FONT               = "text_font";
    public static final String PREF_TEXT_COLOR              = "text_color";
    public static final String PREF_TEXT_BACKGROUND_COLOR   = "text_background_color";
    public static final String PREF_TEXTURE_COLOR_URL       = "texture_color_url";
    public static final String PREF_TEXTURE_BACKGROUND_URL  = "texture_background_url";
    public static final String PREF_TEXT_OUTLINE_COLOR      = "text_outline_color";
    public static final String PREF_TEXT_OUTLINE_ENABLED    = "text_outline_enabled";

    /** for cello project */
    public static final float MAX_HEAD_TRACK_Z_MM = 400;

    /** PicCollage resources folder constants */
    public static final String DIR_RESOURCES_NAME = "resources";
    public static final String DIR_RESOURCES = DIR_RESOURCES_NAME + "/";
    public static final String DIR_RESOURCES_DOWNLOAD =
            DIR_RESOURCES + "/" + Environment.DIRECTORY_DOWNLOADS;
    public static final String DIR_RESOURCES_PICTURE =
            DIR_RESOURCES + "/" + Environment.DIRECTORY_PICTURES;

    /** watermark purchase ID */
    public static final String WATERMARK_PRODUCT_ID = "com.cardinalblue.piccollage.watermark";
    public static final float WATERMARK_PRICE = 1.99f;

    public static final Bitmap.Config CB_SCRAP_BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

    /** in-app notification frequency */
    public static final int CREATE_COLLAGE_REMINDER_PHOTO_NUMBER_THRESHOLD = 10;

    public static final String KEY_LAST_SEARCH_TERM_WEB_PHOTO       = "key_last_search_term_web_photo";
    public static final String KEY_LAST_SEARCH_TERM_WEB_BACKGROUND  = "key_last_search_term_web_background";

    public static final String KEY_EXTRA_FROM = "from";

    /** key for tag model type */
    public static final String KEY_TAG_TYPE_VERSION_CODE = "type_version_code";

    // for output animated collage
    public static final int DEFAULT_FRAMERATE = 5;
    public static final float DEFAULT_VIDEO_DURATION = 3f;

    public static final int NEW_COLLAGE_ID = -1;

    /** template category */
    public static final int MAX_CATEGORY_SIZE = 25;
    public static final int Max_TEMPLATE_SIZE = 30;
    public static final int DEFAULT_CATEGORY_QUERY_SIZE = 25;
    public static final int DEFAULT_TEMPLATE_QUERY_SIZE  = 30;

    /** dismiss save tip dialogue after a time delay */
    public static final int SAVE_TIP_DISMISS_MILLI_SECOND = 2000;

    public static final int NOTIFICATION_ID_UNFINISHED_NOTIFICATION = 30000;
    public static final int NOTIFICATION_ID_UNCREATED_NOTIFICATION = 30001;

    /** Activity Route */
    public static final String ACTIVITY_FROM  = "activity_from";
    public static final String FROM_FAST_MODE_PICKER = "fast_mode_picker";
    public static final String FROM_COLLAGE_EDITOR   = "collage_editor";
    public static final String FROM_MY_COLLAGES_PREVIEW   = "my_collages_preview";

    // below are the Consts duplicated from other places to avoid unnecessary coupling.
    public enum ShareFormat {
        VIDEO,
        GIF,
        JPEG
    }
    public final static String SPLITTER = ",";
    public final static String BUNDLE_DIRECTORY = "Bundles/Backgrounds/";

    public static final float POINTS_DISTANCE_ON_SCREEN = 10;
}
