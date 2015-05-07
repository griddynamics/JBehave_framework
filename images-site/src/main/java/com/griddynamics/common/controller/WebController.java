package com.griddynamics.common.controller;

import com.griddynamics.data.dao.TestPageDAO;
import com.griddynamics.data.model.*;
import com.griddynamics.imwrapper.ImageMagickWrapper;
import com.griddynamics.data.dao.TestAreaDAO;
import com.griddynamics.data.dao.TestResultDAO;
import com.griddynamics.data.testresult.validator.FileValidator;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import static com.griddynamics.data.model.TestArea.getTestAreaProcessor;
import static com.griddynamics.data.model.TestPage.getTestPageProcessor;
import static com.griddynamics.data.model.TestResult.getTestResultProcessor;

@Controller
public class WebController {

    @Autowired
    FileValidator fileValidator;

    @Autowired
    public FileStructure fileStructure;

    @Autowired
    public ImageMagickWrapper imageMagickWrapper;

    @Autowired
    public TestAreaDAO testAreaDAO;

    @Autowired
    public TestResultDAO testResultDAO;

    @Autowired
    public TestPageDAO testPageDAO;


    @RequestMapping(value = "/testResult", method = RequestMethod.GET, params = {"getId"})
    public ModelAndView showTestResult(@RequestParam int getId, ModelMap model) {
        TestResult testResult2 = testResultDAO.getTestResult(getId);
        System.out.println(testResult2);
        ModelAndView mv = new ModelAndView();
        mv.addObject("testResult", testResult2);
        mv.addObject("testResultImagesFolder", testResult2.getResultFolderName());
        mv.addObject("fileStructure", fileStructure);
        mv.addObject(testResultDAO.getTestResultsOnePerSession());
        return mv;
    }

    @RequestMapping(value = "/testSessions", method = RequestMethod.GET)
    public ModelAndView showTestSessions() {
        return new ModelAndView().addObject("testResultList", testResultDAO.getTestResultsOnePerSession());
    }

    @RequestMapping(value = "/testCases", method = RequestMethod.GET, params = {"sessionId"})
    public ModelAndView showTestCases(@RequestParam String sessionId) {
        return new ModelAndView().addObject("testResultList", testResultDAO.getSessionResults(sessionId));
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() throws SQLException {
        return "home";
    }

    @RequestMapping(value = "/testResult", method = RequestMethod.POST, params = {"acceptNewBaseline", "getId"})
    public String acceptNewBaseline(@RequestParam int getId) throws IOException {
        TestResult testResult = testResultDAO.getTestResult(getId);

        File baselineImg = new File(fileStructure.getBaselineImagePath() + testResult.getTestArea().getFileName());
        baselineImg.delete();

        File newResult = new File(fileStructure.getResultImagePath() + testResult.getResultFolderName() + "/" +
                testResult.getTestFolderName() + "/" + fileStructure.getCroppedScreenshotFileName());
        FileUtils.copyFile(newResult, baselineImg);

        testResultDAO.setTestResult(getId, true);
        testResultDAO.setVerified(getId, true);

        return "redirect:/testResult?getId=" + getId;
    }

    @RequestMapping(value = "/testObjects", method = RequestMethod.GET)
    public ModelAndView showTestObjects() {
        return new ModelAndView().
                addObject("testObjectsMap", testAreaDAO.getTestAreasGroupedByPage());
    }

    @RequestMapping(value = "/testPages", method = RequestMethod.GET)
    public ModelAndView showTestPages() {
        return new ModelAndView().
                addObject("testPagesMap", testPageDAO.getTestPagesGroupedByPageName());
    }

    @RequestMapping(value = "/testObject", method = RequestMethod.GET, params = {"testObjectId"})
    public ModelAndView showTestObject(@RequestParam String testObjectId) {
        TestArea testArea = testAreaDAO.getTestArea(testObjectId);
        return new ModelAndView().addObject("testObject", testArea).addObject("fileStructure", fileStructure);
    }

    @RequestMapping(value = "/testPage", method = RequestMethod.GET, params = {"testPageId"})
    public ModelAndView showTestPage(@RequestParam String testPageId) {
        TestPage testPage = testPageDAO.getTestPage(testPageId);
        return new ModelAndView().
                addObject("testPage", testPage).
                addObject("fileStructure", fileStructure).
                addObject("testObjects", testAreaDAO.getTestAreasForPage(testPageId));
    }

    @RequestMapping(value = "/deleteArea", method = RequestMethod.GET, params = {"id"})
    public String deleteArea(@RequestParam String id) {
        String pageId = testAreaDAO.getTestArea(id).getPage().getPageID();
        File toDelete = new File(fileStructure.getBaselineImagePath() + testAreaDAO.getTestArea(id).getFileName());
        toDelete.delete();
        testAreaDAO.deleteTestArea(id);
        return "redirect:/testPage?testPageId=" + pageId;
    }

    @RequestMapping(value = "/deletePage", method = RequestMethod.GET, params = {"id"})
    public String deletePage(@RequestParam String id) {
        File toDelete = new File(fileStructure.getTestPagesPath() + testPageDAO.getTestPage(id).getFileName());
        toDelete.delete();
        testPageDAO.deleteTestPage(id);
        return "redirect:/testPages";
    }

    @RequestMapping(value = "/testResult", method = RequestMethod.POST, params = {"acceptFailure", "getId"})
    public String acceptFailure(@RequestParam int getId) {
        testResultDAO.setVerified(getId, true);
        return "redirect:/testResult?getId=" + getId;
    }


    @RequestMapping(value = "/addTestAreaForPage", method = RequestMethod.GET, params = {"id"})
    public ModelAndView addTestAreaForPage(@RequestParam String id) {
        return new ModelAndView("showFile").
                addObject("testPage", testPageDAO.getTestPage(id)).
                addObject("fileStructure", fileStructure);
    }


    @RequestMapping("/DBActions")
    public void getDBActions() {
    }

    @RequestMapping(value = "/runInitDB", method = RequestMethod.POST)
    public void runInitDB() {
        testAreaDAO.initTestAreaDB();
        testResultDAO.initTestResultsDB();
        testPageDAO.initTestPageDB();
    }


    @RequestMapping(value = "/dbactions/exportTestPages", method = RequestMethod.GET)
    public void exportTestPages(HttpServletResponse response) throws IOException {
        List<TestPage> pages = testPageDAO.getTestPages();
        String csvFileName = "TestPages.csv";
        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
        response.setHeader(headerKey, headerValue);
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);
        String[] header = {
                "pageId", "pageName", "fileName",
                "browserName", "browserVersion",
                "browserWidth", "browserHeight", "imageHeight"};
        csvWriter.writeHeader(header);
        for (TestPage page : pages) {
            csvWriter.write(page, header);
        }
        csvWriter.close();
    }

