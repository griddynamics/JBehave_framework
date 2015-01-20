package com.griddynamics.common.controller;

import com.griddynamics.testresult.dao.TestAreaDAO;
import com.griddynamics.testresult.dao.TestResultDAO;
import com.griddynamics.testresult.model.ImageSelection;
import com.griddynamics.testresult.model.TestArea;
import com.griddynamics.testresult.model.TestResult;
import com.griddynamics.testresult.model.UploadedFile;
import com.griddynamics.testresult.validator.FileValidator;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.*;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

import static com.griddynamics.testresult.model.TestArea.getTestAreaProcessor;
import static com.griddynamics.testresult.model.TestResult.getTestResultProcessor;
import static com.griddynamics.utils.CropImageUtil.cropImage;

@Controller
public class WebController {

    @Autowired
    FileValidator fileValidator;


    @Value("${baseline.image.path}")
    private String baselineImagePath;

    @Value("${result.image.path}")
    private String resultImagePath;

    @Value("${full.image.name}")
    private String fullImageName;

    @Value("${cropped.image.name}")
    private String croppedImageName;

    @Value("${comparison.image.name}")
    private String comparisonImageName;

    @Value("${test.areas.image.path}")
    private String testAreasImagesPath;


    @RequestMapping(value = "/testResult", method = RequestMethod.GET, params = {"getId"})
    public ModelAndView showTestResult(@RequestParam int getId, ModelMap model) {
        ApplicationContext context =
                new ClassPathXmlApplicationContext("Spring-Module.xml");

        TestResultDAO testResultDAO = (TestResultDAO) context.getBean("testResultDAO");

        TestResult testResult2 = testResultDAO.getTestResult(getId);
        System.out.println(testResult2);

        ModelAndView mv = new ModelAndView();

        mv.addObject("testResult", testResult2);
        mv.addObject("testResultImagesFolder", testResult2.getResultFolderName());
        mv.addObject(testResultDAO.getTestResultsOnePerSession());

        return mv;

    }

    @RequestMapping(value = "/testSessions", method = RequestMethod.GET)
    public ModelAndView showTestSessions() {
        ApplicationContext context =
                new ClassPathXmlApplicationContext("Spring-Module.xml");
        TestResultDAO testResultDAO = (TestResultDAO) context.getBean("testResultDAO");

        return new ModelAndView().addObject("testResultList", testResultDAO.getTestResultsOnePerSession());
    }


    @RequestMapping(value = "/testCases", method = RequestMethod.GET, params = {"sessionId"})
    public ModelAndView showTestCases(@RequestParam String sessionId) {
        ApplicationContext context =
                new ClassPathXmlApplicationContext("Spring-Module.xml");
        TestResultDAO testResultDAO = (TestResultDAO) context.getBean("testResultDAO");

        return new ModelAndView().addObject("testResultList", testResultDAO.getSessionResults(sessionId));
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() throws SQLException {
        return "home";
    }

    @RequestMapping(value = "/testResult", method = RequestMethod.POST, params = {"acceptNewBaseline", "getId"})
    public String acceptNewBaseline(@RequestParam int getId) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
        TestResultDAO testResultDAO = (TestResultDAO) context.getBean("testResultDAO");
        TestResult testResult = testResultDAO.getTestResult(getId);

        File baselineImg = new File(baselineImagePath + testResult.getTestArea().getFileName());
        baselineImg.delete();

        File newResult = new File(resultImagePath + testResult.getResultFolderName() + "/" + testResult.getTestFolderName() + "/" + croppedImageName);
        FileUtils.copyFile(newResult, baselineImg);

        testResultDAO.setTestResult(getId, true);
        testResultDAO.setVerified(getId, true);

        return "redirect:/testResult?getId=" + getId;
    }

    @RequestMapping(value = "/testObjects", method = RequestMethod.GET)
    public ModelAndView showTestObjects() {
        ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
        TestAreaDAO testAreaDAO = (TestAreaDAO) context.getBean("testAreaDAO");

        return new ModelAndView().addObject("testObjectsList", testAreaDAO.getTestAreas());
    }

