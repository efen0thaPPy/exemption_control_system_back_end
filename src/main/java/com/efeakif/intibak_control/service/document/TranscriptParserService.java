package com.efeakif.intibak_control.service.document;

import com.efeakif.intibak_control.dto.llm.TranscriptDto;
import com.efeakif.intibak_control.dto.llm.TranscriptDto.TranscriptCourses;
import com.efeakif.intibak_control.enums.CourseLanguage;
import com.efeakif.intibak_control.enums.Grade;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.*;

@Component
public class TranscriptParserService {

    private static final Pattern SEMESTER = Pattern.compile(
            "(\\d{4}-\\d{4}\\s+(?:Güz Dönemi|Bahar Dönemi|Yaz Okulu|Yaz Dönemi|Transfer Dönemi|[A-ZÇĞİÖŞÜa-zçğıöşü]+(?:\\s+(?:Dönemi|Yarıyılı|Okulu))?))",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern ROW_TAIL = Pattern.compile(
            "^(.*?)\\s*\\b([A-ZÇĞİÖŞÜ]{1,2})\\s+(Tr|İng\\.|\\S+\\.)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+(?:[.,]\\d+)?)\\s+(\\d+(?:[.,]\\d+)?)\\s+([A-Z]{1,2}|-)(?:\\s+.*)?$");
    private static final Pattern NUMBER = Pattern.compile("^\\d+[A-Z]?$");
    private static final Pattern WRAPPED_NUMBER = Pattern.compile("^(\\d+[A-Z]?)(?:\\s+\\(.*\\))?$");
    private static final Pattern COURSE_CODE_PATTERN = Pattern
            .compile("^\\*?([A-ZÇĞİÖŞÜ]{2,8}\\s*\\d{2,6}[A-Z]?(?:/\\d+)?)(?:\\s+(.*))?$");

    private static final Pattern UNIVERSITY_PATTERN = Pattern.compile(
            "^([A-ZÇĞİÖŞÜ\\s]+(?:ÜNİVERSİTESİ|UNIVERSITESI|ENSTİTÜSÜ))\\s*$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern SURNAME_PATTERN = Pattern.compile(
            "^(?:Soyadı|Soyad)(?:\\s*\\(.*?\\))?\\s*:\\s*(.+)$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern FIRSTNAME_PATTERN = Pattern.compile(
            "^(?:Adı|Ad)(?:\\s*\\(.*?\\))?\\s*:\\s*(.+)$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern TC_PATTERN = Pattern.compile("^\\d{11}$");
    private static final Pattern BIRTH_DATE_PATTERN = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");

    public TranscriptDto parseTranscript(String text) {
        TranscriptDto dto = new TranscriptDto();
        if (text == null || text.isBlank()) {
            dto.setTranscriptCourses(Collections.emptyList());
            return dto;
        }

        List<String> lines = text.lines().map(String::strip).filter(l -> !l.isEmpty()).toList();
        extractMetadata(lines, dto);
        dto.setTranscriptCourses(parseCourseRows(lines));
        return dto;
    }

    public List<TranscriptCourses> parseCourses(String text) {
        return parseTranscript(text).getTranscriptCourses();
    }

    private void extractMetadata(List<String> lines, TranscriptDto dto) {
        String university = null;
        String lastName = null;
        String firstName = null;

        for (String line : lines) {
            if (university == null) {
                Matcher mUni = UNIVERSITY_PATTERN.matcher(line);
                if (mUni.matches()) {
                    university = mUni.group(1).trim();
                }
            }

            if (lastName == null) {
                Matcher mSur = SURNAME_PATTERN.matcher(line);
                if (mSur.matches()) {
                    lastName = mSur.group(1).trim();
                }
            }

            if (firstName == null) {
                Matcher mFirst = FIRSTNAME_PATTERN.matcher(line);
                if (mFirst.matches()) {
                    firstName = mFirst.group(1).trim();
                }
            }
        }

        // If firstName was not on the same line (PDFBox multi-column extraction),
        // look for the name line positioned between the 11-digit T.C. No and dd/MM/yyyy birth date
        if (firstName == null) {
            for (int i = 0; i < lines.size() - 2; i++) {
                if (TC_PATTERN.matcher(lines.get(i)).matches()
                        && BIRTH_DATE_PATTERN.matcher(lines.get(i + 2)).matches()) {
                    firstName = lines.get(i + 1).trim();
                    break;
                }
            }
        }

        dto.setUniversity(university);
        dto.setLastName(lastName);
        dto.setFirstName(firstName);
    }

    private List<TranscriptCourses> parseCourseRows(List<String> lines) {
        List<TranscriptCourses> courses = new ArrayList<>();
        List<String> buf = new ArrayList<>();
        String currentSemester = null;
        TranscriptCourses pendingNoNumber = null;

        for (String line : lines) {
            if (!line.startsWith("(")) {
                Matcher sm = SEMESTER.matcher(line);
                if (sm.find()) {
                    currentSemester = sm.group(1).trim();
                    buf.clear();
                    continue;
                }
            }

            Matcher wm = WRAPPED_NUMBER.matcher(line);
            if (pendingNoNumber != null && wm.matches()) {
                pendingNoNumber.setCourseCode(pendingNoNumber.getCourseCode() + " " + wm.group(1));
                pendingNoNumber = null;
                buf.clear();
                continue;
            }

            Matcher rm = ROW_TAIL.matcher(line);
            if (rm.matches()) {
                String head = rm.group(1).strip();
                String langStr = rm.group(3);
                double ects = Double.parseDouble(rm.group(7).replace(',', '.'));
                String gradeStr = rm.group(8);

                TranscriptCourses course = new TranscriptCourses();
                course.setSemester(currentSemester != null ? currentSemester : "");
                course.setCourseLanguage(CourseLanguage.fromString(langStr));
                course.setEcts(ects);
                course.setGrade(Grade.mapGrades(gradeStr));

                // If course code & title were inline on the same row as ROW_TAIL
                if (!head.isEmpty()) {
                    String[] parts = head.split("\\s+", 2);
                    String code = parts[0].replaceFirst("^\\*", "");
                    String name = parts.length > 1 ? parts[1].strip() : "";

                    course.setCourseCode(code);
                    course.setCourseName(name);
                    courses.add(course);

                    if (code.contains("_") || !code.matches(".*\\d{3,}[A-Z]?$")) {
                        pendingNoNumber = course;
                    } else {
                        pendingNoNumber = null;
                    }
                    buf.clear();
                    continue;
                }

                // Otherwise, the course details were buffered across previous lines
                // 1. Check for split course code (prefix on line i, bare number on line i+1)
                int splitStart = -1;
                for (int i = 0; i < buf.size() - 1; i++) {
                    if (!buf.get(i).contains(" ") && NUMBER.matcher(buf.get(i + 1)).matches()) {
                        splitStart = i;
                    }
                }

                if (splitStart >= 0) {
                    course.setCourseCode(buf.get(splitStart).replaceFirst("^\\*", "") + " " + buf.get(splitStart + 1));
                    StringBuilder sb = new StringBuilder();
                    for (int i = splitStart + 2; i < buf.size() && !buf.get(i).startsWith("("); i++) {
                        sb.append(sb.isEmpty() ? "" : " ").append(buf.get(i));
                    }
                    course.setCourseName(sb.toString());
                    courses.add(course);
                    pendingNoNumber = null;
                } else {
                    // 2. Check for standard course code line (e.g. "BMI1121 Linear Algebra")
                    int stdStart = -1;
                    Matcher stdMatch = null;
                    for (int i = 0; i < buf.size(); i++) {
                        Matcher m = COURSE_CODE_PATTERN.matcher(buf.get(i));
                        if (m.matches()) {
                            stdStart = i;
                            stdMatch = m;
                        }
                    }

                    if (stdStart >= 0 && stdMatch != null) {
                        String code = stdMatch.group(1).strip().replaceFirst("^\\*", "");
                        String remainder = stdMatch.group(2) != null ? stdMatch.group(2).strip() : "";

                        StringBuilder sb = new StringBuilder();
                        if (!remainder.isEmpty() && !remainder.startsWith("(")) {
                            sb.append(remainder);
                        }
                        for (int i = stdStart + 1; i < buf.size() && !buf.get(i).startsWith("("); i++) {
                            sb.append(sb.isEmpty() ? "" : " ").append(buf.get(i));
                        }

                        course.setCourseCode(code);
                        course.setCourseName(sb.toString());
                        courses.add(course);
                        pendingNoNumber = null;
                    }
                }

                buf.clear();
                continue;
            }

            buf.add(line);
        }

        return courses;
    }
}
