package mba.vm.smart.parking;

import mba.vm.smart.parking.frontend.ui.Navigation;

/**
 * SmartParking - mba.vm.smart.parking
 *
 * @author VictorModi
 * @description https://github.com/VictorModi/Smart_Parking
 * @email victormodi@outlook.com
 * @date 2024/6/1 下午3:24
 */
/**
 * NavigationConstant类，用于定义导航相关的常量。
 */
public class NavigationConstant {
    public static final Navigation HOME = new Navigation("home--rounded", "首页", "root");
    public static final Navigation USER_LOGIN = new Navigation("person--rounded", "用户登录", "user-login");
    public static final Navigation USER_RESET__PASSWORD = new Navigation("lock_reset--rounded", "重置密码", "user-reset--password");
    public static final Navigation CHARGE_HISTORY = new Navigation("reorder--rounded", "收费历史", "charge-history");
    public static final Navigation CHARGE_SETTINGS = new Navigation("monetization_on--rounded", "收费设置", "charge-settings");
    public static final Navigation SYSTEM_USER__MANAGEMENT = new Navigation("admin_panel_settings--rounded", "用户管理", "system-user--management");
    public static final Navigation SYSTEM_SETTINGS = new Navigation("settings--rounded", "系统设置", "system-settings");
    public static final Navigation PARKING_STATUS = new Navigation("directions_car_filled--rounded", "停车状态", "parking-status");
    public static final Navigation PARKING_CAR__INFO = new Navigation("commute--rounded","车辆管理", "parking-car--info");
    public static final Navigation PARKING_SETTINGS = new Navigation("edit_road--rounded", "停车设置", "parking-settings");
    public static final Navigation SECURITY_CAMERAS = new Navigation("videocam--rounded", "监控摄像头", "security-cameras");
    public static final Navigation SECURITY_SETTINGS = new Navigation("local_police--rounded", "安保设置", "security-settings");
    public static final Navigation CUSTOMER_REQUESTS = new Navigation("contact_page--rounded", "客服请求", "customer-requests");
    public static final Navigation CUSTOMER_FEEDBACK = new Navigation("feedback--rounded", "客服反馈", "customer-feedback");

    public static final Navigation USER = new Navigation("people--rounded", "用户", "user", new Navigation[]{
            USER_LOGIN,
            USER_RESET__PASSWORD
    });

    public static final Navigation CHARGE = new Navigation("attach_money--rounded", "收费管理", "charge", new Navigation[]{
            CHARGE_HISTORY,
            CHARGE_SETTINGS
    });

    public static final Navigation SYSTEM = new Navigation("settings_suggest--rounded", "系统管理", "system", new Navigation[]{
            SYSTEM_USER__MANAGEMENT,
            SYSTEM_SETTINGS
    });

    public static final Navigation PARKING = new Navigation("local_parking--rounded", "车位管理", "parking", new Navigation[]{
            PARKING_CAR__INFO,
            PARKING_STATUS,
//            PARKING_SETTINGS
    });

    public static final Navigation SECURITY = new Navigation("security--rounded", "安保管理", "security", new Navigation[]{
            SECURITY_CAMERAS,
            SECURITY_SETTINGS
    });

    public static final Navigation CUSTOMER_SERVICE = new Navigation("headset_mic--rounded", "客服管理", "customer_service", new Navigation[]{
            CUSTOMER_REQUESTS,
            CUSTOMER_FEEDBACK
    });

    public static Navigation[] getNavigationsByUserType(final UserType userType) {
        Navigation[] defaultNav = new Navigation[]{HOME, USER};

//        return switch (userType) {
//            case SUPER_ADMIN -> new Navigation[]{HOME, USER, CHARGE, SYSTEM, PARKING, SECURITY, CUSTOMER_SERVICE};
//            case CHARGE_ADMIN -> new Navigation[]{HOME, USER, CHARGE};
//            case SYSTEM_ADMIN -> new Navigation[]{HOME, USER, SYSTEM};
//            case PARKING_ADMIN -> new Navigation[]{HOME, USER, PARKING};
//            case SECURITY_ADMIN -> new Navigation[]{HOME, USER, SECURITY};
//            case CUSTOMER_SERVICE_ADMIN -> new Navigation[]{HOME, USER, CUSTOMER_SERVICE};
//            default -> defaultNav; // 默认导航菜单
//        };
        return switch (userType) {
            case SUPER_ADMIN, CHARGE_ADMIN, SYSTEM_ADMIN, PARKING_ADMIN, SECURITY_ADMIN, CUSTOMER_SERVICE_ADMIN ->
                    new Navigation[]{HOME, USER, PARKING};
            default -> defaultNav;
        };
    }
}
