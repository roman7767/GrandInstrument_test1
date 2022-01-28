package com.example.grandinstrument.utils;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;

public final class DataBaseContract implements BaseColumns {

    public static final int DATA_BASE_VERSION = 30;
    public static final String DATA_BASE_NAME = "gi_mobile_base";
    public static final String GOODS_TABLE_NAME = "goods_table";

    public static final String GOODS_PICTURE_TABLE_NAME = "picture_table";

    public static final String LOGIN_TABLE_NAME = "login_table";

    public static final String CART_TABLE_NAME = "cart_table";
    public static final String ORDER_HEAD_TABLE_NAME = "order_head_table";
    public static final String ORDER_ROW_TABLE_NAME = "order_row_table";
    public static final String TYPE_OF_SHIPMENT_TABLE_NAME = "type_of_shipment_table";
    public static final String BRANDS_TABLE_NAME = "brands_table";

    public static final String SCHEME = "content://";
    public static final String URI_AUTHORITY = "com.example.grandinstrument.data_base_adapter";
    public static final String BASE_CONTENT_URI = SCHEME + URI_AUTHORITY;
    public static final Uri BASE_CONTENT_URI_GOODS = Uri.parse(BASE_CONTENT_URI+"/"+GOODS_TABLE_NAME);

    public static final Uri BASE_CONTENT_URI_GOODS_PICTURE = Uri.parse(BASE_CONTENT_URI+"/"+GOODS_PICTURE_TABLE_NAME);
    public static final Uri BASE_CONTENT_URI_LOGIN = Uri.parse(BASE_CONTENT_URI+"/"+LOGIN_TABLE_NAME);

    public static final Uri BASE_CONTENT_URI_CART = Uri.parse(BASE_CONTENT_URI+"/"+CART_TABLE_NAME);
    public static final Uri BASE_CONTENT_URI_HEAD_ORDER = Uri.parse(BASE_CONTENT_URI+"/"+ORDER_HEAD_TABLE_NAME);
    public static final Uri BASE_CONTENT_URI_ROW_ORDER = Uri.parse(BASE_CONTENT_URI+"/"+ORDER_ROW_TABLE_NAME);
    public static final Uri BASE_CONTENT_URI_TYPE_OF_SHIPMENT = Uri.parse(BASE_CONTENT_URI+"/"+TYPE_OF_SHIPMENT_TABLE_NAME);
    public static final Uri BASE_CONTENT_URI_BRANDS = Uri.parse(BASE_CONTENT_URI+"/"+BRANDS_TABLE_NAME);



    public static final int URI_CODE_ALL_TABLE = 1;
    public static final int URI_CODE_ONE_ROW = 2;

    public static final String STATUS_UPLOADED = "Выгружен";

    public static final class ALL_TABLES{

        public static ArrayList<String> all_tables(){
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(GOODS_TABLE_NAME);
            arrayList.add(GOODS_PICTURE_TABLE_NAME);
            arrayList.add(LOGIN_TABLE_NAME);
            arrayList.add(CART_TABLE_NAME);
            arrayList.add(ORDER_HEAD_TABLE_NAME);
            arrayList.add(ORDER_ROW_TABLE_NAME);
            arrayList.add(TYPE_OF_SHIPMENT_TABLE_NAME);
            arrayList.add(BRANDS_TABLE_NAME);
            return arrayList;
        }


        private ALL_TABLES(){};

    }

    public static final class  R_BRANDS{
        public static final String RB_KEY_ID = BaseColumns._ID;
        public static final String RB_NAME = "name";

        public static final String[] BRANDS_COLUMNS_FOR_LIST = new String[] {
                BRANDS_TABLE_NAME + "." +
                        RB_KEY_ID,
                        RB_NAME

        };

        private R_BRANDS() {
        }
    }

    //columns the goods table
    public static final class R_GOODS{

