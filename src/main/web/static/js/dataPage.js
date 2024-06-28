// only isWriteable
let isModifying = false;
let isAsking = false;

let isLoading = false;
let isAllLoaded = false;

let loadMaxByTop = undefined;

window.addEventListener("contentPageChanged", function () {
    isModifying = false;
    isAsking = false;
    isLoading = false;
    isAllLoaded = false
    loadMaxByTop = undefined;
});

class CallbackQueue {
    constructor() {
        this.queue = [];
        this.processing = false;
        this.locked = false;
    }

    enqueue(callback) {
        if (this.locked) return;
        this.queue.push(callback);
        this.process();
    }

    process() {
        if (this.processing || this.queue.length === 0) return;

        this.processing = true;
        const callback = this.queue.shift();
        callback(() => {
            this.processing = false;
            this.process();
        });
    }
}

/**
 * Need Compose DataPageBuild (Kotlin), it will create column to table and other thing...?
 *
 * @param pageName
 * @param isWriteable
 * @param nameWithDisplayObject
 * @param dataType
 */
function setDataPage(pageName, isWriteable, nameWithDisplayObject, dataType) {
    const layoutMain = document.querySelector(".layout-main");

    let nextOffset = 0;
    const loadMaxRows = 15;

    const infoTheadMainTr = document.querySelector(`.${pageName}-thead`).children[0];
    const countTd = document.createElement("td");

    const callbackQueue = new CallbackQueue();

    countTd.innerText = "#";
    countTd.classList.add(`.${pageName}-column`, `.${pageName}-column-count`);
    infoTheadMainTr.appendChild(countTd);
    const keys = Object.keys(nameWithDisplayObject);
    for (const key of keys) {
        if (nameWithDisplayObject.hasOwnProperty(key)) {
            const currentTd = document.createElement("td");
            currentTd.classList.add(`.${pageName}-column`, `.${pageName}-column-${key}`);
            currentTd.innerText = nameWithDisplayObject[key];
            infoTheadMainTr.appendChild(currentTd);
        }
    }
    if (isWriteable) {
        const methodTd = document.createElement("td");
        methodTd.classList.add(`.${pageName}-column`, `.${pageName}-column-method`);
        methodTd.innerText = "操作";
        infoTheadMainTr.appendChild(methodTd);
    }
    const tableBody = document.querySelector(`.${pageName}-tbody`);
    function startLoad() {
        if (isAllLoaded || isLoading) return;
        isLoading = true;
        callbackQueue.enqueue((done) => {
            function handleSuccess(res) {
                const listData = res.data.list;
                isAllLoaded = listData.length < loadMaxRows;
                if (isAllLoaded) window.mdui.snackbar({ message: "已经到底啦" });
                insertDataToTable(isWriteable, listData, tableBody, nameWithDisplayObject, pageName, nextOffset, dataType);
                nextOffset += loadMaxRows;
            }

            getDataArray(dataType, undefined, loadMaxRows, nextOffset,
                function (res) {
                    try {
                        const data = JSON.parse(res.xhr.response);
                        snakeBar({
                            message: "获取数据失败, 原因: " + data["message"]
                        });
                    } catch(e) {
                        console.error(e);
                        snakeBar({
                            message: "与服务器连接出现问题，状态码: " + res.xhr.status
                        });
                    }
                })
                .then(handleSuccess)
                .finally(() => {
                    isLoading = false;
                    done();
                });
        });
    }

    function loadMore() {
        if (isAllLoaded) {
            layoutMain.removeEventListener("scroll", loadMore);
            return
        }
        const startLoadScrollTop = this.scrollHeight - this.clientHeight - 5;
        const aboutScrollTop = Math.round(this.scrollTop);
        const isChecked = aboutScrollTop > startLoadScrollTop ||
            (startLoadScrollTop === 0 && aboutScrollTop === 0);
        if (isChecked) startLoad();
    }
    layoutMain.addEventListener("scroll", loadMore);

    function _loadByMaxTop() {
        if (layoutMain.scrollHeight - layoutMain.clientHeight - 5 < 0 &&
            !isAllLoaded) {
            startLoad()
            callbackQueue.enqueue((done) => {
                _loadByMaxTop();
                done();
            })
        }
    }

    setTimeout(function () {
        _loadByMaxTop();
        window.addEventListener("contentPageChanged", function () {
            layoutMain.removeEventListener("scroll", loadMore)
            console.log(`${pageName} scroll Listener is removed.`);
        }, {once: true});

        document.querySelectorAll(".dialog-button-cancel").forEach(cancelButton => {
            cancelButton.addEventListener("click", (event) => {
                const _this = event.target;
                if (_this.parentElement) {
                    _this.parentElement.open = false;
                    isModifying = false;
                    isAsking = false;
                }
            })
        })

        loadMaxByTop = _loadByMaxTop;
    }, 0);
}

