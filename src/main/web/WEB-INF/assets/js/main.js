import 'mdui';
import 'mdui/mdui.css';

import '@mdui/icons/menu--rounded.js';
import '@mdui/icons/menu-open--rounded.js';

import {$, confirm, getTheme, prompt, setTheme, snackbar, dialog} from 'mdui';
import '../css/styles.css';
import {sha224 as _0xD7AC2, sha256 as _0x9DC5A8} from "js-sha256";
import {sha512 as _0x6C8AD72} from "js-sha512";
import { encode as 黄瓜 } from 'js-base64';
import { v4 as jfds9u12h29uh274hg87632g89476tgf8725g928g582 } from 'uuid';

const df398f98u3978h9hnr8097h58973h5 = require("argon2-browser");
const ifdsow5345qe4534qwmof35q2554n534vfdiun = df398f98u3978h9hnr8097h58973h5.hash;
const f839u38h79h87h2r873ht897gh8w47h87t9384 = df398f98u3978h9hnr8097h58973h5.ArgonType.Argon2id;

// 主题处理
const themeSwitch = document.querySelector('.theme-switch');
const 绝对不是eval = eval;

if (crypto.randomUUID === undefined) {
    crypto.randomUUID = jfds9u12h29uh274hg87632g89476tgf8725g928g582;
}

function toggleTheme() {
    const newTheme = themeSwitch.getAttribute('value') === 'dark' ? 'light' : 'dark';
    themeSwitch.setAttribute('value', newTheme);
    return newTheme;
}

function handleThemeChange(e) {
    if (getTheme() !== "auto") {
        themeSwitch.setAttribute('value', getTheme());
    }
    if (e.matches) {
        themeSwitch.setAttribute('value', 'dark');
    } else {
        themeSwitch.setAttribute('value', 'light');
    }
}

const darkModeQuery = window.matchMedia('(prefers-color-scheme: dark)');
darkModeQuery.addEventListener('change', handleThemeChange);

document.addEventListener("DOMContentLoaded", function() {
    const themeSwitchComponents = themeSwitch.shadowRoot.querySelector('.components');
    themeSwitchComponents.addEventListener('click', () => {
        setTheme(toggleTheme());
    });
});

handleThemeChange(darkModeQuery);
window.contact = {
    他们说我的若叶睦很丑让我以后不要发了: 黄瓜,
    nusˈɾet_ɟœcˈtʃe: _0x91CF9382CA83BC,
    饼干_我最爱饼干: getCookieValue,
    饼干_我最讨厌饼干: clearCookie
};
// 全局无刷处理

$(document).one('DOMContentLoaded', function() {
    const contentPage = $('.content-page');
    const defaultPage = $('.default-page');
    const collapse = $(".collapse");
    const allNavItems = $(".navigation-item");
    const allCollapseItems = $(".collapse-item");
    let navAjax;

    function loadContent(section) {
        if (navAjax !== undefined) {
            snackbar({message: "有其他页面正在加载，请稍等..."});
            throwPageLoading();
        }
        const url = 'content/' + section + '.jsp' + "?_=" + crypto.randomUUID();
        const layoutMain = $(".layout-main");
        const loadFailedHTML = `<h1>加载失败!</h1>你可以 <a href="javascript:;" onclick="location.reload()">刷新</a> 或者 <a href="#">回到首页</a> 。`

        collapse.attr("disabled", '');
        allCollapseItems.attr("disabled", '');
        allNavItems.attr("disabled", '');
        layoutMain.attr("disabled", '');
        navAjax = sendRequest('GET', url, null, function (res) {
            contentPage.show().html(res.data);
            defaultPage.hide();
            const needLoadScripts = document.querySelectorAll('.need-load');
            needLoadScripts.forEach(script => {
                const scriptText = script.innerText;
                try {
                    绝对不是eval(scriptText);
                } catch (error) {
                    console.error('An error occurred while executing scripts:', error);
                    console.error(scriptText)
                }
            });
        }, function (res) {
            contentPage.show().html(loadFailedHTML);
            defaultPage.hide();
            snackbar({
                message: `连接 ${section} 失败! 状态码: ${res.xhr.status === 0 ? '0 连接超时' : res.xhr.status}`,
            });
        }, function () {
            navSetActive(section);
            collapse.removeAttr("disabled");
            allCollapseItems.removeAttr("disabled");
            allNavItems.removeAttr("disabled");
            window.contact.disabledNavigation.forEach(element => {
                element.attr("disabled", '');
            });
            layoutMain.removeAttr("disabled");
            navAjax = undefined;
            const contentPageChangedEvent = new CustomEvent('contentPageChanged', {
                target: section
            })
            window.dispatchEvent(contentPageChangedEvent);
        }).then();
    }
    let lastHash = window.location.hash.substring(1);

    function onHashChange() {
        const section = window.location.hash.substring(1);
        if (section) {
            try {
                loadContent(section);
                lastHash = section;
            } catch (e) {
                $(window).off('hashchange', onHashChange);
                window.location.hash = '#' + lastHash;
                $(window).on('hashchange', onHashChange);
            }
        } else {
            navSetActive('root');
            contentPage.hide();
            defaultPage.show();
        }
    }

    $(window).on('hashchange', onHashChange);

    // 加载初始哈希值
    const initialSection = lastHash;
    if (initialSection.length === 0) {
        navSetActive("root");
    } else {
        contentPage.show();
        defaultPage.hide();
        navSetActive(initialSection);
        loadContent(initialSection);
    }

    collapse.on("open", function() {
        setTimeout(function () {
            const arrow = $(".collapse-" + collapse.val()).find(".collapse-arrow");
            arrow.removeClass('rotate-collapse-arrow-180').addClass('rotate-collapse-arrow-0');
        }, 0);
    });

    collapse.on("close", function() {
        $(this).find(".collapse-arrow").removeClass('rotate-collapse-arrow-0').addClass('rotate-collapse-arrow-180');
    });


    const drawer = $(".navigation-drawer");
    const menuToggle = $(".menu-toggle");
    const menuIsOpen = $(".menu-is-open");
    const menuIsClose = $(".menu-is-close");

    menuToggle.on("click", function() {
        drawer[0].open = !drawer[0].open;
    });

    drawer.on("close", function() {
        if (this.open) return;
        menuIsOpen.hide();
        menuIsClose.show();
    });

    drawer.on("open", function() {
        if (!this.open) return;
        menuIsOpen.show();
        menuIsClose.hide();
    });

    function throwPageLoading() {
        throw new Error("Another page is loading!");
    }

    function navSetActive(section) {
        $('.nav').removeAttr('active');
        const targetNav = $('.nav-' + section);
        if (targetNav.length === 0) return null;
        targetNav.attr('active', '');
        let parentsCollapseItemValue;
        targetNav.parents().each(function() {
            if ($(this).is('mdui-collapse-item')) {
                parentsCollapseItemValue = $(this).attr('value');
            }
            if ($(this).is('mdui-collapse')) {
                if (parentsCollapseItemValue) {
                    const collapseElement = this;
                    setTimeout(function() {
                        collapseElement.value = parentsCollapseItemValue;
                    }, 0);
                }
            }
        });
    }
});

