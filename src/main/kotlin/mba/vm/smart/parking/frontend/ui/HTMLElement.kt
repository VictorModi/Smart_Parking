package mba.vm.smart.parking.frontend.ui

/**
 * SmartParking - mba.vm.smart.parking.frontend.ui
 * @description TODO: coming soon.
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/22 下午6:29
 */


/**
 * 表示一个HTML元素，具有各种属性和方法来操作它。
 * @property tagName HTML元素的标签名。
 * @property classList 与元素关联的类名列表。
 * @property attributes 元素的属性（名称-值对）映射。
 * @property content 元素的内部内容。
 * @property children 子HTMLElement的可变列表。
 */
data class HTMLElement(
    private var tagName: String,
    private var classList: MutableList<String> = mutableListOf(),
    private var attributes: MutableMap<String, String> = mutableMapOf(),
    private var content: String = "",
    private var children: MutableList<HTMLElement> = mutableListOf()
) {
    constructor(tagName: String) : this(tagName, mutableListOf(), mutableMapOf(), "", mutableListOf())

    /**
     * 设置HTML元素的属性。
     * @param attributeName 属性的名称。
     * @param attributeValue 属性的值。
     * @throws IllegalArgumentException 如果attributeName为"class"。
     * @return 用于链式调用的HTMLElement实例本身。
     */
    fun setAttribute(attributeName: String, attributeValue: String): HTMLElement {
        if (attributeName == "class") throw IllegalArgumentException("Please use addClass to add the class name")
        attributes[attributeName] = attributeValue
        return this
    }

    /**
     * 获取指定属性的值。
     * @param attributeName 属性的名称。
     * @return 属性的值，如果未找到属性则返回null。
     */
    fun getAttribute(attributeName: String): String? {
        return attributes[attributeName]
    }


    /**
     * 移除HTML元素的指定属性。
     * @param attributeName 要移除的属性名称。
     * @throws IllegalArgumentException 如果attributeName为"class"。
     * @return 用于链式调用的HTMLElement实例本身。
     */
    fun removeAttribute(attributeName: String): HTMLElement {
        if (attributeName == "class") throw IllegalArgumentException("Please use removeClass to remove the class name")
        attributes.remove(attributeName)
        return this
    }

    /**
     * 向HTML元素添加类名。
     * @param className 要添加的类名。
     * @return 用于链式调用的HTMLElement实例本身。
     */
    fun addClass(className: String): HTMLElement {
        if (!classList.contains(className)) {
            classList.add(className)
        }
        return this
    }

    /**
     * 从HTML元素中移除类名。
     * @param className 要移除的类名。
     * @return 用于链式调用的HTMLElement实例本身。
     */
    fun removeClass(className: String): HTMLElement {
        classList.remove(className)
        return this
    }

    /**
     * 获取与HTML元素关联的类名列表。
     * @return 类名列表。
     */
    fun getClassList(): List<String> {
        return classList
    }

    /**
     * 设置HTML元素的内部内容。
     * @param content 要设置的内容。
     * @return 用于链式调用的HTMLElement实例本身。
     */
    fun setContent(content: String): HTMLElement {
        this.content = content
        return this
    }

    /**
     * 获取HTML元素的内部内容。
     * @return 元素的内部内容。
     */
    fun getContent(): String {
        return content
    }

    /**
     * 向当前HTML元素添加子HTMLElement。
     * @param child 要添加的子元素。
     * @return 用于链式调用的HTMLElement实例本身。
     */
    fun addChild(child: HTMLElement): HTMLElement {
        children.add(child)
        return this
    }

    /**
     * 从当前HTML元素中移除子HTMLElement。
     * @param child 要移除的子元素。
     * @return 用于链式调用的HTMLElement实例本身。
     */
    fun removeChild(child: HTMLElement): HTMLElement {
        children.remove(child)
        return this
    }

    /**
     * 生成HTML元素的字符串表示形式。
     * @return 表示元素及其内容的HTML字符串。
     */
    override fun toString(): String {
        val attrString = buildString {
            if (classList.isNotEmpty()) {
                append("class=\"${classList.joinToString(" ")}\"")
            }
            if (attributes.isNotEmpty()) {
                if (isNotEmpty()) append(" ")
                append(attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" })
            }
        }

        val childrenString = children.joinToString("") { it.toString() }
        return if (content.isEmpty() && children.isEmpty()) {
            "<$tagName${if (attrString.isNotEmpty()) " $attrString" else ""} />"
        } else {
            "<$tagName${if (attrString.isNotEmpty()) " $attrString" else ""}>$content$childrenString</$tagName>"
        }
    }
}
