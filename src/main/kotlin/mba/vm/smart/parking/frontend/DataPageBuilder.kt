package mba.vm.smart.parking.frontend

import com.google.gson.Gson
import jakarta.servlet.http.HttpServletRequest
import mba.vm.smart.parking.UserType
import mba.vm.smart.parking.data.DataRequest
import mba.vm.smart.parking.data.DataRequest.Companion.handlerMap
import mba.vm.smart.parking.data.handler.BaseDataHandler
import mba.vm.smart.parking.frontend.ui.HTMLElement
import mba.vm.smart.parking.tool.OrNothingTool.addChildOrNothing
import mba.vm.smart.parking.tool.OrNothingTool.appendOrNothing
import mba.vm.smart.parking.tool.UserLoginTool.getUserByRequest
import org.owasp.encoder.Encode

/**
 * SmartParking - mba.vm.smart.parking.frontend
 * @description https://github.com/VictorModi/Smart_Parking
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/22 下午6:25
 */

/**
 * 表示用于构建数据页面的构建器类。
 * @property request HTTP请求对象。
 * @property pageName 页面名称。
 * @property pageTitle 页面标题。
 * @property dataType 数据类型。
 * @property keyDisplayNameMap 键-显示名称映射。
 * @property notAllowDirect 是否不允许直接访问。
 * @property extraHTMLElement 额外的HTML元素列表。
 */
