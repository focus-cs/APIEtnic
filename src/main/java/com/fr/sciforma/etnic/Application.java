package com.fr.sciforma.etnic;

import com.fr.sciforma.etnic.service.SciformaService;
import com.sciforma.psnext.api.LockException;
import com.sciforma.psnext.api.PSException;
import com.sciforma.psnext.api.Project;
import java.util.List;
import org.pmw.tinylog.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    SciformaService sciformaService;
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        sciformaService.createConnection();
                        
        List<Project> projectList = sciformaService.getProjectList(Project.VERSION_WORKING, Project.READWRITE_ACCESS);
        for(Project project : projectList) {
            
            Logger.info("Treating project " + project.getStringField("Name"));
            
            try {
                
                project.open(false);
                
//                project.applyTimesheets(date, date1);
                
                project.save();
                
                try {
                    project.publish();
                    Logger.info("Project has been published");
                } catch (PSException e) {
                    Logger.error("Failed to publish project", e);
                }
                
            } catch(LockException e) {
                Logger.error("Project is locked by ", e.getLockingUser());
            } catch (PSException e) {
                Logger.error("Failed to open project", e);
            } finally {
                project.close();
            }
            
        }
        
        sciformaService.closeConnection();
                
    }
}
