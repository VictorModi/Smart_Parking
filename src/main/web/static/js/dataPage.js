// only for isWriteable
let isModifying = false;
let isAsking = false;

let isLoading = false;
let isAllLoaded = false;
let loadMaxByTop = undefined;
let unknownIndexRow = [];

let nextOffset = 0;

window.addEventListener("contentPageChanged", function () {
    isModifying = false;
    isAsking = false;
    isLoading = false;
    isAllLoaded = false
    loadMaxByTop = undefined;

    setTimeout(function () {
        nextOffset = 0;
        unknownIndexRow = [];
    }, 0);
});

class CallbackQueue {
    constructor() {
        this.queue = [];
        this.processing = false;
        this.paused = false;
        this.locked = false;
    }

    enqueue(callback) {
        if (this.locked) return;
        this.queue.push(callback);
        this.process();
    }

    process() {
        if (this.processing || this.queue.length === 0 || this.paused) return;

        this.processing = true;
        const callback = this.queue.shift();
        callback(() => {
            this.processing = false;
            this.process();
        });
    }

    pause() {
        this.paused = true;
    }

    resume() {
        this.paused = false;
        this.process();
    }
}

const dataPageCallbackQueue = new CallbackQueue();

/**
 * Need Cooperate DataPageBuild (Kotlin || mba.vm.smart.parking.frontend), it will create column to table and other thing...?
 *
 * @param pageName
 * @param isWriteable
 * @param nameWithDisplayObject
 * @param dataType
 */
