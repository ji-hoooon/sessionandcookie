package shop.mtcoding.hiberpc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.hiberpc.dto.UserRequest;
import shop.mtcoding.hiberpc.handler.ex.MyException;
import shop.mtcoding.hiberpc.model.User;
import shop.mtcoding.hiberpc.model.UserRepository;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

/**
 * DI는 생성자 주입(IoC), 메서드 주입(DispatcherServlet)
 */
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserRepository userRepository;
    private final HttpSession session;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserRequest.JoinDto joinDto) {
//        User user =joinDto.toEntity(); // 비영속
//        User userPS = userRepository.save(user); // 비영속 -> 영속화(insert)
        User userPS = userRepository.save(joinDto.toEntity()); // 비영속 -> 영속화(insert)
        return new ResponseEntity<>(userPS, HttpStatus.CREATED); // 영속화된 객체를 MessageConverter가 JSON으로 직렬화 (Getter)
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest.LoginDto loginDto) {

//        Optional<User> userOP = userRepository.findByUsername(loginDto.getUsername(), loginDto.getPassword());
//        //Optional 객체로, userOP는 유저객체가 아님
//        if (userOP.isPresent()) {
//            //null이 아니면 즉, userOP에 값이 있으면
//            session.setAttribute("loginUser", userOP.get());
//            return new ResponseEntity<>(HttpStatus.OK);
//        } else {
//            throw new MyException("아이디 혹은 패스워드가 틀렸습니다.");
//        }
//    }
        User userPS = userRepository.findByUsername(loginDto.getUsername(), loginDto.getPassword()).orElseThrow(
                ()-> new MyException("아이디 혹은 비밀번호가 틀렸습니다")
                //Supplier를 상한 와일드카드 로 하므로, 매개변수를 전달하지 않는다.
        );
        session.setAttribute("loginUser", userPS);
        return new ResponseEntity<>(HttpStatus.OK);
}

    // 인증 체크
    @GetMapping("/users")
    public ResponseEntity<?> userList() {

//        User loginUser = (User) session.getAttribute("loginUser");
//        if(loginUser == null){
//            throw new MyException("인증이 필요합니다", HttpStatus.UNAUTHORIZED); // 401
//        }

        List<User> userListPS = userRepository.findAll();
        return new ResponseEntity<>(userListPS, HttpStatus.OK);
    }

    //주소로 전달되는 데이터는 조작이 가능한 신뢰할 수 없는 데이터라는 것을 명심해야한다.
    //-> 검증 필수
    // 인증과 권한 - 세션 찾기
    @GetMapping("/users/{id}/v1")
    public ResponseEntity<?> userDetailV1(@PathVariable Integer id) {
        User loginUser = (User) session.getAttribute("loginUser");
//        if (!loginUser.getId().equals(id)) {
//            throw new MyException("고객 정보를 볼 수 있는 권한이 없습니다");
//        }
        if (!loginUser.getId().equals(id)) {
            throw new MyException("고객 정보를 볼 수 있는 권한이 없습니다", HttpStatus.FORBIDDEN);  //권한이 없어서 접근 불가능한 403예외
        }
        User userPS = userRepository.findById(id).orElseThrow(
                () -> new MyException("해당 유저를 찾을 수 없습니다")
        );
        return new ResponseEntity<>(userPS, HttpStatus.OK);
    }
//    @PostMapping("/products/{id}/cart")
//    public ResponseEntity<?> addCartV2(HttpServletRequest request, HttpServletResponse response) {
////        response.setHeader("Set-cookie", "name=asdf");
////        Cookie cookie=new Cookie("uuuuu", "cos");
////        response.addCookie(cookie);
//
//        Cookie cookie=new Cookie("myId", "asdf");
//        response.addCookie(cookie);
////        response.setHeader("Set-Cookie", "username=asdf; httpOnly=true");
//        cookie.setHttpOnly(true);
//
//        return new ResponseEntity<>("쿠키등록됨", HttpStatus.OK);
//    }

    @PostMapping("/products/{id}/cart")
    public ResponseEntity<?> addCart(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response) {
        String productNames = "";
        Cookie[] cookies = request.getCookies();

        // 장바구니가 존재할 때
        boolean checkCookie = false;
        for (Cookie c : cookies) {
            if (c.getName().equals("cart")) {
                productNames += c.getValue() + "/" + id.toString();
                checkCookie = true;
            }
        }

        // 장바구니가 존재하지 않을 때
        if (checkCookie == false) {
            productNames = id.toString();
        }

        Cookie cookie = new Cookie("cart", productNames);
        cookie.setPath("/");
        cookie.setMaxAge(1000 * 60 * 60); // 1시간
        cookie.setHttpOnly(false); // document.cookie
        response.addCookie(cookie);
        return new ResponseEntity<>("쿠키등록됨", HttpStatus.OK);
    }
}
