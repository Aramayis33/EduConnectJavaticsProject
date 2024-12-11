package org.example.educonnectjavaproject.controller;


//import org.example.educonnectjavaproject.model.interfaces.GroupRepository;
import jakarta.servlet.http.HttpSession;
import org.example.educonnectjavaproject.model.*;
import org.example.educonnectjavaproject.model.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class AppController {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HttpSession session;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private HomeWorkRepository homeWorkRepository;
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    FeedbackRepository feedbackRepository;


    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("error", "");

        return "index";
    }

    @PostMapping("/login")
    public String logIn(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model) {

        Teacher teacher = teacherRepository.findTeacherByUsername(username);
        Student student = studentRepository.findStudentByEmail(username);
        Admin admin = adminRepository.findByUsername(username);

        if (teacher != null && encoder.matches(password, teacher.getPassword())) {
            session.setAttribute("teacher", teacher);
            session.setAttribute("subject", teacher.getSubject());

            List<GroupInfo> groups = groupRepository.findAll();
            session.setAttribute("allGroups", groups);

            GroupInfo first = groupRepository.findFirstByOrderByGroupNumber();
            // List<Student> studentList = studentRepository.findStudentByGroupNumber(first.getGroupNumber());
            List<Student> studentList = studentRepository.findAllStudentsWithSubjects();
            session.setAttribute("students", studentList);
            //    session.setAttribute("subjects", subjectList);
            model.addAttribute("teacher", teacher);


            return "teacherPage";
        } else if (student != null && encoder.matches(password, student.getPassword())) {
            session.setAttribute("student", student);
            model.addAttribute("studentImage", student.getImage());
            session.setAttribute("studentImage", student.getImage());

            session.setAttribute("studentId", student.getId());
            Subject subject = subjectRepository.findByStudentId(student.getId());
            //  model.addAttribute("subject", subject);
            session.setAttribute("subject", subject);
            List<HomeWork> homeWorkList = homeWorkRepository.findHomeWorkByGroupNumber(student.getGroupNumber());
            //   model.addAttribute("homeWorks", homeWorkList);
            session.setAttribute("homeWorks", homeWorkList);


            return "studentPage";

        } else if (admin != null && encoder.matches(password, admin.getPassword())) {
            session.setAttribute("admin", admin);
            List<Student> allStudents = studentRepository.findAll();
            session.setAttribute("student", allStudents);
            List<Teacher> teacherList = teacherRepository.findAll();
            session.setAttribute("teacher", teacherList);
            model.addAttribute("students", allStudents);
            model.addAttribute("teachers", teacherList);
            return "adminPage";

        } else {
            model.addAttribute("error", "Username or password is incorrect");
            return "index";

        }

    }

    @GetMapping("/logout")
    public String logOut(Model model) {
        model.addAttribute("error", "");
        session.invalidate();
        return "index";
    }

    @GetMapping("/homework")
    public String homework(Model model) {
        model.addAttribute("subject", session.getAttribute("subject"));

        return "addHomeWork";
    }

    @PostMapping("/main")
    public String goBack(Model model) {

        return "teacherPage";
    }

    @PostMapping("/update")
    public String update(@RequestParam("studentId") int studentId,
                         @RequestParam("mathValue") String mathValue,
                         @RequestParam("englishValue") String englishValue,
                         @RequestParam("javaValue") String javaValue,
                         Model model) {

        //  Subject subject=subjectRepository.findByStudentId(studentId);
        Student student = studentRepository.findStudentById(studentId);

        String subjectName = (String) session.getAttribute("subject");
        //  System.out.println(subject.getMath()+" "+subject.getEnglish()+" "+subject.getJava());

        int mathGrade = Integer.parseInt(mathValue);
        student.getSubjects().get(0).setMath(mathGrade);
        int englishGrade = Integer.parseInt(englishValue);
        student.getSubjects().get(0).setEnglish(englishGrade);
        int javaGrade = Integer.parseInt(javaValue);
        student.getSubjects().get(0).setJava(javaGrade);

        studentRepository.save(student);
        List<Student> studentList = studentRepository.findAllStudentsWithSubjects();
        session.setAttribute("students", studentList);


        return "teacherPage";

    }

    @PostMapping("addHomework")
    public String homeWorkPage(@RequestParam("groupNumber") String groupNumber, @RequestParam("title") String title, Model model) {

        int group = Integer.parseInt(groupNumber);
        String subjectName = (String) session.getAttribute("subject");
        LocalDate inputDate = LocalDate.now();
        HomeWork homeWork = new HomeWork(group, title, java.sql.Date.valueOf(inputDate), subjectName);
        homeWorkRepository.save(homeWork);

        return "teacherPage";

    }

    @GetMapping("/groups")
    public String groups(@RequestParam("filterGroup") String groupNumber, Model model) {

        List<Student> filterStudents = studentRepository.findAllByGroupNumber(Integer.parseInt(groupNumber));
        session.setAttribute("students", filterStudents);
        return "teacherPage";


    }

    @GetMapping("/feedback")
    public String feedback(Model model) {
        return "feedback";
    }

    @PostMapping("/addFeedback")
    public String addFeedback(@RequestParam("title") String title, Model model) {

        int studentId = (int) session.getAttribute("studentId");
        LocalDate inputDate = LocalDate.now();
        Feedback feedback = new Feedback(studentId, java.sql.Date.valueOf(inputDate), title);
        feedbackRepository.save(feedback);


        return "studentPage";


    }

    @PostMapping("/studentMain")
    public String studentMain(Model model) {

        return "studentPage";

    }

    @PostMapping("/faq")
    public String faq(Model model) {
        return "faq";
    }

    @PostMapping("/deleteStudent")
    public String deleteStudent(@RequestParam("studentId") String id, Model model) {
        int studentId = Integer.parseInt(id);
        studentRepository.deleteById(studentId);
        List<Student> allStudents = studentRepository.findAll();
        session.setAttribute("student", allStudents);
        List<Teacher> teacherList = teacherRepository.findAll();
        session.setAttribute("teacher", teacherList);

        return "adminPage";


    }

    @PostMapping("/deleteTeacher")
    public String deleteTeacher(@RequestParam("teacherId") String id, Model model) {
        int teacherId = Integer.parseInt(id);
        teacherRepository.deleteById(teacherId);
        List<Student> allStudents = studentRepository.findAll();
        session.setAttribute("student", allStudents);
        List<Teacher> teacherList = teacherRepository.findAll();
        session.setAttribute("teacher", teacherList);

        return "adminPage";
    }

    @PostMapping("/addStudent")
    public String addStudentPage() {
        return "addStudent";
    }

    @PostMapping("/addTeacher")
    public String addTeacherPage() {
        return "addTeacher";
    }
    @PostMapping("/addStudentSubmit")
    public String addStudentSubmit(
            @RequestParam("stName") String name,
            @RequestParam("stSurname") String surname,
            @RequestParam("stEmail") String email,
            @RequestParam("stGroup") String group,
            @RequestParam("stFee") String fee,
            @RequestParam("stDate") String date,
            @RequestParam("stPassword") String password,
            @RequestParam("photo") MultipartFile photo,

            @RequestParam("stId") int studentId,
            Model model) {
        try {
            String fileName;
            if (!photo.isEmpty()) {
                String uploadDir = new File("src/main/resources/static/images").getAbsolutePath();
                String originalFileName = photo.getOriginalFilename();
                 fileName = System.currentTimeMillis() + "_" + originalFileName;
                File destinationFile = new File(uploadDir + File.separator + fileName);
                photo.transferTo(destinationFile);
            }
            else{
                String uploadDir = new File("src/main/resources/static/images").getAbsolutePath();
                String originalFileName = (String) session.getAttribute("studentOldImage");
                 fileName = System.currentTimeMillis() + "_" + originalFileName;
                File destinationFile = new File(uploadDir + File.separator + fileName);
                photo.transferTo(destinationFile);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(date, formatter);
            Date sqlDate = Date.valueOf(localDate);

            String hashedPassword=encoder.encode(password);

            Student student = new Student(name, surname, sqlDate, Integer.parseInt(fee), email, hashedPassword, Integer.parseInt(group), fileName, null);


            if(studentId!=0){
                studentRepository.deleteById(studentId);
            }
            studentRepository.save(student);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Student> allStudents = studentRepository.findAll();
        session.setAttribute("student", allStudents);
        List<Teacher> teacherList = teacherRepository.findAll();
        session.setAttribute("teacher", teacherList);
        return "adminPage";
    }
    @PostMapping("/addTeacherSubmit")
    public String addTeacherSubmit( @RequestParam("teName") String name,
 @RequestParam("teSurname") String surname,
@RequestParam("teUsername")String username,
  @RequestParam("teSubject")String subject,
@RequestParam("tePassword")String password, Model model) {

        String hashedPassword=encoder.encode(password);
        Teacher teacher=new Teacher(name,surname,username,subject,hashedPassword);
        teacherRepository.save(teacher);
        List<Student> allStudents = studentRepository.findAll();
        session.setAttribute("student", allStudents);
        List<Teacher> teacherList = teacherRepository.findAll();
        session.setAttribute("teacher", teacherList);
        return "adminPage";
    }

    @PostMapping("/updateStudent")
    public String updateStudent(@RequestParam("stId") String id,Model model) {
        int studentId = Integer.parseInt(id);
        Student student = studentRepository.findStudentById(studentId);
        model.addAttribute("studentUpdate", student);
        session.setAttribute("studentOldImage", student.getImage());
        return "updateStudent";
    }
}