    @RequestMapping(value = "/testObject", method = RequestMethod.GET, params = {"testObjectId"})
    public ModelAndView showTestObject(@RequestParam String testObjectId) {
        ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
        TestAreaDAO testAreaDAO = (TestAreaDAO) context.getBean("testAreaDAO");
        TestArea testArea = testAreaDAO.getTestArea(testObjectId);
        return new ModelAndView().addObject("testObject", testArea);
    }


    @RequestMapping(value = "/testResult", method = RequestMethod.POST, params = {"acceptFailure", "getId"})
    public String acceptFailure(@RequestParam int getId) {
        ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
        TestResultDAO testResultDAO = (TestResultDAO) context.getBean("testResultDAO");
        testResultDAO.setVerified(getId, true);

        return "redirect:/testResult?getId=" + getId;
    }

    @RequestMapping("/DBActions")
    public void getDBActions() {
    }

    @RequestMapping(value = "/runInitDB", method = RequestMethod.POST)
    public void runInitDB() {
        ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");

        TestAreaDAO testAreaDAO = (TestAreaDAO) context.getBean("testAreaDAO");
        testAreaDAO.initTestAreaDB();

        TestResultDAO testResultDAO = (TestResultDAO) context.getBean("testResultDAO");
        testResultDAO.initTestResultsDB();
    }

    @RequestMapping(value = "/dbactions/exportTestAreas", method = RequestMethod.GET)
    public void exportTestAreas(HttpServletResponse response) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");

        TestAreaDAO testAreaDAO = (TestAreaDAO) context.getBean("testAreaDAO");
        List<TestArea> areas = testAreaDAO.getTestAreas();

