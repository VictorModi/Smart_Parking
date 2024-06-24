
const KEY_ENTER = 13;

const userNameInput = $(".login--input-username");
const passwordInput = $(".login--input-password");
const loginBtn = $(".login--btn-login");

userNameInput.on('input', handleUsernameInput).on('keydown', handleUsernameInputKeyDown);
passwordInput.on('input', handlePasswordInput).on("keydown", handlePasswordInputKeyDown);

loginBtn.on('click', handleLoginButtonClick);

function handleUsernameInput() {
    const input = $(this);
    const value = input.val();

    if (value.indexOf("@") !== -1) {
        setInputAttributes(input, "email", "邮箱", "alternate_email--rounded");
    } else {
        setInputAttributes(input, "text", "用户名或邮箱", "person--rounded");
    }

    if (/^[a-zA-Z0-9@.]+$/.test(value)) {
        setInputCustomValidityAndReportValidity(userNameInput, "")

    } else {
        setInputCustomValidityAndReportValidity(userNameInput, '输入有误!');
    }
}

function setInputAttributes(input, type, label, iconName) {
    input.prop("type", type)
        .prop("inputmode", type)
        .prop("label", label)
        .children("mdui-icon")
        .prop("name", iconName);
}

function handleUsernameInputKeyDown(event) {
    if (event.which === KEY_ENTER) {
        passwordInput.each(function () {
            this.focus();
        })
    }
}

function handlePasswordInput() {
    if (/^[a-zA-Z0-9!@#$%^&*()_+{}\[\]:;<>,.?/\\|-]+$/.test(this.value)) {
        setInputCustomValidityAndReportValidity(passwordInput, "")
    } else {
        setInputCustomValidityAndReportValidity(passwordInput, '输入有误!');
    }
}

function handlePasswordInputKeyDown(event) {
    if (event.which === KEY_ENTER) {
        loginBtn.each(function () {
            this.click();
        })
    }
}

function handleLoginButtonClick() {
    function setInputEnable(enable) {
        if (enable) {
            userNameInput.removeAttr("disabled");
            passwordInput.removeAttr("disabled");
        } else {
            userNameInput.attr("disabled", "");
            passwordInput.attr("disabled", "");
        }
    }
    setInputEnable(false);
    this.setAttribute("loading", "");
    this.setAttribute("disabled", "");

    const username = userNameInput.val();
    const password = passwordInput.val();

    let isValid = true;

    if (password.length === 0) {
        setInputCustomValidityAndReportValidity(passwordInput, '密码不能为空');
        isValid = false;
    }

    if (username.length === 0) {
        setInputCustomValidityAndReportValidity(userNameInput, '用户名不能为空');
        isValid = false;
    }

    if (!isValid) {
        snakeBar({ message: '输入有错误，请检查' });
        this.removeAttribute("loading");
        this.removeAttribute("disabled");
        setInputEnable(true);
        return;
    }

    doLogin(username, password, function () {
        userNameInput.val("");
        passwordInput.val("");
    }, undefined, function () {
        tryAllowLoginBtn();
        setInputEnable(true);
    });
}

function setInputCustomValidityAndReportValidity(input, message) {
    if (message) {
        loginBtn.attr("disabled", "");
    } else {
        tryAllowLoginBtn();
    }
    input.each(function () {
        this.setCustomValidity(message);
        this.reportValidity();
        this.focus();
    })
}

function tryAllowLoginBtn() {
    const username = userNameInput.val();
    const password = passwordInput.val();
    if (username.length === 0 || password === 0) {
        return
    }
    loginBtn.removeAttr("loading");
    loginBtn.removeAttr("disabled");
}


let loginAjax;

function doLogin(username, password, success, error, complete) { // 错了实际上是SHA-1
    let passwordEncoded; // 这可是 MD5
    window.contact.nusˈɾet_ɟœcˈtʃe({_0x4ACF6DB9A: password}).then(hash => { // 经过 base64 绝对很安全捏 ~(￣▽￣)~*
        passwordEncoded = window.contact.他们说我的若叶睦很丑让我以后不要发了(hash.encoded);
        sendRequest("POST", API_ROOT + "login", {
            [username.indexOf("@") === -1 ? "username" : "email"]: username,
            password: passwordEncoded
        }, function (res) {
            if (success !== undefined) success(res);
            snakeBar({
                message: "登录成功!"
            });
            setTimeout(function (){
                window.location = window.contact.CONTENT_PATH;
            }, 2000);
            window.contact.nusˈɾet_ɟœcˈtʃe = undefined;
        }, function (res) {
            if (error !== undefined) error(res);
            try {
                const data = JSON.parse(res.xhr.response);
                snakeBar({
                    message: "登录失败, 原因: " + data["message"]
                });
            } catch (e) {
                console.error(e);
                snakeBar({
                    message: "与服务器连接出现问题，状态码: " + res.xhr.status
                });
            }
        }, complete);
    }).catch(error => {
        snakeBar({
            message: "未知错误。" + error
        });
        complete()
    });
}
