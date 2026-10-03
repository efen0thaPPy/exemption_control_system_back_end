package com.efeakif.intibak_control.service;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.jsoup.nodes.Element;

import com.efeakif.intibak_control.entity.Course;
import com.efeakif.intibak_control.entity.WeeklyContent;
import com.efeakif.intibak_control.enums.CourseType;
import com.efeakif.intibak_control.repository.CourseRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ErciyesScrapingService {

    private final CourseRepo courseRepo;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

    @Transactional
    public void scrapeAndSeedtheCirriculum(String connectionUrl, String academicYear) {

        try {

            Document doc = Jsoup.connect(connectionUrl).timeout(15000).userAgent(USER_AGENT).get();

            Elements trs = doc.select("table.my-table88 tr");

            for (Element tr : trs) {

                Elements tds = tr.select("td");

                String firstColumn = tds.get(0).text().trim();

                if (firstColumn.contains("Ders"))
                    continue;

                String courseCode = tds.get(0).text();

                List<Course> courses = courseRepo.findByAcademicYearAndCourseCode(academicYear, courseCode);

                if (!courses.isEmpty())
                    continue;

                Course course = new Course();

                course.setCourseCode(tds.get(0).text());

                course.setCourseName(tds.get(1).text());
                course.setCourseType(CourseType.fromString(tds.get(2).text()));

                String hours = tds.get(3).text().trim();
                String[] hourTypes = hours.split("\\+");
                course.setTheoryHours(Integer.parseInt(hourTypes[0]));
                course.setPracticeHours(Integer.parseInt(hourTypes[1]));

                course.setLocalCredit(Double.parseDouble(tds.get(4).text()));
                course.setAkts(Double.parseDouble(tds.get(5).text()));

                course.setAcademicYear(academicYear);

                Element a = tds.get(1).select("a").first();

                String url = a.absUrl("href");

                scrapeAndSeedWeeklyContents(url, course);

                courseRepo.save(course);

                Thread.sleep(300);

            }

        } catch (IOException exception) {
            System.err.println(exception.getMessage());

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            System.err.println("Scraping was interrupted: " + exception.getMessage());

        }

    }

    public void scrapeAndSeedWeeklyContents(String url, Course course) {
        try {

            Document doc = Jsoup.connect(url).timeout(15000).userAgent(USER_AGENT).get();
            Elements trs = doc.select("div#MainContent_Panel3 table.my-table88 tr");
            for (Element tr : trs) {

                Elements tds = tr.select("td");

                if (tds.size() != 2)
                    continue;

                String description = tds.get(1).text();

                if (description.isBlank())
                    continue;

                int weekNumber = Integer.parseInt(tds.get(0).text());

                WeeklyContent weeklyContent = new WeeklyContent();
                weeklyContent.setWeekNumber(weekNumber);
                weeklyContent.setDescription((description));
                weeklyContent.setCourse(course);
                course.getWeeklyContents().add(weeklyContent);
            }
        } catch (IOException ioException) {

        }

    }

}
