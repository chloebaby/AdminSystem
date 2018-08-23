package com.mcit.AdmissionSystem.controller;

import com.mcit.AdmissionSystem.model.Course;
import com.mcit.AdmissionSystem.model.Professor;
import com.mcit.AdmissionSystem.service.CourseService;
import com.mcit.AdmissionSystem.service.ProfessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class CourseController {

    private static final Logger log = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseService courseService;

    @Autowired
    private ProfessorService professorService;

    @GetMapping("/course")
    @ResponseBody
    public ModelAndView course( ModelAndView modelAndView ) {
        log.info("/course called");

        if (modelAndView==null)
            modelAndView = new ModelAndView("course");

        try {

            List<Course> courseList = courseService.findAll();
            List<Professor> professorList = professorService.findAll();
            modelAndView.addObject("courses", courseList);
            modelAndView.addObject("professors", professorList);
        } catch (Exception e) {
            log.error("Error retrieving courses",e);
            modelAndView.addObject("error", "Error retrieving courses");
        }

        return modelAndView;
    }

    @PostMapping("/course/new")
    @ResponseBody
    public ModelAndView newCourse(@ModelAttribute Course course) {

        ModelAndView modelAndView = new ModelAndView("course");

        if (course != null && course.getName() != null) {

            Course course_ = courseService.findByName(course.getName());

            if (course_ == null) {
                try {
                    courseService.add(course);
                    modelAndView.addObject("message", "Course successfully added");
                } catch (Exception e) {
                    log.error("Could not add course " + course.getName(), e);
                    modelAndView.addObject("error", "Error adding course");
                }
            } else
                modelAndView.addObject("error", "Course already exists");
        } else
            modelAndView.addObject("error", "Invalid course name");

        return course(modelAndView);
    }

    @PostMapping("/course/edit")
    @ResponseBody
    public ModelAndView editCourse(@ModelAttribute Course course) {

        ModelAndView modelAndView = new ModelAndView("course");

        if (course != null && course.getName() != null) {

            Course course_ = courseService.findById(course.getId());

            if (course_ != null) {
                try {
                    course_ = courseService.findByName(course.getName());
                    if (course_ == null || course.getId() == course_.getId()) {
                        Professor professor = professorService.findById(course.getProfessor().getId());
                        course.setProfessor(professor);
                        courseService.update(course);
                        modelAndView.addObject("message", "Course successfully updated");
                    } else {
                        modelAndView.addObject("error", "Course already exists");
                    }

                } catch (Exception e) {
                    log.error("Could not update course " + course.getName(), e);
                    modelAndView.addObject("error", "Error updating course");
                }
            } else
                modelAndView.addObject("error", "Course does not exist");
        } else
            modelAndView.addObject("error", "Invalid course name");

        return course(modelAndView);
    }

    @PostMapping("/course/delete")
    @ResponseBody
    public ModelAndView deleteCourse(@ModelAttribute Course course) {

        ModelAndView modelAndView = new ModelAndView("course");

        if (course != null && course.getId() != null) {

            Course course_ = courseService.findById(course.getId());

            if (course_ != null) {
                try {
                    courseService.deleteById(course.getId());
                    modelAndView.addObject("message", "Course successfully deleted");
                } catch (Exception e) {
                    log.error("Could not delete course " + course_.getName(), e);
                    modelAndView.addObject("error", "Error adding course");
                }
            } else
                modelAndView.addObject("error", "Course does not exist");
        } else
            modelAndView.addObject("error", "Invalid course");

        return course(modelAndView);
    }

}
