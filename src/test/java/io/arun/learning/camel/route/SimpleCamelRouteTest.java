package io.arun.learning.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("dev")
@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class SimpleCamelRouteTest {

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    Environment env;

    @BeforeClass
    public static void startCleanUp() throws IOException {
        FileUtils.cleanDirectory(new File("data/input"));
        FileUtils.deleteDirectory(new File("data/output"));
        FileUtils.deleteDirectory(new File("data/input/error"));
    }

    @Test
    public void testMoveFile() throws InterruptedException {
        String msg = "type,sku#,itemdescription,price\n" +
                "ADD,100,Samsung TV,500\n" +
                "ADD,101,LG TV,400";
        String fileName = "fileTest.txt";
        producerTemplate.sendBodyAndHeader(env.getProperty("fromRoute"),msg, Exchange.FILE_NAME,fileName);
        Thread.sleep(3000);
        File outFile = new File("data/output/"+fileName);
        assertTrue(outFile.exists());
    }

    @Test
    public void testMoveFile_ADD() throws InterruptedException, IOException {
        String msg = "type,sku#,itemdescription,price\n" +
                "ADD,100,Samsung TV,500\n" +
                "ADD,101,LG TV,400";
        String fileName = "fileTest.txt";
        producerTemplate.sendBodyAndHeader(env.getProperty("fromRoute"),msg, Exchange.FILE_NAME,fileName);
        Thread.sleep(3000);
        File outFile = new File("data/output/"+fileName);
        assertTrue(outFile.exists());
        String message = "Data updated successfully!";
        String outputMessage = new String(Files.readAllBytes(Paths.get("data/output/Success.txt")));
        assertEquals(outputMessage,message);
    }

    @Test
    public void testMoveFile_ADD_Exception() throws InterruptedException, IOException {
        String message = "type,sku#,itemdescription,price\n" +
                "ADD,,Samsung TV,500\n"+
                "ADD,108,LG TV,400";
        String fileName="fileTest.txt";

        producerTemplate.sendBodyAndHeader(env.getProperty("fromRoute")
                ,message, Exchange.FILE_NAME,fileName);

        Thread.sleep(3000);

        File outFile = new File("data/output/"+fileName);
        assertTrue(outFile.exists());
        File errorDirectory = new File("data/input/error");
        assertTrue(errorDirectory.exists());
    }

    @Test
    public void testMoveFile_UPDATE() throws InterruptedException, IOException {
        String msg = "type,sku#,itemdescription,price\n" +
                "UPDATE,100,Samsung TV,800";
        String fileName = "fileUpdate.txt";
        producerTemplate.sendBodyAndHeader(env.getProperty("fromRoute"),msg, Exchange.FILE_NAME,fileName);
        Thread.sleep(3000);
        File outFile = new File("data/output/"+fileName);
        assertTrue(outFile.exists());
        String message = "Data updated successfully!";
        String outputMessage = new String(Files.readAllBytes(Paths.get("data/output/Success.txt")));
        assertEquals(outputMessage,message);
    }

    @Test
    public void testMoveFile_DELETE() throws InterruptedException, IOException {
        String msg = "type,sku#,itemdescription,price\n" +
                "DELETE,100,Samsung TV,800";
        String fileName = "fileDelete.txt";
        producerTemplate.sendBodyAndHeader(env.getProperty("fromRoute"),msg, Exchange.FILE_NAME,fileName);
        Thread.sleep(3000);
        File outFile = new File("data/output/"+fileName);
        assertTrue(outFile.exists());
        String message = "Data updated successfully!";
        String outputMessage = new String(Files.readAllBytes(Paths.get("data/output/Success.txt")));
        assertEquals(outputMessage,message);
    }

}
