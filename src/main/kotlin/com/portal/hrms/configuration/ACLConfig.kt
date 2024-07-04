package com.portal.hrms.configuration

import com.portal.hrms.authentication.UserPermissionEvaluator
import com.portal.hrms.utils.Role
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.core.env.Environment
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.acls.domain.*
import org.springframework.security.acls.jdbc.BasicLookupStrategy
import org.springframework.security.acls.jdbc.JdbcMutableAclService
import org.springframework.security.acls.model.MutableAclService
import org.springframework.security.acls.model.PermissionGrantingStrategy
import org.springframework.security.acls.model.Sid
import org.springframework.security.acls.model.SidRetrievalStrategy
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


@Configuration
@EnableMethodSecurity
class ACLConfig(
    private val env: Environment,
    @Lazy private val customPermissionEvaluator: UserPermissionEvaluator
) {

    @Bean
    fun aclCache() = SpringCacheBasedAclCache(
        ConcurrentMapCache("acl_cache"),
        permissionGrantingStrategy(),
        aclAuthorizationStrategy()
    )

    @Bean
    fun auditLogger(): AuditLogger = ConsoleAuditLogger()

    @Bean
    fun dataSource() = DriverManagerDataSource().apply {
        env.getProperty("spring.datasource.driver-class-name")?.let { setDriverClassName(it) }
        url = env.getProperty("spring.datasource.url")
        username = env.getProperty("spring.datasource.username")
        password = env.getProperty("spring.datasource.password")
    }

    @Bean
    fun lookupStrategy() = BasicLookupStrategy(
        dataSource(),
        aclCache(),
        aclAuthorizationStrategy(),
        auditLogger()
    )

    @Bean
    fun aclService(): MutableAclService = JdbcMutableAclService(dataSource(), lookupStrategy(), aclCache()).apply {
        setSidIdentityQuery("select currval(pg_get_serial_sequence('acl_sid', 'id'))")
        setClassIdentityQuery("select currval(pg_get_serial_sequence('acl_class', 'id'))")
    }

    @Bean
    fun aclAuthorizationStrategy(): AclAuthorizationStrategy = AclAuthorizationStrategyImpl(SimpleGrantedAuthority(Role.ADMIN))

    @Bean
    fun permissionGrantingStrategy(): PermissionGrantingStrategy = DefaultPermissionGrantingStrategy(auditLogger())

    @Bean
    fun sidRetrievalStrategy(): SidRetrievalStrategy {
        return object : SidRetrievalStrategyImpl() {
            override fun getSids(authentication: Authentication?): MutableList<Sid> {
                val principal = authentication?.principal
                val sid: Sid = if (principal is UserDetails) {
                    PrincipalSid(principal.username)
                } else {
                    PrincipalSid(principal.toString())
                }
                return mutableListOf(sid)
            }
        }
    }

    @Bean
    fun aclSpringCacheManager() = SimpleCacheManager().apply {
        setCaches(listOf(ConcurrentMapCache("aclCache")))
    }

    @Bean
    fun methodSecurityExpressionHandler(): MethodSecurityExpressionHandler {
        val expressionHandler = DefaultMethodSecurityExpressionHandler()
        expressionHandler.setPermissionEvaluator(customPermissionEvaluator)

        return expressionHandler
    }
}