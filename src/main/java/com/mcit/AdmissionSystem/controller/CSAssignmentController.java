package com.mcit.AdmissionSystem.controller;

import com.mcit.AdmissionSystem.model.CS;
import com.mcit.AdmissionSystem.model.CSKey;
import com.mcit.AdmissionSystem.model.Course;
import com.mcit.AdmissionSystem.model.Student;
import com.mcit.AdmissionSystem.service.CSService;
import com.mcit.AdmissionSystem.service.CourseService;
import com.mcit.AdmissionSystem.service.StudentService;
import com.mcit.AdmissionSystem.utils.PdfGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CSAssignmentController {

    private static final Logger log = LoggerFactory.getLogger(CSAssignmentController.class);

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private PdfGenerator pdfGenerator;

    @Autowired
    private CSService csService;


    @GetMapping("/cs-assignment")
    @ResponseBody
    public ModelAndView index( ModelAndView modelAndView ) {
        log.info("/cs-assignment called");

        if (modelAndView==null)
            modelAndView = new ModelAndView("cs-assignment");

        try {
            List<Course> courses = courseService.findAll();
            List<Student> students = studentService.findAll();

            modelAndView.addObject("courses", courses);
            modelAndView.addObject("allStudents", students);
        } catch (Exception e) {
            log.error("Error retrieving courses",e);
            modelAndView.addObject("error", "Error retrieving courses");
        }

        return modelAndView;
    }

    @GetMapping("/cs-assignment/{id}")
    @ResponseBody
    public ModelAndView findByCourse(@PathVariable("id") Long id ) {
        log.info("/cs-assignment/ {"+id +"}called");

        ModelAndView modelAndView = new ModelAndView("cs-assignment-student");
        try {
            Course course = courseService.findById(id);
            List<Student> students = studentService.findAll();
            students.removeIf(s -> course.getCourseStudents().stream().filter(cs -> cs.getStudent().getId() == s.getId()).count() > 0);

            modelAndView.addObject("course", course);
            modelAndView.addObject("allStudents", students);

        } catch (Exception e) {
            log.error("Error retrieving courses",e);
            modelAndView.addObject("error", "Error retrieving courses");
        }

        return modelAndView;
    }

    @PostMapping("/cs-assignment/remove")
    @ResponseBody
    public ModelAndView unassign(@ModelAttribute CSKey courseStudent) {

        ModelAndView modelAndView = new ModelAndView("cs-assignment-student");

        try {
            CS cs = csService.findById(courseStudent);
            csService.delete(cs);

            Course course = courseService.findById(courseStudent.getCourse().getId());
            List<Student> students = studentService.findAll();
            students.removeIf(s -> course.getCourseStudents().stream().filter(cs_ -> cs_.getStudent().getId() == s.getId()).count() > 0);

            modelAndView.addObject("course", course);
            modelAndView.addObject("allStudents", students);
            modelAndView.addObject("success", "Student successfully removed from course");
        } catch (Exception e) {
            log.error("Error retrieving courses",e);
            modelAndView.addObject("error", "Error retrieving courses");
        }

        return modelAndView;
    }

    @PostMapping("/cs-assignment/mark")
    @ResponseBody
    public ModelAndView unassign(@ModelAttribute CS courseStudent) {

        ModelAndView modelAndView = new ModelAndView("cs-assignment-student");

        try {
            Student student = studentService.findById(courseStudent.getStudent().getId());
            student.getStudentCourses().stream().forEach(sc -> {
                if (sc.getStudent().getId()== courseStudent.getStudent().getId() &&
                        sc.getCourse().getId() == courseStudent.getCourse().getId()) {
                    sc.setMark(courseStudent.getMark());
                }
            });
            studentService.update(student);

            modelAndView = findByCourse(courseStudent.getCourse().getId());
            modelAndView.addObject("success", "Mark successfully set to student");
        } catch (Exception e) {
            log.error("Error setting mark to student",e);
            modelAndView.addObject("error", "Error setting mark to student");
        }

        return modelAndView;
    }

    @PostMapping("/cs-assignment/add")
    @ResponseBody
    public ModelAndView assign(@ModelAttribute CSKey courseStudent) {

        ModelAndView modelAndView = new ModelAndView("cs-assignment-student");

        try {
            Course course = courseService.findById(courseStudent.getCourse().getId());
            course.getCourseStudents().removeIf(cs -> cs.getStudent().getId() == courseStudent.getStudent().getId());
            Student student = studentService.findById(courseStudent.getStudent().getId());
            CS cs = new CS();
            CSKey csKey = new CSKey(course, student);
            cs.setCsKey(csKey);
            course.getCourseStudents().add(cs);
            courseService.update(course);

            modelAndView = findByCourse(course.getId());
            modelAndView.addObject("success", "Student successfully added to course");
        } catch (Exception e) {
            log.error("Error adding student to course",e);
            modelAndView.addObject("error", "Error adding student to course");
        }

        return modelAndView;
    }

    @GetMapping("/cs-assignment/students-by-course/{id}")
    @ResponseBody
    public ModelAndView listStudentByCourse(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("course-assignment :: course-assignment-modal");

        List<Course> courses = courseService.findAll().stream().filter(c -> c.getId() == id).collect(Collectors.toList());
        List<CS> courseStudents = courses.get(0).getCourseStudents();
        modelAndView.addObject("courseStudents", courseStudents);
        return modelAndView;
    }

    @RequestMapping("/cs-assignment/pdf")
    @ResponseBody
    public ResponseEntity<byte[]> generatePdf(@RequestParam Long id) {

        try {

            Course course = courseService.findById(id);

            String pathToDocument = pdfGenerator.generateCoursePdf(course);
            if(pathToDocument == null){
                return null;
            }

            Path pdfPath = Paths.get(pathToDocument);
            byte[] pdf = Files.readAllBytes(pdfPath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
            String filename = "course_report.pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(pdf, headers, HttpStatus.OK);
            return response;
        } catch (Exception e) {
            log.error("Error generating Course Report" , e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
