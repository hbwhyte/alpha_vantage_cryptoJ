package alpha_vantage.controllers;

import alpha_vantage.model.AppUser;
import alpha_vantage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * POST request that registers users to access the API, and
     * makes them eligible for their JWT token.
     *
     * Passwords stored using bCrypt.
     *
     * @param user appUser object that includes an email and
     *             password.
     */
    @PostMapping("registration")
    public void register(@RequestBody AppUser user) {
        user.setActive(1);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}