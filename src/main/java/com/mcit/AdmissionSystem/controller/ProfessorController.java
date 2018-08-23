package com.mcit.AdmissionSystem.controller;

import com.mcit.AdmissionSystem.model.*;
import com.mcit.AdmissionSystem.service.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.*;

@Controller
public class ProfessorController {

    private static final Logger log = LoggerFactory.getLogger(ProfessorController.class);

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MailService mailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CourseService courseService;

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${as.mcit.url}")
    private String url;

    @GetMapping("/professor")
    @ResponseBody
    public ModelAndView professor( ModelAndView modelAndView ) {
        log.info("/professor called");

        modelAndView = new ModelAndView("professor");

        try {

            List<Professor> professorList = professorService.findAll();
            modelAndView.addObject("professors", professorList);
        } catch (Exception e) {
            log.error("Error retrieving professors",e);
            modelAndView.addObject("error", "Error retrieving professors");
        }

        return modelAndView;
    }

    @GetMapping("/professor/dashboard")
    @ResponseBody
    public ModelAndView professorDashBoard( ModelAndView modelAndView, Principal principal ) {
        log.info("/professor/dashboard called");

        modelAndView = new ModelAndView("professor-dashboard");

        try {

            List<Course> courses = courseService.findAllByProfessor(principal.getName());
            modelAndView.addObject("courses", courses);
        } catch (Exception e) {
            log.error("Error retrieving professor's courses",e);
            modelAndView.addObject("error", "Error retrieving professor's courses");
        }

        return modelAndView;
    }

    @PostMapping("/professor/new")
    @ResponseBody
    public ModelAndView newProfessor(@ModelAttribute Professor professor) {

        ModelAndView modelAndView = new ModelAndView("professor");

        try {

            User user = userService.findByUserName(professor.getUser().getUserName());

            if (user != null) {
                log.error("Could not add professor " + professor.getFirstName() + " "
                        + professor.getLastName() + ". Username already in use: "
                        + professor.getUser().getUserName());
                modelAndView.addObject("error", "Error adding professor - Username already in use");
            } else {
                String password = RandomStringUtils.random(10, true, true);
                String passwordEncrypted = passwordEncoder.encode(password);
                Role adminRole = roleService.findByCode("ROLE_PROFESSOR");
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                user = new User(professor.getUser().getUserName(), passwordEncrypted, professor.getUser().getEmail(), roles);
                userService.add(user);
                professor.setUser(user);
                professorService.add(professor);
                Map<String, Object> params = new HashMap<>();
                params.put("name", professor.getFirstName() + " " + professor.getLastName());
                params.put("username", professor.getUser().getUserName());
                params.put("password", password);
                params.put("url", url);
                params.put("type", "professor");

                mailService.send(mailFrom, user.getEmail(), "Welcome to MCIT professor", "new_account.html", params);

                modelAndView.addObject("message", "Professor successfully added");
            }
        } catch (Exception e) {
            log.error("Could not add professor " + professor.getFirstName() + " " + professor.getLastName(), e);
            modelAndView.addObject("error", "Error adding professor");
       }


        return professor(modelAndView);
    }

    @PostMapping("/professor/edit")
    @ResponseBody
    public ModelAndView editProfessor(@ModelAttribute Professor professor) {

        ModelAndView modelAndView = new ModelAndView("professor");

        Professor professor_ = professorService.findById(professor.getId());

        if (professor_ != null) {
            try {
                professor.setUser(userService.findByUserName(professor.getUser().getUserName()));
                professorService.update(professor);
                modelAndView.addObject("message", "Professor successfully updated");
            } catch (Exception e) {
                log.error("Could not update professor " +  professor.getFirstName() + " " + professor.getLastName(), e);
                modelAndView.addObject("error", "Error updating professor");
            }
        } else
            modelAndView.addObject("error", "Professor does not exist");


        return professor(modelAndView);
    }

    @PostMapping("/professor/delete")
    @ResponseBody
    public ModelAndView deleteProfessor(@ModelAttribute Professor professor) {

        ModelAndView modelAndView = new ModelAndView("professor");

        Professor professor_ = professorService.findOneWithUserAndRoles(professor.getId());

        if (professor_ != null) {

            if(professor_.getCourses() != null && professor_.getCourses().size() > 0) {
                log.error("Could not delete professor " + professor.getFirstName() + " " + professor.getLastName() + " professor is registered on course(s)");
                modelAndView.addObject("error", "Professor cannot be deleted because this professor is linked to at least one course");
            } else {
                try {
                    professorService.delete(professor_);
                    modelAndView.addObject("message", "Professor successfully deleted");
                } catch (Exception e) {
                    log.error("Could not delete professor " + professor.getFirstName() + " " + professor.getLastName(), e);
                    modelAndView.addObject("error", "Error deleting professor");
                }
            }
        } else
            modelAndView.addObject("error", "Professor does not exist");


        return professor(modelAndView);
    }

    @PostMapping("/professor/reset")
    @ResponseBody
    public ModelAndView resetPassword(@ModelAttribute Professor professor) {

        ModelAndView modelAndView = new ModelAndView("professor");
        Professor professor_ = null;
        try {

            professor_ = professorService.findById(professor.getId());

            if (professor_ == null) {
                log.error("Could not reset professor's password. Invalid Professor Id"
                        + professor.getId());
                modelAndView.addObject("error", "Error resetting professor's password - Invalid Professor ID");
            } else {
                String password = RandomStringUtils.random(10, true, true);
                String passwordEncrypted = passwordEncoder.encode(password);
                professor_.getUser().setPassword(passwordEncrypted);
                userService.update(professor_.getUser());

                Map<String, Object> params = new HashMap<>();
                params.put("name", professor_.getFirstName() + " " + professor_.getLastName());
                params.put("username", professor_.getUser().getUserName());
                params.put("password", password);
                params.put("url", url);
                params.put("type", "professor");

                mailService.send(mailFrom, professor_.getUser().getEmail(), "MCIT professor's password reset", "password_reset.html", params);

                modelAndView.addObject("message", "Password successfully reset");
            }
        } catch (Exception e) {
            log.error("Could not reset professor's password " + professor.getId(), e);
            modelAndView.addObject("error", "Error resetting professor's password");
        }


        return professor(modelAndView);
    }

}
