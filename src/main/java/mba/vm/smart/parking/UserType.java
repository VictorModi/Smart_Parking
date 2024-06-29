package mba.vm.smart.parking;

import lombok.Getter;

/**
 * SmartParking - mba.vm.smart.parking
 *
 * @author VictorModi
 * @description https://github.com/VictorModi/Smart_Parking
 * @email victormodi@outlook.com
 * @date 2024/6/5 下午6:08
 */
@Getter
public enum UserType {
    GUEST("游客"),
    SUPER_ADMIN("超级管理员"),
    SYSTEM_ADMIN("系统管理员"),
    PARKING_ADMIN("车位管理员"),
    CHARGE_ADMIN("收费管理员"),
    SECURITY_ADMIN("安保管理员"),
    CUSTOMER_SERVICE_ADMIN("客服管理员");

    private final String roleName;

    UserType(String roleName) {
        this.roleName = roleName;
    }

    public static UserType getUserTypeByInt(int userTypeLevel) {
        return switch (userTypeLevel) {
            case 0 -> SUPER_ADMIN;
            case 1 -> SYSTEM_ADMIN;
            case 2 -> PARKING_ADMIN;
            case 3 -> CHARGE_ADMIN;
            case 4 -> SECURITY_ADMIN;
            case 5 -> CUSTOMER_SERVICE_ADMIN;
            default -> GUEST;
        };
    }
}
