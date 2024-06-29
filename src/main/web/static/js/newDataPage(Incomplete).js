
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

class DataPageManager {
    constructor() {
        this.isModifying = false;
        this.isAsking = false;
        this.isLoading = false;
        this.isAllLoaded = false;
        this.loadMaxByTop = undefined;
        this.unknownIndexRow = [];
        this.nextOffset = 0;
        this.dataPageCallbackQueue = new CallbackQueue();

        window.addEventListener("contentPageChanged", () => this.resetFlags());
    }

    resetFlags() {
        this.isModifying = false;
        this.isAsking = false;
        this.isLoading = false;
        this.isAllLoaded = false;
        this.loadMaxByTop = undefined;
        setTimeout(() => {
            this.nextOffset = 0;
            this.unknownIndexRow = [];
        }, 0);
    }

    initPage(pageName, isWriteable, nameWithDisplayObject, dataType) {
        this.pageName = pageName;
        this.isWriteable = isWriteable;
        this.nameWithDisplayObject = nameWithDisplayObject;
        this.dataType = dataType;
        this.loadMaxRows = 15;

        this.layoutMain = document.querySelector(".layout-main");
        this.infoTheadMainTr = document.querySelector(`.${pageName}-thead`).children[0];
        this.tableBody = document.querySelector(`.${pageName}-tbody`);

        this.createTableHeader();
        this.addScrollEvent();
        this.setupInsertButton();
        this.loadInitialData();
    }

    createTableHeader() {
        const countTd = document.createElement("td");
        countTd.innerText = "#";
        countTd.classList.add(`${this.pageName}-column`, `${this.pageName}-column-count`);
        this.infoTheadMainTr.appendChild(countTd);

        Object.entries(this.nameWithDisplayObject).forEach(([key, value]) => {
            const currentTd = document.createElement("td");
            currentTd.classList.add(`${this.pageName}-column`, `${this.pageName}-column-${key}`);
            currentTd.innerText = value;
            this.infoTheadMainTr.appendChild(currentTd);
        });

        if (this.isWriteable) {
            const methodTd = document.createElement("td");
            methodTd.classList.add(`${this.pageName}-column`, `${this.pageName}-column-method`);
            methodTd.innerText = "操作";
            this.infoTheadMainTr.appendChild(methodTd);
        }
    }

    addScrollEvent() {
        this.layoutMain.addEventListener("scroll", () => this.loadMore());
    }

    setupInsertButton() {
        document.querySelector(`.data-page-insert-button-${this.pageName}`).addEventListener("click", () => {
            this.inputDialog("插入", "插入一条新数据", (obj) => this.insertRow(obj));
        });
    }

    async loadInitialData() {
        setTimeout(() => {
            this.loadByMaxTop();
            window.addEventListener("contentPageChanged", () => {
                this.layoutMain.removeEventListener("scroll", () => this.loadMore());
                console.log(`${this.pageName} scroll Listener is removed.`);
            }, { once: true });
        }, 0);
    }

    async startLoad() {
        if (this.isAllLoaded || this.isLoading) return;
        this.isLoading = true;
        await this.dataPageCallbackQueue.enqueue(async (done) => {
            try {
                const res = await this.getDataArray(this.loadMaxRows, this.nextOffset);
                const listData = res.data.list;
                this.isAllLoaded = listData.length < this.loadMaxRows;
                if (this.isAllLoaded) window.mdui.snackbar({ message: "已经到底啦" });
                this.insertDataToTable(listData);
                this.nextOffset += this.loadMaxRows;
            } catch (e) {
                console.error(e);
                window.mdui.snackbar({ message: "获取数据失败, 原因: " + e.message });
                this.isAllLoaded = true;
            } finally {
                this.isLoading = false;
                done();
            }
        });
    }