data class DataPageBuilder(
    val request: HttpServletRequest,
    val pageName: String,
    val pageTitle: String,
    val dataType: DataRequest.DataType,
    val keyDisplayNameMap: Map<String, String>,
    val notAllowDirect: Boolean = true,
    val readOnlyColumns: List<String>?,
    val extraHTMLElement: List<HTMLElement>?
) {

    /**
     * 重写toString方法，返回DataPageBuilder的字符串表示形式。
     * @return DataPageBuilder对象的字符串表示形式。
     */
    override fun toString(): String {
        return "DataPageBuilder(pageName='$pageName', pageTitle='$pageTitle', dataType=$dataType, keyDisplayNameMap=$keyDisplayNameMap)"
    }

    /**
     * 生成数据页面的HTML字符串。
     * @return 数据页面的HTML字符串。
     */
    fun toHTML(): String {
        val accessLevel: BaseDataHandler.AccessLevel = getUserByRequest(request)?.let {
            handlerMap[dataType]?.getAccessLevel(UserType.getUserTypeByInt(it.first.permission_level))
        } ?: BaseDataHandler.AccessLevel.NO_ACCESS
        val sb = StringBuilder()
        sb.append(
            HTMLElement("style")
                .setContent("""
                    .$pageName-row-odd {
                        background-color: rgb(var(--mdui-color-surface-container-low));
                    }
                """.trimIndent())
        )
        sb.appendOrNothing(notAllowDirect, HTMLElement("script").setContent("""
            alert("Wrong access method.");
            window.location = "${request.contextPath}/#$pageName"
        """.trimIndent()))
        var isWritable = false
        val gson = Gson()
        val nameWithDisplayObject = gson.toJson(keyDisplayNameMap)
        val readOnlyColumnsArray = gson.toJson(readOnlyColumns)
        val scriptString: String = when (accessLevel) {
            BaseDataHandler.AccessLevel.READ -> {
                "setDataPage(\"$pageName\", false, $nameWithDisplayObject, \"$dataType\", $readOnlyColumnsArray);"
//                "dataPageManager.initPage(\"$pageName\", false, $nameWithDisplayObject, \"$dataType\");"
            }

            BaseDataHandler.AccessLevel.WRITE -> {
                isWritable = true
                "setDataPage(\"$pageName\", true, $nameWithDisplayObject, \"$dataType\", $readOnlyColumnsArray);"
//                "dataPageManager.initPage(\"$pageName\", true, $nameWithDisplayObject, \"$dataType\");"
            }

            BaseDataHandler.AccessLevel.NO_ACCESS -> {
                val noAccessScript: HTMLElement = HTMLElement("script")
                    .addClass("need-load")
                    .setContent("""
                            window.mdui.snackbar({message: "权限不足，无法访问该页面"});
                            window.location = "${request.contextPath}/#";
                        """.trimIndent())
                sb.append(noAccessScript)
                return sb.toString()
            }
        }
        val dataModifyDialog: HTMLElement by lazy {
            HTMLElement("mdui-dialog")
                .addClass("data-modify-dialog")
                .addClass("data-modify-dialog-$pageName")
                .setAttribute("close-on-esc", "")
        }
        val dataModifyAskDialog: HTMLElement by lazy {
            HTMLElement("mdui-dialog")
                .addClass("data-modify-ask-dialog")
                .addClass("data-modify-ask-dialog-$pageName")
                .setAttribute("close-on-esc", "")
        }
        val insertRowButton: HTMLElement by lazy {
            HTMLElement("mdui-button")
                .addClass("data-page-insert-button")
                .addClass("data-page-insert-button-$pageName")
                .setContent("插入")
        }
        val cancelButton = HTMLElement("mdui-button")
            .addClass("dialog-button")
            .setAttribute("slot","action")
            .setAttribute("variant", "text")
            .addClass("dialog-button-cancel")
            .setContent("取消")

        val confirmButton = HTMLElement("mdui-button")
            .addClass("dialog-button")
            .setAttribute("slot","action")
            .setAttribute("variant", "filled")
            .addClass("dialog-button-confirm")
            .setContent("确定")

        dataModifyAskDialog
            .addChild(cancelButton)
            .addChild(confirmButton)

        for (key in keyDisplayNameMap.keys) {
            if (readOnlyColumns != null && readOnlyColumns.contains(key)) {
                continue
            }
            val textField = HTMLElement("mdui-text-field")
            textField
                .addClass("data-modify-dialog-field")
                .addClass("data-modify-dialog-field-$key")
                .setAttribute("data-key", key)
                .setAttribute("variant", "filled")
                .setAttribute("label", keyDisplayNameMap[key]!!)
            dataModifyDialog.addChild(textField)
        }
        dataModifyDialog
            .addChild(cancelButton)
            .addChild(confirmButton)
        sb.append(dataModifyDialog, dataModifyAskDialog)
        val aioElement: HTMLElement = HTMLElement("div").addChild(
            HTMLElement("div")
                .addClass("data-page-title-container")
                .addClass("data-page-title-container-$pageName")
                .addChild(
                    HTMLElement("h1")
                        .addClass("data-page-title-display")
                        .addClass("data-page-title-display-$pageName")
                        .setContent(Encode.forHtml(pageTitle))
                )
                .addChildOrNothing(isWritable, insertRowButton)
        ).addChild(
            HTMLElement("p")
                .addClass("data-page-row-counter")
                .addClass("data-page-row-counter-$pageName")
        ).addChild(
            HTMLElement("div").addClass("mdui-table").addChild(
                HTMLElement("table").addChild(
                    HTMLElement("thead").addClass("$pageName-thead").addChild(
                        HTMLElement("tr")
                    )
                ).addChild(
                    HTMLElement("tbody").addClass("$pageName-tbody")
                )
            )
        ).addChild(
            HTMLElement("div")
                .addClass("data-page-bottom-placeholder")
                .addClass("data-page-bottom-placeholder-$pageName")
                .setContent("----- 我是有底线的 (￣へ￣) -----")
        )
        sb.append(aioElement)
        val javaScript: HTMLElement = HTMLElement("script").addClass("need-load")
            .setContent(scriptString)
        sb.append(javaScript)
        sb.append(
            HTMLElement("mdui-tooltip")
                .setAttribute("content", "筛选器 (已关闭)")
                .addChild(
                    HTMLElement("mdui-fab")
                        .setAttribute("icon", "filter_alt_off--rounded")
                        .addClass("data-page-filter-button")
                        .addClass("data-page-filter-button-$pageName")
                )
        )
        extraHTMLElement?.forEach { sb.append(it) }
        return sb.toString()
    }
}