        String csvFileName = "TestAreas.csv";
        response.setContentType("text/csv");

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
        response.setHeader(headerKey, headerValue);

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);

        String[] header = {"areaId", "areaName", "fileName", "width", "height", "posX", "posY",
                "browserWidth", "browserHeight", "browserName", "browserVersion"};
        csvWriter.writeHeader(header);

        for (TestArea area : areas) {
            csvWriter.write(area, header);
        }

        csvWriter.close();
    }

    @RequestMapping(value = "/dbactions/exportTestResults", method = RequestMethod.GET)
    public void exportTestResults(HttpServletResponse response) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");

        TestResultDAO testResultDAO = (TestResultDAO) context.getBean("testResultDAO");
        List<TestResult> testResults = testResultDAO.getAllTestResults();

        String csvFileName = "TestResults.csv";
        response.setContentType("text/csv");

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
        response.setHeader(headerKey, headerValue);

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);

        String[] header = {"testId", "testAreaId", "testSessionTs", "testRunTs", "testResult",
                "numberOfDiffPixels", "isVerified", "testFolderName"};
        csvWriter.writeHeader(header);

        for (TestResult testResult : testResults) {
            csvWriter.write(testResult, header);
        }

        csvWriter.close();
    }


    @RequestMapping("/uploadFile")
    public void getUploadForm() {
    }

    @RequestMapping(value = "/showFile", method = RequestMethod.POST)
    public ModelAndView fileUploaded(@ModelAttribute("uploadedFile") UploadedFile uploadedFile, BindingResult result) {
        InputStream inputStream;
        OutputStream outputStream;

        MultipartFile file = uploadedFile.getFile();
        fileValidator.validate(uploadedFile, result);

        String fileName = file.getOriginalFilename();

        if (result.hasErrors()) {
            return new ModelAndView("uploadFile");
        }

        try {
            inputStream = file.getInputStream();

            File newFile = new File(testAreasImagesPath + fileName);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            outputStream = new FileOutputStream(newFile);
            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ModelAndView("showFile", "message", fileName);
    }

    @RequestMapping(value = "/createTestArea", method = RequestMethod.POST)
    public ModelAndView fileUploaded(@ModelAttribute("imageSelection") ImageSelection imageSelection)
            throws IOException {
        TestArea testArea = new TestArea();
        testArea.setPosX(imageSelection.getX1());
        testArea.setPosY(imageSelection.getY1());
        testArea.setWidth(imageSelection.getX2() - imageSelection.getX1());
        testArea.setHeight(imageSelection.getY2() - imageSelection.getY1());
        testArea.setCroppedFromFileName(imageSelection.getFileName());

        File imageFile = new File(testAreasImagesPath + imageSelection.getFileName());
        BufferedImage bimg = ImageIO.read(imageFile);

        testArea.setBrowserHeight(bimg.getHeight());
        testArea.setBrowserWidth(bimg.getWidth());

        ModelAndView model = new ModelAndView("createTestArea");
        model.addObject("testArea", testArea);

        return model;
    }

    @RequestMapping(value = "/testAreaCreated", method = RequestMethod.POST)
    public ModelAndView testAreaCreated(@ModelAttribute("testArea") TestArea testArea) {
        // generate area ID and area file name
        String testAreaID = testArea.getBrowserName() + testArea.getBrowserVersion() +
                "_" + testArea.getBrowserWidth() + "_" + testArea.getAreaName().replaceAll(" ", "");
        testArea.setAreaId(testAreaID);
        testArea.setFileName(testAreaID + ".png");

        // create new baseline image
        cropImage(
                testAreasImagesPath + testArea.getCroppedFromFileName(),
                baselineImagePath + testArea.getFileName(),
                testArea.getPosX(),
                testArea.getPosY(),
                testArea.getWidth(),
                testArea.getHeight());

        // delete temp baseline image
        File toDelete = new File(testAreasImagesPath + testArea.getCroppedFromFileName());
        toDelete.delete();


        // save test area to DB
        ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
        TestAreaDAO testAreaDAO = (TestAreaDAO) context.getBean("testAreaDAO");
        testAreaDAO.addTestArea(testArea);

        // return testArea
        ModelAndView model = new ModelAndView("testAreaCreated");
        model.addObject("testArea", testArea);
        return model;
    }


    @RequestMapping(value = "/dbactions/importTestAreas", method = RequestMethod.POST)
    public ModelAndView importTestAreas(@ModelAttribute("uploadedFile") UploadedFile uploadedFile, BindingResult result)
            throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
        TestAreaDAO testAreaDAO = (TestAreaDAO) context.getBean("testAreaDAO");

        MultipartFile file = uploadedFile.getFile();
        fileValidator.validate(uploadedFile, result);

        String fileName = file.getOriginalFilename();

        if (result.hasErrors()) {
            return new ModelAndView("uploadFile");
        }

        ICsvBeanReader beanReader = null;
        try {
            beanReader = new CsvBeanReader(new InputStreamReader(file.getInputStream()), CsvPreference.STANDARD_PREFERENCE);

            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getTestAreaProcessor();

            TestArea testArea;
            while ((testArea = beanReader.read(TestArea.class, header, processors)) != null) {
                testAreaDAO.addTestArea(testArea);
            }

        } finally {
            if (beanReader != null) {
                beanReader.close();
            }
        }


        return new ModelAndView("/dbactions/testAreasImported", "message", fileName);
    }


    @RequestMapping(value = "/dbactions/importTestResults", method = RequestMethod.POST)
    public ModelAndView importTestResults(@ModelAttribute("uploadedFile") UploadedFile uploadedFile, BindingResult result)
            throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
        TestResultDAO testResultDAO = (TestResultDAO) context.getBean("testResultDAO");

        MultipartFile file = uploadedFile.getFile();
        fileValidator.validate(uploadedFile, result);

        String fileName = file.getOriginalFilename();

        if (result.hasErrors()) {
            return new ModelAndView("uploadFile");
        }

        ICsvBeanReader beanReader = null;
        try {
            beanReader = new CsvBeanReader(new InputStreamReader(file.getInputStream()), CsvPreference.STANDARD_PREFERENCE);

            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getTestResultProcessor();

            TestResult testResult;
            while ((testResult = beanReader.read(TestResult.class, header, processors)) != null) {
                testResultDAO.importTestResult(testResult);
            }

        } finally {
            if (beanReader != null) {
                beanReader.close();
            }
        }


        return new ModelAndView("/dbactions/testResultsImported", "message", fileName);
    }


}