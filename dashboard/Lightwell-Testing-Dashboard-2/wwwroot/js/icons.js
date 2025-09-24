var apiId = 1000;
var dbId = 100;
var destructiveId = 10;
var webId = 1;

var ALL_TYPES = 1111;

var API_DB_DEST = 1110;
var API_DB_WEB = 1101;
var API_DB = 1100;
var API_DEST_WEB = 1011;
var API_DEST = 1010;
var API_WEB = 1001;
var API = 1000;

var DB_DEST_WEB = 111;
var DB_DEST = 110;
var DB_WEB = 101;
var DB = 100;

var DEST_WEB = 11;
var DEST = 10;

var WEB = 1;

function configTestTypeIcon(tags, icon) {
    var iconPathRoot = "./images/";
    var iconPath;
    var titleText = "";

    var web = tags.includes("web");
    var db = tags.includes("db");
    var api = tags.includes("api");
    var destructive = tags.includes("destructive");

    var iconId = getIconId(tags);

    switch (iconId) {
        case ALL_TYPES:
            iconPath = iconPathRoot + "all_tests_icon.png";
            titleText = "api, db, destructive, and web";
            break;
        case API_DB_DEST:
            iconPath = iconPathRoot + "api_db_dest_icon.png";
            titleText = "api, db, and destructive";
            break;
        case API_DB_WEB:
            iconPath = iconPathRoot + "api_db_web_icon.png";
            titleText = "api, db, and web";
            break;
        case API_DB:
            iconPath = iconPathRoot + "api_db_icon.png";
            titleText = "api and db";
            break;
        case API_DEST_WEB:
            iconPath = iconPathRoot + "api_dest_web_icon.png";
            titleText = "api, destructive, and web";
            break;
        case API_DEST:
            iconPath = iconPathRoot + "api_dest_icon.png";
            titleText = "api and destructive";
            break;
        case API_WEB:
            iconPath = iconPathRoot + "api_web_icon.png";
            titleText = "api and web";
            break;
        case API:
            iconPath = iconPathRoot + "api_icon.png";
            titleText = "api";
            break;
        case DB_DEST_WEB:
            iconPath = iconPathRoot + "db_dest_web_icon.png";
            titleText = "db, destructive, and web";
            break;
        case DB_DEST:
            iconPath = iconPathRoot + "db_dest_icon.png";
            titleText = "db and destructive";
            break;
        case DB_WEB:
            iconPath = iconPathRoot + "db_web_icon.png";
            titleText = "db and web";
            break;
        case DB:
            iconPath = iconPathRoot + "db_icon.png";
            titleText = "db";
            break;
        case DEST_WEB:
            iconPath = iconPathRoot + "dest_web_icon.png";
            titleText = "destructive and web";
            break;
        case DEST:
            iconPath = iconPathRoot + "dest_icon.png";
            titleText = "destructive";
            break;
        case WEB:
            iconPath = iconPathRoot + "web_icon.png";
            titleText = "web";
            break;
        default:
            icon.style = "display:none";
            iconPath = "";
    } 

    icon.src = iconPath;
    icon.title = titleText;

    return icon;
}

function getIconId(tags) {
    var web = tags.includes("web");
    var db = tags.includes("db");
    var api = tags.includes("api");
    var destructive = tags.includes("destructive");

    var iconId = 0;

    if (api) {
        iconId += apiId;
    }

    if (db) {
        iconId += dbId;
    }

    if (destructive) {
        iconId += destructiveId;
    }

    if (web) {
        iconId += webId;
    }

    return iconId;
}