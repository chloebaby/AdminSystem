package com.mcit.AdmissionSystem.model;

import javax.persistence.*;

@Entity
@Table(name="AS_COURSE_STUDENT")
@AssociationOverrides({
        @AssociationOverride(name = "csKey.course",  joinColumns = @JoinColumn(name = "course_id")),
        @AssociationOverride(name = "csKey.student", joinColumns = @JoinColumn(name = "student_id")),
})
public class CS {

    @EmbeddedId
    private CSKey csKey;

    @Column(name = "mark")
    private Double mark;

    public Double getMark() {
        return mark;
    }

    public void setMark(Double mark) {
        this.mark = mark;
    }

    public CSKey getCsKey() {
        return csKey;
    }

    public void setCsKey(CSKey csKey) {
        this.csKey = csKey;
    }

    @Transient
    public Course getCourse() {
        return csKey.getCourse();
    }

    @Transient
    public Student getStudent() {
        return csKey.getStudent();
    }

}
