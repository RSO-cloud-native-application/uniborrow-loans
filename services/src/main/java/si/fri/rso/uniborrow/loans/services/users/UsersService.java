package si.fri.rso.uniborrow.loans.services.users;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UsersService {
    public boolean checkUserExists(String userId) {
        System.out.println("We got a call bby");
        return true;
    }
}
