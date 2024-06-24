package mba.vm.smart.parking.frontend

import com.google.gson.Gson
import jakarta.servlet.http.HttpServletRequest
import mba.vm.smart.parking.UserType
import mba.vm.smart.parking.data.DataRequest
import mba.vm.smart.parking.data.DataRequest.Companion.handlerMap
import mba.vm.smart.parking.data.handler.BaseDataHandler
import mba.vm.smart.parking.frontend.ui.HTMLElement
import mba.vm.smart.parking.tool.UserLoginTool.getUserByRequest
import org.owasp.encoder.Encode

/**
 * SmartParking - mba.vm.smart.parking.frontend
 * @description TODO: coming soon.
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
        if (notAllowDirect) {
            val warnScript: HTMLElement = HTMLElement("script").setContent("""
                alert("Wrong access method.");
                window.location = "${request.contextPath}/#$pageName"
            """.trimIndent())
            sb.append(warnScript)
        }
        when (accessLevel) {
            BaseDataHandler.AccessLevel.READ -> {}
            BaseDataHandler.AccessLevel.WRITE -> {}
            BaseDataHandler.AccessLevel.NO_ACCESS -> {
                val noAccessScript: HTMLElement = HTMLElement("script")
                    .addClass("need-load")
                    .setContent("""
                        window.mdui.snackbar({message: "权限不足，无法访问该页面"});
                        window.location = "${request.contextPath}/#";
                    """.trimIndent())
                sb.append(noAccessScript);
                return sb.toString()
            }
        }
        val aioElement: HTMLElement = HTMLElement("div").addChild(
            HTMLElement("h1").setContent(Encode.forHtml(pageTitle))
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
        )
        sb.append(aioElement)
        val nameWithDisplayObject: String = Gson().toJson(keyDisplayNameMap)
        val javaScript: HTMLElement = HTMLElement("script").addClass("need-load")
            .setContent("""// @language=JavaScript
            const nameWithDisplayObject = $nameWithDisplayObject;
            const infoTheadMainTr = document.querySelector(".$pageName-thead").children[0];
            for (let key in nameWithDisplayObject) {
                if (nameWithDisplayObject.hasOwnProperty(key)) {
                    const currentTd = document.createElement("td");
                    currentTd.classList.add("$pageName-column", "$pageName-column-" + key);
                    currentTd.innerText = nameWithDisplayObject[key];
                    infoTheadMainTr.appendChild(currentTd);
                }
            }
            sendRequest("POST", API_ROOT + "data", JSON.stringify({
                "type": "$dataType",
                "action": "SELECT",
                "data": {}
            }), function (result) {
                const infoTbody = document.querySelector(".$pageName-tbody");
                const listData = result.data.data.list;
                console.log(listData);
                let count = 0;
                for (const item of listData) {
                    count++;
                    const current = document.createElement("tr");
                    current.classList.add("$pageName-row", "$pageName-row-id-" + item["id"], "$pageName-row-count-" + count);
                    current.classList.add(count % 2 === 0 ? "$pageName-row-even" : "$pageName-row-odd");
                    const fields = Object.keys(nameWithDisplayObject);
                    for (const field of fields) {
                        const cell = document.createElement("td");
                        cell.classList.add("$pageName-row-field", "$pageName-row-field-" + field)
                        cell.innerText = item[field] === undefined ? "" : item[field];
                        current.appendChild(cell);
                    }
                    infoTbody.appendChild(current);
                }
            }, function (res) {
                try {
                    const data = JSON.parse(res.xhr.response);
                    snakeBar({
                        message: "获取数据失败, 原因: " + data["message"]
                    });
                } catch (e) {
                    console.error(e);
                    snakeBar({
                        message: "与服务器连接出现问题，状态码: " + res.xhr.status
                    });
                }
            }, undefined, "application/json")
        """.trimIndent())
        sb.append(javaScript)
        extraHTMLElement?.forEach { sb.append(it) }
        return sb.toString()
    }
}
