package com.efeakif.intibak_control.service.seeding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.ST;
import org.jsoup.nodes.Element;

import com.efeakif.intibak_control.entity.ErciyesCourse;
import com.efeakif.intibak_control.entity.ErciyesWeeklyContent;

import com.efeakif.intibak_control.enums.CourseType;
import com.efeakif.intibak_control.repository.ErciyesCourseRepo;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ErciyesScrapingSeedingService {

    private final ErciyesCourseRepo courseRepo;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

    @Value("${erciyesUrl}")
    private String connectionUrl;

    @Transactional
    public void scrapeAndSeedtheCirriculum(String academicYear) {

        try {

            int count = 0;

            List<String> semesterNames = new ArrayList<>();

            Document doc = Jsoup.connect(connectionUrl).timeout(30000).userAgent(USER_AGENT).get();

            Elements tables = doc.select("table.my-table88");

            Elements semesters = doc.select(".container-fluid");

            for (Element sm : semesters) {

                String[] parts = sm.text().split(" ");

                semesterNames.add(parts[0].replace(".", "").trim());

            }

            for (Element table : tables) {

                Elements trs = table.select("tbody tr");

                for (Element tr : trs) {

                    Elements tds = tr.select("td");

                    String firstColumn = tds.get(0).text().trim();

                    if (firstColumn.contains("Ders"))
                        continue;

                    String courseCode = tds.get(0).text();

                    ErciyesCourse checkedCourse = courseRepo.findByAcademicYearAndCourseCode(academicYear, courseCode);

                    if (checkedCourse != null)
                        continue;

                    ErciyesCourse course = new ErciyesCourse();

                    System.out.println(courseCode);

                    course.setCourseCode(tds.get(0).text());

                    course.setCourseName(tds.get(1).text());
                    course.setCourseType(CourseType.fromWeb(tds.get(0).text()));

                    String hours = tds.get(3).text().trim();
                    String[] hourTypes = hours.split("\\+");
                    course.setTheoryHours(Integer.parseInt(hourTypes[0]));
                    course.setPracticeHours(Integer.parseInt(hourTypes[1]));

                    course.setLocalCredit(Double.parseDouble(tds.get(4).text()));
                    course.setEcts(Double.parseDouble(tds.get(5).text()));

                    course.setAcademicYear(academicYear);

                    course.setSemester(
                            Integer.parseInt(semesterNames.get(count)));

                    Element a = tds.get(1).select("a").first();

                    if (a == null || course.getCourseType() == CourseType.COMMON_MANDATORY) {
                        courseRepo.save(course);
                        continue;
                    }

                    String url = a.absUrl("href");

                    scrapeAndSeedWeeklyContents(url, course);

                    courseRepo.save(course);

                    Thread.sleep(300);

                }

                count++;

            }

        } catch (IOException exception) {
            System.err.println(exception.getMessage());

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            System.err.println("Scraping was interrupted: " + exception.getMessage());

        }

    }

    public void scrapeAndSeedWeeklyContents(String url, ErciyesCourse course) {
        try {

            Document doc = Jsoup.connect(url).timeout(15000).userAgent(USER_AGENT).get();
            Elements trs = doc.select("div#MainContent_Panel3 table.my-table88 tr");
            for (Element tr : trs) {

                Elements tds = tr.select("td");

                if (tds.size() != 2)
                    continue;

                String description = tds.get(1).text();

                if (description.isBlank() || description.length() < 3)
                    continue;

                int weekNumber = Integer.parseInt(tds.get(0).text());

                ErciyesWeeklyContent weeklyContent = new ErciyesWeeklyContent();
                weeklyContent.setWeekNumber(weekNumber);
                weeklyContent.setDescription((description));
                weeklyContent.setCourse(course);
                course.getWeeklyContents().add(weeklyContent);
            }
        } catch (IOException ioException) {

        }

    }

}
