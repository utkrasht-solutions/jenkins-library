import jenkins.model.Jenkins

import hudson.security.PermissionGroup
import hudson.security.Permission

import com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy
import com.michelin.cio.hudson.plugins.rolestrategy.Role
import com.synopsys.arc.jenkins.plugins.rolestrategy.RoleType

import org.jenkinsci.plugins.rolestrategy.permissions.PermissionHelper

Jenkins jenkins = Jenkins.get()
def rbas = new RoleBasedAuthorizationStrategy()

/* create admin role */
Set<Permission> permissions = new HashSet<>();
def groups = new ArrayList<>(PermissionGroup.getAll());
groups.remove(PermissionGroup.get(Permission.class));
Role adminRole = new Role("admin",permissions)

/* assign admin role to admin user */
globalRoleMap = rbas.getRoleMaps()[RoleType.Global]
globalRoleMap.addRole(adminRole)
globalRoleMap.assignRole(adminRole, 'admin')

jenkins.setAuthorizationStrategy(rbas)

jenkins.save()