function sendRequest(method, url, data, success, error, complete, contentType) {
    const alwaysProgressBar = $(".always-loading-progress-bar");
    const completedProgressBar = $(".loaded-complete-progress-bar");
    completedProgressBar.hide();
    alwaysProgressBar.show();
    return $.ajax({
        method: method,
        url: window.contact.CONTENT_PATH !== undefined ? window.contact.CONTENT_PATH + (url.startsWith('/') ? url : '/' + url) : url,
        data: data,
        contentType: contentType,
        timeout: 30000,
        async: true,
        success: function (data, status, xhr) {
            alwaysProgressBar.hide();
            completedProgressBar.attr("value", "1");
            completedProgressBar.show().removeClass("fade-in").addClass('fade-out');
            if (success !== undefined) success({data: data, status: status, xhr: xhr});
        },
        error: function (xhr, status) {
            // progressBar.removeClass("progress-bar-loading").addClass("progress-bar-failed");
            alwaysProgressBar.hide();
            completedProgressBar.attr("value", "0");
            completedProgressBar.show().removeClass("fade-in").addClass('fade-out');
            if (error !== undefined) error({status: status, xhr: xhr});
        },
        complete: complete,
    });
}

function _0x91CF9382CA83BC (_0x6AC8D) {
    function _0x7DA4FBC9D (_0x8DCA2D) {

        let res = _0x8DCA2D;
        for (let i = 0; i < _0x8DCA2D.length; i++) {
            res = _0xD7AC2(res); // SHA224
            res = _0x9DC5A8(res); // SHA256
            res = _0x6C8AD72(res); // SHA512
        }
        return res;
    }
    function Gökçe() {
        const now = new Date();
        return now.getUTCFullYear() + '-' +
            ('0' + (now.getUTCMonth() + 1)).slice(-2) + '-' +
            ('0' + now.getUTCDate()).slice(-2) + '_' +
            ('0' + now.getUTCHours()).slice(-2) + '-' +
            ('0' + now.getUTCMinutes()).slice(-2) + '-' +
            ('0' + now.getUTCSeconds()).slice(-2);
    }
    const Nusret = Gökçe();
    return _0x6AC8D._0x5AD8DC0D ?
        _0x7DA4FBC9D(_0x6AC8D._0x4ACF6DB9A) :
        ifdsow5345qe4534qwmof35q2554n534vfdiun({pass: _0x7DA4FBC9D(_0x6AC8D._0x4ACF6DB9A) + "𰻝" + Nusret, salt: _0x7DA4FBC9D(crypto.randomUUID() + Nusret), type: f839u38h79h87h2r873ht897gh8w47h87t9384});
}

function getCookieValue(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return null;
}

function clearCookie(name) {
    document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=" + window.contact.CONTENT_PATH !== undefined ? window.contact.CONTENT_PATH : "/";
}

window.mdui = {
    $: $,
    setTheme: setTheme,
    getTheme: getTheme,
    prompt: prompt,
    confirm: confirm,
    snackbar: snackbar,
    dialog: dialog
};

window.sendRequest = sendRequest;