        public static final String RG_KEY_ID = BaseColumns._ID;
        public static final String RG_CODE_1C = "code_1c";
        public static final String RG_ID_1C = "id_1c";
        public static final String RG_ARTICLE = "article";
        public static final String RG_GOOD_OF_WEEK =  "good_of_week";
        public static final String RG_DESCRIPTION = "description";
        public static final String RG_BRAND = "brand";
        public static final String RG_CATEGORY = "category";
        public static final String RG_AVAILABLE = "available";
        public static final String RG_RRC =  "rrc";
        public static final String RG_PRICE =  "price";
        public static final String RG_QUANTITY =  "quantity";
        public static final String RG_DATE_OF_RENOVATION= "date_of_renovation";
        public static final String RG_URL_IMAGE =  "url_of_image";
        public static final String RG_BOX =  "in_box";
        public static final String RG_PACKAGE=  "in_package";



        public static final String[] GOODS_COLUMNS_FOR_LIST = new String[] {
                GOODS_TABLE_NAME + "." +
                        RG_KEY_ID,
                        RG_ID_1C,
                        RG_ARTICLE,
                        RG_GOOD_OF_WEEK,
                        RG_DESCRIPTION,
                        RG_BRAND,
                        RG_CATEGORY,
                        RG_AVAILABLE,
                        RG_RRC,
                        RG_PRICE,
                        RG_QUANTITY,
                        RG_DATE_OF_RENOVATION,
                        RG_URL_IMAGE,
                        RG_BOX,
                        RG_PACKAGE

        };


        private R_GOODS() {
        }
    }

    public static final class R_PICTURE{
        public static final String RP_KEY_ID = BaseColumns._ID;
        public static final String RP_ID_1C = "id_1c";
        public static final String RP_URL_IMAGE =  "url_of_image";
        public static final String RP_IMAGE =  "image";


        public static final String[] PICTURE_COLUMNS = new String[] {
                GOODS_PICTURE_TABLE_NAME + "." +
                        RP_KEY_ID,
                        RP_URL_IMAGE,
                        RP_ID_1C,
                        RP_IMAGE,

        };


        private R_PICTURE() {
        }
    }

    public static final class R_LOGIN{
        public static final String RL_KEY_ID = BaseColumns._ID;
        public static final String RL_EMAIL = "email";
        public static final String RL_PASSWORD =  "password";
        public static final String RL_LOADED =  "loaded";


        public static final String[] LOGIN_COLUMNS = new String[] {
                LOGIN_TABLE_NAME + "." +
                        RL_KEY_ID,
                RL_EMAIL,
                RL_PASSWORD,
                RL_LOADED

        };


        private R_LOGIN() {
        }
    }

    public static final class R_CART{
        public static final String RC_KEY_ID = BaseColumns._ID;
        public static final String RC_UUID = "uuid";
        public static final String RC_UUID_ORDER = "uuid_order";
        public static final String RC_DELIVERY_DATE = "delivery_date";
        public static final String RC_CLIENT_NAME = "client_name";
        public static final String RC_CLIENT_ID_1C =  "client_id_1c";
        public static final String RC_CLIENT_GUID_1C =  "client_guid_1c";
        public static final String RC_CLIENT_PHONE =  "client_phone";
        public static final String RC_CLIENT_API_KEY =  "client_api_key";
        public static final String RC_TYPE_OF_SHIPMENT =  "type_of_shipment";
        public static final String RC_TYPE_OF_SHIPMENT_CODE =  "type_of_shipment_code";

        public static final String RC_GOOD_GUID_1C =  "good_guid_1c";
        public static final String RC_QTY=  "qty";
        public static final String RC_PRICE=  "price";
        public static final String RC_TOTAL=  "total";

