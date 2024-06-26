// only isWriteable
let isModifying = false;
let isAsking = false;

let isAllLoaded = false;

window.addEventListener("contentPageChanged", function () {
    isModifying = false;
    isAsking = false;
    isAllLoaded = false
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

    const tableBody = document.querySelector(`.${pageName}-tbody`);
    function startLoad() {
        if (isAllLoaded) return;
        callbackQueue.enqueue((done) => {
            function handleSuccess(res) {
                const listData = res.data.list;
                isAllLoaded = listData.length < loadMaxRows;
                if (isAllLoaded) window.mdui.snackbar({ message: "已经到底啦" });
                insertDataToTable(isWriteable, listData, tableBody, nameWithDisplayObject, pageName, nextOffset);
                nextOffset += loadMaxRows;
            }

            function handleFailure(res) {
                console.log(res);
                let errorMessage = "与服务器连接出现问题";
                if (res.xhr) {
                    errorMessage += "，状态码: " + res.xhr.status;
                }
                window.mdui.snackbar({ message: errorMessage });
            }

            getDataArray(dataType, undefined, loadMaxRows, nextOffset)
                .then(handleSuccess)
                .catch(handleFailure)
                .finally(() => {
                    done();
                });
        });
    }

    function loadMore() {
        if (isAllLoaded) {
            document.querySelector(".layout-main").removeEventListener("scroll", loadMore);
            return -1;
        }
        const startLoadScrollTop = this.scrollHeight - this.clientHeight - 5;
        const aboutScrollTop = Math.round(this.scrollTop);
        const isChecked = aboutScrollTop > startLoadScrollTop ||
            (startLoadScrollTop === 0 && aboutScrollTop === 0);
        if (isChecked) startLoad();
        return isChecked ? 1 : 0;
    }
    layoutMain.addEventListener("scroll", loadMore);

    function loadByMaxTop() {
        if (layoutMain.scrollHeight - layoutMain.clientHeight - 5 < 0 &&
            !isAllLoaded) {
            startLoad()
            callbackQueue.enqueue((done) => {
                loadByMaxTop();
                done();
            })
        }
    }

    setTimeout(function () {
        loadByMaxTop();
        window.addEventListener("contentPageChanged", function () {
            layoutMain.removeEventListener("scroll", loadMore)
            console.log(`${pageName} scroll Listener is removed.`);
        }, {once: true});
    }, 0);
}

function getDataArray(dataType, extra, limit, offset) {
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
        undefined,
        undefined,
        "application/json"
    );
}

function insertDataToTable(isWriteable, dataArray, tableBody, nameWithDisplayObject, pageName, countStartAt) {
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

                    const modifyInput = document.createElement("mdui-text-field");
                    modifyInput.setAttribute("variant", "filled");
                    modifyInput.setAttribute("label", nameWithDisplayObject[key]);
                    modifyInput.setAttribute("value", fieldData);

                    modifyInput.addEventListener("blur", modify);

                    modifyInput.addEventListener("keydown", function(event) {
                        if (event.which === 13) modify.call(this);
                    });

                    setTimeout(() => {
                        modifyInput.focus();
                    }, 0);

                    this.appendChild(modifyInput);
                });
                function modify() {
                    if (isAsking) return;
                    isAsking = true;
                    const currentValue = $(this).val();
                    if (currentValue !== display.textContent) {
                        window.mdui.dialog({
                            headline: "提示",
                            description: "当前文本框 (" + "第 " + currentCount + " 行 - " + nameWithDisplayObject[key] + ") 已修改, 是否保存?",
                            actions: [{
                                text: "取消",
                            },
                                {
                                    text: "确定",
                                    onClick: () =>{
                                    // display.innerText = currentValue;
                                    const data = {
                                        "type": "CAR",
                                        "action": "UPDATE",
                                        "data": {
                                            "id": item["id"]
                                        }
                                    }
                                    data.data[key] = currentValue;
                                    sendRequest("POST", API_ROOT + "data", JSON.stringify(data),
                                    function() {
                                        window.mdui.snackbar({
                                            message: "修改成功"
                                        });
                                        display.innerText = currentValue;
                                    },
                                    function(res) {
                                        try {
                                            const data = JSON.parse(res.xhr.response);
                                            snakeBar({
                                                message: "提交数据失败, 原因: " + data["message"]
                                            });
                                        } catch(e) {
                                            console.error(e);
                                            snakeBar({
                                                message: "与服务器连接出现问题，状态码: " + res.xhr.status
                                            });
                                        }
                                    },
                                    undefined, "application/json")
                    },
                    }],
                        onClosed: () =>{
                            isAsking = false;
                            isModifying = false;
                            this.remove();
                            display.style.display = "block";
                        }
                    });
                    } else {
                        isAsking = false;
                        isModifying = false;
                        this.remove();
                        display.style.display = "block";
                    }
                }
                cell.appendChild(display);
            }
            currentRow.appendChild(cell);
        }
        tableBody.appendChild(currentRow);
    }
}