function setDataPage(pageName, isWriteable, nameWithDisplayObject, dataType) {
    const layoutMain = document.querySelector(".layout-main");
    const loadMaxRows = 15;
    const filters = getFiltersFromUrl();
    const isFiltered = Object.keys(filters).length > 0;
    const infoTheadMainTr = document.querySelector(`.${pageName}-thead`).children[0];
    const countTd = document.createElement("td");

    countTd.innerText = "#";
    countTd.classList.add(`${pageName}-column`, `${pageName}-column-count`);
    infoTheadMainTr.appendChild(countTd);
    Object.entries(nameWithDisplayObject).forEach(([key, value]) => {
        const currentTd = document.createElement("td");
        currentTd.classList.add(`${pageName}-column`, `${pageName}-column-${key}`);
        currentTd.innerText = value;
        infoTheadMainTr.appendChild(currentTd);
    });

    if (isWriteable) {
        const methodTd = document.createElement("td");
        methodTd.classList.add(`${pageName}-column`, `${pageName}-column-method`);
        methodTd.innerText = "操作";
        infoTheadMainTr.appendChild(methodTd);
    }
    const tableBody = document.querySelector(`.${pageName}-tbody`);
    function startLoad() {
        if (isAllLoaded || isLoading) return;
        isLoading = true;
        dataPageCallbackQueue.enqueue((done) => {
            function handleSuccess(res) {
                const listData = res.data.list;
                isAllLoaded = listData.length < loadMaxRows;
                const snakeBarData = { message: "已经到底啦" };
                if (isFiltered) {
                    snakeBarData.message += ", 是否清空筛选器以查看更多?";
                    snakeBarData.action = "清空筛选器";
                    snakeBarData.onActionClick = function () {
                        removeQueryParams();
                        location.reload();
                    }
                }
                if (isAllLoaded) window.mdui.snackbar(snakeBarData);
                insertDataToTable(isWriteable, listData, tableBody, nameWithDisplayObject, pageName, nextOffset, dataType);
                nextOffset += loadMaxRows;
            }

            getDataArray(dataType, filters, loadMaxRows, nextOffset,
                function (res) {
                    dataPageCallbackQueue.pause();
                    try {
                        const data = JSON.parse(res.xhr.response);
                        snakeBar({
                            message: "获取数据失败, 原因: " + data["message"],
                            action: "重试",
                            onActionClick: function () {
                                dataPageCallbackQueue.resume();
                            }
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
        if (isAllLoaded) return;
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
            dataPageCallbackQueue.enqueue((done) => {
                _loadByMaxTop();
                done();
            })
        }
    }

    setTimeout(function () {
        _loadByMaxTop();
        window.addEventListener("contentPageChanged", function () {
            layoutMain.removeEventListener("scroll", loadMore);
            removeQueryParams();
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

        function insertRow (obj) {
            console.log(obj);
            const requestData = {
                type: dataType,
                action: "INSERT",
                data: {...obj}
            }
            sendRequest("POST", API_ROOT + "data", JSON.stringify(requestData),
                function () {
                    window.mdui.snackbar({message: "插入成功"});
                    insertDataToTable(false,[obj], tableBody, nameWithDisplayObject, pageName, -1, dataType);
                    const tr = tableBody.children[tableBody.children.length - 1];
                    tr.classList.add(`${pageName}-row-unknown-index`);
                    if (isWriteable) tr.appendChild(document.createElement("tr"));
                    unknownIndexRow.push(tr);
                    isAllLoaded = false;
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
                    isAsking = false;
                },  "application/json")
        }

        const insertButton = document.querySelector(`.data-page-insert-button-${pageName}`);
        if (insertButton) insertButton.addEventListener("click", function () {
            inputDialog(pageName, "插入", "插入一条新数据", insertRow);
        });
        loadMaxByTop = _loadByMaxTop;

        const filterButton = document.querySelector(`.data-page-filter-button-${pageName}`);
        if (isFiltered) {
            filterButton.setAttribute("icon", "filter_alt--rounded");
        }
        function filter(obj) {
            Object.entries(obj).forEach(([key, value]) => {
                if (value) {
                    updateQueryParam(`filter-${key}`, value);
                } else {
                    removeQueryParam(`filter-${key}`);
                }
            })
            location.reload();
        }
        filterButton.addEventListener("click", function () {
            inputDialog(pageName, "筛选", "请填写您的筛选条件", filter, filters);
        })
    }, 0);
}

function getDataArray(dataType, extra, limit, offset, ifError) {
    const requestData = {
        type: dataType,
        action: "SELECT",
        data: {
            ...extra,
            limit: limit,
            offset: offset || 0,
        }
    };

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


/**
 *  插入数据， 以及配置相关事件
 *
 * @param isWriteable 当前用户是否可写
 * @param dataArray 数据数组
 * @param tableBody 表内容元素
 * @param nameWithDisplayObject 包含每个列的 MAP
 * @param pageName 当前页面名称，用于确定元素的 class Name
 * @param countStartAt 计数开始于 ?
 * @param dataType 数据类型，发送 request 时使用
 */
function insertDataToTable(isWriteable, dataArray, tableBody, nameWithDisplayObject, pageName, countStartAt, dataType) {
    let count = countStartAt || 0;
    let lastRow = undefined;
    for (const item of dataArray) {
        count++;
        const currentCount = count;
        const currentRow = document.createElement("tr");
        currentRow.dataset.id = item.id;
        currentRow.dataset.index = currentCount;
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
            cell.dataset.key = key;
            cell.classList.add(
                `${pageName}-cell`,
                `${pageName}-data-cell`,
                `${pageName}-cell-${key}`
            );
            const toolTips = document.createElement("mdui-tooltip");
            const display = document.createElement("span");
            display.innerText = fieldData;
            display.classList.add("cell-display");
            toolTips.appendChild(display);
            toolTips.setAttribute("content", nameWithDisplayObject[key])
            if (isWriteable) {
                cell.addEventListener("dblclick", function() {
                    if (isModifying) return;
                    isModifying = true;
                    display.style.display = "none";

                    const modify = function () {
                        // const value = $(this).val();
                        const value = this.value;
                        if (value !== fieldData) {
                            modifyCell(
                                item.id,
                                currentRow.dataset.index,
                                key,
                                value,
                                nameWithDisplayObject[key],
                                display,
                                this
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
            }
            cell.appendChild(toolTips);
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
                modifyRow(currentRow, dataType);
            });

            deleteLink.addEventListener("click", () => {
                deleteRow(item.id, currentRow.dataset.index, currentRow, dataType, pageName);
            });
            modifyOrDeleteCell.appendChild(modifyLink);
            modifyOrDeleteCell.appendChild(deleteLink);
            currentRow.appendChild(modifyOrDeleteCell);
        }

        if (unknownIndexRow.length > 0 && currentCount !== 0) {
            const currentRowObj = trToObj(pageName, currentRow);
            const firstUnknownIndexRow = trToObj(pageName, unknownIndexRow[0]);
            if (isAllMatch(currentRowObj, firstUnknownIndexRow)) {
                unknownIndexRow[0].remove();
                unknownIndexRow.shift();
                tableBody.appendChild(currentRow);
            } else {
                tableBody.insertBefore(currentRow, unknownIndexRow[0]);
            }
        } else {
            tableBody.appendChild(currentRow);
        }
        lastRow = currentRow;
    }

    function modifyRow(rowElement, dataType) {
        function startModify(obj) {
            const requestData = {
                type: dataType,
                action: "UPDATE",
                data: {
                    "id": +rowElement.dataset.id,
                    ...obj
                }
            }

            sendRequest("POST", API_ROOT + "data", JSON.stringify(requestData),
                function () {
                    Object.entries(obj).forEach(([key, value]) => {
                        const cellDisplay = rowElement.querySelector(`.${pageName}-cell-${key}`).querySelector(".cell-display");
                        if (cellDisplay) {
                            cellDisplay.innerText = value;
                        }
                    })
                    window.mdui.snackbar({message: "修改成功"});
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
                    isAsking = false;
                },  "application/json")
        }
        inputDialog(
            pageName,
            "修改",
            `正在修改第 ${rowElement.dataset.index} 行`,
            startModify,
            trToObj(pageName, rowElement)
        );
    }

    function deleteRow(id, count, rowElement, dataType) {
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
                    nextOffset--;
                    loadMaxByTop();
                    dataPageCallbackQueue.enqueue((done) => {
                        reCount();
                        done();
                    });
                    window.mdui.snackbar({message: "删除成功!"});
                }, function (res) {
                    try {
                        const responseData = JSON.parse(res.xhr.response);
                        window.mdui.snackbar({
                            message: `提交数据失败, 原因: ${responseData.message} \n 可能是数据已经被删除过了, 刷新页面后重新检查?`
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
        setTimeout(function () {
            confirmButton.focus();
        }, 0);
    }

    function reCount() {
        document.querySelectorAll(`.${pageName}-cell-count`).forEach((cell, index) => {
            cell.innerText = index + 1;
        });
        document.querySelectorAll(`.${pageName}-row`).forEach((row, index) => {
            const countPrefix = `${pageName}-row-count-`;
            const classesToRemove = [];
            const newCount = index + 1;
            let isUnknownIndexRow = false;

            for (const className of row.classList) {

                if (className.startsWith(countPrefix) && !isNaN(className.slice(countPrefix.length))) {
                    if (className === `${pageName}-row-count-${newCount}`) return;
                    classesToRemove.push(className);
                }

                if (className === `${pageName}-row-odd`) {
                    classesToRemove.push(className);
                } else if (className === `${pageName}-row-even`) {
                    classesToRemove.push(className);
                }
            }


            for (const className of classesToRemove) {
                row.classList.remove(className);
            }

            if (isUnknownIndexRow) {
                row.classList.add(newCount % 2 === 0 ? `${pageName}-row-even` : `${pageName}-row-odd`);
            } else {
                row.classList.add(
                    `${pageName}-row-count-${newCount}`,
                    newCount % 2 === 0 ? `${pageName}-row-even` : `${pageName}-row-odd`
                );
                row.dataset.index = newCount;
            }
        });
    }

    function modifyCell(targetID, line, key, value, keyDisplay, displayElement, input) {
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
                    dialog.open = false;
                },
                "application/json"
            );
        }

        confirmButton.addEventListener("click", confirmCLickEvent, { once: true });
        dialog.addEventListener("close", function () {
            isAsking = false;
            isModifying = false;
            displayElement.style.display = "block";
            input.remove();
            confirmButton.removeEventListener("click", confirmCLickEvent);
        }, { once: true })
        dialog.open = true;
        setTimeout(function () {
            confirmButton.focus();
        }, 0);
    }
}

function inputDialog(pageName, diaLogHeadLine, dialogDescription, confirmCallBack, defaultValues) {
    const dialog = document.querySelector(`.data-modify-dialog-${pageName}`);
    const inputArray = dialog.querySelectorAll(".data-modify-dialog-field");
    const confirmButton = dialog.querySelector(".dialog-button-confirm");
    dialog.setAttribute("headline", diaLogHeadLine);
    dialog.setAttribute("description", dialogDescription);

    if (defaultValues !== undefined) {
        Object.entries(defaultValues).forEach(([key, value]) => {
            const field = dialog.querySelector(`.data-modify-dialog-field-${key}`);
            if (field) {
                field.value = value;
            }
        });
    }
    function callback() {
        const inputData = [...inputArray].reduce((acc, value) => {
            acc[value.dataset.key] = value.value;
            return acc;
        }, {});
        confirmCallBack(inputData);
        dialog.open = false;
    }
    confirmButton.addEventListener("click", callback);
    dialog.addEventListener("close", () => {
        isAsking = false;
        isModifying = false;
        confirmButton.removeEventListener("click", callback)
        setTimeout(function () {
            inputArray.forEach(element => {
                element.value = '';
            });
        }, 0);
    });
    dialog.open = true;
    setTimeout(function () {
        dialog.querySelector(".data-modify-dialog-field").focus();
    }, 0);
}

function trToObj(pageName, trElement) {
    const res = {};

    trElement.querySelectorAll(`.${pageName}-data-cell`).forEach((cell) => {
        const key = cell.dataset.key;
        res[key] = cell.textContent;
    })

    return res;
}

function isAllMatch(obj1, obj2) {
    if (obj1 === obj2) {
        return true;
    }

    if (typeof obj1 !== typeof obj2) {
        return false;
    }

    if (typeof obj1 === 'object' && obj1 !== null && obj2 !== null) {
        const keys1 = Object.keys(obj1);
        const keys2 = Object.keys(obj2);

        if (keys1.length !== keys2.length) {
            return false;
        }

        for (const key of keys1) {
            if (!isAllMatch(obj1[key], obj2[key])) {
                return false;
            }
        }

        return true;
    }

    return obj1 === obj2;
}



function updateQueryParam(key, value) {
    const url = new URL(window.location);
    const hash = url.hash;
    url.hash = '';
    url.searchParams.set(key, value);
    url.hash = hash;
    window.history.replaceState(null, null, url.toString());
}

function removeQueryParams() {
    const url = new URL(window.location);
    url.search = '';
    window.history.replaceState(null, null, url.toString());
}

function removeQueryParam(key) {
    const url = new URL(window.location);
    url.searchParams.delete(key);
    window.history.replaceState(null, null, url.toString());
}


function getFiltersFromUrl() {
    const url = new URL(window.location);
    const params = new URLSearchParams(url.search);
    const filters = {};

    params.forEach((value, key) => {
        if (key.startsWith('filter-')) {
            const filterKey = key.slice(7); // 移除 'filter-' 前缀
            filters[filterKey] = value;
        }
    });

    return filters;
}
