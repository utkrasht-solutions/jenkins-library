
import java.util.*
import java.lang.reflect.*
import jenkins.model.Jenkins
import hudson.security.PermissionGroup
import hudson.security.Permission
import com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy
import com.michelin.cio.hudson.plugins.rolestrategy.Role
import com.synopsys.arc.jenkins.plugins.rolestrategy.RoleType


//def userName = params.User_Name
//def roleName = params.Project_Role_Name
//def globalroleName = params.Global_Role_Name

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
  properties([parameters([
          string(name: "Access_Request_ID", description: "Jira ticket id only"),
          string(name: "User_Name", description: "user email address or username"),
          string(name: "Global_Role_Name", defaultValue: "readonly", trim: true, description: "Global Role name"),
          string(name: "Project_Role_Name", trim: true, description: "Global Role name")
  ])])
//  roleName = config.roleName
//  userName = config.userName
//  globalroleName = config.globalroleName

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

    def grantedRoles = authStrategy.getGrantedRoles(RoleBasedAuthorizationStrategy.PROJECT);
    if (grantedRoles != null) {
      // println "Got grantedRoles for " + RoleBasedAuthorizationStrategy.PROJECT

      def roleEntry = findRoleEntry(grantedRoles, config.roleName);
      if (roleEntry != null) {
        // println "Found role " + config.roleName

        def sidList = roleEntry.getValue()
        if (sidList.contains(config.userName)) {
          println "User " + config.userName + " already assigned to role " + config.roleName
        } else {
          println "Adding user " + config.userName + " to role " + config.roleName
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
  } else {
    println "Role Strategy Plugin not found!"
  }

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

    def grantedRoles = authStrategy.getGrantedRoles(RoleBasedAuthorizationStrategy.GLOBAL);
    if (grantedRoles != null) {
      println "Got grantedRoles for " + RoleBasedAuthorizationStrategy.GLOBAL

      def roleEntry = findRoleEntry(grantedRoles, config.globalroleName);
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