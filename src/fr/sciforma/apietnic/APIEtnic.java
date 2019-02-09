/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.sciforma.apietnic;

import com.sciforma.psnext.api.LockException;
import com.sciforma.psnext.api.PSException;
import com.sciforma.psnext.api.Project;
import fr.sciforma.apietnic.service.SciformaService;
import java.util.Date;
import java.util.List;
import org.pmw.tinylog.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author mehdi
 */
@ComponentScan(basePackages = "fr.sciforma")
@Configuration
public class APIEtnic {

    @Autowired
    SciformaService sciformaService;
    
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(APIEtnic.class);
        APIEtnic api = context.getBean(APIEtnic .class);
        
        try {
            
            Logger.info("Start APIEtnic at " + new Date());
            api.start();
            
        } catch (Exception ex) {
            
            Logger.error("An error occured during execution", ex);
            
        }
    }

    private void start() throws Exception {
        
        if (sciformaService.createConnection()) {

            Date now = new Date();

            List<Project> projectList = sciformaService.getProjectList(Project.VERSION_WORKING, Project.READWRITE_ACCESS);
            for (Project project : projectList) {

                Logger.info("Processing of project " + project.getStringField("Name"));

                try {

                    project.open(false);

                    Date startDate = project.getDateField("Start");
                    Date endDate = project.getDateField("Finish");

                    if (startDate != null && endDate != null && now.before(endDate)) {

                        Logger.info("Applying timesheet");
                        project.applyTimesheets(startDate, endDate, true, true, false);

                        project.save();
                        try {
                            project.publish();
                            Logger.info("Project has been published");
                        } catch (PSException e) {
                            Logger.error("Failed to publish project", e);
                        }

                    } else {
                        Logger.warn("Project is finished or dates are wrong -> skipping");
                    }

                } catch (LockException e) {
                    Logger.error("Project is locked by " + e.getLockingUser());
                } catch (PSException e) {
                    Logger.error("An error has occured", e);
                } finally {
                    project.close();
                }

            }

            sciformaService.closeConnection();

        }
    }
    
}
