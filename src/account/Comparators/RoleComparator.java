package account.Comparators;

import account.Entities.Role;

import java.util.Comparator;

public class RoleComparator implements Comparator<Role> {
    //we can also implement this role class , but it ws working with tree set only when I pass new comparator ans also over ride there as well
        @Override
    public int compare(Role role1, Role role2) {
            System.out.println("comparator me aaya");
        return role1.getRole().compareTo(role2.getRole());

    }
}
