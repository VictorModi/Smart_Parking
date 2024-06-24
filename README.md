# 智泊停车管理系统

## 题目要求

### “某智能停车场服务”平台设计与实现
随着自驾车出行的增加，停车成为人们出行考虑且头疼的事。为了解决停车问题，建设智能停车场服务平台，让人们出行时可查阅车位的多少、收费情况，同时可搜索附近的停车情况及优惠条件等。
该系统所涉及的模块可参考如下：管理员管理、车位管理、汽车信息管理、停车管理、收费管理、监控管理、预约管理、公告管理等。
### 硬性需求:
 - MySQL
 - JSP
 - Servlet
 - Tomcat

## 所使用技术栈
### 前端
 - JSP
 - MDUI
 - Npm
 - Webpack
 - Argon2
### 后端
 - Java
 - Kotlin
 - Servlet
 - GSON
 - lombok
 - SQLDelight
 - Bouncy Castle
 - OWASP

所以部署及其麻烦，我已经不想再写 JSP 或者 Servlet 了。

项目名没有在内涵任何一家企业或组织。

大部分代码由 VictorModi 编写。

## 部署?

1. 服务器安装 JDK、NPM , 下载 Tomcat, 项目下执行 `gradlew copyJars` 将绝大部分 jar 丢到目录 need-import-libs。
2. 将 need-import-libs 里的jar放入tomcat的lib内不要覆盖任何东西，出现重复项直接跳过。
3. 在项目目录下执行 `npm run build` 构建 最主要的 Javascript 脚本。
4. 修改 src/main/web/WEB-INF/config.properties 的账户密码乱七八糟。
5. 用 Tomcat 启动，一般来说就可以了。

## 我真的很累。
 - 何时我能学好数学将自己的 LaTeX 挥洒向这片大地，
 - 看了需求还得写注释这作业做得我五体投地。

## 十分感谢
[<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png" width="200"/>](https://www.jetbrains.com)
- 没有 JetBrains 的 IDE 我毛屁都写不出来,直接 DIE 了。
- MDUI 真的是太好看了。
- 没有 GXJZY 我就没有如此标准的摆烂机会。
- 感谢 MyGo!!!!! 能让我 coding 时听到天籁。
- 感谢 OpenAI 的 chatGPT3.5 (4.0o?) 让我在几乎没有人会使用 SQLDelight 做 JSP 网页的情况下帮我把文档翻译成我能看懂的中文并且继续写下去。
- 后面我忘了，总之所有用到的技术背后的工作人员都是最棒的。