        public static final String[] CART_COLUMNS = new String[] {
                CART_TABLE_NAME + "." +
                        RC_KEY_ID,
                        RC_UUID,
                        RC_UUID_ORDER,
                        RC_DELIVERY_DATE,
                        RC_CLIENT_NAME,
                        RC_CLIENT_ID_1C,
                        RC_CLIENT_GUID_1C,
                        RC_CLIENT_PHONE,
                        RC_CLIENT_API_KEY,
                        RC_TYPE_OF_SHIPMENT,
                        RC_TYPE_OF_SHIPMENT_CODE,
                        RC_GOOD_GUID_1C,
                        RC_QTY,
                        RC_PRICE,
                        RC_TOTAL

        };


        private R_CART() {
        }
    }

    public static final class R_ORDER_HEADER{
        public static final String RH_KEY_ID = BaseColumns._ID;
        public static final String RH_UUID = "uuid";
        public static final String RH_DATE = "order_date";
        public static final String RH_DELIVERY_DATE = "delivery_date";
        public static final String RH_STATUS = "order_status";
        public static final String RH_ORDER_NUMBER_1c = "order_number_1c";
        public static final String RH_CLIENT_NAME = "client_name";
        public static final String RH_CLIENT_ID_1C =  "client_id_1c";
        public static final String RH_CLIENT_GUID_1C =  "client_guid_1c";
        public static final String RH_CLIENT_PHONE =  "client_phone";
        public static final String RH_CLIENT_API_KEY =  "client_api_key";
        public static final String RH_TYPE_OF_SHIPMENT =  "type_of_shipment";
        public static final String RH_TYPE_OF_SHIPMENT_CODE =  "type_of_shipment_code";
        public static final String RH_COMMENT =  "comment";


        public static final String RH_QTY=  "qty";
        public static final String RH_TOTAL=  "total";

        public static final String[] ORDER_HEADER_COLUMNS = new String[] {
                ORDER_HEAD_TABLE_NAME + "." +
                        RH_KEY_ID,
                        RH_UUID,
                        RH_DATE,
                        RH_DELIVERY_DATE,
                        RH_STATUS,
                        RH_ORDER_NUMBER_1c,
                        RH_CLIENT_NAME,
                        RH_CLIENT_ID_1C,
                        RH_CLIENT_GUID_1C,
                        RH_CLIENT_PHONE,
                        RH_CLIENT_API_KEY,
                        RH_TYPE_OF_SHIPMENT,
                        RH_TYPE_OF_SHIPMENT_CODE,
                        RH_COMMENT,
                        RH_QTY,
                        RH_TOTAL

        };


        private R_ORDER_HEADER() {
        }
    }

    public static final class R_ORDER_ROW{
        public static final String R_KEY_ID = BaseColumns._ID;
        public static final String R_UUID = "uuid";

        public static final String R_GOOD_GUID_1C =  "good_guid_1c";
        public static final String R_QTY=  "qty";
        public static final String R_PRICE=  "price";
        public static final String R_TOTAL=  "total";

        public static final String[] ORDER_ROW_COLUMNS = new String[] {
                ORDER_ROW_TABLE_NAME+ "." +
                        R_KEY_ID,
                        R_UUID,
                        R_GOOD_GUID_1C,
                        R_QTY,
                        R_PRICE,
                        R_TOTAL

        };


        private R_ORDER_ROW() {
        }
    }

    public static final class R_TYPE_OF_SHIPMENT{
        public static final String R_KEY_ID = BaseColumns._ID;
        public static final String R_CODE=  "code";
        public static final String R_NAME=  "name";
        public static final String R_AVAILABLE=  "available";
        public static final String R_CODE_MAINE=  "code_main";

        public static final String[] TYPE_OF_SHIPMENT_COLUMNS = new String[] {
                TYPE_OF_SHIPMENT_TABLE_NAME+ "." +
                        R_KEY_ID,
                R_CODE,
                R_NAME,
                R_AVAILABLE,
                R_CODE_MAINE

        };


        private R_TYPE_OF_SHIPMENT() {
        }
    }


    private DataBaseContract() {
    }

}