function getDataArray(dataType, extra, limit, offset, ifError) {
    const requestData = {
        type: dataType,
        action: "SELECT",
    };
    extra = extra || {};
    requestData.data = Object.assign({}, extra, {
        limit: limit,
        offset: offset || 0,
    });
    return sendRequest(
        "POST",
        API_ROOT + "data",
        JSON.stringify(requestData),
        undefined,
        ifError,
        undefined,
        "application/json"
    );
}

function modifyCell(pageName, targetID, line, key, value, keyDisplay, displayElement, input, dataType) {
    if (isAsking) return;
    isAsking = true;
    const data = {
        type: dataType,
        action: "UPDATE",
        data: {
            id: targetID,
        }
    };
    data.data[key] = value;
    const dialog = document.querySelector(`.data-modify-ask-dialog-${pageName}`);
    const confirmButton = dialog.querySelector(".dialog-button-confirm");

    dialog.setAttribute("headline", "提示:");
    dialog.setAttribute("description", `当前文本框 (第 ${line} 行 - ${keyDisplay}) 已修改，是否保存?`);

    function confirmCLickEvent() {
            sendRequest("POST", API_ROOT + "data", JSON.stringify(data),
                function () {
                    window.mdui.snackbar({
                        message: "修改成功"
                    });
                    displayElement.innerText = value;
                },
                function (res) {
                    try {
                        const responseData = JSON.parse(res.xhr.response);
                        window.mdui.snackbar({
                            message: "提交数据失败, 原因: " + responseData.message
                        });
                    } catch (e) {
                        console.error(e);
                        snakeBar({
                            message: "与服务器连接出现问题，状态码: " + res.xhr.status
                        });
                    }
                },
                function () {
                    isAsking = false;
                    isModifying = false;
                    dialog.open = false;
                },
                "application/json"
            );
    }

    confirmButton.addEventListener("click", confirmCLickEvent, { once: true });
    dialog.addEventListener("close", function () {
        displayElement.style.display = "block";
        input.remove();
        confirmButton.removeEventListener("click", confirmCLickEvent);
    }, { once: true })
    dialog.open = true;
    setTimeout(function () {
        confirmButton.focus();
    }, 0);
}



