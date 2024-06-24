package mba.vm.smart.parking.tool

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import mba.vm.smart.parking.CookieQueries
import mba.vm.smart.parking.DataSourceProvider.getDatabase
import mba.vm.smart.parking.GetUserByID
import mba.vm.smart.parking.UserQueries
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


/**
 * SmartParking - mba.vm.smart.parking.tool
 * @description TODO: coming soon.
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/4 下午12:54
 */
object UserLoginTool {
    /**
     * 根据请求获取用户和相关的Cookie。
     * @param request HTTP请求对象。
     * @return 包含用户和Cookie的Pair对象，如果未找到则返回null。
     */
    fun getUserByRequest(request: HttpServletRequest): Pair<GetUserByID, Cookie>? {
        val cookies = request.cookies ?: return null
        val cookieQueries = getCookieQueries()
        val userQueries = getUserQueries()

        val tokenCookie = cookies.firstOrNull { it.name == "token" } ?: return null
        val userId = cookieQueries.getUserIDByToken(tokenCookie.value).executeAsOneOrNull() ?: return null
        cookieQueries.updateLastUseByValue(tokenCookie.value)
        cookieQueries.setExpiredTokenInactive()

        val user = userQueries.getUserByID(userId).executeAsOneOrNull() ?: return null
        return Pair(user, tokenCookie)
    }

    /**
     * 生成指定数量的UTC时间字符串数组。
     * @param i 要生成的字符串数量。
     * @return 包含UTC时间字符串的数组。
     */
    private fun generateUtcTimeStrings(i: Int): Array<String> {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd_HH-mm-ss")
        val now = LocalDateTime.now(ZoneOffset.UTC)
        return Array(i) { seconds ->
            now.plusSeconds(seconds.toLong()).format(dateTimeFormatter)
        }
    }

    /**
     * 通过电子邮件和密码登录用户。
     * @param emailAddr 用户的电子邮件地址。
     * @param password 用户的密码。
     * @return 如果登录成功，返回生成的Cookie字符串，否则返回空字符串。
     */
    fun loginByEmail(emailAddr : String, password : String) : String {
        val userQueries = getUserQueries()
        val ids = userQueries.getUsersIDsByEmail(emailAddr).executeAsList()
        if (ids.isEmpty()) {
            return ""
        }
        for (id in ids) {
            if (isPasswordMatches(userQueries, id, password)) {
                return createCookie(id)
            }
        }
        return ""
    }

    /**
     * 通过用户名和密码登录用户。
     * @param username 用户名。
     * @param password 用户的密码。
     * @return 如果登录成功，返回生成的Cookie字符串，否则返回空字符串。
     */
    fun loginByUsername(username: String, password: String): String {
        val userQueries = getUserQueries()
        val id = userQueries.getIDbyUsername(username).executeAsOneOrNull() ?: return ""
        if (isPasswordMatches(userQueries, id, password)) {
            return createCookie(id)
        }
        return ""
    }

    /**
     * 检查用户密码是否匹配。
     * @param userQueries 用户查询对象。
     * @param userid 用户ID。
     * @param password 用户的密码。
     * @return 如果密码匹配，返回true，否则返回false。
     */
    private fun isPasswordMatches(userQueries: UserQueries, userid: Int, password: String): Boolean {
        val argon2PasswordEncoder = Argon2PasswordEncoder(0,0,0,0,0)
        val userPassword = userQueries.getPasswordByID(userid).executeAsOneOrNull() ?: return false
        val offsetMinuteArray = generateUtcTimeStrings(5)
        for (time in offsetMinuteArray) {
            val targetPassword = userPassword + "\uD883\uDEDD" +  time
            if (argon2PasswordEncoder.matches(targetPassword, password)) {
                userQueries.updateLastLoginByID(userid)
                return true
            }
        }
        return false
    }

    /**
     * 生成一个新的Cookie字符串。
     * @return 生成的Cookie字符串。
     */
    private fun genCookie(): String {
        val uuid = UUID.randomUUID().toString()
        val digest = MessageDigest.getInstance("SHA-1")
        val hashBytes = digest.digest(uuid.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * 创建一个新的Cookie并将其插入数据库。
     * @param userid 用户ID。
     * @return 生成的Cookie字符串。
     */
    private fun createCookie(userid: Int) : String {
        val cookieQueries = getCookieQueries()
        val cookie = genCookie()
        cookieQueries.insertCookie(userid, cookie, LocalDateTime.now().plusDays(3))
        return cookie
    }

    /**
     * 获取Cookie查询对象。
     * @return Cookie查询对象。
     */
    private fun getCookieQueries(): CookieQueries {
        return getDatabase().cookieQueries
    }

    /**
     * 获取用户查询对象。
     * @return 用户查询对象。
     */
    private fun getUserQueries(): UserQueries {
        return getDatabase().userQueries
    }
}
