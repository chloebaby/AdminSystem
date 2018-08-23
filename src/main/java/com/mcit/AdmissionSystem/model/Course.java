package com.mcit.AdmissionSystem.model;


import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="as_course")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    //@SequenceGenerator(name="SEQ_GEN", sequenceName="SEQ_AS_COURSE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name")
    private String name;

    @ManyToOne
    @JoinTable(name = "as_course_professor",
            joinColumns = @JoinColumn(name="course_id", referencedColumnName="id"),
            inverseJoinColumns = @JoinColumn(name="professor_id", referencedColumnName="id"))
    private Professor professor;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "csKey.course", cascade = CascadeType.ALL)
    private List<CS> courseStudents;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public List<CS> getCourseStudents() {
        return courseStudents;
    }

    public void setCourseStudents(List<CS> courseStudents) {
        this.courseStudents = courseStudents;
    }
}