function insertDataToTable(isWriteable, dataArray, tableBody, nameWithDisplayObject, pageName, countStartAt, dataType) {
    let count = countStartAt || 0;
    for (const item of dataArray) {
        count++;
        const currentCount = count;
        const currentRow = document.createElement("tr");
        currentRow.classList.add(
            `${pageName}-row`,
            `${pageName}-row-id-${item.id}`,
            `${pageName}-row-count-${currentCount}`,
            currentCount % 2 === 0 ?
                `${pageName}-row-even` :
                `${pageName}-row-odd`
        );
        const countCell = document.createElement("td");
        countCell.classList.add(
            `${pageName}-cell`,
            `${pageName}-cell-count`,
        )
        countCell.innerText = currentCount;
        currentRow.appendChild(countCell);
        for (const key of Object.keys(nameWithDisplayObject)) {
            const fieldData = item[key] || "";
            const cell = document.createElement("td");
            cell.classList.add(
                `${pageName}-cell`,
                `${pageName}-cell-${key}`
            );
            if (!isWriteable) {
                cell.innerHTML = fieldData;
            } else {
                const display = document.createElement("span");

                display.innerText = fieldData;
                cell.addEventListener("dblclick", function() {
                    if (isModifying) return;
                    isModifying = true;
                    display.style.display = "none";

                    const modify = function () {
                        const value = $(this).val();
                        if (value !== fieldData) {
                            modifyCell(
                                pageName,
                                item.id,
                                currentCount,
                                key,
                                value,
                                nameWithDisplayObject[key],
                                display,
                                this,
                                dataType
                            )
                        } else {
                            display.style.display = "block";
                            this.remove();
                            isModifying = false;
                            isAsking = false;
                        }
                    }

                    const modifyInput = document.createElement("mdui-text-field");
                    modifyInput.setAttribute("variant", "filled");
                    modifyInput.setAttribute("label", nameWithDisplayObject[key]);
                    modifyInput.setAttribute("value", display.innerText);

                    modifyInput.addEventListener("blur", modify);

                    modifyInput.addEventListener("keydown", function(event) {
                        if (event.which === 13) modify.call(this);
                    });

                    setTimeout(() => {
                        modifyInput.focus();
                    }, 0);

                    this.appendChild(modifyInput);
                });
                cell.appendChild(display);
            }
            currentRow.appendChild(cell);
        }
        if (isWriteable) {
            const modifyOrDeleteCell = document.createElement("td");
            modifyOrDeleteCell.classList.add(
                "modify-or-delete-cell",
                `modify-or-delete-cell-${pageName}`
            );
            const modifyLink = document.createElement("a");
            const deleteLink = document.createElement("a");

            modifyLink.setAttribute("href", "javascript:;");
            deleteLink.setAttribute("href", "javascript:;");

            modifyLink.innerText = "修改";
            deleteLink.innerText = "删除";

            modifyLink.addEventListener("click", () => {

            });

            deleteLink.addEventListener("click", () => {
                deleteRow(item.id, currentCount, currentRow, dataType, pageName);
            });
            modifyOrDeleteCell.appendChild(modifyLink);
            modifyOrDeleteCell.appendChild(deleteLink);
            currentRow.appendChild(modifyOrDeleteCell);
        }
        tableBody.appendChild(currentRow);
    }

    function deleteRow(id, count, rowElement, dataType, pageName) {
        const dialog = document.querySelector(`.data-modify-ask-dialog-${pageName}`);
        const confirmButton = dialog.querySelector(".dialog-button-confirm");
        dialog.setAttribute("headline", "提示:");
        dialog.setAttribute("description", `你确定要删除 ${count} 行的数据吗?(无法恢复!)`);

        function confirmButtonClickEvent() {
            const requestData = {
                type: dataType,
                action: "DELETE",
                data: {
                    id: id,
                }
            };

            sendRequest("POST", API_ROOT + "data", JSON.stringify(requestData),
                function () {
                    rowElement.remove();
                    loadMaxByTop();
                    reCount(pageName);
                    window.mdui.snackbar({message: "删除成功!"});
                }, function (res) {
                    try {
                        const responseData = JSON.parse(res.xhr.response);
                        window.mdui.snackbar({
                            message: "提交数据失败, 原因: " + responseData.message
                        });
                    } catch (e) {
                        console.error(e);
                        snakeBar({
                            message: "与服务器连接出现问题，状态码: " + res.xhr.status
                        });
                    }
                }, function () {
                    dialog.open = false;
                    isAsking = false;
                },  "application/json")
        }

        confirmButton.addEventListener("click", confirmButtonClickEvent, {once: true});
        dialog.addEventListener("close", function () {
            confirmButton.removeEventListener("click", confirmButtonClickEvent);
            isAsking = false;
        }, {once: true});
        isAsking = true;
        dialog.open = true;
    }

    function reCount(pageName) {
        const countElementArray = document.querySelectorAll(`.${pageName}-cell-count`);
        for (let i = 0; i < countElementArray.length; i++) {
            countElementArray[i].innerText = i + 1;
        }
    }

    function insertNewRow() {}

    function modifyRow() {}
}