    @RequestMapping(value = "/dbactions/exportTestAreas", method = RequestMethod.GET)
    public void exportTestAreas(HttpServletResponse response) throws IOException {
        List<TestArea> areas = testAreaDAO.getTestAreas();
        String csvFileName = "TestAreas.csv";
        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
        response.setHeader(headerKey, headerValue);
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);
        String[] header = {
                "areaId", "areaName", "fileName", "pageId",
                "width", "height", "posX", "posY", "countFromTop"};
        csvWriter.writeHeader(header);
        for (TestArea area : areas) {
            csvWriter.write(area, header);
        }
        csvWriter.close();
    }

    @RequestMapping(value = "/dbactions/exportTestResults", method = RequestMethod.GET)
    public void exportTestResults(HttpServletResponse response) throws IOException {
        List<TestResult> testResults = testResultDAO.getAllTestResults();
        String csvFileName = "TestResults.csv";
        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
        response.setHeader(headerKey, headerValue);
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);
        String[] header = {
                "testId", "testAreaId", "testSessionTs", "testRunTs", "testResult",
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
            File newFile = new File(fileStructure.getBaselineImagePath() + fileName);
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
        return new ModelAndView("showFile", "message", fileName).addObject("fileStructure", fileStructure);
    }

    @RequestMapping(value = "/createTestArea", method = RequestMethod.POST)
    public ModelAndView fileUploaded(@ModelAttribute("imageSelection") ImageSelection imageSelection)
            throws IOException {
        TestPage page = testPageDAO.getTestPage(imageSelection.getPageId());
        String pageFile = page.getFileName();
        TestArea testArea = new TestArea();
        testArea.setPosX(imageSelection.getX1());
        testArea.setPosY(imageSelection.getY1());
        testArea.setWidth(imageSelection.getX2() - imageSelection.getX1());
        testArea.setHeight(imageSelection.getY2() - imageSelection.getY1());
        testArea.setCroppedFromFileName(pageFile);
        File imageFile = new File(fileStructure.getTestPagesPath() + pageFile);
        BufferedImage bimg = ImageIO.read(imageFile);
        ModelAndView model = new ModelAndView("createTestArea");
        model.addObject("testArea", testArea);
        model.addObject("page", page);
        return model;
    }


    @RequestMapping(value = "/editTestArea", method = RequestMethod.GET, params = {"id"})
    public ModelAndView editTestArea(@RequestParam String id)
            throws IOException {
        TestArea testArea = testAreaDAO.getTestArea(id);
        ModelAndView model = new ModelAndView("editTestArea");
        model.addObject("testArea", testArea);
        return model;
    }


    @RequestMapping(value = "/testAreaUpdated", method = RequestMethod.POST)
    public String testAreaUpdated(@ModelAttribute("testArea") TestArea testArea) {
        testAreaDAO.updateTestArea(testArea);
        return "redirect:/testObjects";
    }


    @RequestMapping(value = "/testAreaCreated", method = RequestMethod.POST)
    public String testAreaCreated(@ModelAttribute("testArea") TestArea testArea) {
        // generate area ID and area file name
        String testAreaID = testArea.getAreaName().replaceAll(" ", "") + "_" + testArea.getPage().getPageID();
        testArea.setAreaId(testAreaID);
        testArea.setFileName(testAreaID + ".png");
        // create new baseline image


        imageMagickWrapper.crop(fileStructure.getTestPagesPath() + testArea.getCroppedFromFileName(),
                fileStructure.getBaselineImagePath() + testArea.getFileName(),
                testArea.getPosX(),
                testArea.getPosY(),
                testArea.getWidth(),
                testArea.getHeight());

        // delete temp baseline image
        File toDelete = new File(fileStructure.getBaselineImagePath() + testArea.getCroppedFromFileName());
        toDelete.delete();

        if (!testArea.getCountFromTop()) {
            testArea.setPosY(testPageDAO.getTestPage(testArea.getPage().getPageID()).getImageHeight()
                    - testArea.getPosY() - testArea.getHeight());
        }

        // save test area to DB
        testAreaDAO.addTestArea(testArea);


        return "redirect:/testPage?testPageId=" + testArea.getPage().getPageID();
        /*// return testArea
        ModelAndView model = new ModelAndView("testAreaCreated");
        model.addObject("testArea", testArea);
        model.addObject("fileStructure", fileStructure);
        return model;    */
    }


    @RequestMapping(value = "/testPageCreated", method = RequestMethod.POST)
    public String testPageCreated(@ModelAttribute("testPage") TestPage testPage) {

        File toMove = new File(fileStructure.getTestPagesPath() + testPage.getFileName());

        // generate page ID and page file name
        String pageID = testPage.getPageName().replaceAll(" ", "_") + "_" + testPage.getBrowserName() +
                "_" + testPage.getBrowserVersion() + "_" + testPage.getBrowserWidth() + "x" + testPage.getBrowserHeight();
        testPage.setPageID(pageID);
        testPage.setFileName(pageID + ".png");


        File newFile = new File(fileStructure.getTestPagesPath() + testPage.getFileName());
        toMove.renameTo(newFile);


        // save test page to DB
        testPageDAO.addTestPage(testPage);

        return "redirect:/testPages";
    }

    @RequestMapping(value = "/dbactions/importTestAreas", method = RequestMethod.POST)
    public ModelAndView importTestAreas(@ModelAttribute("uploadedFile") UploadedFile uploadedFile, BindingResult result)
            throws IOException {
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


    @RequestMapping(value = "/dbactions/importTestPages", method = RequestMethod.POST)
    public ModelAndView importTestPages(@ModelAttribute("uploadedFile") UploadedFile uploadedFile, BindingResult result)
            throws IOException {
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
            final CellProcessor[] processors = getTestPageProcessor();
            TestPage testPage;
            while ((testPage = beanReader.read(TestPage.class, header, processors)) != null) {
                testPageDAO.addTestPage(testPage);
            }
        } finally {
            if (beanReader != null) {
                beanReader.close();
            }
        }
        return new ModelAndView("/dbactions/testPagesImported", "message", fileName);
    }


    @RequestMapping(value = "/dbactions/importTestResults", method = RequestMethod.POST)
    public ModelAndView importTestResults(@ModelAttribute("uploadedFile") UploadedFile uploadedFile, BindingResult result)
            throws IOException {
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


    @RequestMapping(value = "/uploadNewPage", method = RequestMethod.POST)
    public ModelAndView uploadNewPage(@ModelAttribute("uploadedFile") UploadedFile uploadedFile, BindingResult result)
            throws IOException {
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
            File newFile = new File(fileStructure.getTestPagesPath() + fileName);
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

        TestPage testPage = new TestPage();
        File imageFile = new File(fileStructure.getTestPagesPath() + fileName);
        BufferedImage bimg = ImageIO.read(imageFile);
        testPage.setBrowserHeight(bimg.getHeight());
        testPage.setBrowserWidth(bimg.getWidth());
        testPage.setImageHeight(bimg.getHeight());


        return new ModelAndView("createNewPage", "message", fileName).
                addObject("fileStructure", fileStructure).
                addObject("testPage", testPage);
    }
}