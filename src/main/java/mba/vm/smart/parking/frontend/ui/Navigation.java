package mba.vm.smart.parking.frontend.ui;

import lombok.Data;
import lombok.Getter;
import org.owasp.encoder.Encode;

import java.util.Arrays;
import java.util.Objects;

import static mba.vm.smart.parking.util.ContentFetch.escapeHtml;

/**
 * SmartParking - mba.vm.smart.parking.object.ui
 *
 * @author VictorModi
 * @description TODO: coming soon.
 * @email victormodi@outlook.com
 * @date 2024/6/1 下午2:42
 */

/**
 * Navigation类，表示导航项的结构。
 */
@Data
public class Navigation {
    private final String icon;           // 图标名称或路径
    private final String displayName;    // 显示名称
    private final String name;           // 导航项名称
    private final Navigation[] child;    // 子导航项数组
    private final HTMLElement element;   // 关联的HTML元素

    public Navigation(String displayName, String name) {
        this("", displayName, name);
    }

    public Navigation(String icon, String displayName, String name) {
        this(icon, displayName, name, null);
    }

    public Navigation(String icon, String displayName, String name, Navigation[] child) {
        this.icon = icon != null ? icon : "";
        this.displayName = displayName;
        this.name = name;
        this.child = child;
        this.element = (child == null || child.length == 0) ? buildListItem() : buildCollapseItem(child);
    }

    private HTMLElement buildCollapseItem(Navigation[] children) {
        HTMLElement allChildDiv = new HTMLElement("div")
                .setAttribute("style", "margin-left: 2.5rem");
        Arrays.stream(children).forEach(child -> allChildDiv.addChild(child.element));

        return new HTMLElement("mdui-collapse-item")
                .addClass("navigation-item")
                .setAttribute("value", "item-" + name)
                .addChild(new HTMLElement("mdui-list-item")
                        .setAttribute("slot", "header")
                        .setAttribute("icon", icon)
                        .addClass("navigation-item")
                        .addClass("collapse--header")
                        .addClass("collapse-item-" + name)
                        .setContent(Encode.forHtml(displayName))
                        .addChild(new HTMLElement("mdui-icon")
                                .setAttribute("slot", "end-icon")
                                .setAttribute("name", "keyboard_arrow_up--rounded")
                                .addClass("collapse-arrow")
                                .addClass("rotate-collapse-arrow-180")
                        )
                ).addChild(allChildDiv);
    }

    private HTMLElement buildListItem() {
        return new HTMLElement("mdui-list-item")
                .setAttribute("href", "#" + (Objects.equals(name, "root") ? "" : name))
                .addClass("navigation-item")
                .addClass("nav")
                .addClass("nav-" + name)
                .setAttribute("icon", icon)
                .setContent(Encode.forHtml(displayName));
    }

    public String toHTML() {
        return this.element.toString();
    }
}
