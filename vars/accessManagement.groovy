
import java.util.*
import java.lang.reflect.*
import jenkins.model.Jenkins
import hudson.security.PermissionGroup
import hudson.security.Permission
import com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy
import com.michelin.cio.hudson.plugins.rolestrategy.Role
import com.synopsys.arc.jenkins.plugins.rolestrategy.RoleType

def findRoleEntry(grantedRoles, roleName) {
  for (def entry : grantedRoles) {
    Role role = entry.getKey()

    if (role.getName().equals(roleName)) {
      return entry
    }
  }

  return null
}

def call(Map config) {
  properties([
          parameters([
                  string(description: 'Jira ticket id only', name: 'Access_Request_ID', trim: true),
                  string(description: 'user email address or username', name: 'User_Name', trim: true),
                  string(defaultValue: 'ReadOnly', description: 'Global Role name', name: 'Global_Role_Name', trim: true),
                  [$class: 'ChoiceParameter', choiceType: 'PT_CHECKBOX', filterLength: 1, filterable: false, name: 'Project_Role_Name', randomName: 'choice-parameter-75219681082247', script: [$class: 'GroovyScript', fallbackScript: [classpath: [], oldScript: '', sandbox: false, script: ''], script: [classpath: [], oldScript: '', sandbox: false, script: '''import hudson.model.User
import hudson.model.Hudson
import hudson.security.AuthorizationStrategy
import hudson.security.Permission
import com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy
import com.michelin.cio.hudson.plugins.rolestrategy.RoleMap

AuthorizationStrategy strategy = Hudson.getInstance().getAuthorizationStrategy();

if (strategy != null && strategy instanceof com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy) {
    roleStrategy = (RoleBasedAuthorizationStrategy) strategy;
    // not very straightforward to get the groups for a given user
    roles = roleStrategy.getGrantedRoles("projectRoles")
  	def result = []
    for (entry in roles) {
          role = entry.key
          result.add(role.getName())
      }
 	return result   
}
''']]]])])

  def authStrategy = Jenkins.instance.getAuthorizationStrategy()

  if (authStrategy instanceof RoleBasedAuthorizationStrategy) {
    RoleBasedAuthorizationStrategy roleAuthStrategy = (RoleBasedAuthorizationStrategy) authStrategy

    // Make constructors available
    Constructor[] constrs = Role.class.getConstructors();
    for (Constructor<?> c : constrs) {
      c.setAccessible(true);
    }
    // Make the method assignRole accessible for Project role
    Method assignRoleMethod = RoleBasedAuthorizationStrategy.class.getDeclaredMethod("assignRole", RoleType.class, Role.class, String.class);
    assignRoleMethod.setAccessible(true);

    for (roleName in config.roleName) {
      def grantedRoles = authStrategy.getGrantedRoles(RoleBasedAuthorizationStrategy.PROJECT);
      if (grantedRoles != null) {
        // println "Got grantedRoles for " + RoleBasedAuthorizationStrategy.PROJECT

        def roleEntry = findRoleEntry(grantedRoles, roleName);
        if (roleEntry != null) {
          // println "Found role " + roleName

          def sidList = roleEntry.getValue()
          if (sidList.contains(config.userName)) {
            println "User " + config.userName + " already assigned to role " + roleName
          } else {
            println "Adding user " + config.userName + " to role " + roleName
            roleAuthStrategy.assignRole(RoleType.fromString(RoleBasedAuthorizationStrategy.PROJECT), roleEntry.getKey(), config.userName);
            println "OK"
          }

          Jenkins.instance.save()
        } else {
          println "Unable to find role " + config.roleName
        }
      } else {
        println "Unable to find grantedRoles for " + RoleBasedAuthorizationStrategy.PROJECT
      }
    }
    def grantedGlobalRoles = authStrategy.getGrantedRoles(RoleBasedAuthorizationStrategy.GLOBAL);
    if (grantedGlobalRoles != null) {

      def roleEntry = findRoleEntry(grantedGlobalRoles, config.globalroleName);
      if (roleEntry != null) {
        println "Found role " + config.globalroleName

        def sidList = roleEntry.getValue()
        if (sidList.contains(config.userName)) {
          println "User " + config.userName + " already assigned to Global role " + config.globalroleName
        } else {
          println "Adding user " + config.userName + " to Global role " + config.globalroleName
          roleAuthStrategy.assignRole(RoleType.fromString(RoleBasedAuthorizationStrategy.GLOBAL), roleEntry.getKey(), config.userName);
          println "OK"
        }

        Jenkins.instance.save()
      } else {
        println "Unable to find Global role " + config.globalroleName
      }
    } else {
      println "Unable to find grantedRoles for " + RoleBasedAuthorizationStrategy.GLOBAL
    }
  } else {
    println "Role Strategy Plugin not found!"
  }
}