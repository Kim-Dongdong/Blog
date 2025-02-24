package blogstudy.blog.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ExampleController {

    @GetMapping("/thymeleaf/example")
    public String thymeleafExample(Model model) {
        Person exPerson = new Person();
        exPerson.setId(1L);
        exPerson.setName("홍길동");
        exPerson.setAge(24);
        exPerson.setHobbies(List.of("운동", "독서"));

        model.addAttribute("person", exPerson);
        model.addAttribute("today", LocalDate.now());

        return "example";
    }

    @Getter
    @Setter
    class Person {
        private long id;
        private String name;
        private int age;
        private List<String> hobbies;
    }
}