    loadMore() {
        if (this.isAllLoaded) return;
        const startLoadScrollTop = this.layoutMain.scrollHeight - this.layoutMain.clientHeight - 5;
        const aboutScrollTop = Math.round(this.layoutMain.scrollTop);
        if (aboutScrollTop > startLoadScrollTop || (startLoadScrollTop === 0 && aboutScrollTop === 0)) {
            this.startLoad();
        }
    }

    loadByMaxTop() {
        if (this.layoutMain.scrollHeight - this.layoutMain.clientHeight - 5 < 0 && !this.isAllLoaded) {
            this.startLoad();
            this.dataPageCallbackQueue.enqueue((done) => {
                this.loadByMaxTop();
                done();
            });
        }
    }

    async getDataArray(limit, offset) {
        const requestData = {
            type: this.dataType,
            action: "SELECT",
            data: { limit, offset: offset || 0 }
        };
        const response = await fetch(window.contact.CONTENT_PATH + API_ROOT + "data", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestData)
        });
        if (!response.ok) {
            throw new Error(response.statusText);
        }
        return await response.json();
    }

    insertRow(obj) {
        const requestData = {
            type: this.dataType,
            action: "INSERT",
            data: { ...obj }
        };
        this.sendRequest("POST", API_ROOT + "data", requestData)
            .then(() => {
                window.mdui.snackbar({ message: "插入成功" });
                this.insertDataToTable([obj], -1);
            })
            .catch((err) => {
                console.error(err);
                window.mdui.snackbar({ message: "提交数据失败, 原因: " + err.message });
            });
    }

    async sendRequest(method, url, data) {
        const response = await fetch(window.contact.CONTENT_PATH + url, {
            method: method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });
        if (!response.ok) {
            throw new Error(await response.text());
        }
        return await response.json();
    }

    insertDataToTable(dataArray, countStartAt = 0) {
        let count = countStartAt;
        dataArray.forEach((item) => {
            count++;
            const currentRow = this.createTableRow(item, count);
            if (this.unknownIndexRow.length > 0 && count !== 0) {
                const currentRowObj = this.trToObj(currentRow);
                const firstUnknownIndexRow = this.trToObj(this.unknownIndexRow[0]);
                if (this.isAllMatch(currentRowObj, firstUnknownIndexRow)) {
                    this.unknownIndexRow[0].remove();
                    this.unknownIndexRow.shift();
                    this.tableBody.appendChild(currentRow);
                } else {
                    this.tableBody.insertBefore(currentRow, this.unknownIndexRow[0]);
                }
            } else {
                this.tableBody.appendChild(currentRow);
            }
        });
    }

    createTableRow(item, count) {
        const currentRow = document.createElement("tr");
        currentRow.dataset.id = item.id;
        currentRow.dataset.index = count;
        currentRow.classList.add(`${this.pageName}-row`, `${this.pageName}-row-id-${item.id}`, `${this.pageName}-row-count-${count}`, count % 2 === 0 ? `${this.pageName}-row-even` : `${this.pageName}-row-odd`);

        const countCell = document.createElement("td");
        countCell.classList.add(`${this.pageName}-cell`, `${this.pageName}-cell-count`);
        countCell.innerText = count;
        currentRow.appendChild(countCell);

        Object.keys(this.nameWithDisplayObject).forEach((key) => {
            const fieldData = item[key] || "";
            const cell = document.createElement("td");
            cell.dataset.key = key;
            cell.classList.add(`${this.pageName}-cell`, `${this.pageName}-data-cell`, `${this.pageName}-cell-${key}`);
            if (!this.isWriteable) {
                cell.innerHTML = fieldData;
            } else {
                const display = document.createElement("span");
                display.innerText = fieldData;
                display.classList.add("cell-display");
                cell.appendChild(display);
                this.addModifyEvent(cell, display, item.id, count, key, fieldData);
            }
            currentRow.appendChild(cell);
        });

        if (this.isWriteable) {
            const modifyOrDeleteCell = document.createElement("td");
            modifyOrDeleteCell.classList.add("modify-or-delete-cell", `modify-or-delete-cell-${this.pageName}`);
            this.addModifyDeleteLinks(modifyOrDeleteCell, item.id, currentRow);
            currentRow.appendChild(modifyOrDeleteCell);
        }

        return currentRow;
    }

    addModifyEvent(cell, display, id, count, key, originalData) {
        const input = document.createElement("input");
        input.classList.add("cell-input");
        input.type = "text";
        input.value = originalData;

        display.addEventListener("click", () => {
            if (this.isModifying) return;
            this.isModifying = true;
            display.remove();
            cell.appendChild(input);
            input.focus();
        });

        input.addEventListener("blur", () => {
            if (input.value === originalData) {
                input.remove();
                cell.appendChild(display);
                this.isModifying = false;
                return;
            }
            this.updateData(id, key, input.value)
                .then(() => {
                    display.innerText = input.value;
                    input.remove();
                    cell.appendChild(display);
                    this.isModifying = false;
                })
                .catch((err) => {
                    console.error(err);
                    window.mdui.snackbar({ message: "修改数据失败, 原因: " + err.message });
                });
        });
    }

    async updateData(id, key, value) {
        const requestData = {
            type: this.dataType,
            action: "UPDATE",
            data: { id, key, value }
        };
        await this.sendRequest("POST", API_ROOT + "data", requestData);
    }

    addModifyDeleteLinks(cell, id, row) {
        const modifyLink = document.createElement("a");
        modifyLink.href = "javascript:void(0)";
        modifyLink.classList.add("modify-link");
        modifyLink.innerText = "修改";
        modifyLink.addEventListener("click", () => {
            this.inputDialog("修改", `修改第 ${row.dataset.index} 行数据`, (obj) => this.modifyRow(id, obj), trToObj(row));
        });
        cell.appendChild(modifyLink);

        const deleteLink = document.createElement("a");
        deleteLink.href = "javascript:void(0)";
        deleteLink.classList.add("delete-link");
        deleteLink.innerText = "删除";
        deleteLink.addEventListener("click", () => {
            this.deleteRow(id, row);
        });
        cell.appendChild(deleteLink);
    }

    modifyRow(id, obj) {
        const requestData = {
            type: this.dataType,
            action: "UPDATE",
            data: { id, ...obj }
        };
        this.sendRequest("POST", API_ROOT + "data", requestData)
            .then(() => {
                window.mdui.snackbar({ message: "修改成功" });
                const row = this.tableBody.querySelector(`.data-row-id-${id}`);
                Object.entries(obj).forEach(([key, value]) => {
                    row.querySelector(`.data-cell-${key}`).querySelector(".cell-display").innerText = value;
                });
            })
            .catch((err) => {
                console.error(err);
                window.mdui.snackbar({ message: "提交数据失败, 原因: " + err.message });
            });
    }

    deleteRow(id, row) {
        const requestData = {
            type: this.dataType,
            action: "DELETE",
            data: { id }
        };
        this.sendRequest("POST", API_ROOT + "data", requestData)
            .then(() => {
                window.mdui.snackbar({ message: "删除成功" });
                row.remove();
            })
            .catch((err) => {
                console.error(err);
                window.mdui.snackbar({ message: "提交数据失败, 原因: " + err.message });
            });
    }

    inputDialog(title, message, _callback, defaultValues) {
        const dialog = document.querySelector(`.data-modify-dialog-${this.pageName}`);
        const inputArray = dialog.querySelectorAll(".data-modify-dialog-field");
        const confirmButton = dialog.querySelector(".dialog-button-confirm");
        dialog.setAttribute("headline", title);
        dialog.setAttribute("description", message);

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
            _callback(inputData);
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

    trToObj(row) {
        const res = {};

        row.querySelectorAll(`.${this.pageName}-data-cell`).forEach((cell) => {
            const key = cell.dataset.key;
            res[key] = cell.textContent;
        })

        return res;
    }

    isAllMatch(obj1, obj2) {
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
    }
}

// 初始化页面管理器
const dataPageManager = new DataPageManager();
