import java.util.*
import java.lang.reflect.*
import jenkins.model.Jenkins
import hudson.security.PermissionGroup
import hudson.security.Permission
import com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy
import com.michelin.cio.hudson.plugins.rolestrategy.Role
import com.synopsys.arc.jenkins.plugins.rolestrategy.RoleType

def call(Map roles) {
def findRoleEntry(grantedRoles, roleName)
{
  for (def entry : grantedRoles)
  {
    Role role = entry.getKey()

    if (role.getName().equals(roleName))
    {
      return entry
    }
  }

  return null
}


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

      def roleEntry = findRoleEntry(grantedRoles, ${roles.roleName});
      if (roleEntry != null) {
        // println "Found role " + ${roles.roleName}

        def sidList = roleEntry.getValue()
        if (sidList.contains(${roles.userName})) {
          println "User " + ${roles.userName} + " already assigned to role " + ${roles.roleName}
        } else {
          println "Adding user " + ${roles.userName} + " to role " + ${roles.roleName}
          roleAuthStrategy.assignRole(RoleType.fromString(RoleBasedAuthorizationStrategy.PROJECT), roleEntry.getKey(), ${roles.userName});
          println "OK"
        }

        Jenkins.instance.save()
      } else {
        println "Unable to find role " + ${roles.roleName}
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

      def roleEntry = findRoleEntry(grantedRoles, ${roles.globalroleName});
      if (roleEntry != null) {
         println "Found role " + ${roles.globalroleName}

        def sidList = roleEntry.getValue()
        if (sidList.contains(${roles.userName})) {
          println "User " + ${roles.userName} + " already assigned to Global role " + ${roles.globalroleName}
        } else {
          println "Adding user " + ${roles.userName} + " to Global role " + ${roles.globalroleName}
          roleAuthStrategy.assignRole(RoleType.fromString(RoleBasedAuthorizationStrategy.GLOBAL), roleEntry.getKey(), ${roles.userName});
          println "OK"
        }

        Jenkins.instance.save()
      } else {
        println "Unable to find Global role " + ${roles.globalroleName}
      }
    } else {
      println "Unable to find grantedRoles for " + RoleBasedAuthorizationStrategy.GLOBAL
    }
  } else {
    println "Role Strategy Plugin not found!"
  }
}